/*
 * LogEntryTimestampAssigner.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;

public class LogEntryTimestampAssigner implements SerializableTimestampAssigner<LogEntry> {
    @Override
    public long extractTimestamp(LogEntry element, long recordTimestamp) {
        return element.getTimestamp().toInstant().toEpochMilli();
    }
}
