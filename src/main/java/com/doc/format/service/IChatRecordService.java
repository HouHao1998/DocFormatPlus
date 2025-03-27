package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.ChatRecordEntity;
import com.doc.format.bo.ChatRecordSaveBo;
import com.doc.format.bo.ChatRecordQueryBo;
import com.doc.format.vo.ChatRecordDetailVo;
import com.doc.format.vo.ChatRecordListVo;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * AI对话记录Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
public interface IChatRecordService extends IService<ChatRecordEntity> {

    /**
     * AI对话记录分页查询
     *
     * @param queryBo 查询实体
     * @return AI对话记录分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<Page<ChatRecordListVo>> page(ChatRecordQueryBo queryBo);

    /**
     * AI对话记录列表查询
     *
     * @param queryBo 查询实体
     * @return AI对话记录列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<List<ChatRecordListVo>> list(ChatRecordQueryBo queryBo);

    /**
     * AI对话记录查询
     *
     * @param id 主键ID
     * @return AI对话记录详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<ChatRecordDetailVo> get(long id);

    /**
     * AI对话记录新增
     *
     * @param saveBo AI对话记录保存实体
     * @return Result<ChatRecordDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<ChatRecordDetailVo> insert(ChatRecordSaveBo saveBo);

    /**
     * AI对话记录修改
     *
     * @param saveBo AI对话记录保存实体
     * @return Result<ChatRecordDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<ChatRecordDetailVo> update(ChatRecordSaveBo saveBo);

    /**
     * AI对话记录批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(List<Long> ids);

    /**
     * AI对话记录删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(long id);

    /**
     * AI对话记录批量查询
     *
     * @param ids id集合
     * @return 2025-03-16 14:15:24详情实体
     */
    Result<List<ChatRecordListVo>> selectIdsList(List<Long> ids);

    SseEmitter handleStreamChat(ChatRecordQueryBo request);
}
