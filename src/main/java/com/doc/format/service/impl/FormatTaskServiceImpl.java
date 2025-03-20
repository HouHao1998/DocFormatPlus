package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.ContentCheckTaskEntity;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.FormatTaskEntity;
import com.doc.format.enums.CheckStatusEnum;
import com.doc.format.enums.FormatStatusEnum;
import com.doc.format.mapper.FormatTaskMapper;
import com.doc.format.service.IFormatTaskService;
import com.doc.format.bo.FormatTaskQueryBo;
import com.doc.format.bo.FormatTaskSaveBo;
import com.doc.format.util.deep.ClassificationResult;
import com.doc.format.util.deep.DocumentClassifierUtil;
import com.doc.format.util.deep.DocumentParagraph;
import com.doc.format.vo.ContentCheckTaskDetailVo;
import com.doc.format.vo.FormatTaskDetailVo;
import com.doc.format.vo.FormatTaskListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * 文档格式化任务Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Service
public class FormatTaskServiceImpl extends ServiceImpl<FormatTaskMapper, FormatTaskEntity> implements IFormatTaskService {
    @Value("${file.upload.dir}")
    private String fileUploadDir;
    @Resource
    private DocumentClassifierUtil documentationUtil;
    @Resource
    private ExecutorService executorService;

    @Override
    public Result<Page<FormatTaskListVo>> page(FormatTaskQueryBo queryBo) {
        // 转换参数实体
        Page<FormatTaskListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<FormatTaskListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<FormatTaskListVo>> list(FormatTaskQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<FormatTaskDetailVo> get(long id) {
        // 调用查询方法
        FormatTaskEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, FormatTaskDetailVo.class));
    }

