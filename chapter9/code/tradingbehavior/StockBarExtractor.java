/*
 * StockBarExtractor.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;

/**
 * {@code StockBarExtractor} 类将按行分隔的原始交易数据解析成 {@link StockBar} 类集合。
 * <p>
 * 原始数据格式参见 data 目录下各数据文件头部说明，此处解析逻辑与数据说明严格对应。
 */
public class StockBarExtractor implements FlatMapFunction<String, StockBar> {
    @Override
    /**
     * 此方法用于将原始交易数据解析为 {@code StockBar} 类集合，解析逻辑与 data 目录下数据文件头部说明严格对应。
     *
     * @param value 原始交易数据。
     * @param out {@code StockBar} 类集合。
     */
    public void flatMap(String value, Collector<StockBar> out) {
        String[] lines = Arrays.stream(value.split("\n")).map(String::strip).toArray(String[]::new);
        if (lines.length == 1) {
            StockBar.timePeriod = this.getBarPeriod(lines[0]);
        } else {
            ZonedDateTime endTime = ZonedDateTime.parse(lines[0]).plus(StockBar.timePeriod);
            for (int i = 1; i < lines.length; i++) {
                String[] fields = lines[i].split("\t");
                if (fields.length == 6) {
                    String stockCode = fields[0];
                    double openPrice = Double.parseDouble(fields[1]);
                    double highPrice = Double.parseDouble(fields[2]);
                    double lowPrice = Double.parseDouble(fields[3]);
                    double closePrice = Double.parseDouble(fields[4]);
                    double volume = Double.parseDouble(fields[5]);
                    StockBar stockBar = new StockBar(
                        stockCode,
                        endTime,
                        openPrice,
                        highPrice,
                        lowPrice,
                        closePrice,
                        volume
                    );
                    out.collect(stockBar);
                } else {
                    throw new IllegalArgumentException(lines[i]);
                }
            }
        }
    }

    private Duration getBarPeriod(String line) {
        String prefix = "Bar period:\t";
        if (line.startsWith(prefix)) {
            String barPeriod = line.substring(prefix.length()).strip();
            switch (barPeriod) {
                case "1sec":
                    return Duration.ofSeconds(1);
                case "1min":
                    return Duration.ofMinutes(1);
                case "15min":
                    return Duration.ofMinutes(15);
                default:
                    throw new IllegalArgumentException(line);
            }
        } else {
            throw new IllegalArgumentException(line);
        }
    }
}
