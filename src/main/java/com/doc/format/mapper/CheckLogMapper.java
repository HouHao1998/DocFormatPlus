package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.CheckLogEntity;
import com.doc.format.bo.CheckLogQueryBo;
import com.doc.format.vo.CheckLogListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 爱校对校验日志Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-30 20:57:05
 */
@Mapper
@Repository
public interface CheckLogMapper extends BaseMapper<CheckLogEntity> {
    /**
     * 爱校对校验日志分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return 爱校对校验日志分页信息
     * @author HouHao
     * @date 2024-10-30 20:57:05
     */
    List<CheckLogListVo> getList(Page<CheckLogListVo> page, @Param("queryBo") CheckLogQueryBo queryBo);

    /**
     * 爱校对校验日志列表查询
     *
     * @param queryBo 查询实体
     * @return 爱校对校验日志列表信息
     * @author HouHao
     * @date 2024-10-30 20:57:05
     */
    List<CheckLogListVo> getList(@Param("queryBo") CheckLogQueryBo queryBo);
}
