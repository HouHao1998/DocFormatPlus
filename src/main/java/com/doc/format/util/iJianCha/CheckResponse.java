package com.doc.format.util.iJianCha;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

/**
 * <b>CheckResponse类用于接收校对接口返回的响应数据。</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/8/28 15:28
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckResponse {
    /**
     * 返回码，表示请求的状态，仅在有错误时返回
     */
    private int code;
    /**
     * 响应信息，通常是错误信息
     */
    private String msg;
    /**
     * 错误数量
     */
    private int sum;
    /**
     * 校对结果的具体内容
     */
    private Result result;
    /**
     * 服务器返回的message字段
     */
    private String message;

    /**
     * Result类表示校对结果中的具体内容。
     */
    @Data
    public static class Result {
        /**
         * 校对后的文本，应该与请求的text相同
         */
        private String sentence;
        /**
         * 检测到的错误数量
         */
        private int mistake_num;
        /**
         * 错误集合，包含具体的错误信息
         */
        private List<Mistake> mistakes;

        /**
         * Mistake类表示每个错误的具体信息。
         */
        @Data
        public static class Mistake {
            /**
             * 错误位置的左索引（左闭）
             */
            private int l;
            /**
             * 错误位置的右索引（右开）
             */
            private int r;
            /**
             * 段落中错误位置的左索引（左闭）
             */
            private int pl;
            /**
             * 段落中错误位置的右索引（右开）
             */
            private int pr;
            /**
             * 段落索引
             */
            private int pIndex;
            /**
             * 错误的详细描述信息
             */
            private List<Info> infos;

            /**
             * Info类表示每个错误的详细描述信息。
             */
            @Data
            public static class Info {
                /**
                 * 推荐的修改建议，可能为空
                 */
                private String recommend;
                /**
                 * 推荐程度
                 */
                private int type;
                /**
                 * 错误类别
                 */
                private String category;
                /**
                 * 错误的描述信息1，可能为空
                 */
                private String desc1;
                /**
                 * 错误的进一步描述信息2，可能为空
                 */
                private String desc2;

            }
        }
    }
}
