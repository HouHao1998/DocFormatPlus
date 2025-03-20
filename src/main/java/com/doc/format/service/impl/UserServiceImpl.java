package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.UserEntity;
import com.doc.format.mapper.UserMapper;
import com.doc.format.service.IUserService;
import com.doc.format.bo.UserQueryBo;
import com.doc.format.bo.UserSaveBo;
import com.doc.format.vo.UserDetailVo;
import com.doc.format.vo.UserListVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 用户Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements IUserService {

    @Override
    public Result<Page<UserListVo>> page(UserQueryBo queryBo) {
        // 转换参数实体
        Page<UserListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<UserListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<UserListVo>> list(UserQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<UserDetailVo> get(long id) {
        // 调用查询方法
        UserEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, UserDetailVo.class));
    }

    @Override
    public Result<UserDetailVo> insert(UserSaveBo saveBo) {
        // 转换参数实体
        UserEntity entity = BeanUtil.copyProperties(saveBo, UserEntity.class);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, UserDetailVo.class));
    }

    @Override
    public Result<UserDetailVo> update(UserSaveBo saveBo) {
        // 转换参数实体
        UserEntity entity = BeanUtil.copyProperties(saveBo, UserEntity.class);
        entity.setUpdateTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, UserDetailVo.class));
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
    public Result<List<UserListVo>> selectIdsList(List<Long> ids) {
        List<UserEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<UserEntity>()
                .in(UserEntity::getId, ids));
        List<UserListVo> teacherFeedbackListVos = new ArrayList<>();
        for (UserEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, UserListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }
}
