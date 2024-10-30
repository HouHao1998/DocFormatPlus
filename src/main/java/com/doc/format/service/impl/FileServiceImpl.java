package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.FileEntity;
import com.doc.format.mapper.FileMapper;
import com.doc.format.service.IFileService;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.bo.FileSaveBo;
import com.doc.format.util.docx4j.JsonToHtmlConverter;
import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ValidationResult;
import com.doc.format.util.iJianCha.ProofreadingUtil;
import com.doc.format.util.spire.*;
import com.doc.format.vo.FileDetailVo;
import com.doc.format.vo.FileListVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

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
        // 将documentElements转换为JSON并保存到文件中
        String documentElementsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentElements);
        String documentElementsFilePath = saveJsonToFile(documentElementsJson, directoryPath, "documentElements.json");
        entity.setResultJson(documentElementsFilePath); // 只存储文件路径
        String wordBatchCheckResult = ProofreadingUtil.wordBatchCheck(directoryPath + File.separator + "documentElements.json");

        String contentVerificationFilePath = saveJsonToFile(wordBatchCheckResult, directoryPath, "contentVerification.json");
        entity.setAddTime(new Date());
        entity.setContentVerificationJson(contentVerificationFilePath);
        if (htmlPath != null) {
            ContentVerificationToHtml.addIdx(htmlPath,contentVerificationFilePath);
            entity.setContentVerificationHtml(htmlPath.replace(".html", "_文件校验后.html"));
        }
        baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }
}
