package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.ContentCheckTaskEntity;
import com.doc.format.bo.ContentCheckTaskSaveBo;
import com.doc.format.bo.ContentCheckTaskQueryBo;
import com.doc.format.util.iJianCha.CheckResponse;
import com.doc.format.vo.ContentCheckTaskDetailVo;
import com.doc.format.vo.ContentCheckTaskListVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 内容校验任务Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
public interface IContentCheckTaskService extends IService<ContentCheckTaskEntity> {

    /**
     * 内容校验任务分页查询
     *
     * @param queryBo 查询实体
     * @return 内容校验任务分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<Page<ContentCheckTaskListVo>> page(ContentCheckTaskQueryBo queryBo);

    /**
     * 内容校验任务列表查询
     *
     * @param queryBo 查询实体
     * @return 内容校验任务列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<List<ContentCheckTaskListVo>> list(ContentCheckTaskQueryBo queryBo);

    /**
     * 内容校验任务查询
     *
     * @param id 主键ID
     * @return 内容校验任务详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<ContentCheckTaskDetailVo> get(long id);

    /**
     * 内容校验任务新增
     *
     * @param saveBo 内容校验任务保存实体
     * @return Result<ContentCheckTaskDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<ContentCheckTaskDetailVo> insert(ContentCheckTaskSaveBo saveBo);

    /**
     * 内容校验任务修改
     *
     * @param saveBo 内容校验任务保存实体
     * @return Result<ContentCheckTaskDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<ContentCheckTaskDetailVo> update(ContentCheckTaskSaveBo saveBo);

    /**
     * 内容校验任务批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(List<Long> ids);

    /**
     * 内容校验任务删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2025-03-16 14:15:24
     */
    Result<String> remove(long id);

    /**
     * 内容校验任务批量查询
     *
     * @param ids id集合
     * @return 2025-03-16 14:15:24详情实体
     */
    Result<List<ContentCheckTaskListVo>> selectIdsList(List<Long> ids);

    Result<ContentCheckTaskDetailVo> uploadAndParseFile(MultipartFile file);

    Result<CheckResponse> check(Integer id, String text) throws Exception;

    Result<ContentCheckTaskDetailVo> download(ContentCheckTaskSaveBo saveBo) throws IOException;
}
