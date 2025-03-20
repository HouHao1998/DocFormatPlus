package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.UserEntity;
import com.doc.format.bo.UserQueryBo;
import com.doc.format.vo.UserListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<UserEntity> {
    /**
     * 用户分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return 用户分页信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<UserListVo> getList(Page<UserListVo> page, @Param("queryBo") UserQueryBo queryBo);

    /**
     * 用户列表查询
     *
     * @param queryBo 查询实体
     * @return 用户列表信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<UserListVo> getList(@Param("queryBo") UserQueryBo queryBo);
}
