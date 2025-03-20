package com.doc.format.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.ContentCheckTaskEntity;
import com.doc.format.enums.CheckStatusEnum;
import com.doc.format.mapper.ContentCheckTaskMapper;
import com.doc.format.service.IContentCheckTaskService;
import com.doc.format.bo.ContentCheckTaskQueryBo;
import com.doc.format.bo.ContentCheckTaskSaveBo;
import com.doc.format.service.IIJianChaService;
import com.doc.format.util.JedisUtil;
import com.doc.format.util.iJianCha.CheckRequest;
import com.doc.format.util.iJianCha.CheckResponse;
import com.doc.format.util.iJianCha.ProofreadingUtil;
import com.doc.format.util.iJianCha.TokenUtil;
import com.doc.format.vo.ContentCheckTaskDetailVo;
import com.doc.format.vo.ContentCheckTaskListVo;
import com.spire.doc.*;
import com.spire.doc.documents.HorizontalAlignment;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.formatting.ParagraphFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.doc.format.util.spire.JsonToWord.removeTite;


/**
 * 内容校验任务Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Service
@Slf4j
public class ContentCheckTaskServiceImpl extends ServiceImpl<ContentCheckTaskMapper, ContentCheckTaskEntity> implements IContentCheckTaskService {
    private static final String I_JIAN_CHA_RECOMMENDATION_CATEGORIES = "IJianCha_Recommendation_Categories";
    private static final String I_JIAN_CHA_RECOMMENDATION_LEVEL = "IJianCha_Recommendation_Level";
    @Value("${file.upload.dir}")
    private String fileUploadDir;
    @Resource
    private JedisUtil jedisUtil;
    @Resource
    private IIJianChaService iiJianChaService;

    public static Map<String,String> getDocumentText(String filePath) {
        // 加载测试文档
        Document document = new Document(filePath);
        //判断文档是否有修改
        if (document.hasChanges()) {
            //接受修订
            document.acceptChanges();
        }
        StringBuilder text = new StringBuilder();
        StringBuilder textNotN = new StringBuilder();

        List< Map<String,String> > formats = new ArrayList<>();


        // 循环遍历各个节
        for (int i = 0; i < document.getSections().getCount(); i++) {

            Body body = document.getSections().get(i).getBody();
            // 循环遍历特定节的段落
            for (int j = 0; j < body.getChildObjects().getCount(); j++) {
                DocumentObject documentObject = body.getChildObjects().get(j);
                if (documentObject instanceof Paragraph) {
                    Map<String,String>  format = new HashMap<>();
                    // 获取特定段落
                    // 记录添加前的文本长度作为起始索引
                    int startIndex = textNotN.length();
                    Paragraph paragraph = (Paragraph) documentObject;
                    text.append(paragraph.getText());
                    textNotN.append(paragraph.getText());
                    // 计算结束索引（包含换行符）
                    int endIndex = textNotN.length();
                    format.put("bold", String.valueOf(paragraph.getFormat().getHorizontalAlignment()));
                    format.put("start", String.valueOf(startIndex));
                    format.put("end", String.valueOf(endIndex));
                    formats.add(format);
                    text.append("\n");
                }
            }

        }
        Map<String,String> map = new HashMap<>();
        map.put("text",text.toString());
        map.put("format", JSONObject.toJSONString(formats));
        return map;
    }

    @Override
    public Result<Page<ContentCheckTaskListVo>> page(ContentCheckTaskQueryBo queryBo) {
        // 转换参数实体
        Page<ContentCheckTaskListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<ContentCheckTaskListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<ContentCheckTaskListVo>> list(ContentCheckTaskQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<ContentCheckTaskDetailVo> get(long id) {
        // 调用查询方法
        ContentCheckTaskEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, ContentCheckTaskDetailVo.class));
    }

    @Override
    public Result<ContentCheckTaskDetailVo> insert(ContentCheckTaskSaveBo saveBo) {
        // 转换参数实体
        ContentCheckTaskEntity entity = BeanUtil.copyProperties(saveBo, ContentCheckTaskEntity.class);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, ContentCheckTaskDetailVo.class));
    }

    @Override
    public Result<ContentCheckTaskDetailVo> update(ContentCheckTaskSaveBo saveBo) {
        // 转换参数实体
        ContentCheckTaskEntity entity = BeanUtil.copyProperties(saveBo, ContentCheckTaskEntity.class);
        entity.setUpdateTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, ContentCheckTaskDetailVo.class));
    }

    @Override
    public Result<String> remove(List<Long> ids) {
        int row = baseMapper.deleteBatchIds(ids);
        if (row != 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败");
        }
    }

    @Override
    public Result<String> remove(long id) {
        int row = baseMapper.deleteById(id);
        if (row != 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败");
        }
    }

    @Override
    public Result<List<ContentCheckTaskListVo>> selectIdsList(List<Long> ids) {
        List<ContentCheckTaskEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<ContentCheckTaskEntity>()
                .in(ContentCheckTaskEntity::getId, ids));
        List<ContentCheckTaskListVo> teacherFeedbackListVos = new ArrayList<>();
        for (ContentCheckTaskEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, ContentCheckTaskListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }

    @Override
    public Result<ContentCheckTaskDetailVo> uploadAndParseFile(MultipartFile file) {
        ContentCheckTaskEntity entity = new ContentCheckTaskEntity();
        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();

        // 增加临时文件路径记录
        String sourceFilePath = null;
        String parsedFilePath = null;
        String checkedFilePath = null; // 新增校验文件路径跟踪

        try {
            // 基础路径（示例：uploads/2023/08/）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String baseDir = fileUploadDir + File.separator + datePath;

            // 构建存储目录结构
            String taskDir = baseDir + File.separator + uuid;
            String sourceDir = taskDir + File.separator + "source";
            String parsedDir = taskDir + File.separator + "parsed";
            String checkedDir = taskDir + File.separator + "checked"; // 新增校验文件目录

            // 创建目录
            createDirectory(sourceDir);
            createDirectory(parsedDir);
            createDirectory(checkedDir); // 创建校验文件目录

            // === 1. 存储原始文件 ===
            sourceFilePath = sourceDir + File.separator + originalFilename;
            file.transferTo(new File(sourceFilePath));

            // === 2. 解析文档内容 ===
            Map<String, String> document = getDocumentText(sourceFilePath);
            String documentText = document.get("text");

            // === 3. 生成解析文件 ===
            String parsedFilename = getParsedFilename(originalFilename);
            parsedFilePath = parsedDir + File.separator + parsedFilename;
            writeTextFile(documentText, parsedFilePath);

            // === 4. 生成校验文件路径 ===
            String checkedFilename = generateCheckedFileName(originalFilename);
            checkedFilePath = checkedDir + File.separator + checkedFilename;

            // === 5. 设置实体字段 ===
            // 路径统一转换为相对路径存储
            entity.setOriginalFile(sourceFilePath);
            entity.setParsedText(documentText); // 直接存储文本内容
            entity.setParsedJson(StringUtils.defaultString(document.get("format"), "{}")); // 防null处理
            entity.setCheckedFile(checkedFilePath); // 设置校验文件路径

            // 新增字段设置（根据实际业务补充数据来源）
            entity.setTaskStatus(CheckStatusEnum.UPLOADED.getCode()); // 初始状态
            entity.setCheckToken(0); // 默认0，后续更新
            entity.setCheckResult("{}"); // 默认空JSON
            entity.setFormatTaskId(-1L); // 默认-1表示未关联

            baseMapper.insert(entity);

            return Result.success(BeanUtil.copyProperties(entity, ContentCheckTaskDetailVo.class));
        } catch (IOException | IllegalArgumentException e) {
            log.error("文件处理失败 | UUID:{} | 文件名:{}", uuid, originalFilename, e);
            // 清理已创建的文件（新增校验文件清理）
            deleteFileIfExists(sourceFilePath);
            deleteFileIfExists(parsedFilePath);
            deleteFileIfExists(checkedFilePath);
            return Result.fail("文件处理失败: " + e.getMessage());
        }
    }

    // 生成校验文件名方法（根据原始文件名添加 _checked 后缀）
    private String generateCheckedFileName(String originalFilename) {
        if (originalFilename == null) return "校验之后的文档";

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            return originalFilename.substring(0, dotIndex) + "_校验结果" + originalFilename.substring(dotIndex);
        }
        return originalFilename + "_校验结果";
    }

    /**
     * 校验文本内容
     * @param id
     * @param text
     * @return
     * @throws Exception
     */

    @Override
    public Result<CheckResponse> check(Integer id, String text) throws Exception {

        String accessToken = TokenUtil.getAccessToken();
        ContentCheckTaskEntity contentCheckTaskEntity = baseMapper.selectById(id);
        contentCheckTaskEntity.setCheckToken(text.length());
        Map<String, String> recommendationCategories = jedisUtil.getMap(I_JIAN_CHA_RECOMMENDATION_CATEGORIES);
        if (recommendationCategories == null || recommendationCategories.isEmpty()) {
            recommendationCategories = iiJianChaService.setRecommendationCategoriesInfo();
            jedisUtil.setMap(I_JIAN_CHA_RECOMMENDATION_CATEGORIES, recommendationCategories);
        }
        Map<String, String> typeMap = jedisUtil.getMap(I_JIAN_CHA_RECOMMENDATION_LEVEL);
        if (typeMap == null || typeMap.isEmpty()) {
            typeMap = iiJianChaService.setRecommendationLevelInfo();
            jedisUtil.setMap(I_JIAN_CHA_RECOMMENDATION_LEVEL, typeMap);
        }
        CheckResponse checkResponse = ProofreadingUtil.checkText(accessToken, new CheckRequest(text));
        CheckResponse.Result result = checkResponse.getResult();
        int mistakeIndex = 0;
        for (CheckResponse.Result.Mistake mistake : result.getMistakes()) {
            Map<String, String> finalMap = recommendationCategories;
            Map<String, String> typeMap1 = typeMap;
            mistake.setIdx(mistakeIndex++);
            mistake.getInfos().forEach(info -> {
                info.setTypeName(typeMap1.get(String.valueOf(info.getType())));
                info.setCategoryName(finalMap.get(info.getCategory()));
            });
        }

        contentCheckTaskEntity.setTaskStatus(CheckStatusEnum.VERIFIED.getCode());
        updateById(contentCheckTaskEntity);
        return Result.success(checkResponse);
    }

    /**
     * 下载文件
     * @param saveBo
     * @return
     * @throws IOException
     */

    @Override
    public Result<ContentCheckTaskDetailVo> download(ContentCheckTaskSaveBo saveBo) throws IOException {
        ContentCheckTaskEntity contentCheckTaskEntity = baseMapper.selectById(saveBo.getId());
        //创建 Document 类的对象
        Document document = new Document();

        //添加一个节
        Section section = document.addSection();
        saveBo.getTextList().forEach(text -> {
            text.get("format");
            //添加一个段落并设置为左对齐
            Paragraph paragraph = section.addParagraph();
            paragraph.appendText(text.get("value"));
            ParagraphFormat paragraphFormat = paragraph.getFormat();
            if(text.get("format").equals("Left")){
                paragraphFormat.setHorizontalAlignment(HorizontalAlignment.Left);
            }else if (text.get("format").equals("Center")){
                paragraphFormat.setHorizontalAlignment(HorizontalAlignment.Center);
            }else if (text.get("format").equals("Right")){
                paragraphFormat.setHorizontalAlignment(HorizontalAlignment.Right);
            }else if (text.get("format").equals("Justify")){
                paragraphFormat.setHorizontalAlignment(HorizontalAlignment.Justify);
            }else if (text.get("format").equals("Distribute")){
                paragraphFormat.setHorizontalAlignment(HorizontalAlignment.Distribute);
            }

        });
        //保存结果文档
        document.saveToFile(contentCheckTaskEntity.getCheckedFile(), FileFormat.Docm_2019);
        removeTite(contentCheckTaskEntity.getCheckedFile());
        contentCheckTaskEntity.setTaskStatus(CheckStatusEnum.EXPORTED.getCode());
        updateById(contentCheckTaskEntity);
        return Result.success(BeanUtil.copyProperties(contentCheckTaskEntity, ContentCheckTaskDetailVo.class));
    }

    //---------- 工具方法 ----------
    private void createDirectory(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("目录创建失败: " + path);
        }
    }

    private String getParsedFilename(String originalName) {
        // 处理带空格等特殊字符的文件名
        String safeName = originalName.replaceAll("[^a-zA-Z0-9.-]", "_");

        // 保留原始文件名（去掉扩展名）+ .txt
        int lastDotIndex = safeName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return safeName.substring(0, lastDotIndex) + ".txt";
        }
        return safeName + ".txt";
    }

    private void writeTextFile(String content, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write(content);
        }
    }


    private void deleteFileIfExists(String filePath) {
        if (filePath != null) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException ex) {
                log.error("文件清理失败: {}", filePath);
            }
        }
    }
}
