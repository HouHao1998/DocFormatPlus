package com.doc.format.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.bo.FileSaveBo;
import com.doc.format.entity.FileEntity;
import com.doc.format.entity.Result;
import com.doc.format.vo.FileDetailVo;
import com.doc.format.vo.FileListVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 文件总览Service接口类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
public interface IIJianChaService {

    Map<String, String> getCategoriesMap() throws Exception;

    String getRecommendationCategoriesByCode(String code) throws Exception;

    Map<String, String> setRecommendationCategoriesInfo() throws Exception;

    String getRecommendationLevelByType(String type) throws Exception;

    Map<String, String> getLevelMap() throws Exception;

    Map<String, String> setRecommendationLevelInfo() throws Exception;
}
