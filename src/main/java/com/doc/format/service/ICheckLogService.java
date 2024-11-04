package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.CheckLogEntity;
import com.doc.format.bo.CheckLogSaveBo;
import com.doc.format.bo.CheckLogQueryBo;
import com.doc.format.vo.CheckLogDetailVo;
import com.doc.format.vo.CheckLogListVo;

import java.util.List;

/**
 * 爱校对校验日志Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-30 20:57:05
 */
public interface ICheckLogService extends IService<CheckLogEntity> {

    /**
     * 爱校对校验日志分页查询
     *
     * @param queryBo 查询实体
     * @return 爱校对校验日志分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<Page<CheckLogListVo>> page(CheckLogQueryBo queryBo);

    /**
     * 爱校对校验日志列表查询
     *
     * @param queryBo 查询实体
     * @return 爱校对校验日志列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<List<CheckLogListVo>> list(CheckLogQueryBo queryBo);

    /**
     * 爱校对校验日志查询
     *
     * @param id 主键ID
     * @return 爱校对校验日志详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<CheckLogDetailVo> get(long id);

    /**
     * 爱校对校验日志新增
     *
     * @param saveBo 爱校对校验日志保存实体
     * @return Result<CheckLogDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<CheckLogDetailVo> insert(CheckLogSaveBo saveBo);

    /**
     * 爱校对校验日志修改
     *
     * @param saveBo 爱校对校验日志保存实体
     * @return Result<CheckLogDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<CheckLogDetailVo> update(CheckLogSaveBo saveBo);

    /**
     * 爱校对校验日志批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<String> remove(List<Long> ids);

    /**
     * 爱校对校验日志删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-10-30 20:57:05
     */
    Result<String> remove(long id);

    /**
     * 爱校对校验日志批量查询
     *
     * @param ids id集合
     * @return 2024-10-30 20:57:05详情实体
     */
    Result<List<CheckLogListVo>> selectIdsList(List<Long> ids);
}
