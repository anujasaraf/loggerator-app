package com.loggerator.data.record;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Table
public record Log(String ip, @PrimaryKey String user, Instant timestamp, String method, String path, String proto, int statusCode, int bytes) {
    public static final DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm:ss Z");
    public static final Pattern parser = Pattern.compile("(?<ip>[0-9.]+)\\s-\\s(?<user>[\\w]+)\\s\\[(?<time>.+)\\]\\s\"(?<method>[\\w]+)\\s(?<uri>[/\\w]+)\\s(?<proto>[/\\w.]+)\"\\s(?<code>\\d+)\\s(?<bytes>\\d+)");
    public static final String format = "%s - %s [%s] \"%s %s %s\" %s %s";

    public static Log parse(String logline) {
        Matcher m = parser.matcher(logline);
        try {
            if (m.find()) {
                return new Log(m.group("ip"),
                        m.group("user"),
                        LocalDateTime.parse(m.group("time"), df).toInstant(ZoneOffset.UTC),
                        m.group("method"),
                        m.group("uri"),
                        m.group("proto"),
                        Integer.parseInt(m.group("code")),
                        Integer.parseInt(m.group("bytes")));
            } else {
                throw new RuntimeException("Invalid log format\n" + logline);
            }
        } catch (Exception e) {
            throw new RuntimeException("Invalid log format\n" + logline, e);
        }
    }

    public String toLogFormat() {
        return String.format(format, ip, user, df.format(timestamp.atZone(ZoneOffset.UTC)), method, path, proto, statusCode, bytes);
    }
}
