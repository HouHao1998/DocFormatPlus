package com.doc.format.parser;

import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ElementType;
import com.doc.format.util.entity.ImageElement;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.wml.Drawing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/23 15:32
 */
public class ImageParser implements DocumentElementParser {

    private static String saveImageToLocal(Drawing drawing, WordprocessingMLPackage wordMLPackage) throws Exception {
        Inline inline = null;

        // 检查 Drawing 是否有 AnchorOrInline
        if (drawing.getAnchorOrInline() != null && !drawing.getAnchorOrInline().isEmpty()) {
            for (Object obj : drawing.getAnchorOrInline()) {
                if (obj instanceof Inline) {
                    inline = (Inline) obj;
                    break; // 找到第一个 Inline，退出循环
                }
            }
        }

        // 如果 inline 为空，可能是 Anchor 类型，记录警告并返回
        if (inline == null) {
            System.out.println("没有检测到 Inline 图片");
            return null;
        }

        // 检查 Graphic 是否存在
        if (inline.getGraphic() == null || inline.getGraphic().getGraphicData() == null ||
                inline.getGraphic().getGraphicData().getPic() == null ||
                inline.getGraphic().getGraphicData().getPic().getBlipFill() == null) {
            System.out.println("图片的 Graphic 数据缺失");
            return null;
        }

        String blipId = inline.getGraphic().getGraphicData().getPic().getBlipFill().getBlip().getEmbed();

        // 根据关系 ID 获取图片对应的 Part
        Part imagePart = wordMLPackage.getMainDocumentPart().getRelationshipsPart().getPart(blipId);
        if (imagePart instanceof BinaryPartAbstractImage) {
            BinaryPartAbstractImage binaryImagePart = (BinaryPartAbstractImage) imagePart;
            byte[] imageBytes = binaryImagePart.getBytes();

            // 保存图片并返回路径
            return saveBytesAsImage(imageBytes, "images/");
        } else {
            System.out.println("无法处理的图片类型");
            return null;
        }
    }

    private static String saveBytesAsImage(byte[] imageBytes, String directoryPath) throws IOException {
        // 创建目录（如果不存在）
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一的文件名
        String fileName = "image_" + System.currentTimeMillis() + ".png";
        File imageFile = new File(directory, fileName);

        // 将字节数据写入文件
        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            fos.write(imageBytes);
        }

        // 返回文件的路径
        return imageFile.getAbsolutePath();
    }

    @Override
    public DocumentElement parse(Object element, WordprocessingMLPackage wordMLPackage) throws Exception {
        // 处理图片
        Drawing drawing = (Drawing) element;

        // 确保同一个 Drawing 只处理一次
        if (drawing == null) {
            System.out.println("Drawing 为 null，无法处理");
            return null;
        }

        ImageElement imageElement = new ImageElement();
        imageElement.setType(ElementType.IMAGE);

        // 尝试提取并保存图片到本地
        String imagePath = saveImageToLocal(drawing, wordMLPackage);
        if (imagePath != null) {
            imageElement.setLocalPath(imagePath);  // 将图片的本地路径保存到 JSON 中
        }
        // 提取宽度和高度
        extractImageDimensions(drawing, imageElement);

        return imageElement;
    }

    private void extractImageDimensions(Drawing drawing, ImageElement imageElement) {
        // 提取图像的宽度和高度
        // 需要根据实际的 Drawing 对象结构来提取
        // 这里是示例，实际可能需要根据你的具体实现进行调整
        if (drawing.getAnchorOrInline() != null && !drawing.getAnchorOrInline().isEmpty()) {
            Inline inline = (Inline) drawing.getAnchorOrInline().get(0);
            if (inline.getExtent() != null) {
                imageElement.setWidth(inline.getExtent().getCx());
                imageElement.setHeight(inline.getExtent().getCy());
            }
        }
    }

}