    @Override
    public Result<FormatTaskDetailVo> insert(FormatTaskSaveBo saveBo) {
        // 转换参数实体
        FormatTaskEntity entity = BeanUtil.copyProperties(saveBo, FormatTaskEntity.class);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, FormatTaskDetailVo.class));
    }

    @Override
    public Result<FormatTaskDetailVo> update(FormatTaskSaveBo saveBo) {
        // 转换参数实体
        FormatTaskEntity entity = BeanUtil.copyProperties(saveBo, FormatTaskEntity.class);
        entity.setUpdateTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, FormatTaskDetailVo.class));
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
    public Result<List<FormatTaskListVo>> selectIdsList(List<Long> ids) {
        List<FormatTaskEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<FormatTaskEntity>()
                .in(FormatTaskEntity::getId, ids));
        List<FormatTaskListVo> teacherFeedbackListVos = new ArrayList<>();
        for (FormatTaskEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, FormatTaskListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }

    @Override
    public Result<FormatTaskDetailVo> formatWord(MultipartFile file) {
        FormatTaskEntity entity = new FormatTaskEntity();
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());

        String uuid = UUID.randomUUID().toString();
        String originalFilename = file.getOriginalFilename();

        // 增加临时文件路径记录
        String sourceFilePath = null;
        String paragraphsPath = null;
        String formatedFilePath = null; // 新增校验文件路径跟踪

        try {
            // 基础路径（示例：uploads/2023/08/）
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
            String baseDir = fileUploadDir + File.separator + datePath;

            // 构建存储目录结构
            String taskDir = baseDir + File.separator + uuid;
            String sourceDir = taskDir + File.separator + "source";
            String paragraphsDir = taskDir + File.separator + "paragraphs";
            String formatedDir = taskDir + File.separator + "formated"; // 新增校验文件目录

            // 创建目录
            createDirectory(sourceDir);
            createDirectory(paragraphsDir);
            createDirectory(formatedDir);
            // 创建校验文件目录
            sourceFilePath = sourceDir + File.separator + originalFilename;
            paragraphsPath = paragraphsDir + File.separator + "paragraphs.json";
            String formatedFilename = generateFormatedFileName(originalFilename);
            formatedFilePath = formatedDir + File.separator + formatedFilename;
            // 保存入数据库
            entity.setOriginalFile(sourceFilePath);
            entity.setParsedJson(paragraphsPath);
            entity.setFormattedFile(formatedFilePath);
            entity.setTaskStatus(FormatStatusEnum.UPLOADED.getCode());
            baseMapper.insert(entity);
            deepSeekFormatWord(sourceFilePath, paragraphsPath, entity, formatedFilePath);
            // 提交异步任务
            // 在调用 executorService.submit 之前，声明 final 变量
            final String finalSourcePath = sourceFilePath;
            final String finalParagraphsPath = paragraphsPath;
            final String finalFormatedPath = formatedFilePath;
            final FormatTaskEntity finalEntity = entity;
            executorService.submit(() -> deepSeekFormatWord(
                    finalSourcePath, finalParagraphsPath, finalEntity, finalFormatedPath
            ));
            return Result.success(BeanUtil.copyProperties(entity, FormatTaskDetailVo.class));
        } catch (Exception e) {
            log.error("文件处理失败 | UUID:" + uuid + " | 文件名:" + originalFilename + "{}", e);
            // 清理已创建的文件（新增校验文件清理）
            deleteFileIfExists(sourceFilePath);
            deleteFileIfExists(paragraphsPath);
            deleteFileIfExists(formatedFilePath);
            return Result.fail("文件处理失败: " + e.getMessage());
        }
    }

    private void deepSeekFormatWord(String sourceFilePath, String paragraphsPath, FormatTaskEntity entity, String formatedFilePath) {
        //1. 解析源文件docx的段落信息并保存到文件
        List<DocumentParagraph> paragraphs = documentationUtil.getDocumentElements(sourceFilePath);

        //保存段落信息到文件
        documentationUtil.saveDocumentParagraphs(paragraphs, paragraphsPath);
        entity.setTaskStatus(FormatStatusEnum.VERIFIED.getCode());
        baseMapper.updateById(entity);
        List<ClassificationResult> results = new ArrayList<>();
        try {
            // 2. 构建结构化请求
            String requestBody = documentationUtil.buildClassificationRequest(paragraphs);
            entity.setTaskStatus(FormatStatusEnum.DEEPSEEK_CALLED.getCode());
            baseMapper.updateById(entity);

            // 3. 调用API获取分类结果
            String apiResponse = documentationUtil.callClassificationApi(requestBody, entity.getId());

            // 4. 处理并输出结果
            results = documentationUtil.parseApiResponse(apiResponse);
            entity.setTaskStatus(FormatStatusEnum.EXPORTED.getCode());
            baseMapper.updateById(entity);
        } catch (Exception e) {
            log.error("文件格式化失败 | UUID:" + entity.getId() + " | 文件名:" + entity.getOriginalFile() + "{}", e);
            entity.setTaskStatus(FormatStatusEnum.API_CALL_FAILED.getCode());
            baseMapper.updateById(entity);
        }
        // documentationUtil.generateOutputReport(results);

        //5. 保存格式化后的文档
        documentationUtil.getDocumentElements(sourceFilePath, results, formatedFilePath);
        entity.setTaskStatus(FormatStatusEnum.FORMATTED.getCode());
        baseMapper.updateById(entity);
    }


    private void createDirectory(String path) throws IOException {
        File dir = new File(path);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("目录创建失败: " + path);
        }
    }

    private String generateFormatedFileName(String originalFilename) {
        if (originalFilename == null) return "格式化之后的文档";

        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            return originalFilename.substring(0, dotIndex) + "_格式化之后的结果" + originalFilename.substring(dotIndex);
        }
        return originalFilename + "_格式化之后的结果";
    }

    private void deleteFileIfExists(String filePath) {
        if (filePath != null) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
            } catch (IOException ex) {
                log.error("文件清理失败: {}" + filePath);
            }
        }
    }
}
