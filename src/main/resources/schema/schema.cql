USE loggerator;

CREATE TABLE IF NOT EXISTS log (
    user TEXT, ip TEXT,
    timestamp TIMESTAMP,
    method TEXT,
    path TEXT,
    proto TEXT,
    statusCode INT,
    bytes INT,
    PRIMARY KEY(user, timestamp, method, statuscode, path, bytes, ip, proto)
) WITH CLUSTERING ORDER BY (timestamp DESC);

CREATE MATERIALIZED VIEW IF NOT EXISTS logByTS_mv
    AS SELECT * FROM log
    WHERE proto IS NOT NULL
    AND timestamp IS NOT NULL
    AND user IS NOT NULL
    AND method IS NOT NULL
    AND statuscode IS NOT NULL
    AND path IS NOT NULL
    AND bytes IS NOT NULL
    AND ip IS NOT NULL
    PRIMARY KEY ((proto), timestamp, user ,method, statuscode, path, bytes, ip)
    WITH CLUSTERING ORDER BY (timestamp DESC);
