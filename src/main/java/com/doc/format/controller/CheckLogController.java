package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.Result;
import com.doc.format.service.ICheckLogService;
import com.doc.format.bo.CheckLogQueryBo;
import com.doc.format.bo.CheckLogSaveBo;
import com.doc.format.vo.CheckLogDetailVo;
import com.doc.format.vo.CheckLogListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 爱校对校验日志Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-30 20:57:05
 */
@Api(tags = "爱校对校验日志管理")
@RestController
@RequestMapping("/api/v1/checkLog")
public class CheckLogController {

    @Resource
    private ICheckLogService checkLogService;

    @ApiOperation(value = "爱校对校验日志分页查询", notes = "爱校对校验日志分页查询")
    @PostMapping("/page")
    public Result<Page<CheckLogListVo>> page(@RequestBody CheckLogQueryBo queryBo) throws Exception {
        return checkLogService.page(queryBo);
    }

    @ApiOperation(value = "爱校对校验日志列表查询", notes = "爱校对校验日志列表查询")
    @PostMapping("/list")
    public Result<List<CheckLogListVo>> list(@RequestBody CheckLogQueryBo queryBo) throws Exception {
        return checkLogService.list(queryBo);
    }

    @ApiOperation(value = "爱校对校验日志查询", notes = "爱校对校验日志查询")
    @GetMapping("/info/{id}")
    public Result<CheckLogDetailVo> get(@PathVariable("id") long id) throws Exception {
        return checkLogService.get(id);
    }

    @ApiOperation(value = "爱校对校验日志新增", notes = "爱校对校验日志新增")
    @PostMapping("/save")
    public Result<CheckLogDetailVo> insert(@RequestBody CheckLogSaveBo saveBo) throws Exception {
        return checkLogService.insert(saveBo);
    }

    @ApiOperation(value = "爱校对校验日志修改", notes = "爱校对校验日志修改")
    @PostMapping("/update")
    public Result<CheckLogDetailVo> update(@RequestBody CheckLogSaveBo saveBo) throws Exception {
        return checkLogService.update(saveBo);
    }

    @ApiOperation(value = "爱校对校验日志批量删除", notes = "爱校对校验日志批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return checkLogService.remove(ids);
    }

    @ApiOperation(value = "爱校对校验日志批量查询", notes = "爱校对校验日志批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<CheckLogListVo>> selectIdsList(@RequestBody CheckLogQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return checkLogService.selectIdsList(queryBo.getIds());
    }
}
