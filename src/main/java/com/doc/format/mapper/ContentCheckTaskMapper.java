package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.ContentCheckTaskEntity;
import com.doc.format.bo.ContentCheckTaskQueryBo;
import com.doc.format.vo.ContentCheckTaskListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 内容校验任务Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Mapper
@Repository
public interface ContentCheckTaskMapper extends BaseMapper<ContentCheckTaskEntity> {
    /**
     * 内容校验任务分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return 内容校验任务分页信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<ContentCheckTaskListVo> getList(Page<ContentCheckTaskListVo> page, @Param("queryBo") ContentCheckTaskQueryBo queryBo);
    Integer getCount( @Param("queryBo") ContentCheckTaskQueryBo queryBo);

    /**
     * 内容校验任务列表查询
     *
     * @param queryBo 查询实体
     * @return 内容校验任务列表信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<ContentCheckTaskListVo> getList(@Param("queryBo") ContentCheckTaskQueryBo queryBo);
}
