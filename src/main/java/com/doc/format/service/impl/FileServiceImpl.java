package com.doc.format.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.CheckLogEntity;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.FileEntity;
import com.doc.format.mapper.CheckLogMapper;
import com.doc.format.mapper.FileMapper;
import com.doc.format.service.IFileService;
import com.doc.format.bo.FileQueryBo;
import com.doc.format.bo.FileSaveBo;
import com.doc.format.service.IIJianChaService;
import com.doc.format.util.docx4j.JsonToHtmlConverter;
import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ParagraphElement;
import com.doc.format.util.entity.SimplePara;
import com.doc.format.util.entity.ValidationResult;
import com.doc.format.util.iJianCha.CheckRequest;
import com.doc.format.util.iJianCha.CheckResponse;
import com.doc.format.util.iJianCha.ProofreadingUtil;
import com.doc.format.util.iJianCha.TokenUtil;
import com.doc.format.util.spire.*;
import com.doc.format.vo.FileDetailVo;
import com.doc.format.vo.FileListVo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.doc.format.util.iJianCha.ProofreadingUtil.parseDocumentElements;

/**
 * 文件总览Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-08-28 11:03:27
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements IFileService {
    @Value("${http.upload.dir}")
    private String httpUploadDir;
    @Value("${file.upload.dir}")
    private String fileUploadDir;
    @Resource
    private IIJianChaService iJianChaService;
    @Resource
    private CheckLogMapper checkLogMapper;

    @Override
    public Result<Page<FileListVo>> page(FileQueryBo queryBo) {
        // 转换参数实体
        Page<FileListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<FileListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<FileListVo>> list(FileQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<FileDetailVo> get(long id) {
        // 调用查询方法
        FileEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }

    @Override
    public Result<FileDetailVo> insert(FileSaveBo saveBo) throws IOException {
        // 转换参数实体
        FileEntity entity = BeanUtil.copyProperties(saveBo, FileEntity.class);
        String filePath = entity.getFilePath();

        // 将Word转换为HTML并保存HTML文件路径
        String htmlPath = WordToHtml.wordToHtml(filePath);
        HtmlParser.addIdx(htmlPath); // 添加索引文件
        HtmlParser.updateImgSrc(htmlPath, httpUploadDir + saveBo.getUuid() + File.separator);
        entity.setHtmlPath(htmlPath);


        // 获取文档元素列表
        List<DocumentElement> documentElements = WordRevision.getDocumentElements(filePath);
        ObjectMapper objectMapper = new ObjectMapper();

        // 获取文件目录路径
        String directoryPath = Paths.get(filePath).getParent().toString();


        String htmlContent = JsonToHtmlConverter.convertToHtml(documentElements);

        // 将 HTML 内容写入文件1
        entity.setJsonHtmlPath(saveJsonToFile(htmlContent, directoryPath, "原始WORD解析HTML.html"));
        JsonToHtmlConverter.mapHtmlToJson(htmlContent, documentElements);
        // 将documentElements转换为JSON并保存到文件中1
        String documentElementsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentElements);
        String documentElementsFilePath = saveJsonToFile(documentElementsJson, directoryPath, "WORD解析全面的JSON结构.json");
        entity.setResultJson(documentElementsFilePath); // 只存储文件路径1

        List<SimplePara> simpleParaList = SimplePara.getSimpleParaList(documentElements);
        String simpleParaListJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(simpleParaList);
        saveJsonToFile(simpleParaListJson, directoryPath, "WORD简易的JSON结构.json");
        List<SimplePara> pageList = SimplePara.getPageList(documentElements);
        String pageListJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageList);
        saveJsonToFile(pageListJson, directoryPath, "WORD只有段落信息.json");

        // 校验文档内容并生成validationResults
        List<ValidationResult> validationResults = new ArrayList<>();
        DocumentFormatChecker.checkDocumentContent(documentElements, validationResults);

        // 将validationResults转换为JSON并保存到文件中
        String validationResultsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(validationResults);
        String validationResultsFilePath = saveJsonToFile(validationResultsJson, directoryPath, "按照格式分组的JSON.json");
        entity.setValidationResultJson(validationResultsFilePath); // 只存储文件路径

        entity.setAddTime(new Date());

        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }

    private String saveJsonToFile(String jsonContent, String directoryPath, String fileName) {
        try {
            // 确保目录存在
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs(); // 创建目录
            }

            // 创建文件并写入JSON内容
            File file = new File(directory, fileName);
            try (FileWriter fileWriter = new FileWriter(file)) {
                fileWriter.write(jsonContent); // 写入JSON内容
            }

            return file.getAbsolutePath(); // 返回文件路径
        } catch (IOException e) {
            e.printStackTrace();
            return null; // 处理错误情况
        }
    }

    @Override
    public Result<FileDetailVo> update(FileSaveBo saveBo) {
        // 转换参数实体
        FileEntity entity = BeanUtil.copyProperties(saveBo, FileEntity.class);
        entity.setAddTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
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
    public Result<List<FileListVo>> selectIdsList(List<Long> ids) {
        List<FileEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<FileEntity>()
                .in(FileEntity::getId, ids));
        List<FileListVo> teacherFeedbackListVos = new ArrayList<>();
        for (FileEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, FileListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }

    @Override
    public Result<FileDetailVo> wordBatchCheck(FileSaveBo saveBo) throws Exception {
        // 转换参数实体
        FileEntity entity = BeanUtil.copyProperties(saveBo, FileEntity.class);
        String filePath = entity.getFilePath();

        // 将Word转换为HTML并保存HTML文件路径
        String htmlPath = WordToHtml.wordToHtml(filePath);
        HtmlParser.addIdx(htmlPath); // 添加索引文件
        HtmlParser.updateImgSrc(htmlPath, httpUploadDir + saveBo.getUuid() + File.separator);
        entity.setHtmlPath(htmlPath);


        // 获取文档元素列表
        List<DocumentElement> documentElements = WordRevision.getDocumentElements(filePath);
        ObjectMapper objectMapper = new ObjectMapper();

        // 获取文件目录路径
        String directoryPath = Paths.get(filePath).getParent().toString();

        String htmlContent = JsonToHtmlConverter.convertToHtml(documentElements);

        // 将 HTML 内容写入文件
        entity.setJsonHtmlPath(saveJsonToFile(htmlContent, directoryPath, "原始WORD解析HTML.html"));
        JsonToHtmlConverter.mapHtmlToJson(htmlContent, documentElements);
        baseMapper.insert(entity);
        // 将documentElements转换为JSON并保存到文件中
        String documentElementsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(documentElements);
        String documentElementsFilePath = saveJsonToFile(documentElementsJson, directoryPath, "WORD解析全面的JSON结构.json");
        entity.setResultJson(documentElementsFilePath); // 只存储文件路径
        //通关爱检查把内容映射到html
        List<CheckResponse.Result.Mistake> mistakes = wordBatchCheck(directoryPath + File.separator + "WORD解析全面的JSON结构.json", entity.getId());
        Map<String, String> levelMap = iJianChaService.getLevelMap();
        Map<String, String> categoriesMap = iJianChaService.getCategoriesMap();
        if (mistakes != null) {
            for (CheckResponse.Result.Mistake mistake : mistakes) {
                for (CheckResponse.Result.Mistake.Info info : mistake.getInfos()) {
                    info.setTypeName(levelMap.get(String.valueOf((info.getType()))));
                    info.setCategoryName(categoriesMap.get((info.getCategory())));
                }
            }
        }
        String contentVerificationFilePath = saveJsonToFile(JSON.toJSONString(mistakes), directoryPath, "爱检查校验的.json");
        entity.setAddTime(new Date());
        entity.setContentVerificationJson(contentVerificationFilePath);
        if (htmlPath != null) {
            //把校验后的内容添加到html文件中
            ContentVerificationToHtml.addIdx(htmlPath, contentVerificationFilePath, documentElements);
            entity.setContentVerificationHtml(htmlPath.replace(".html", "_文件校验后.html"));
        }
        baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, FileDetailVo.class));
    }

    @Override
    public Result<String> htmlToDoc(long id, String htmlPath) {
        FileEntity fileEntity = baseMapper.selectById(id);
        if (fileEntity == null) {
            return Result.fail("文件不存在");
        }
        String documentElements = WordRevisionTest.getDocumentElements(fileEntity.getFilePath(), htmlPath);
        return Result.success(documentElements);
    }

    @Override
    public Result<String> onlineCheck(String s, UUID uuid) {

        return Result.success(JSON.toJSONString(wordBatchCheck(s, uuid.toString())));
    }

    /**
     * 教研word批量校对示例
     */

    public List<CheckResponse.Result.Mistake> wordBatchCheck(String path, Long fileId) {
        try {
            // 获取 accessToken
            String accessToken = TokenUtil.getAccessToken();

            // 读取 JSON 文件并解析为 List<DocumentElement>
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(path);
            List<DocumentElement> documentElements = parseDocumentElements(objectMapper.readTree(file).toString());

            List<String> batches = new ArrayList<>();
            List<List<ParagraphElement>> batchParagraphs = new ArrayList<>(); // 记录每批次的段落
            List<CheckResponse.Result.Mistake> mistakes = new ArrayList<>();

            StringBuilder currentBatch = new StringBuilder();
            int currentBatchSize = 0;
            int batchSize = 1000;
            List<ParagraphElement> currentBatchParagraphs = new ArrayList<>();

            for (DocumentElement element : documentElements) {
                if (element instanceof ParagraphElement) {
                    ParagraphElement paragraph = (ParagraphElement) element;
                    String paragraphContent = paragraph.getContent();
                    int paragraphLength = paragraphContent.length();

                    // 如果当前批次内容接近上限，提交当前批次
                    if (currentBatchSize + paragraphLength > batchSize) {
                        batches.add(currentBatch.toString());
                        batchParagraphs.add(new ArrayList<>(currentBatchParagraphs));

                        // 重置当前批次
                        currentBatch = new StringBuilder(paragraphContent).append("\n"); // 注意：保留换行符
                        currentBatchSize = paragraphLength;
                        currentBatchParagraphs.clear();
                        currentBatchParagraphs.add(paragraph);
                    } else {
                        currentBatch.append(paragraphContent).append("\n"); // 每个段落末尾加换行符
                        currentBatchSize += paragraphLength;
                        currentBatchParagraphs.add(paragraph);
                    }
                }
            }

            // 添加最后的批次内容
            if (currentBatchSize > 0) {
                batches.add(currentBatch.toString());
                batchParagraphs.add(new ArrayList<>(currentBatchParagraphs));
            }

            // 调用检测接口并处理错误映射
            for (int i = 0; i < batches.size(); i++) {
                String batch = batches.get(i);
                List<ParagraphElement> paragraphs = batchParagraphs.get(i);
                CheckLogEntity entity = new CheckLogEntity();
                entity.setFileId(fileId);
                entity.setPosition(i + 1);
                entity.setTotal(batches.size());
                entity.setQuantity(batch.length());
                CheckRequest request = new CheckRequest(batch);
                CheckResponse response = ProofreadingUtil.checkText(accessToken, request);
                checkLogMapper.insert(entity);
                // 映射错误到对应段落
                if (response != null && response.getResult() != null) {
                    ProofreadingUtil.mapResponseToParagraphs(response, paragraphs, mistakes);
                }
            }

            return mistakes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Map<String, String> wordBatchCheck(String path, String uuid) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            // 获取 accessToken1
            String accessToken = TokenUtil.getAccessToken();

            // 读取 HTML 文件内容
            String htmlContent = Files.readString(Paths.get(path));

            // 解析 HTML 文件内容
            Document doc = Jsoup.parse(htmlContent);

            // 获取所有 <p> 标签
            Elements pElements = doc.select("p");

            // 记录每个段落的长度和累积偏移量
            List<Integer> paragraphLengths = new ArrayList<>();
            int cumulativeOffset = 0;

            for (Element pElement : pElements) {
                int length = pElement.text().length();
                cumulativeOffset += length + 1; // +1 表示段落之间的换行符
                paragraphLengths.add(cumulativeOffset);
            }

            // 当前批次处理
            StringBuilder currentBatch = new StringBuilder();
            int currentBatchSize = 0;
            int batchSize = 1000;

            // 存储批次
            List<BatchItem> batchItems = new ArrayList<>();
            int currentOffset = 0;

            for (Element pElement : pElements) {
                String paragraphContent = pElement.text();
                int paragraphLength = paragraphContent.length();

                if (currentBatchSize + paragraphLength > batchSize) {
                    batchItems.add(new BatchItem(currentBatch.toString(), currentOffset, new ArrayList<>(pElements)));
                    currentBatch = new StringBuilder(paragraphContent).append("\n");
                    currentBatchSize = paragraphLength;
                    currentOffset += currentBatchSize;
                } else {
                    currentBatch.append(paragraphContent).append("\n");
                    currentBatchSize += paragraphLength;
                }
            }

            if (currentBatchSize > 0) {
                batchItems.add(new BatchItem(currentBatch.toString(), currentOffset, new ArrayList<>(pElements)));
            }

            // 存储检测结果
            List<CheckResponse.Result.Mistake> allMistakes = new ArrayList<>();
            int mistakeIndex = 0;

            for (BatchItem batch : batchItems) {
                CheckRequest request = new CheckRequest(batch.getText());
                CheckResponse response = ProofreadingUtil.checkText(accessToken, request);

                if (response != null && response.getResult() != null && response.getResult().getMistakes() != null) {
                    for (CheckResponse.Result.Mistake mistake : response.getResult().getMistakes()) {
                        mistake.setIdx(mistakeIndex++); // 设置索引
                        allMistakes.add(mistake);

                        // 找到对应段落
                        int adjustedOffset = mistake.getL();
                        for (int i = 0; i < paragraphLengths.size(); i++) {
                            if (adjustedOffset < paragraphLengths.get(i)) {
                                Element targetParagraph = pElements.get(i);
                                int paragraphStartOffset = (i == 0) ? 0 : paragraphLengths.get(i - 1);
                                mapMistakeToHtml(targetParagraph, mistake, paragraphStartOffset);
                                break;
                            } else {
                                adjustedOffset -= paragraphLengths.get(i);
                            }
                        }
                    }
                }
            }

            // 保存更新的 HTML 文件
            String updatedHtmlPath = fileUploadDir + File.separator + uuid + File.separator + "在线文本爱检查检测之后的HTML.html";
            Files.write(Paths.get(updatedHtmlPath), doc.html().getBytes(StandardCharsets.UTF_8));
            resultMap.put("htmlFile", updatedHtmlPath);

            // 保存检测错误的 JSON 文件
            String mistakesJsonPath = fileUploadDir + File.separator + uuid + File.separator + "在线文本爱检查检测结果.json";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(Paths.get(mistakesJsonPath).toFile(), allMistakes);
            resultMap.put("jsonFile", mistakesJsonPath);

            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    private void mapMistakeToHtml(Element element, CheckResponse.Result.Mistake mistake, int offset) {
        if (element == null) return;

        // 获取段落的子节点
        List<TextNode> textNodes = getTextNodes(element);

        // 计算错误在段落内的起始和结束位置
        int start = mistake.getL() - offset;
        int end = mistake.getR() - offset;

        // 检查边界
        if (start < 0 || end <= start) {
            System.out.println("错误范围不在段落内: start=" + start + ", end=" + end);
            return;
        }

        int currentOffset = 0;
        for (TextNode textNode : textNodes) {
            String text = textNode.text();
            int textLength = text.length();

            if (currentOffset + textLength > start) {
                // 错误开始在当前节点中
                int relativeStart = Math.max(start - currentOffset, 0);
                int relativeEnd = Math.min(end - currentOffset, textLength);

                // 标记错误文本
                String errorText = text.substring(relativeStart, relativeEnd);
                String markedText = "<span class=\"custom-underline-red idx" + mistake.getIdx() + " \" data-proof-id=\"" + mistake.getIdx() + "\">" + errorText + "</span>";

                // 更新当前节点
                String updatedText = text.substring(0, relativeStart) + markedText + text.substring(relativeEnd);
                textNode.text(""); // 清空当前节点
                textNode.before(updatedText); // 插入更新内容

                // 检查错误是否完全处理
                end -= relativeEnd - relativeStart;
                if (end <= currentOffset + relativeEnd) {
                    break;
                }
            }

            currentOffset += textLength;
        }
    }

    private List<TextNode> getTextNodes(Element element) {
        List<TextNode> textNodes = new ArrayList<>();
        for (Node node : element.childNodes()) {
            if (node instanceof TextNode) {
                textNodes.add((TextNode) node);
            } else if (node instanceof Element) {
                textNodes.addAll(getTextNodes((Element) node));
            }
        }
        return textNodes;
    }


    private static class BatchItem {
        private final String text; // 当前批次的文本内容
        private final List<Element> elements; // 当前批次的段落集合
        private int offset;        // 当前批次的起始偏移量

        public BatchItem(String text, int offset, List<Element> elements) {
            this.text = text;
            this.offset = offset;
            this.elements = elements;
        }

        public String getText() {
            return text;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public List<Element> getElements() {
            return elements;
        }
    }


}
