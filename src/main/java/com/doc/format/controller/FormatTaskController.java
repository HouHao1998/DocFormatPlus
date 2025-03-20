package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.Result;
import com.doc.format.service.IFormatTaskService;
import com.doc.format.bo.FormatTaskQueryBo;
import com.doc.format.bo.FormatTaskSaveBo;
import com.doc.format.vo.ContentCheckTaskDetailVo;
import com.doc.format.vo.FormatTaskDetailVo;
import com.doc.format.vo.FormatTaskListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * 文档格式化任务Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Api(tags = "文档格式化任务管理")
@RestController
@RequestMapping("/api/v1/formatTask")
public class FormatTaskController {

    @Resource
    private IFormatTaskService formatTaskService;

    @ApiOperation(value = "文档格式化任务分页查询", notes = "文档格式化任务分页查询")
    @PostMapping("/page")
    public Result<Page<FormatTaskListVo>> page(@RequestBody FormatTaskQueryBo queryBo) throws Exception {
        return formatTaskService.page(queryBo);
    }

    @ApiOperation(value = "文档格式化任务列表查询", notes = "文档格式化任务列表查询")
    @PostMapping("/list")
    public Result<List<FormatTaskListVo>> list(@RequestBody FormatTaskQueryBo queryBo) throws Exception {
        return formatTaskService.list(queryBo);
    }

    @ApiOperation(value = "文档格式化任务查询", notes = "文档格式化任务查询")
    @GetMapping("/info/{id}")
    public Result<FormatTaskDetailVo> get(@PathVariable("id") long id) throws Exception {
        return formatTaskService.get(id);
    }

    @ApiOperation(value = "文档格式化任务新增", notes = "文档格式化任务新增")
    @PostMapping("/save")
    public Result<FormatTaskDetailVo> insert(@RequestBody FormatTaskSaveBo saveBo) throws Exception {
        return formatTaskService.insert(saveBo);
    }

    @ApiOperation(value = "文档格式化任务修改", notes = "文档格式化任务修改")
    @PostMapping("/update")
    public Result<FormatTaskDetailVo> update(@RequestBody FormatTaskSaveBo saveBo) throws Exception {
        return formatTaskService.update(saveBo);
    }

    @ApiOperation(value = "文档格式化任务批量删除", notes = "文档格式化任务批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return formatTaskService.remove(ids);
    }

    @ApiOperation(value = "文档格式化任务批量查询", notes = "文档格式化任务批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<FormatTaskListVo>> selectIdsList(@RequestBody FormatTaskQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return formatTaskService.selectIdsList(queryBo.getIds());
    }
    @ApiOperation(value = "论文格式化", notes = "论文格式化")
    @PostMapping("/formatWord")
    public Result<FormatTaskDetailVo> formatWord(@RequestParam("file") MultipartFile file) throws Exception {
        return formatTaskService.formatWord(file);
    }

}
