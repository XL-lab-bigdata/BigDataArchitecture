/*
 * TradingSignal.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.time.ZonedDateTime;

/**
 * {@code TradingSignal} 类表示交易信号。
 */
@Getter
@AllArgsConstructor
public class TradingSignal {
    /**
     * {@code time} 表示交易时间。
     */
    @NonNull
    private final ZonedDateTime time;
    /**
     * {@code subjectMatter} 表示标的物。
     */
    @NonNull
    private final String subjectMatter;
    /**
     * {@code position} 表示交易头寸。
     */
    @NonNull
    private final Position position;

    @Override
    public String toString() {
        return "Trading signal{" + "\n  " +
            "Time: " + time + "\n  " +
            "Subject matter: " + subjectMatter + "\n  " +
            "Position: " + position + "\n" +
            '}';
    }
}
