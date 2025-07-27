/*
 * LogAnalysisPostgreSQLSinkFactory.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LogAnalysisPostgreSQLSinkFactory {
    public static SinkFunction<LogAnalysisResult> produce() {
        Properties prop = new Properties();
        try (InputStream inputStream = LogAnalysisPostgreSQLSinkFactory.class.getResourceAsStream("/jdbc.properties")) {
            prop.load(inputStream);
            String url = prop.getProperty("url");
            String driver = prop.getProperty("driver");
            String username = prop.getProperty("username");
            String password = prop.getProperty("password");

            return JdbcSink.<LogAnalysisResult>sink(
                "INSERT INTO web01_nginx_log_analysis "
                    + "(start_timestamp, end_timestamp, "
                    + "request_count, min_request_time, max_request_time, mean_request_time, std_dev_request_time) "
                    + "values (?, ?, ?, ?, ?, ?, ?) "
                    + "ON CONFLICT (start_timestamp, end_timestamp) DO UPDATE SET "
                    + "request_count = EXCLUDED.request_count, "
                    + "min_request_time = EXCLUDED.min_request_time, "
                    + "max_request_time = EXCLUDED.max_request_time, "
                    + "mean_request_time = EXCLUDED.mean_request_time, "
                    + "std_dev_request_time = EXCLUDED.std_dev_request_time",
                (statement, logAnalysisResult) -> {
                    statement.setTimestamp(1, java.sql.Timestamp.from(logAnalysisResult.getStartTimestamp().toInstant()));
                    statement.setTimestamp(2, java.sql.Timestamp.from(logAnalysisResult.getEndTimestamp().toInstant()));
                    statement.setInt(3, logAnalysisResult.getRequestCount());
                    statement.setDouble(4, logAnalysisResult.getMinRequestTime());
                    statement.setDouble(5, logAnalysisResult.getMaxRequestTime());
                    statement.setDouble(6, logAnalysisResult.getMeanRequestTime());
                    statement.setDouble(7, logAnalysisResult.getStdDevRequestTime());
                },
                JdbcExecutionOptions.builder()
                    .withBatchSize(1)
                    .withBatchIntervalMs(0)
                    .withMaxRetries(3)
                    .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                    .withUrl(url)
                    .withDriverName(driver)
                    .withUsername(username)
                    .withPassword(password)
                    .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        Properties prop = new Properties();
        try (InputStream inputStream = LogAnalysisPostgreSQLSinkFactory.class.getResourceAsStream("/jdbc.properties")) {
            prop.load(inputStream);
            System.out.println("url: " + prop.getProperty("url"));
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
    }
}
