package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.FormatTaskEntity;
import com.doc.format.bo.FormatTaskQueryBo;
import com.doc.format.vo.FormatTaskListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文档格式化任务Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Mapper
@Repository
public interface FormatTaskMapper extends BaseMapper<FormatTaskEntity> {
    /**
     * 文档格式化任务分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return 文档格式化任务分页信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<FormatTaskListVo> getList(Page<FormatTaskListVo> page, @Param("queryBo") FormatTaskQueryBo queryBo);
    Integer getCount( @Param("queryBo") FormatTaskQueryBo queryBo);

    /**
     * 文档格式化任务列表查询
     *
     * @param queryBo 查询实体
     * @return 文档格式化任务列表信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<FormatTaskListVo> getList(@Param("queryBo") FormatTaskQueryBo queryBo);
}
