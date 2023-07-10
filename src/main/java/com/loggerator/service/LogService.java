package com.loggerator.service;

import com.loggerator.data.record.Log;
import com.loggerator.data.repo.LogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

@Service
public class LogService {
    @Value("${log.repo.pagesize:1000}")
    private int pageSize;
    @Autowired
    private LogRepo logRepo;

    @Async
    public void findLogs(String user, String method, HttpStatus code, DeferredResult<ResponseEntity<List<String>>> asyncResponse) {
        Slice<Log> logs;
        CassandraPageRequest pr = getPageRequest(null);
        if (!stringIsNullOrEmpty(user)) {
            logs = logRepo.findByUser(user, pr);
        } else {
            logs = logRepo.findAllOrderedByTimestamp(pr);
        }

        Stream<Log> logStream = logs.getContent().stream();

        if (code != null) {
            logStream = logStream.filter(l -> l.statusCode() == code.value());
        }

        if (!stringIsNullOrEmpty(method)) {
            logStream = logStream.filter(l -> method.equals(l.method()));
        }

        List<String> logStrings = logStream.map(Log::toLogFormat).toList();
        asyncResponse.setResult(ResponseEntity.status(HttpStatus.OK).body(logStrings));
    }

    /**
     * Default paging is set to pagesize limit on records fetched
     * TODO: extend this service to implement pagination
     *
     * @param cur
     * @return
     */
    private CassandraPageRequest getPageRequest(@Nullable CassandraPageRequest cur) {
        if (cur != null) {
            return cur.next();
        }

        return CassandraPageRequest.of(0, pageSize);
    }

    public static boolean stringIsNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
