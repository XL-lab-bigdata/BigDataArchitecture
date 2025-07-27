/*
 * Position.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

/**
 * {@code Position} 类表示交易头寸。
 */
public enum Position {
    /**
     * {@code LONG} 表示看多。
     */
    LONG {
        @Override
        public String toString() {
            return "long";
        }
    },
    /**
     * {@code SHORT} 表示看空。
     */
    SHORT {
        @Override
        public String toString() {
            return "short";
        }
    },
}
