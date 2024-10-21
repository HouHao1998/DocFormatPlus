package com.doc.format.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口返回实体类
 *
 * @author cw
 */
@Data
public class Result<T> implements Serializable {


    private Integer code = 200;

    /**
     * 消息
     */
    @ApiModelProperty(value = "主键")
    private String msg;

    /**
     * 如果接口正常，则data存返回的数据，如果接口错误，则data为空
     */
    private T data;

    /**
     * true：成功 代表本次请求是否成功
     */
    private Boolean success;

    /**
     * 追踪id
     */
    private String tranceId;

    /**
     * 接口耗时
     */
    private Long cost;

    public Result() {

    }

    public Result(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> Result<T> success() {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setSuccess(true);
        return ajaxResult;
    }

    public static <T> Result<T> success(T data) {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setSuccess(true);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static <T> Result<T> success(T data, String msg) {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setSuccess(true);
        ajaxResult.setMsg(msg);
        ajaxResult.setData(data);
        return ajaxResult;
    }

    public static <T> Result<T> fail() {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setCode(500);
        ajaxResult.setSuccess(false);
        return ajaxResult;
    }


    public static <T> Result<T> fail(Integer errorCode, String errorMsg) {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setSuccess(false);
        ajaxResult.setCode(errorCode);
        ajaxResult.setMsg(errorMsg);
        return ajaxResult;
    }

    public static <T> Result<T> fail(String errorMsg) {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setCode(500);
        ajaxResult.setSuccess(false);
        ajaxResult.setMsg(errorMsg);
        return ajaxResult;
    }

    public static <T> Result<T> fail(String errorMsg, T data) {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setCode(500);
        ajaxResult.setSuccess(false);
        ajaxResult.setData(data);
        ajaxResult.setMsg(errorMsg);
        return ajaxResult;
    }

    public static <T> Result<T> unload() {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setCode(401);
        ajaxResult.setSuccess(false);
        ajaxResult.setMsg("请先登录");
        return ajaxResult;
    }

    public static <T> Result<T> point(String errorMsg) {
        Result<T> ajaxResult = new Result<>();
        ajaxResult.setCode(1001);
        ajaxResult.setSuccess(true);
        ajaxResult.setMsg(errorMsg);
        return ajaxResult;
    }

}
