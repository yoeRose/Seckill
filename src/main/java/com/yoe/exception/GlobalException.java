package com.yoe.exception;

import com.yoe.utils.CodeMsg;

public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private CodeMsg msg;

    public GlobalException(CodeMsg msg) {
        super(msg.toString());
        this.msg = msg;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public CodeMsg getMsg() {
        return msg;
    }
}
