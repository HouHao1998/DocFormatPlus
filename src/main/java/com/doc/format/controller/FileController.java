package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.Result;
import com.doc.format.service.IFileService;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.bo.FileSaveBo;
import com.doc.format.util.spire.JsonToWord;
import com.doc.format.vo.FileDetailVo;
import com.doc.format.vo.FileListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * 文件总览Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
@Api(tags = "文件总览管理")
@RestController
@RequestMapping("/api/v1/file")
public class FileController {

    @Resource
    private IFileService fileService;
    @Value("${file.upload.dir}")
    private String uploadDir;

    @ApiOperation(value = "文件总览分页查询", notes = "文件总览分页查询")
    @PostMapping("/page")
    public Result<Page<FileListVo>> page(@RequestBody FileQueryBo queryBo) throws Exception {
        return fileService.page(queryBo);
    }

    @ApiOperation(value = "文件总览列表查询", notes = "文件总览列表查询")
    @PostMapping("/list")
    public Result<List<FileListVo>> list(@RequestBody FileQueryBo queryBo) throws Exception {
        return fileService.list(queryBo);
    }

    @ApiOperation(value = "文件总览查询", notes = "文件总览查询")
    @GetMapping("/info/{id}")
    public Result<FileDetailVo> get(@PathVariable("id") long id) throws Exception {
        return fileService.get(id);
    }

    @ApiOperation(value = "文件总览新增", notes = "文件总览新增")
    @PostMapping("/save")
    public Result<FileDetailVo> insert(@RequestBody FileSaveBo saveBo) throws Exception {
        return fileService.insert(saveBo);
    }

    @ApiOperation(value = "文件总览修改", notes = "文件总览修改")
    @PostMapping("/update")
    public Result<FileDetailVo> update(@RequestBody FileSaveBo saveBo) throws Exception {
        return fileService.update(saveBo);
    }

    @ApiOperation(value = "文件总览批量删除", notes = "文件总览批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return fileService.remove(ids);
    }

    @ApiOperation(value = "文件总览批量查询", notes = "文件总览批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<FileListVo>> selectIdsList(@RequestBody FileQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return fileService.selectIdsList(queryBo.getIds());
    }

    @ApiOperation(value = "上传doc文件", notes = "上传并保存到指定文件夹")
    @PostMapping("/uploadDoc")
    public Result<FileDetailVo> uploadDocFile(@RequestParam("file") MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.fail("上传的文件不能为空");
        }
        UUID uuid = UUID.randomUUID();
        // 获取文件的原始文件名
        String originalFilename = file.getOriginalFilename();
        // 提取文件后缀
        String fileType = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 确保上传的是 .doc 或 .docx 文件
        if (originalFilename != null && !(originalFilename.endsWith(".doc") || originalFilename.endsWith(".docx"))) {
            return Result.fail("只允许上传 .doc 或 .docx 文件");
        }

        // 保存文件到指定路径
        try {
            Path directory = Paths.get(uploadDir + File.separator + uuid);
            Files.createDirectories(directory);
            Path filePath = directory.resolve(originalFilename);
            Files.copy(file.getInputStream(), filePath);
            FileSaveBo saveBo = new FileSaveBo();
            saveBo.setFilePath(filePath.toString());
            saveBo.setFileName(originalFilename);
            saveBo.setFileType(fileType);
            saveBo.setUuid(uuid.toString());

            return fileService.insert(saveBo);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败");
        }
    }

    @ApiOperation(value = "word批量检查", notes = "word批量检查")
    @PostMapping("/wordBatchCheck")
    public Result<FileDetailVo> wordBatchCheck(@RequestParam("file") MultipartFile file) throws Exception {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.fail("上传的文件不能为空");
        }
        UUID uuid = UUID.randomUUID();
        // 获取文件的原始文件名
        String originalFilename = file.getOriginalFilename();
        // 提取文件后缀
        String fileType = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        // 确保上传的是 .doc 或 .docx 文件
        if (originalFilename != null && !(originalFilename.endsWith(".doc") || originalFilename.endsWith(".docx"))) {
            return Result.fail("只允许上传 .doc 或 .docx 文件");
        }

        // 保存文件到指定路径
        try {
            Path directory = Paths.get(uploadDir + File.separator + uuid);
            Files.createDirectories(directory);
            Path filePath = directory.resolve(originalFilename);
            Files.copy(file.getInputStream(), filePath);
            FileSaveBo saveBo = new FileSaveBo();
            saveBo.setFilePath(filePath.toString());
            saveBo.setFileName(originalFilename);
            saveBo.setFileType(fileType);
            saveBo.setUuid(uuid.toString());

            return fileService.wordBatchCheck(saveBo);

        } catch (IOException e) {
            e.printStackTrace();
            return Result.fail("文件上传失败");
        }
    }

    @ApiOperation(value = "一键格式化", notes = "一键格式化")
    @PostMapping("/formatDoc/{id}")
    public Result<FileDetailVo> formatDoc(@PathVariable("id") long id) throws IOException {
        // 获取文件信息
        Result<FileDetailVo> fileDetailVoResult = fileService.get(id);
        if (fileDetailVoResult == null || fileDetailVoResult.getData() == null) {
            return Result.fail("未找到指定的文件");
        }

        // 获取文件路径和 JSON 数据
        String filePath = fileDetailVoResult.getData().getFilePath();
        String json = fileDetailVoResult.getData().getValidationResultJson();

        // 调用 JsonToWord 处理文档
        String newFilePath = JsonToWord.jsonToWord(filePath, json, true);

        // 更新文件保存信息
        FileSaveBo saveBo = new FileSaveBo();
        saveBo.setId(id);
        saveBo.setResultDocPath(newFilePath);

        // 返回更新结果
        return fileService.update(saveBo);
    }

    @ApiOperation(value = "json 转 doc 格式化", notes = "上传并处理 JSON")
    @PostMapping("/jsonToDoc/{id}")
    public Result<FileDetailVo> jsonToDoc(@PathVariable("id") long id, @RequestParam("file") MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Result.fail("上传的文件不能为空");
        }

        // 获取上传文件的原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".json")) {
            return Result.fail("只允许上传 .json 文件");
        }

        // 创建上传文件的路径
        UUID uuid = UUID.randomUUID();
        Path directory = Paths.get(uploadDir + File.separator + uuid.toString());
        Files.createDirectories(directory);  // 确保目录存在
        Path jsonFilePath = directory.resolve(originalFilename);
        Files.copy(file.getInputStream(), jsonFilePath);

        // 获取源文件路径
        Result<FileDetailVo> fileDetailVoResult = fileService.get(id);
        if (fileDetailVoResult == null || fileDetailVoResult.getData() == null) {
            return Result.fail("未找到指定的源文件");
        }

        // 调用 JsonToWord 处理文档
        String filePath = fileDetailVoResult.getData().getFilePath();
        String newFilePath = JsonToWord.jsonToWord(filePath, jsonFilePath.toString(), true);

        // 更新文件保存信息
        FileSaveBo saveBo = new FileSaveBo();
        saveBo.setId(id);
        saveBo.setResultDocPath(newFilePath);

        // 返回更新结果
        return fileService.update(saveBo);
    }
}
