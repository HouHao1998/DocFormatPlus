package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.FormatTaskEntity;
import com.doc.format.bo.FormatTaskSaveBo;
import com.doc.format.bo.FormatTaskQueryBo;
import com.doc.format.vo.ContentCheckTaskDetailVo;
import com.doc.format.vo.FormatTaskDetailVo;
import com.doc.format.vo.FormatTaskListVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文档格式化任务Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
public interface IFormatTaskService extends IService<FormatTaskEntity> {

    /**
     * 文档格式化任务分页查询
     *
     * @param queryBo 查询实体
     * @return 文档格式化任务分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<Page<FormatTaskListVo>> page(FormatTaskQueryBo queryBo);

    /**
     * 文档格式化任务列表查询
     *
     * @param queryBo 查询实体
     * @return 文档格式化任务列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<List<FormatTaskListVo>> list(FormatTaskQueryBo queryBo);

    /**
     * 文档格式化任务查询
     *
     * @param id 主键ID
     * @return 文档格式化任务详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<FormatTaskDetailVo> get(long id);

    /**
     * 文档格式化任务新增
     *
     * @param saveBo 文档格式化任务保存实体
     * @return Result<FormatTaskDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<FormatTaskDetailVo> insert(FormatTaskSaveBo saveBo);

    /**
     * 文档格式化任务修改
     *
     * @param saveBo 文档格式化任务保存实体
     * @return Result<FormatTaskDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<FormatTaskDetailVo> update(FormatTaskSaveBo saveBo);

    /**
     * 文档格式化任务批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(List<Long> ids);

    /**
     * 文档格式化任务删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(long id);

    /**
     * 文档格式化任务批量查询
     *
     * @param ids id集合
     * @return 2025-03-16 14:15:24详情实体
     */
    Result<List<FormatTaskListVo>> selectIdsList(List<Long> ids);

    Result<FormatTaskDetailVo> formatWord(MultipartFile file);
}
