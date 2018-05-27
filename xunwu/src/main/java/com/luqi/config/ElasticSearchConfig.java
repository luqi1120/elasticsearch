package com.luqi.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by luQi
 * 2018-05-27 14:12.
 */
@Configuration
public class ElasticSearchConfig {

//    @Value("${elasticsearch.host}")
//    private String esHost;
//
//    @Value("${elasticsearch.port}")
//    private int esPort;
//
//    @Value("${elasticsearch.cluster.name}")
//    private String esName;

    @Bean
    public TransportClient esClient() throws UnknownHostException {
        Settings settings = Settings.builder()
//                .put("cluster.name", this.esName)
                .put("cluster.name", "luqi")  // luqi为es配置文件中集群的名称
                .put("client.transport.sniff", true)   // 自动发现节点
                .build();

        // 目标地址
        InetSocketTransportAddress master = new InetSocketTransportAddress(
//                InetAddress.getByName(esHost), esPort
          InetAddress.getByName("192.168.0.9"), 9300  // 使用tcp的端口而不是http的
        );

        // 构建配置
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(master);
//              .add  如果很多节点就继续添加

        return client;
    }
}
