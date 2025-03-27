package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.UserEntity;
import com.doc.format.bo.UserSaveBo;
import com.doc.format.bo.UserQueryBo;
import com.doc.format.util.user.User;
import com.doc.format.vo.UserDetailVo;
import com.doc.format.vo.UserListVo;

import java.util.List;

/**
 * 用户Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
public interface IUserService extends IService<UserEntity> {

    /**
     * 用户分页查询
     *
     * @param queryBo 查询实体
     * @return 用户分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<Page<UserListVo>> page(UserQueryBo queryBo);

    /**
     * 用户列表查询
     *
     * @param queryBo 查询实体
     * @return 用户列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<List<UserListVo>> list(UserQueryBo queryBo);

    /**
     * 用户查询
     *
     * @param id 主键ID
     * @return 用户详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<UserDetailVo> get(long id);

    /**
     * 用户新增
     *
     * @param saveBo 用户保存实体
     * @return Result<UserDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<UserDetailVo> insert(UserSaveBo saveBo);

    /**
     * 用户修改
     *
     * @param saveBo 用户保存实体
     * @return Result<UserDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<UserDetailVo> update(UserSaveBo saveBo);

    /**
     * 用户批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(List<Long> ids);

    /**
     * 用户删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(long id);

    /**
     * 用户批量查询
     *
     * @param ids id集合
     * @return 2025-03-16 14:15:24详情实体
     */
    Result<List<UserListVo>> selectIdsList(List<Long> ids);

    UserDetailVo getUser();

    UserDetailVo login(String username, String password);
}
