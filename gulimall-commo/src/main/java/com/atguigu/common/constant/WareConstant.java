package com.atguigu.common.constant;

public class WareConstant {

    public enum PurchaseStatusEnum{
        CREATED(0,"新建状态 "),ASSIGNED(1,"已分配")
        ,RECEIVE(2,"已领取")
        ,FINISH(3,"已完成"),HASERROR(4,"有异常");

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public enum PurchaseDetailEnum{
        CREATED(0,"新建状态 "),ASSIGNED(1,"已分配")
        ,BUYING(2,"正在采购")
        ,FINISH(3,"已完成"),HASERROR(4,"采购失败");

        PurchaseDetailEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        private int code;
        private String msg;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}
