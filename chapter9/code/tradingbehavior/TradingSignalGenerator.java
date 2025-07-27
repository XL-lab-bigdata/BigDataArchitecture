/*
 * TradingSignalGenerator.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.util.Collector;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Strategy;

/**
 * {code TradingSignalGenerator} 类按 {@link JokerStrategyBuilder} 交易策略构造器给定的
 * 交易策略产生交易信号 {@link TradingSignal}.
 */
public class TradingSignalGenerator extends ProcessWindowFunction<StockBar, TradingSignal, String, GlobalWindow> {
    /**
     * 此方法用于实时生成交易信号。
     *
     * @param stockCode 股票代码。
     * @param context flink 上下文对象。
     * @param stockBars bar 数据序列。
     * @param out 输出的交易信号。
     */
    @Override
    public void process(String stockCode,
                        Context context,
                        Iterable<StockBar> stockBars, Collector<TradingSignal> out) {
        BarSeries series = new BaseBarSeriesBuilder().withName(stockCode).build();
        for (StockBar stockBar : stockBars) {
            series.addBar(
                StockBar.timePeriod,
                stockBar.getEndTime(),
                stockBar.getOpenPrice(),
                stockBar.getHighPrice(),
                stockBar.getLowPrice(),
                stockBar.getClosePrice(),
                stockBar.getVolume()
            );
        }
        Strategy joker = JokerStrategyBuilder.buildStrategy(series);
        int endIndex = series.getEndIndex();
        // 产生看多交易信号
        if (joker.shouldEnter(endIndex)) {
            TradingSignal signal = new TradingSignal(
                series.getBar(endIndex).getEndTime(),
                stockCode,
                Position.LONG
            );
            out.collect(signal);
        }
        // 产生看空交易信号
        if (joker.shouldExit(endIndex)) {
            TradingSignal signal = new TradingSignal(
                series.getBar(endIndex).getEndTime(),
                stockCode,
                Position.SHORT
            );
            out.collect(signal);
        }
    }
}
