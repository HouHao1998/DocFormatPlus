package com.doc.format.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.CheckLogEntity;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.FileEntity;
import com.doc.format.mapper.CheckLogMapper;
import com.doc.format.mapper.FileMapper;
import com.doc.format.service.IFileService;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.bo.FileSaveBo;
import com.doc.format.service.IIJianChaService;
import com.doc.format.util.docx4j.JsonToHtmlConverter;
import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ParagraphElement;
import com.doc.format.util.entity.ValidationResult;
import com.doc.format.util.iJianCha.CheckRequest;
import com.doc.format.util.iJianCha.CheckResponse;
import com.doc.format.util.iJianCha.ProofreadingUtil;
import com.doc.format.util.iJianCha.TokenUtil;
import com.doc.format.util.spire.*;
import com.doc.format.vo.FileDetailVo;
import com.doc.format.vo.FileListVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;

import static com.doc.format.util.iJianCha.ProofreadingUtil.parseDocumentElements;
import static com.doc.format.util.iJianCha.ProofreadingUtil.pathToDocumentElements;

/**
 * 文件总览Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements IFileService {
    @Value("${http.upload.dir}")
    private String httpUploadDir;
    @Resource
    private IIJianChaService iJianChaService;
    @Resource
    private CheckLogMapper checkLogMapper;

    @Override
    public Result<Page<FileListVo>> page(FileQueryBo queryBo) {
        // 转换参数实体
        Page<FileListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<FileListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<FileListVo>> list(FileQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<FileDetailVo> get(long id) {
        // 调用查询方法
        FileEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }

    @Override
    public Result<FileDetailVo> insert(FileSaveBo saveBo) throws IOException {
        // 转换参数实体
        FileEntity entity = BeanUtil.copyProperties(saveBo, FileEntity.class);
        String filePath = entity.getFilePath();

        // 将Word转换为HTML并保存HTML文件路径
        String htmlPath = WordToHtml.wordToHtml(filePath);
        HtmlParser.addIdx(htmlPath); // 添加索引文件
        HtmlParser.updateImgSrc(htmlPath, httpUploadDir + saveBo.getUuid() + File.separator);
        entity.setHtmlPath(htmlPath);


        // 获取文档元素列表
        List<DocumentElement> documentElements = WordRevision.getDocumentElements(filePath);
        ObjectMapper objectMapper = new ObjectMapper();

        // 获取文件目录路径
        String directoryPath = Paths.get(filePath).getParent().toString();


        String htmlContent = JsonToHtmlConverter.convertToHtml(documentElements);

        // 将 HTML 内容写入文件
        entity.setJsonHtmlPath(saveJsonToFile(htmlContent, directoryPath, "output.html"));
        JsonToHtmlConverter.mapHtmlToJson(htmlContent, documentElements);
        // 将documentElements转换为JSON并保存到文件中
        String documentElementsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentElements);
        String documentElementsFilePath = saveJsonToFile(documentElementsJson, directoryPath, "documentElements.json");
        entity.setResultJson(documentElementsFilePath); // 只存储文件路径

        // 校验文档内容并生成validationResults
        List<ValidationResult> validationResults = new ArrayList<>();
        DocumentFormatChecker.checkDocumentContent(documentElements, validationResults);

        // 将validationResults转换为JSON并保存到文件中
        String validationResultsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(validationResults);
        String validationResultsFilePath = saveJsonToFile(validationResultsJson, directoryPath, "validationResults.json");
        entity.setValidationResultJson(validationResultsFilePath); // 只存储文件路径

        entity.setAddTime(new Date());

        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }

    private String saveJsonToFile(String jsonContent, String directoryPath, String fileName) {
        try {
            // 确保目录存在
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs(); // 创建目录
            }

            // 创建文件并写入JSON内容
            File file = new File(directory, fileName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(jsonContent); // 写入JSON内容
            }

            return file.getAbsolutePath(); // 返回文件路径
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 处理错误情况
        }
    }

    @Override
    public Result<FileDetailVo> update(FileSaveBo saveBo) {
        // 转换参数实体
        FileEntity entity = BeanUtil.copyProperties(saveBo, FileEntity.class);
        entity.setAddTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
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
    public Result<List<FileListVo>> selectIdsList(List<Long> ids) {
        List<FileEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<FileEntity>()
                .in(FileEntity::getId, ids));
        List<FileListVo> teacherFeedbackListVos = new ArrayList<>();
        for (FileEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, FileListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }

    @Override
    public Result<FileDetailVo> wordBatchCheck(FileSaveBo saveBo) throws Exception {
        // 转换参数实体
        FileEntity entity = BeanUtil.copyProperties(saveBo, FileEntity.class);
        String filePath = entity.getFilePath();

        // 将Word转换为HTML并保存HTML文件路径
        String htmlPath = WordToHtml.wordToHtml(filePath);
        HtmlParser.addIdx(htmlPath); // 添加索引文件
        HtmlParser.updateImgSrc(htmlPath, httpUploadDir + saveBo.getUuid() + File.separator);
        entity.setHtmlPath(htmlPath);


        // 获取文档元素列表
        List<DocumentElement> documentElements = WordRevision.getDocumentElements(filePath);
        ObjectMapper objectMapper = new ObjectMapper();

        // 获取文件目录路径
        String directoryPath = Paths.get(filePath).getParent().toString();


        String htmlContent = JsonToHtmlConverter.convertToHtml(documentElements);

        // 将 HTML 内容写入文件
        entity.setJsonHtmlPath(saveJsonToFile(htmlContent, directoryPath, "output.html"));
        JsonToHtmlConverter.mapHtmlToJson(htmlContent, documentElements);
        baseMapper.insert(entity);
        // 将documentElements转换为JSON并保存到文件中
        String documentElementsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentElements);
        String documentElementsFilePath = saveJsonToFile(documentElementsJson, directoryPath, "documentElements.json");
        entity.setResultJson(documentElementsFilePath); // 只存储文件路径
        List<CheckResponse.Result.Mistake> mistakes = wordBatchCheck(directoryPath + File.separator + "documentElements.json", entity.getId());
        Map<String, String> levelMap = iJianChaService.getLevelMap();
        Map<String, String> categoriesMap = iJianChaService.getCategoriesMap();
        if (mistakes != null) {
            for (CheckResponse.Result.Mistake mistake : mistakes) {
                for (CheckResponse.Result.Mistake.Info info : mistake.getInfos()) {
                    info.setTypeName(levelMap.get(String.valueOf((info.getType()))));
                    info.setCategoryName(categoriesMap.get((info.getCategory())));
                }
            }
        }
        String contentVerificationFilePath = saveJsonToFile(JSON.toJSONString(mistakes), directoryPath, "contentVerification.json");
        entity.setAddTime(new Date());
        entity.setContentVerificationJson(contentVerificationFilePath);
        if (htmlPath != null) {
            ContentVerificationToHtml.addIdx(htmlPath, contentVerificationFilePath, documentElements);
            entity.setContentVerificationHtml(htmlPath.replace(".html", "_文件校验后.html"));
        }
        baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }

    @Override
    public Result<String> htmlToDoc(long id, String htmlPath) {
        FileEntity fileEntity = baseMapper.selectById(id);
        if (fileEntity == null) {
            return Result.fail("文件不存在");
        }
        String documentElements = WordRevisionTest.getDocumentElements(fileEntity.getFilePath(), htmlPath);
        return Result.success(documentElements);
    }

    /**
     * 教研word批量校对示例
     */

    public List<CheckResponse.Result.Mistake> wordBatchCheck(String path, Long fileId) {
        try {
            // 获取 accessToken
            String accessToken = TokenUtil.getAccessToken();

            // 读取 JSON 文件并解析为 List<DocumentElement>
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(path);
            List<DocumentElement> documentElements = parseDocumentElements(objectMapper.readTree(file).toString());

            List<String> batches = new ArrayList<>();
            List<List<ParagraphElement>> batchParagraphs = new ArrayList<>(); // 记录每批次的段落
            List<CheckResponse.Result.Mistake> mistakes = new ArrayList<>();

            StringBuilder currentBatch = new StringBuilder();
            int currentBatchSize = 0;
            int batchSize = 1000;
            List<ParagraphElement> currentBatchParagraphs = new ArrayList<>();

            for (DocumentElement element : documentElements) {
                if (element instanceof ParagraphElement) {
                    ParagraphElement paragraph = (ParagraphElement) element;
                    String paragraphContent = paragraph.getContent();
                    int paragraphLength = paragraphContent.length();

                    // 如果当前批次内容接近上限，提交当前批次
                    if (currentBatchSize + paragraphLength > batchSize) {
                        batches.add(currentBatch.toString());
                        batchParagraphs.add(new ArrayList<>(currentBatchParagraphs));

                        // 重置当前批次
                        currentBatch = new StringBuilder(paragraphContent).append("\n"); // 注意：保留换行符
                        currentBatchSize = paragraphLength;
                        currentBatchParagraphs.clear();
                        currentBatchParagraphs.add(paragraph);
                    } else {
                        currentBatch.append(paragraphContent).append("\n"); // 每个段落末尾加换行符
                        currentBatchSize += paragraphLength;
                        currentBatchParagraphs.add(paragraph);
                    }
                }
            }

            // 添加最后的批次内容
            if (currentBatchSize > 0) {
                batches.add(currentBatch.toString());
                batchParagraphs.add(new ArrayList<>(currentBatchParagraphs));
            }

            // 调用检测接口并处理错误映射
            for (int i = 0; i < batches.size(); i++) {
                String batch = batches.get(i);
                List<ParagraphElement> paragraphs = batchParagraphs.get(i);
                CheckLogEntity entity = new CheckLogEntity();
                entity.setFileId(fileId);
                entity.setPosition(i + 1);
                entity.setTotal(batches.size());
                entity.setQuantity(batch.length());
                CheckRequest request = new CheckRequest(batch);
                CheckResponse response = ProofreadingUtil.checkText(accessToken, request);
                checkLogMapper.insert(entity);
                // 映射错误到对应段落
                if (response != null && response.getResult() != null) {
                    ProofreadingUtil.mapResponseToParagraphs(response, paragraphs, mistakes);
                }
            }

            return mistakes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
