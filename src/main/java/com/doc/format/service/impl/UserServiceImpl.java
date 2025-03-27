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
import com.doc.format.util.JwtUtil;
import com.doc.format.util.user.User;
import com.doc.format.util.user.YourUserDetails;
import com.doc.format.vo.UserDetailVo;
import com.doc.format.vo.UserListVo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Resource
    private PasswordEncoder passwordEncoder; // Spring Security 提供的接口
    @Resource
    private JwtUtil jwtUtil;

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

    @Override
    public UserDetailVo getUser() {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetailVo) {
                UserDetailVo userDetails = (UserDetailVo) principal;
                return userDetails;
            }
        }
        return null;
    }

    @Override
    public UserDetailVo login(String username, String password) {
        // 1. 根据用户名查询用户
        UserEntity userEntity = baseMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, username));
        if (userEntity == null) return null;
        // 2. 校验密码（需对接加密逻辑，如 BCrypt）
        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            return null;
        }

        // 3. 返回用户信息（脱敏处理）
        UserDetailVo userDetailVo = BeanUtil.copyProperties(userEntity, UserDetailVo.class);
        // 可选：生成 JWT Token 返回
        String token = jwtUtil.generateToken(userDetailVo.getId(), userDetailVo.getUsername());
        userDetailVo.setPassword(null);
        userDetailVo.setToken(token); // 或直接返回 Token
        return userDetailVo;
    }
}
