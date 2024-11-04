package com.doc.format.service;

import com.doc.format.entity.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.entity.FileEntity;
import com.doc.format.bo.FileSaveBo;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.vo.FileDetailVo;
import com.doc.format.vo.FileListVo;

import java.io.IOException;
import java.util.List;

/**
 * 文件总览Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
public interface IFileService extends IService<FileEntity> {

    /**
     * 文件总览分页查询
     *
     * @param queryBo 查询实体
     * @return 文件总览分页信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<Page<FileListVo>> page(FileQueryBo queryBo);

    /**
     * 文件总览列表查询
     *
     * @param queryBo 查询实体
     * @return 文件总览列表信息
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<List<FileListVo>> list(FileQueryBo queryBo);

    /**
     * 文件总览查询
     *
     * @param id 主键ID
     * @return 文件总览详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<FileDetailVo> get(long id);

    /**
     * 文件总览新增
     *
     * @param saveBo 文件总览保存实体
     * @return Result<FileDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<FileDetailVo> insert(FileSaveBo saveBo) throws IOException;

    /**
     * 文件总览修改
     *
     * @param saveBo 文件总览保存实体
     * @return Result<FileDetailVo> 详情实体
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<FileDetailVo> update(FileSaveBo saveBo);

    /**
     * 文件总览批量删除
     *
     * @param ids 主键ID集合
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<String> remove(List<Long> ids);

    /**
     * 文件总览删除
     *
     * @param id 主键ID
     * @return Result<String> 是否成功
     * @author: HouHao
     * @version: 1.0
     * @date: 2024-08-28 11:03:27
     */
    Result<String> remove(long id);

    /**
     * 文件总览批量查询
     *
     * @param ids id集合
     * @return 2024-08-28 11:03:27详情实体
     */
    Result<List<FileListVo>> selectIdsList(List<Long> ids);

    Result<FileDetailVo> wordBatchCheck(FileSaveBo saveBo) throws Exception;

    Result<String> htmlToDoc(long id, String file);
}
