package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.Result;
import com.doc.format.service.IChatRecordService;
import com.doc.format.bo.ChatRecordQueryBo;
import com.doc.format.bo.ChatRecordSaveBo;
import com.doc.format.vo.ChatRecordDetailVo;
import com.doc.format.vo.ChatRecordListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI对话记录Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Api(tags = "AI对话记录管理")
@RestController
@RequestMapping("/api/v1/chatRecord")
public class ChatRecordController {

    @Resource
    private IChatRecordService chatRecordService;

    @ApiOperation(value = "AI对话记录分页查询", notes = "AI对话记录分页查询")
    @PostMapping("/page")
    public Result<Page<ChatRecordListVo>> page(@RequestBody ChatRecordQueryBo queryBo) throws Exception {
        return chatRecordService.page(queryBo);
    }

    @ApiOperation(value = "AI对话记录列表查询", notes = "AI对话记录列表查询")
    @PostMapping("/list")
    public Result<List<ChatRecordListVo>> list(@RequestBody ChatRecordQueryBo queryBo) throws Exception {
        return chatRecordService.list(queryBo);
    }

    @ApiOperation(value = "AI对话记录查询", notes = "AI对话记录查询")
    @GetMapping("/info/{id}")
    public Result<ChatRecordDetailVo> get(@PathVariable("id") long id) throws Exception {
        return chatRecordService.get(id);
    }

    @ApiOperation(value = "AI对话记录新增", notes = "AI对话记录新增")
    @PostMapping("/save")
    public Result<ChatRecordDetailVo> insert(@RequestBody ChatRecordSaveBo saveBo) throws Exception {
        return chatRecordService.insert(saveBo);
    }

    @ApiOperation(value = "AI对话记录修改", notes = "AI对话记录修改")
    @PostMapping("/update")
    public Result<ChatRecordDetailVo> update(@RequestBody ChatRecordSaveBo saveBo) throws Exception {
        return chatRecordService.update(saveBo);
    }

    @ApiOperation(value = "AI对话记录批量删除", notes = "AI对话记录批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return chatRecordService.remove(ids);
    }

    @ApiOperation(value = "AI对话记录批量查询", notes = "AI对话记录批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<ChatRecordListVo>> selectIdsList(@RequestBody ChatRecordQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return chatRecordService.selectIdsList(queryBo.getIds());
    }
}
