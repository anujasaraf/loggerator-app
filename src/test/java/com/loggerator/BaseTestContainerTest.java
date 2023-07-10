package com.loggerator;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseTestContainerTest {
    static final String KEYSPACE = "loggerator";
    @Container
    public static final CassandraContainer cassandra
            = (CassandraContainer) new CassandraContainer("cassandra:3.11.2")
            .withFileSystemBind("src/main/resources/schema", "/tmp/schema")
            .withExposedPorts(9042);

    private static void createKeyspace() {
        Cluster cluster = cassandra.getCluster();
        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS loggerator WITH REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor':1};");
            session.execute("USE loggerator;");
            session.execute("CREATE TABLE IF NOT EXISTS log (\n" +
                    "    user TEXT, ip TEXT,\n" +
                    "    timestamp TIMESTAMP,\n" +
                    "    method TEXT,\n" +
                    "    path TEXT,\n" +
                    "    proto TEXT,\n" +
                    "    statusCode INT,\n" +
                    "    bytes INT,\n" +
                    "    PRIMARY KEY(user, timestamp, method, statuscode, path, bytes, ip, proto)\n" +
                    ") WITH CLUSTERING ORDER BY (timestamp DESC);");
            session.execute("CREATE MATERIALIZED VIEW IF NOT EXISTS logByTS_mv\n" +
                    "    AS SELECT * FROM log\n" +
                    "    WHERE proto IS NOT NULL\n" +
                    "    AND timestamp IS NOT NULL\n" +
                    "    AND user IS NOT NULL\n" +
                    "    AND method IS NOT NULL\n" +
                    "    AND statuscode IS NOT NULL\n" +
                    "    AND path IS NOT NULL\n" +
                    "    AND bytes IS NOT NULL\n" +
                    "    AND ip IS NOT NULL\n" +
                    "    PRIMARY KEY ((proto), timestamp, user ,method, statuscode, path, bytes, ip)\n" +
                    "    WITH CLUSTERING ORDER BY (timestamp DESC);");
        }

        System.setProperty("spring.cassandra.keyspace-name", KEYSPACE);
        System.setProperty("spring.cassandra.contact-points", cassandra.getHost() + ":" + cassandra.getMappedPort(9042));
        System.setProperty("spring.cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));
    }

    @BeforeAll
    public static void init() {
        createKeyspace();
    }

    public String[] getLogData() {
        return new String[]{
                "103.171.3.30 - brucebarnes [30/Jul/2000 07:30:07 +0000] \"GET /bookmarks/37 HTTP/1.0\" 200 449",
                "233.130.159.34 - roberthowell [30/Jul/2000 05:06:42 +0000] \"PUT /photos/271 HTTP/1.0\" 200 104",
                "103.171.3.30 - brucebarnes [30/Jul/2000 04:41:05 +0000] \"GET /followers/29 HTTP/1.0\" 200 98",
                "214.93.206.91 - jeremymorales [30/Jul/2000 03:24:58 +0000] \"GET /likes/220 HTTP/1.0\" 200 322",
                "65.39.219.253 - quo_saepe [30/Jul/2000 03:01:29 +0000] \"POST /posts/286 HTTP/1.0\" 200 347",
                "233.130.159.34 - roberthowell [30/Jul/2000 01:42:18 +0000] \"PUT /likes/148 HTTP/1.0\" 200 84",
                "214.93.206.91 - jeremymorales [29/Jul/2000 10:42:34 +0000] \"PUT /bookmarks/220 HTTP/1.0\" 200 249",
                "233.130.159.34 - roberthowell [29/Jul/2000 10:26:29 +0000] \"GET /bookmarks/296 HTTP/1.0\" 200 537",
                "103.171.3.30 - brucebarnes [29/Jul/2000 06:19:04 +0000] \"GET /posts/80 HTTP/1.0\" 200 24",
                "165.76.41.247 - markbutler [29/Jul/2000 05:17:33 +0000] \"PUT /followers/298 HTTP/1.0\" 200 94",
                "103.171.3.30 - brucebarnes [29/Jul/2000 04:28:24 +0000] \"PUT /likes/16 HTTP/1.0\" 200 536"
        };
    }
}
