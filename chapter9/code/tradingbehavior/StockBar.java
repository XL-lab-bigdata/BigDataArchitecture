/*
 * StockBar.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * {@code StockBar} 类表示股票市场一笔 bar 数据。
 */
@Getter
@AllArgsConstructor
public class StockBar {
    /**
     * {@code timePeriod} 表示该笔 bar 数据对应的时间周期。
     * <p>
     * 本程序支持的时间周期由原始数据文件决定，data 目录下有按 1 秒、1 分钟、15 分钟
     * 三种周期整理的交易数据文件，其它值视为非法参数。
     */
    public static Duration timePeriod;
    /**
     * {@code stockCode} 表示股票代码。
     */
    @NonNull
    private final String stockCode;
    /**
     * {@code endTime} 表示该时间周期的截止时间。
     */
    @NonNull
    private final ZonedDateTime endTime;
    /**
     * {@code openPrice} 表示该时间周期内的起始价。
     */
    private final double openPrice;
    /**
     * {@code highPrice} 表示该时间周期内的最高价。
     */
    private final double highPrice;
    /**
     * {@code lowPrice} 表示该时间周期内的最低价。
     */
    private final double lowPrice;
    /**
     * {@code closePrice} 表示该时间周期内的截止价。
     */
    private final double closePrice;
    /**
     * {@code volume} 表示该时间周期内的交易量。
     */
    private final double volume;

    @Override
    public String toString() {
        return "StockBar{" +
            "code='" + stockCode + "'" +
            ", timePeriod=" + timePeriod +
            ", endTime=" + endTime +
            ", open=" + openPrice +
            ", high=" + highPrice +
            ", low=" + lowPrice +
            ", close=" + closePrice +
            ", volume=" + volume +
            '}';
    }
}
