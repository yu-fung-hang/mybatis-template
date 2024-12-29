package com.yufung.demo.configure.builder;

public enum ClientStatusEnum {
    /**
     * 正确
     */
    GOOD(1),
    /**
     * 警告
     */
    WARN(0),
    /**
     * 错误
     */
    ERROR(-1);

    int status;

    ClientStatusEnum(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}