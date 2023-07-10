package com.loggerator.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoggeratorListenerConfigTest {
    @InjectMocks
    public LoggeratorListenerConfig config;

    @BeforeEach
    public void beforeEach() {
        config = Mockito.spy(config);
        Mockito.doReturn("localhost").when(config).getHost();
        Mockito.doReturn(8000).when(config).getPort();
    }

    @Test
    void logTcpReceivingChannelAdapter_success() {
        assertNotNull(config.logTcpReceivingChannelAdapter(config.prepareLogTcpNetClientConnectionFactory()));
    }

    @Test
    void prepareLogTcpNetClientConnectionFactory_success() {
        assertNotNull(config.prepareLogTcpNetClientConnectionFactory());
    }
}
