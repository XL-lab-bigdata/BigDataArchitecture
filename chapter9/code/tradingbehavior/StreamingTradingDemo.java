/*
 * StreamingTradingDemo.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingbehavior;

import com.esotericsoftware.kryo.serializers.TimeSerializers.ZonedDateTimeSerializer;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * {@code StreamingTradingDemo} 类实现交易客户端，是模拟交易程序入口。
 * <p>
 * 客户端程序使用方法：
 * <pre>
 * bin/flink run <交易客户端程序路径> <交易服务器 IP> <交易服务器端口> <保存交易结果的目录路径>
 * </pre>
 */
public class StreamingTradingDemo {
    /**
     * 此方法用于模拟交易过程。
     *
     * @param serverIP 交易服务器 IP 地址。
     * @param serverPort 交易服务器端口。
     * @param outputDirectoryPath 保存交易结果的目录路径。
     */
    private static void trade(final String serverIP,
                              final int serverPort,
                              final Path outputDirectoryPath) {
        try {
            FileSink<String> outputFileSink = generateOutputFileSink(outputDirectoryPath);

            StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
            // NOTE: 此处用于确保 {@link java.time.ZonedDateTime} 可被正确序列化。
            env.getConfig().registerTypeWithKryoSerializer(ZonedDateTime.class, ZonedDateTimeSerializer.class);

            DataStream<String> textStream = env.socketTextStream(serverIP, serverPort, "\n\n");
            var trading = textStream.flatMap(new StockBarExtractor()).setParallelism(1)
                .keyBy(StockBar::getStockCode)
                .countWindow(JokerStrategyBuilder.MIN_BAR_COUNT, 1)
                .process(new TradingSignalGenerator());

            trading.print().setParallelism(1);
            trading.map(TradingSignal::toString).sinkTo(outputFileSink).setParallelism(1);

            env.execute("Flink Trading Demo");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileSink<String> generateOutputFileSink(final Path outputFilePath) {
        return FileSink.forRowFormat(
                outputFilePath,
                new SimpleStringEncoder<String>()
            )
            .withRollingPolicy(
                DefaultRollingPolicy.builder()
                    .withRolloverInterval(Duration.ofMinutes(5))
                    .withInactivityInterval(Duration.ofMinutes(2))
                    .withMaxPartSize(MemorySize.ofMebiBytes(10L * 1024L))
                    .build()
            )
            .build();
    }

    /**
     * 打印命令行帮助信息。
     */
    private static void printHelp() {
        try {
            String jarPath = Paths.get(StreamingTradingDemo.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).toAbsolutePath().toString();
            System.err.println("Usage:\n\nbin/flink run " + jarPath + " <server_ip> <server_port> <output_directory_path>");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 程序入口。
     */
    public static void main(String[] args) {
        if (args.length != 3) {
            printHelp();
            System.exit(1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        Path outputDirectoryPath = new Path(args[2]);
        trade(serverIP, serverPort, outputDirectoryPath);
    }
}
