/*
 * LogHandler.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

import com.esotericsoftware.kryo.serializers.TimeSerializers;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.time.Duration;
import java.time.ZonedDateTime;

public class LogHandler {
    public static void handle(String brokers, String topics, String groupID) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().registerTypeWithKryoSerializer(ZonedDateTime.class, TimeSerializers.ZonedDateTimeSerializer.class);

        KafkaSource<String> source = KafkaSource.<String>builder()
            .setBootstrapServers(brokers)
            .setTopics(topics)
            .setGroupId(groupID)
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        DataStream<String> logRecordSteam = env.fromSource(source, WatermarkStrategy.noWatermarks(), "web01-nginx-log");
        var logAnalysisResultStream = logRecordSteam.flatMap(new LogEntryExtractor())
            .assignTimestampsAndWatermarks(WatermarkStrategy
                .<LogEntry>forBoundedOutOfOrderness(Duration.ofSeconds(5))
                .withTimestampAssigner(new LogEntryTimestampAssigner()))
            .windowAll(TumblingEventTimeWindows.of(Time.seconds(30)))
            .process(new LogAnalysisFunction());

        logAnalysisResultStream.addSink(LogAnalysisPostgreSQLSinkFactory.produce());

        env.execute("Log Analysis");
    }
}
