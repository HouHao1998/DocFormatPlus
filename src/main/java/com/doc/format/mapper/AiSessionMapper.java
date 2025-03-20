package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.AiSessionEntity;
import com.doc.format.bo.AiSessionQueryBo;
import com.doc.format.vo.AiSessionListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI对话会话Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Mapper
@Repository
public interface AiSessionMapper extends BaseMapper<AiSessionEntity> {
    /**
     * AI对话会话分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return AI对话会话分页信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<AiSessionListVo> getList(Page<AiSessionListVo> page, @Param("queryBo") AiSessionQueryBo queryBo);

    /**
     * AI对话会话列表查询
     *
     * @param queryBo 查询实体
     * @return AI对话会话列表信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<AiSessionListVo> getList(@Param("queryBo") AiSessionQueryBo queryBo);
}
