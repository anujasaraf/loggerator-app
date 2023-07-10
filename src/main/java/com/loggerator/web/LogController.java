package com.loggerator.web;

import com.loggerator.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Controller
public class LogController {
    @Autowired
    LogService logService;

    @GetMapping(value = "/logs", produces = APPLICATION_JSON_VALUE)
    public DeferredResult<ResponseEntity<List<String>>> findLogs(
            @RequestParam(required = false) String user,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) Integer code) {
        DeferredResult<ResponseEntity<List<String>>> response = new DeferredResult<>();
        logService.findLogs(user, method, code == null ? null : HttpStatus.resolve(code), response);
        return response;
    }
}
