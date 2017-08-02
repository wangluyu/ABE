package com.abe.entity;

import java.lang.reflect.Array;

/**
 * @author LuyuWang
 * @date 2017/1/19
 * @time 9:33
 */
public class Message {
    private Integer code = 1;

    private String msg = "";

    private Object data = null;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
