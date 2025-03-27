package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.bo.CheckRequestBo;
import com.doc.format.entity.Result;
import com.doc.format.service.IContentCheckTaskService;
import com.doc.format.bo.ContentCheckTaskQueryBo;
import com.doc.format.bo.ContentCheckTaskSaveBo;
import com.doc.format.util.iJianCha.CheckRequest;
import com.doc.format.util.iJianCha.CheckResponse;
import com.doc.format.vo.ContentCheckTaskDetailVo;
import com.doc.format.vo.ContentCheckTaskListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 内容校验任务Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Api(tags = "内容校验任务管理")
@RestController
@RequestMapping("/api/v1/contentCheckTask")
public class ContentCheckTaskController {

    @Resource
    private IContentCheckTaskService contentCheckTaskService;

    @ApiOperation(value = "内容校验任务分页查询", notes = "内容校验任务分页查询")
    @PostMapping("/page")
    public Result<Page<ContentCheckTaskListVo>> page(@RequestBody ContentCheckTaskQueryBo queryBo) throws Exception {
        return contentCheckTaskService.page(queryBo);
    }

    @ApiOperation(value = "内容校验任务列表查询", notes = "内容校验任务列表查询")
    @PostMapping("/list")
    public Result<List<ContentCheckTaskListVo>> list(@RequestBody ContentCheckTaskQueryBo queryBo) throws Exception {
        return contentCheckTaskService.list(queryBo);
    }

    @ApiOperation(value = "内容校验任务查询", notes = "内容校验任务查询")
    @GetMapping("/info/{id}")
    public Result<ContentCheckTaskDetailVo> get(@PathVariable("id") long id) throws Exception {
        return contentCheckTaskService.get(id);
    }

    @ApiOperation(value = "内容校验任务新增", notes = "内容校验任务新增")
    @PostMapping("/save")
    public Result<ContentCheckTaskDetailVo> insert(@RequestBody ContentCheckTaskSaveBo saveBo) throws Exception {
        return contentCheckTaskService.insert(saveBo);
    }

    @ApiOperation(value = "内容校验任务修改", notes = "内容校验任务修改")
    @PostMapping("/update")
    public Result<ContentCheckTaskDetailVo> update(@RequestBody ContentCheckTaskSaveBo saveBo) throws Exception {
        return contentCheckTaskService.update(saveBo);
    }

    @ApiOperation(value = "内容校验任务批量删除", notes = "内容校验任务批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return contentCheckTaskService.remove(ids);
    }

    @ApiOperation(value = "内容校验任务批量查询", notes = "内容校验任务批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<ContentCheckTaskListVo>> selectIdsList(@RequestBody ContentCheckTaskQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return contentCheckTaskService.selectIdsList(queryBo.getIds());
    }

    //上传word文档并解析出文字内容
    @ApiOperation(value = "上传word文档并解析出文字内容", notes = "上传word文档并解析出文字内容")
    @PostMapping("/parseWord")
    public Result<ContentCheckTaskDetailVo> parseWord(@RequestParam("file") MultipartFile file) throws Exception {
        return contentCheckTaskService.uploadAndParseFile(file);
    }

    @ApiOperation(value = "校验文字内容", notes = "校验文字内容")
    @PostMapping("/check")
    public Result<CheckResponse> check( // 使用Long类型更安全1
                                        @RequestBody CheckRequestBo request) throws Exception {
        return contentCheckTaskService.check(request.getId(), request.getText());
    }

    @ApiOperation(value = "下载word文档", notes = "下载word文档")
    @PostMapping("/download")
    public Result<ContentCheckTaskDetailVo> download(@RequestBody ContentCheckTaskSaveBo saveBo) throws Exception {
        return contentCheckTaskService.download(saveBo);
    }
}
