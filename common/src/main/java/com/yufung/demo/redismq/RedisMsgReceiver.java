package com.yufung.demo.redismq;

public interface RedisMsgReceiver {

    /**
     * 管道名称
     * @return
     */
    String getChannelName();

    /**
     * 收到消息执行的方法
     * @param message
     */
    void receiveMsg(String message);

}
