package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.Result;
import com.doc.format.service.IAiSessionService;
import com.doc.format.bo.AiSessionQueryBo;
import com.doc.format.bo.AiSessionSaveBo;
import com.doc.format.vo.AiSessionDetailVo;
import com.doc.format.vo.AiSessionListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI对话会话Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Api(tags = "AI对话会话管理")
@RestController
@RequestMapping("/api/v1/aiSession")
public class AiSessionController {

    @Resource
    private IAiSessionService aiSessionService;

    @ApiOperation(value = "AI对话会话分页查询", notes = "AI对话会话分页查询")
    @PostMapping("/page")
    public Result<Page<AiSessionListVo>> page(@RequestBody AiSessionQueryBo queryBo) throws Exception {
        return aiSessionService.page(queryBo);
    }

    @ApiOperation(value = "AI对话会话列表查询", notes = "AI对话会话列表查询")
    @PostMapping("/list")
    public Result<List<AiSessionListVo>> list(@RequestBody AiSessionQueryBo queryBo) throws Exception {
        return aiSessionService.list(queryBo);
    }

    @ApiOperation(value = "AI对话会话查询", notes = "AI对话会话查询")
    @GetMapping("/info/{id}")
    public Result<AiSessionDetailVo> get(@PathVariable("id") long id) throws Exception {
        return aiSessionService.get(id);
    }

    @ApiOperation(value = "AI对话会话新增", notes = "AI对话会话新增")
    @PostMapping("/save")
    public Result<AiSessionDetailVo> insert(@RequestBody AiSessionSaveBo saveBo) throws Exception {
        return aiSessionService.insert(saveBo);
    }

    @ApiOperation(value = "AI对话会话修改", notes = "AI对话会话修改")
    @PostMapping("/update")
    public Result<AiSessionDetailVo> update(@RequestBody AiSessionSaveBo saveBo) throws Exception {
        return aiSessionService.update(saveBo);
    }

    @ApiOperation(value = "AI对话会话批量删除", notes = "AI对话会话批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return aiSessionService.remove(ids);
    }

    @ApiOperation(value = "AI对话会话批量查询", notes = "AI对话会话批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<AiSessionListVo>> selectIdsList(@RequestBody AiSessionQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return aiSessionService.selectIdsList(queryBo.getIds());
    }
}
