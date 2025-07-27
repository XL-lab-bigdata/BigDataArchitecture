/*
 * LogEntry.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;


import lombok.*;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PRIVATE)
public class LogEntry {
    private static final String LOG_ENTRY_PATTERN_STR = "^(\\S+) (\\S+) (\\S+) \\[([\\w:/]+\\s[+\\-]\\d{4})] \"(.+?)\" (\\d{3}) (\\S+) (\\S+) \"(.*?)\" \"(.*?)\"$";
    private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile(LOG_ENTRY_PATTERN_STR);

    public static LogEntry from(String logRecord) {
        Matcher matcher = LOG_ENTRY_PATTERN.matcher(logRecord);

        if (matcher.matches()) {
            String ipAddress = matcher.group(1);
            ZonedDateTime timestamp = ZonedDateTime.parse(matcher.group(4), DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US));
            String requestLine = matcher.group(5);
            int statusCode = Integer.parseInt(matcher.group(6));
            int bytesSent = Integer.parseInt(matcher.group(7));
            double requestTime = Double.parseDouble(matcher.group(8)) * 1000;
            String referrer = Objects.equals(matcher.group(9), "-") ? null : matcher.group(9);
            String userAgent = matcher.group(10);

            return LogEntry.builder()
                .ipAddress(ipAddress)
                .timestamp(timestamp)
                .requestLine(requestLine)
                .statusCode(statusCode)
                .bytesSent(bytesSent)
                .requestTime(requestTime)
                .referrer(referrer)
                .userAgent(userAgent)
                .build();
        } else {
            throw new IllegalArgumentException("Cannot parse log record: " + logRecord);
        }
    }

    @NonNull
    String ipAddress;
    @NonNull
    ZonedDateTime timestamp;
    @NonNull
    String requestLine;
    int statusCode;
    int bytesSent;
    double requestTime;
    String referrer;
    @NonNull
    String userAgent;

    @Override
    public String toString() {
        return "LogEntry{" +
            "ipAddress='" + ipAddress + '\'' +
            ", timestamp=" + timestamp +
            ", requestLine='" + requestLine + '\'' +
            ", statusCode=" + statusCode +
            ", bytesSent=" + bytesSent +
            ", requestTime=" + requestTime +
            ", referrer='" + referrer + '\'' +
            ", userAgent='" + userAgent + '\'' +
            '}';
    }
}
