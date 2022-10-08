package com.atguigu.common.exception;

public enum BizCodeEnume {

    UNKNOW_EXCEPTION(10000,"系统位置异常"),
    VALID_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000,"商品上架异常");

    private Integer code;
    private String msg;

    BizCodeEnume(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public String getMsg() {
        return msg;
    }



    public Integer getCode() {
        return code;
    }


}
