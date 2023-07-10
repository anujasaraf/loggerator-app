package com.loggerator.data.repo;

import com.loggerator.data.record.Log;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.domain.Slice;

public interface LogRepo extends CassandraRepository<Log, String> {
    Slice<Log> findByUser(String user, CassandraPageRequest pr);
    @Query("select * from logByTS_mv")
    Slice<Log> findAllOrderedByTimestamp(CassandraPageRequest pr);
}
