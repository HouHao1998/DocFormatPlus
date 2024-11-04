package com.doc.format.service.impl;


import com.doc.format.service.IIJianChaService;
import com.doc.format.util.JedisUtil;
import com.doc.format.util.iJianCha.ProofreadingUtil;
import com.doc.format.util.iJianCha.RecommendationCategory;
import com.doc.format.util.iJianCha.RecommendationLevel;
import com.doc.format.util.iJianCha.TokenUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件总览Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
@Service
public class IJianChaServiceImpl implements IIJianChaService {
    private static final String I_JIAN_CHA_RECOMMENDATION_CATEGORIES = "IJianCha_Recommendation_Categories";
    private static final String I_JIAN_CHA_RECOMMENDATION_LEVEL = "IJianCha_Recommendation_Level";
    @Resource
    private JedisUtil jedisUtil;

    @Override
    public Map<String, String> getCategoriesMap() throws Exception {
        Map<String, String> map = jedisUtil.getMap(I_JIAN_CHA_RECOMMENDATION_CATEGORIES);
        if (map.isEmpty()) {
            return setRecommendationCategoriesInfo();

        }
        return map;
    }

    @Override
    public String getRecommendationCategoriesByCode(String code) throws Exception {
        Map<String, String> map = jedisUtil.getMap(I_JIAN_CHA_RECOMMENDATION_CATEGORIES);
        if (map.isEmpty()) {
            map = setRecommendationCategoriesInfo();

        }
        return map.get(code);
    }

    @Override
    public Map<String, String> setRecommendationCategoriesInfo() throws Exception {
        String accessToken = TokenUtil.getAccessToken();
        List<RecommendationCategory> recommendationCategoriesInfo = ProofreadingUtil.getRecommendationCategoriesInfo(accessToken);
        Map<String, String> map = new HashMap<>();

        // 将 List 转换为 Map
        for (RecommendationCategory category : recommendationCategoriesInfo) {
            map.put(category.getCode(), category.getDescription());
        }

        // 将 Map 存储到 Redis
        jedisUtil.setMap(I_JIAN_CHA_RECOMMENDATION_CATEGORIES, map);
        return map;
    }

    @Override
    public String getRecommendationLevelByType(String type) throws Exception {
        Map<String, String> map = jedisUtil.getMap(I_JIAN_CHA_RECOMMENDATION_LEVEL);
        if (map.isEmpty()) {
            map = setRecommendationLevelInfo();

        }
        return map.get(type);
    }

    @Override
    public Map<String, String> getLevelMap() throws Exception {
        Map<String, String> map = jedisUtil.getMap(I_JIAN_CHA_RECOMMENDATION_LEVEL);
        if (map.isEmpty()) {
            return setRecommendationLevelInfo();

        }
        return map;
    }

    @Override
    public Map<String, String> setRecommendationLevelInfo() throws Exception {
        String accessToken = TokenUtil.getAccessToken();
        List<RecommendationLevel> recommendationLevelInfo = ProofreadingUtil.getRecommendationLevelInfo(accessToken);
        Map<String, String> map = new HashMap<>();

        // 将 List 转换为 Map
        for (RecommendationLevel category : recommendationLevelInfo) {
            map.put(String.valueOf(category.getType()), category.getDescription());
        }
        // 将 Map 存储到 Redis
        jedisUtil.setMap(I_JIAN_CHA_RECOMMENDATION_LEVEL, map);
        return map;
    }


}
