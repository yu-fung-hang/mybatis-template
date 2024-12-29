package com.yufung.demo.configure.builder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class RedisNodeBuilder {

    public static List<String> build(String nodes){
        if(StringUtils.isEmpty(nodes)){
            throw new RuntimeException("{\"cn\":\"redis 配置错误：集群节点为空\", \"en\":\"Redis configuration error: cluster node is empty\"}");
        }

        List<String> nodeList  = new ArrayList<>();

        String[] nodeAr =  nodes.split(",");
        for (String node : nodeAr ){
            String[] ipAndPort = node.split(":");
            if (ipAndPort.length < 2) {
                continue;
            }
            nodeList.add(node);
        }

        return nodeList;
    }
}
