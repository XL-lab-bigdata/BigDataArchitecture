/*
 * App.java - comprehensive-case-flink
 *
 * Copyright 2023 Jinsong Zhang
 *
 * This file can NOT be copied and/or distributed without the express permission of Jinsong Zhang
 */

package nudt.sose.bigdataseries.comprehensivecasestudy.flinkcomponent;

public class App {
    public static void main(String[] args) throws Exception {
        String brokers = "hadoop02:9092,hadoop03:9092,hadoop04:9092";
        String topics = "web01-nginx-log";
        String groupID = "web01.nginx.log.flink";
        LogHandler.handle(brokers, topics, groupID);
    }
}
