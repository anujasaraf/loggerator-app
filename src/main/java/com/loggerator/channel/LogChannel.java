package com.loggerator.channel;

import com.loggerator.data.record.Log;
import com.loggerator.data.repo.LogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

@MessageEndpoint
public class LogChannel {
    @Autowired
    private LogRepo logRepo;

    @ServiceActivator(inputChannel = "log-channel")
    public void consume(byte[] bytes) {
        Log log = Log.parse(new String(bytes));
        logRepo.save(log);
    }
}
