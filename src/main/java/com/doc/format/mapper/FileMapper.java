package com.doc.format.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doc.format.entity.FileEntity;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.vo.FileListVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文件总览Mapper
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
@Mapper
@Repository
public interface FileMapper extends BaseMapper<FileEntity> {
    /**
     * 文件总览分页查询
     *
     * @param page    分页信息
     * @param queryBo 查询实体
     * @return 文件总览分页信息
     * @author HouHao
     * @date 2024-08-28 11:03:27
     */
    List<FileListVo> getList(Page<FileListVo> page, @Param("queryBo") FileQueryBo queryBo);

    /**
     * 文件总览列表查询
     *
     * @param queryBo 查询实体
     * @return 文件总览列表信息
     * @author HouHao
     * @date 2024-08-28 11:03:27
     */
    List<FileListVo> getList(@Param("queryBo") FileQueryBo queryBo);
}
