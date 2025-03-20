package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.AiSessionEntity;
import com.doc.format.bo.AiSessionSaveBo;
import com.doc.format.bo.AiSessionQueryBo;
import com.doc.format.vo.AiSessionDetailVo;
import com.doc.format.vo.AiSessionListVo;

import java.util.List;

/**
 * AI对话会话Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
public interface IAiSessionService extends IService<AiSessionEntity> {

    /**
     * AI对话会话分页查询
     *
     * @param queryBo 查询实体
     * @return AI对话会话分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<Page<AiSessionListVo>> page(AiSessionQueryBo queryBo);

    /**
     * AI对话会话列表查询
     *
     * @param queryBo 查询实体
     * @return AI对话会话列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<List<AiSessionListVo>> list(AiSessionQueryBo queryBo);

    /**
     * AI对话会话查询
     *
     * @param id 主键ID
     * @return AI对话会话详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<AiSessionDetailVo> get(long id);

    /**
     * AI对话会话新增
     *
     * @param saveBo AI对话会话保存实体
     * @return Result<AiSessionDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<AiSessionDetailVo> insert(AiSessionSaveBo saveBo);

    /**
     * AI对话会话修改
     *
     * @param saveBo AI对话会话保存实体
     * @return Result<AiSessionDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<AiSessionDetailVo> update(AiSessionSaveBo saveBo);

    /**
     * AI对话会话批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(List<Long> ids);

    /**
     * AI对话会话删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(long id);

    /**
     * AI对话会话批量查询
     *
     * @param ids id集合
     * @return 2025-03-16 14:15:24详情实体
     */
    Result<List<AiSessionListVo>> selectIdsList(List<Long> ids);
}
