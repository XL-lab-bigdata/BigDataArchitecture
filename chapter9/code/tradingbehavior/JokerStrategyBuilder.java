/*
 * JokerStrategyBuilder.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseStrategy;
import org.ta4j.core.Rule;
import org.ta4j.core.Strategy;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.OverIndicatorRule;
import org.ta4j.core.rules.UnderIndicatorRule;

/**
 * {@code JokerStrategyBuilder} 类是交易策略构造器。
 * <p>
 * 此处采用最常见的动量交易策略，仅作 flink 演示用，不代表所用指标在真实市场交易中有效。
 */
public class JokerStrategyBuilder {
    /**
     * {@code MIN_BAR_COUNT} 表示一次策略分析所用的最小 bar 数据量，与具体交易策略绑定。
     */
    public static final int MIN_BAR_COUNT = 26 + 18 - 1;

    /**
     * 此方法构造交易策略。
     *
     * @param series bar 数据序列。
     * @return 交易策略。
     */
    public static Strategy buildStrategy(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        EMAIndicator shortEMA = new EMAIndicator(closePrice, 9);
        EMAIndicator longEMA = new EMAIndicator(closePrice, 26);

        StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(series, 14);

        MACDIndicator macd = new MACDIndicator(closePrice, 9, 26);
        EMAIndicator emaMACD = new EMAIndicator(macd, 18);

        Rule entryRule = new OverIndicatorRule(shortEMA, longEMA)
            .and(new CrossedDownIndicatorRule(stochasticOscillK, 20))
            .and(new OverIndicatorRule(macd, emaMACD));

        Rule exitRule = new UnderIndicatorRule(shortEMA, longEMA)
            .and(new CrossedUpIndicatorRule(stochasticOscillK, 80))
            .and(new UnderIndicatorRule(macd, emaMACD));

        return new BaseStrategy(entryRule, exitRule);
    }
}
