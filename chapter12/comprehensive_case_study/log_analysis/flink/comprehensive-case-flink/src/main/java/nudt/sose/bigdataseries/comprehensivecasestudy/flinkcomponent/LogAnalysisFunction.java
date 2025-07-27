/*
 * LogAnalysisFunction.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

public class LogAnalysisFunction extends ProcessAllWindowFunction<LogEntry, LogAnalysisResult, TimeWindow> {
    @Override
    public void process(ProcessAllWindowFunction<LogEntry, LogAnalysisResult, TimeWindow>.Context context,
                        Iterable<LogEntry> logEntries,
                        Collector<LogAnalysisResult> out) {
        out.collect(LogAnalysisResult.from(context, logEntries));
    }
}
