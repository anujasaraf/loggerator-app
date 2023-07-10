package com.loggerator.web;

import com.loggerator.BaseTestContainerTest;
import com.loggerator.channel.LogChannel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class LogControllerTest extends BaseTestContainerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LogChannel logChannel;

    @BeforeEach
    public void beforeEach() {
        for (String logDatum : getLogData()) {
            logChannel.consume(logDatum.getBytes());
        }
    }

    @Test
    void findLogs_all() throws Exception {
        MvcResult res = mockMvc.perform(get("/logs")).andDo(print()).andExpect(status().isOk()).andReturn();
        Assertions.assertTrue(res.getAsyncResult().toString().contains(Arrays.toString(getLogData())));
    }

    @Test
    void findLogs_user() throws Exception {
        MvcResult res = mockMvc.perform(get("/logs?user=brucebarnes")).andDo(print()).andExpect(status().isOk()).andReturn();

        this.mockMvc.perform(asyncDispatch(res))
                .andExpect(content().string("[" +
                        "\"103.171.3.30 - brucebarnes [30/Jul/2000 07:30:07 +0000] \\\"GET /bookmarks/37 HTTP/1.0\\\" 200 449\"," +
                        "\"103.171.3.30 - brucebarnes [30/Jul/2000 04:41:05 +0000] \\\"GET /followers/29 HTTP/1.0\\\" 200 98\"," +
                        "\"103.171.3.30 - brucebarnes [29/Jul/2000 06:19:04 +0000] \\\"GET /posts/80 HTTP/1.0\\\" 200 24\"," +
                        "\"103.171.3.30 - brucebarnes [29/Jul/2000 04:28:24 +0000] \\\"PUT /likes/16 HTTP/1.0\\\" 200 536\"]"));
    }

    @Test
    void findLogs_user_filterMethod() throws Exception {
        MvcResult res = mockMvc.perform(get("/logs?user=brucebarnes&method=PUT")).andDo(print()).andExpect(status().isOk()).andReturn();

        this.mockMvc.perform(asyncDispatch(res))
                .andExpect(content().string("[" +
                        "\"103.171.3.30 - brucebarnes [29/Jul/2000 04:28:24 +0000] \\\"PUT /likes/16 HTTP/1.0\\\" 200 536\"]"));
    }

    @Test
    void findLogs_user_filterCode() throws Exception {
        MvcResult res = mockMvc.perform(get("/logs?user=brucebarnes&code=403")).andDo(print()).andExpect(status().isOk()).andReturn();

        this.mockMvc.perform(asyncDispatch(res))
                .andExpect(content().string("[]"));
    }
}
