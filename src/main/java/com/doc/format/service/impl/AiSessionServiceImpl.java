package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.AiSessionEntity;
import com.doc.format.mapper.AiSessionMapper;
import com.doc.format.service.IAiSessionService;
import com.doc.format.bo.AiSessionQueryBo;
import com.doc.format.bo.AiSessionSaveBo;
import com.doc.format.vo.AiSessionDetailVo;
import com.doc.format.vo.AiSessionListVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * AI对话会话Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Service
public class AiSessionServiceImpl extends ServiceImpl<AiSessionMapper, AiSessionEntity> implements IAiSessionService {

    @Override
    public Result<Page<AiSessionListVo>> page(AiSessionQueryBo queryBo) {
        // 转换参数实体
        Page<AiSessionListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<AiSessionListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<AiSessionListVo>> list(AiSessionQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<AiSessionDetailVo> get(long id) {
        // 调用查询方法
        AiSessionEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, AiSessionDetailVo.class));
    }

    @Override
    public Result<AiSessionDetailVo> insert(AiSessionSaveBo saveBo) {
        // 转换参数实体
        AiSessionEntity entity = BeanUtil.copyProperties(saveBo, AiSessionEntity.class);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, AiSessionDetailVo.class));
    }

    @Override
    public Result<AiSessionDetailVo> update(AiSessionSaveBo saveBo) {
        // 转换参数实体
        AiSessionEntity entity = BeanUtil.copyProperties(saveBo, AiSessionEntity.class);
        entity.setUpdateTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, AiSessionDetailVo.class));
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
    public Result<List<AiSessionListVo>> selectIdsList(List<Long> ids) {
        List<AiSessionEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<AiSessionEntity>()
                .in(AiSessionEntity::getId, ids));
        List<AiSessionListVo> teacherFeedbackListVos = new ArrayList<>();
        for (AiSessionEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, AiSessionListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }
}
