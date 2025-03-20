package com.doc.format.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.Result;
import com.doc.format.service.IUserService;
import com.doc.format.bo.UserQueryBo;
import com.doc.format.bo.UserSaveBo;
import com.doc.format.vo.UserDetailVo;
import com.doc.format.vo.UserListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户Controller
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Resource
    private IUserService userService;

    @ApiOperation(value = "用户分页查询", notes = "用户分页查询")
    @PostMapping("/page")
    public Result<Page<UserListVo>> page(@RequestBody UserQueryBo queryBo) throws Exception {
        return userService.page(queryBo);
    }

    @ApiOperation(value = "用户列表查询", notes = "用户列表查询")
    @PostMapping("/list")
    public Result<List<UserListVo>> list(@RequestBody UserQueryBo queryBo) throws Exception {
        return userService.list(queryBo);
    }

    @ApiOperation(value = "用户查询", notes = "用户查询")
    @GetMapping("/info/{id}")
    public Result<UserDetailVo> get(@PathVariable("id") long id) throws Exception {
        return userService.get(id);
    }

    @ApiOperation(value = "用户新增", notes = "用户新增")
    @PostMapping("/save")
    public Result<UserDetailVo> insert(@RequestBody UserSaveBo saveBo) throws Exception {
        return userService.insert(saveBo);
    }

    @ApiOperation(value = "用户修改", notes = "用户修改")
    @PostMapping("/update")
    public Result<UserDetailVo> update(@RequestBody UserSaveBo saveBo) throws Exception {
        return userService.update(saveBo);
    }

    @ApiOperation(value = "用户批量删除", notes = "用户批量删除")
    @ApiImplicitParam(name = "ids", value = "主键ID集合，多个以英文逗号拼接", dataType = "String")
    @GetMapping("/delete")
    public Result<String> remove(@RequestParam List<Long> ids) throws Exception {
        return userService.remove(ids);
    }

    @ApiOperation(value = "用户批量查询", notes = "用户批量查询")
    @PostMapping("/selectIdsList")
    public Result<List<UserListVo>> selectIdsList(@RequestBody UserQueryBo queryBo) throws Exception {
        if (CollectionUtils.isEmpty(queryBo.getIds())) {
            return Result.fail("ids不能为空");
        }
        return userService.selectIdsList(queryBo.getIds());
    }
}
