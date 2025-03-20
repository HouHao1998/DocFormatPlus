package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.ChatRecordEntity;
import com.doc.format.mapper.ChatRecordMapper;
import com.doc.format.service.IChatRecordService;
import com.doc.format.bo.ChatRecordQueryBo;
import com.doc.format.bo.ChatRecordSaveBo;
import com.doc.format.vo.ChatRecordDetailVo;
import com.doc.format.vo.ChatRecordListVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * AI对话记录Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecordEntity> implements IChatRecordService {

    @Override
    public Result<Page<ChatRecordListVo>> page(ChatRecordQueryBo queryBo) {
        // 转换参数实体
        Page<ChatRecordListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<ChatRecordListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<ChatRecordListVo>> list(ChatRecordQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<ChatRecordDetailVo> get(long id) {
        // 调用查询方法
        ChatRecordEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, ChatRecordDetailVo.class));
    }

    @Override
    public Result<ChatRecordDetailVo> insert(ChatRecordSaveBo saveBo) {
        // 转换参数实体
        ChatRecordEntity entity = BeanUtil.copyProperties(saveBo, ChatRecordEntity.class);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, ChatRecordDetailVo.class));
    }

    @Override
    public Result<ChatRecordDetailVo> update(ChatRecordSaveBo saveBo) {
        // 转换参数实体
        ChatRecordEntity entity = BeanUtil.copyProperties(saveBo, ChatRecordEntity.class);
        entity.setUpdateTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, ChatRecordDetailVo.class));
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
    public Result<List<ChatRecordListVo>> selectIdsList(List<Long> ids) {
        List<ChatRecordEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<ChatRecordEntity>()
                .in(ChatRecordEntity::getId, ids));
        List<ChatRecordListVo> teacherFeedbackListVos = new ArrayList<>();
        for (ChatRecordEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, ChatRecordListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }
}
