package com.loggerator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ip.tcp.TcpReceivingChannelAdapter;
import org.springframework.integration.ip.tcp.connection.TcpNetClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayLfSerializer;

@Configuration
public class LoggeratorListenerConfig {
    @Value("${log.loggerator.host:localhost}")
    private String host;
    @Value("${log.loggerator.port:8000}")
    private int port;
    @Value("${log.loggerator.retryInterval}")
    private int retryInterval;

    protected String getHost() {
        return host;
    }
    protected int getPort() {
        return port;
    }

    @Bean
    public TcpReceivingChannelAdapter logTcpReceivingChannelAdapter(TcpNetClientConnectionFactory factory) {
        TcpReceivingChannelAdapter adapter = new TcpReceivingChannelAdapter();
        adapter.setRetryInterval(retryInterval);
        adapter.setConnectionFactory(factory);
        adapter.setClientMode(true);
        adapter.setOutputChannelName("log-channel");
        return adapter;
    }

    @Bean
    public TcpNetClientConnectionFactory prepareLogTcpNetClientConnectionFactory(){
        TcpNetClientConnectionFactory factory =
                new TcpNetClientConnectionFactory(getHost(), getPort());
        factory.setDeserializer(new ByteArrayLfSerializer());
        factory.setSoKeepAlive(false);
        return factory;
    }
}
