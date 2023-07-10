package com.loggerator.channel;

import com.loggerator.data.record.Log;
import com.loggerator.data.repo.LogRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogChannelTest {
    @Autowired
    @Mock
    public LogRepo repo;

    @Autowired
    @InjectMocks
    LogChannel channel;

    @Test
    public void consume_invalidLog_failure() {
        assertThrows(RuntimeException.class, () -> {
            channel.consume("invalid_message".getBytes());
        });
    }

    @Test
    public void consume_validLog_success() {
        String inputLog = "33.15.199.187 - garymitchell [21/Jul/2000 08:23:15 +0000] \"POST /likes/218 HTTP/1.0\" 403 239";
        channel.consume(inputLog.getBytes());
        ArgumentCaptor<Log> log = ArgumentCaptor.forClass(Log.class);
        Mockito.verify(repo, Mockito.times(1)).save(log.capture());
        Log captured = log.getValue();
        assertEquals(inputLog, captured.toLogFormat());
    }
}
