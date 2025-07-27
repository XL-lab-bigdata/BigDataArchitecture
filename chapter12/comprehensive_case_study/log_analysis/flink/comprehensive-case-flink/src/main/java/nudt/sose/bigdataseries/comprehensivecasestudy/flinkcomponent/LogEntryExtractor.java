/*
 * LogEntryExtractor.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

public class LogEntryExtractor implements FlatMapFunction<String, LogEntry> {
    @Override
    public void flatMap(String logRecord, Collector<LogEntry> out) throws Exception {
        out.collect(LogEntry.from(logRecord));
    }
}
