package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.CheckLogEntity;
import com.doc.format.mapper.CheckLogMapper;
import com.doc.format.service.ICheckLogService;
import com.doc.format.bo.CheckLogQueryBo;
import com.doc.format.bo.CheckLogSaveBo;
import com.doc.format.vo.CheckLogDetailVo;
import com.doc.format.vo.CheckLogListVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

/**
 * 爱校对校验日志Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-30 20:57:05
 */
@Service
public class CheckLogServiceImpl extends ServiceImpl<CheckLogMapper, CheckLogEntity> implements ICheckLogService {

    @Override
    public Result<Page<CheckLogListVo>> page(CheckLogQueryBo queryBo) {
        // 转换参数实体
        Page<CheckLogListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<CheckLogListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<CheckLogListVo>> list(CheckLogQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<CheckLogDetailVo> get(long id) {
        // 调用查询方法
        CheckLogEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, CheckLogDetailVo.class));
    }

    @Override
    public Result<CheckLogDetailVo> insert(CheckLogSaveBo saveBo) {
        // 转换参数实体
        CheckLogEntity entity = BeanUtil.copyProperties(saveBo, CheckLogEntity.class);
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, CheckLogDetailVo.class));
    }

    @Override
    public Result<CheckLogDetailVo> update(CheckLogSaveBo saveBo) {
        // 转换参数实体
        CheckLogEntity entity = BeanUtil.copyProperties(saveBo, CheckLogEntity.class);
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, CheckLogDetailVo.class));
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
    public Result<List<CheckLogListVo>> selectIdsList(List<Long> ids) {
        List<CheckLogEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<CheckLogEntity>()
                .in(CheckLogEntity::getId, ids));
        List<CheckLogListVo> teacherFeedbackListVos = new ArrayList<>();
        for (CheckLogEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, CheckLogListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }
}
