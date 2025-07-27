/*
 * BarDataServer.java - flink-demo
 */

package bigdataplatform.flinkdemo.tradingdataserver;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * {@code BarDataServer} 类模拟交易行情服务器。服务器从给定的交易数据文件中读取数据，
 * 并将数据按命令行参数指定的时间间隔发送给交易客户端。
 * <p>
 * 服务器程序使用方法：
 * <pre>
 * java -jar <交易服务器程序路径> <交易服务器端口> <交易数据文件路径> [发送交易数据的时间间隔]
 * </pre>
 */
public class BarDataServer {
    /**
     * {@code EMIT_INTERVAL_MILLISECONDS} 定义发送交易数据的默认时间间隔，默认为 10 毫秒。
     */
    public final static int EMIT_INTERVAL_MILLISECONDS = 10;

    /**
     * 此方法启动服务器。
     *
     * @param port 服务器端口。
     * @param inputFilePath 交易数据文件路径。
     * @param emitInterval 发送交易数据的时间间隔。
     */
    public void start(final int port, final Path inputFilePath, final int emitInterval) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port: " + port);
            System.out.println("Waiting for clients to connect...");
            System.out.println();
            ExecutorService es = Executors.newCachedThreadPool();
            try (Socket clientSocket = serverSocket.accept()) {
                System.out.println("Client connection established, emitting bar data...");
                System.out.println();
                this.emitBarsData(clientSocket, inputFilePath, emitInterval);
            } catch (IOException e) {
                System.err.println("Failed to establish client connection.");
            }
        } catch (IOException e) {
            System.err.println("Failed to start server on port: " + port);
        }
    }

    /**
     * 此方法具体实现 bar 数据发送过程。
     *
     * @param clientSocket 客户端 socket.
     * @param inputFilePath 交易数据文件路径。
     * @param emitInterval 发送交易数据的时间间隔。
     */
    public void emitBarsData(final Socket clientSocket, final Path inputFilePath, final int emitInterval) {
        try (Stream<String> lines = Files.lines(inputFilePath, StandardCharsets.UTF_8)) {
            StringBuilder barsStringBuilder = new StringBuilder();
            Iterator<String> lineIterator = lines.iterator();
            try (OutputStream out = clientSocket.getOutputStream()) {
                while (lineIterator.hasNext()) {
                    String line = lineIterator.next();
                    if (!line.startsWith("#")) {
                        if (!line.isEmpty()) {
                            barsStringBuilder.append(line);
                            barsStringBuilder.append("\n");
                        } else {
                            barsStringBuilder.append("\n");
                            String bars = barsStringBuilder.toString();
                            if (!bars.isBlank()) {
                                try {
                                    out.write(bars.getBytes(Charset.defaultCharset()));
                                } catch (IOException e) {
                                    System.err.println("Connection closed by client.");
                                    return;
                                }
                                System.out.println("Emit bars: ");
                                System.out.print(bars);
                                try {
                                    Thread.sleep(emitInterval);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            barsStringBuilder.setLength(0);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to write data to client socket.");
            }
        } catch (IOException e) {
            System.err.println("Failed to read bar data file: " + inputFilePath);
        }
    }

    /**
     * 打印命令行帮助信息。
     */
    private static void printHelp() {
        try {
            final String jarPath = Paths.get(BarDataServer.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).toAbsolutePath().toString();
            System.err.println("Usage:\n\njava -jar " + jarPath + " <port> <bar_data_file_path> [emit_interval_milliseconds]");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 程序入口。
     */
    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) {
            printHelp();
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        File barDataFile = new File(args[1]);
        if (!barDataFile.isFile()) {
            System.err.println("Bar data file not found: " + barDataFile.getAbsolutePath());
            System.exit(1);
        }

        int emitInterval = args.length == 3
            ? Integer.parseInt(args[2])
            : BarDataServer.EMIT_INTERVAL_MILLISECONDS;

        BarDataServer server = new BarDataServer();
        server.start(port, barDataFile.toPath(), emitInterval);
    }
}
