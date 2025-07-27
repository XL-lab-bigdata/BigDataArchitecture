/*
 * LogAnalysisResult.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

import lombok.*;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Iterator;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = lombok.AccessLevel.PRIVATE)
public class LogAnalysisResult {
    public static LogAnalysisResult from(
        ProcessAllWindowFunction<LogEntry, LogAnalysisResult, TimeWindow>.Context context,
        Iterable<LogEntry> logEntries
    ) {
        ZonedDateTime startTimestamp = longToZonedDateTime(context.window().getStart());
        ZonedDateTime endTimestamp = longToZonedDateTime(context.window().getEnd());

        Iterator<LogEntry> logEntryIterator = logEntries.iterator();
        LogEntry firstLogEntry = logEntryIterator.next();

        double requestTime = firstLogEntry.getRequestTime();
        int requestCount = 1;
        double minRequestTime = requestTime;
        double maxRequestTime = requestTime;
        double sumRequestTime = requestTime;
        double sumSqureRequestTime = requestTime * requestTime;

        while (logEntryIterator.hasNext()) {
            LogEntry logEntry = logEntryIterator.next();
            requestTime = logEntry.getRequestTime();

            requestCount++;
            if (requestTime < minRequestTime) {
                minRequestTime = requestTime;
            }
            if (requestTime > maxRequestTime) {
                maxRequestTime = requestTime;
            }
            sumRequestTime += requestTime;
            sumSqureRequestTime += requestTime * requestTime;
        }
        double meanRequestTime = sumRequestTime / requestCount;
        double stdDevRequestTime = Math.pow(sumSqureRequestTime / requestCount - meanRequestTime * meanRequestTime, 0.5);

        return LogAnalysisResult.builder()
            .startTimestamp(startTimestamp)
            .endTimestamp(endTimestamp)
            .requestCount(requestCount)
            .minRequestTime(minRequestTime)
            .maxRequestTime(maxRequestTime)
            .meanRequestTime(meanRequestTime)
            .stdDevRequestTime(stdDevRequestTime)
            .build();
    }

    ZonedDateTime startTimestamp;
    ZonedDateTime endTimestamp;
    int requestCount;
    double maxRequestTime;
    double minRequestTime;
    double meanRequestTime;
    double stdDevRequestTime;

    private static ZonedDateTime longToZonedDateTime(long timestamp) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    @Override
    public String toString() {
        return "LogAnalysisResult{" +
            "startTimestamp=" + startTimestamp +
            ", endTimestamp=" + endTimestamp +
            ", maxRequestTime=" + maxRequestTime +
            ", minRequestTime=" + minRequestTime +
            ", meanRequestTime=" + meanRequestTime +
            ", stdDevRequestTime=" + stdDevRequestTime +
            '}';
    }
}
