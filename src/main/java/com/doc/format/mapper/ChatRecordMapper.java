package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.ChatRecordEntity;
import com.doc.format.bo.ChatRecordQueryBo;
import com.doc.format.vo.ChatRecordListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI对话记录Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Mapper
@Repository
public interface ChatRecordMapper extends BaseMapper<ChatRecordEntity> {
    /**
     * AI对话记录分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return AI对话记录分页信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<ChatRecordListVo> getList(Page<ChatRecordListVo> page, @Param("queryBo") ChatRecordQueryBo queryBo);

    /**
     * AI对话记录列表查询
     *
     * @param queryBo 查询实体
     * @return AI对话记录列表信息
     * @author HouHao
     * @date 2025-03-16 14:15:24
     */
    List<ChatRecordListVo> getList(@Param("queryBo") ChatRecordQueryBo queryBo);
}
