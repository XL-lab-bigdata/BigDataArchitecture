# 简介

* 作者：张劲松
* 时间：2023 年 7 月 25 日

此目录为国防科大吕欣组著《大数据 · 平台系统篇》一书中综合案例章节提供主要内容。

[[_TOC_]]

## 系统功能概述

本案例综合应用 hadoop, kafka, flume, flink, PostgreSQL, nginx, gunicorn, locust 等工具和技术，搭建并演示一个简单的日志实时分析系统，

系统演示功能简要说明如下：

* 运行在 gunicorn 和 nginx 上的 web 应用，通过 Web API 对外提供中文分词服务；
* 通过 locust 对该 web 应用进行性能测试；
* 通过 flume 和 kafka 实时采集该 web 应用的 nginx 日志并将其统一存入 HDFS；
* 通过 kafka 和 flink 分析该 web 应用的 nginx 日志，实时计算服务表现，并将表现指标存入关系型数据库 postgreSQL 中。

### Web 应用

Web 应用提供中文分词服务。

输入：

![webapi_request_demo](./image/webapi_request_demo.png)

返回：

![webapi_response_demo](./image/webapi_response_demo.png)

### 性能测试

为对 web 应用做服务评估，系统通过 locust 模拟发送用户请求，对其进行性能测试。在这一过程中，Web 应用生成的日志可由系统相应模块采集和分析。性能测试如图所示：

![locust_perf_demo](./image/locust_perf_demo.png)

上图中，通过 locust 命令行参数模拟用户访问，图中参数如下：

* `--headless`: 不启用 web 界面；
* `--only-summary`: 只打印测试结果摘要；
* `--host`: 被测试的 web 应用地址；
* `--users`: 最大并发用户数；
* `--spawn-rate`: 每秒产生的用户数；
* `--run-time`: 测试时长；
* `--locustfile`: 描述用户具体访问行为的 python 文件。

### 日志采集与存储

Web 应用节点上的原始 nginx 日志如图：

![nginx_access_log_demo](./image/nginx_access_log_demo.png)

系统调整了 nginx 日志格式，加入了请求时间字段，如上图加红框的部分所示。它表示读取到客户端发送请求的第一个字节到向客户端发送响应的最后一个字节后再写入日志的时间间隔，单位为秒。

系统通过 flume 采集日志，flume 将采集到的日志经由 kafka 保存到 HDFS. HDFS 上可以很方便的统一存储各节点日志，本案例存储 nginx 日志，如图所示：

![hdfs_store_log_demo](./image/hdfs_store_log_demo.png)

### 日志分析

对 Nginx 日志分析结果实时存入 PostgreSQL，如图：

![postgresql_store_log_analysis_demo](./image/postgresql_store_log_analysis_demo.png)

图中各指标说明如下：

* `start_timestamp`：该时间段带时区信息的起始时间戳；
* `end_timestamp`：该时间段带时区信息的结束时间戳；
* `request_count`：该时间段请求数；
* `min_request_time`：该时间段 web 应用单个请求处理时间的最小值，单位为毫秒。请求处理时间为 0 时表示处理该请求的时间小于 1 毫秒；
* `max_request_time`：该时间段 web 应用单个请求处理时间的最大值；
* `mean_request_time`：该时间段 web 应用请求处理时间的均值；
* `std_dev_request_time`：该时间段 web 应用请求处理时间的标准差。

读者可自行修改 flink 程序，实现更多指标的计算。

## 系统部署概述

除本书开始的实验环境一节提及的环境说明外，本案例所需的其它组件及版本说明如下：

| 组件       | 版本   |
| ---------- | ------ |
| Hadoop     | 3.3.5  |
| Kafka      | 3.4.0  |
| Flink      | 1.17.1 |
| Flume      | 1.11.0 |
| Gunicorn   | 21.2.0 |
| Nginx      | 1.18.0 |
| Locust     | 2.15.1 |
| PostgreSQL | 14.8   |

系统部署图如下：

![deployment_graph](./image/deployment_graph.png)

图中每个立方体表示一个物理节点。立方体第一行表示该节点的主机名，第二行表示该节点的 IP 地址，之后各行表示该节点上部署的组件。**注意，此图表示部署状态而非运行状态。比如，本案例中，flink 只需要部署在一个节点上，分布式作业所需资源由 yarn 统一调度，以 yarn session 模式提交作业时，flink 会由 yarn 创建分布式集群来运行作业。**

所有节点部署用户名均为 `hadoop`.

## 系统构建说明

### 构建 web 应用

本节操作均在 `web01` 上进行。

Web 应用详细代码参见 `webapp` 子目录，使用的主要技术栈如下：

* 程序语言：Python 3.10；
* Python 发行版管理：[pyenv](https://github.com/pyenv/pyenv)；
* 工程管理：[poetry](https://github.com/python-poetry/poetry)；
* 中文分词：[jieba](https://github.com/fxsjy/jieba)；
* Web 框架：[FastAPI](https://github.com/tiangolo/fastapi)；
* WSGI 服务器：[Gunicorn](https://github.com/benoitc/gunicorn)；
* 反向代理服务器：[Nginx](https://nginx.org/en/).

本节不对 pyenv, poetry 等管理类工具进行说明，也不对代码进行讲解，仅说明如何利用提供的代码构建实际可运行的 Web 应用。读者可结合各工具的官方文档自行阅读工程代码。

在 `web01` 上建立 `~/Projects/comprehensive-case-study` 目录：

```bash
mkdir -p ~/Projects/comprehensive-case-study
```

将 `webapp` 子目录复制到 `web01` 的 `~/Projects/comprehensive-case-study` 目录下，以下操作在 `web01` 上的 `~/Projects/comprehensive-case-study/webapp` 目录下进行。

部署 web 应用所需的 python 虚拟环境：

```bash
poetry install
```

运行 web 应用：

```bash
poetry run gunicorn main:app -c deploy/gunicorn.py
```

执行以下命令，测试是否运行成功：

```bash
curl -X 'POST' \
  'http://127.0.0.1:8000/' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "raw": "决心是成功的开始"
}'
```

如图所示，如果返回了分词结果，则表示 web 应用正常运行：

![gunicorn_run_test](./image/gunicorn_run_test.png)

安装 nginx 反向代理服务器：

```bash
sudo apt install nginx-full
```

调整 nginx 日志格式，加入记录请求处理时间的 `request_time` 字段：

```bash

```diff
--- /etc/nginx/nginx.conf.bak   2023-07-25 03:28:20.588029404 +0800
+++ /etc/nginx/nginx.conf       2023-07-23 18:49:29.506105138 +0800
@@ -35,8 +35,10 @@
        ##
        # Logging Settings
        ##
-
-       access_log /var/log/nginx/access.log;
+       log_format custom '$remote_addr - $remote_user [$time_local] '
+               '"$request" $status $body_bytes_sent $request_time '
+               '"$http_referer" "$http_user_agent"';
+       access_log /var/log/nginx/access.log custom;
        error_log /var/log/nginx/error.log;

        ##
```

为 web 应用设置 nginx 反向代理，新建 `/etc/nginx/conf.d/comprehensive-case-webapp.conf` 文件，内容为：

```text
server {
    listen 80;
    server_name localhost web01 192.168.122.180 127.0.0.1;
    location / {
        proxy_pass http://127.0.0.1:8000;
        proxy_set_header Host $host;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

执行以下命令，测试 nginx 反向代理是否成功：

```bash
curl -X 'POST' \
  'http://web01' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "raw": "决心是成功的开始"
}'
```

如图所示，如果返回了分词结果，则表示 nginx 反射代理正常运行：

![nginx_run_test](./image/nginx_run_test.png)

### 执行性能测试

本节操作均在 `web01` 上进行。性能测试可以随时执行。在日志存储和分析功能构建完毕之后再进行性能测试，可以实时观测日志的存储和分析结果。

模拟用户行为的代码参见 `webapp/tests/locustfile.py`，单个用户行为定义为：

1. 从 1 到 100 中随机生成一个正整数，记为 `num`；
2. 从 `webapp/data/sentences.txt` 中随机选取一行文本，发送给 web 应用，获取分词结果；
3. 将步骤 2 执行 `num` 次。

`webapp/data/sentences.txt` 数据抽取自[联合国平行语料库](https://conferences.unite.un.org/uncorpus/Home/Index/zh)中的中文语料。

启动 web 应用性能测试：

```bash
cd ~/Projects/comprehensive-case-study/webapp
nohup poetry run locust --headless \
                  --only-summary \
                  --host http://web01 \
                  --users 100 \
                  --spawn-rate 5 \
                  --run-time 10m \
                  --locustfile ./tests/locustfile.py &
```

### 构建日志采集和存储功能

日志存储功能由 hadoop HDFS, kafka 和 flume 实现。Flume 采集 `web01` 上的 nginx 日志 `/var/log/nginx/access.log`，并通过 kafka 提供的可靠传输将其存储到 HDFS 上。

Hadoop 集群的搭建可参考本书附录，本节不再对其做详细说明，所有 hadoop 节点的 `HADOOP_HOME` 环境变量均为 `/usr/local/hadoop`，`PATH` 环境变量均包含 `/usr/local/hadoop/bin`.

#### 搭建 kafka 集群

本节以 kraft 模式搭建 kafka 集群，无需 zookeeper. Kafka 集群部置在节点 `hadoop02`, `hadoop03`, `hadoop04` 上。

如果不做特别说明，以下操作需要在 `hadoop02`, `hadoop03`, `hadoop04` 三个节点上分别进行。

下载安装：

```bash
cd
wget -c https://archive.apache.org/dist/kafka/3.4.0/kafka_2.13-3.4.0.tgz
tar xf kafka_2.13-3.4.0.tgz
sudo mv kafka_2.13-3.4.0 /usr/local/kafka
```

添加 `/usr/local/kafka/bin` 目录到 `PATH` 环境变量：

```bash
sudo sed -i 's/"$/:\/usr\/local\/kafka\/bin"/' /etc/environment
```

退出登录：

```bash
logout
```

重新登录后执行以下命令测试 `PATH` 设置是否成功：

```bash
which kafka-server-start.sh
```

如果打印出 `kafka-server-start.sh` 的绝对路径 `/usr/local/kafka/bin/kafka-server-start.sh`，则表示设置成功。

备份原始配置文件：

```bash
cd /usr/local/kafka/config/kraft
cp server.properties server.properties.bak
```

新建目录用于存储 kafka 数据和日志：

```bash
sudo mkdir -p /hadoop/logs/kafka
sudo chown hadoop:hadoop /hadoop/logs/kafka
```

修改配置文件：

```diff
--- /usr/local/kafka/config/kraft/server.properties.bak 2023-05-12 21:10:31.990397975 +0800
+++ /usr/local/kafka/config/kraft/server.properties     2023-07-24 02:06:14.142444267 +0800
@@ -24,10 +24,10 @@
 process.roles=broker,controller

 # The node id associated with this instance's roles
-node.id=1
+node.id=2

 # The connect string for the controller quorum
-controller.quorum.voters=1@localhost:9093
+controller.quorum.voters=1@hadoop02:9093,2@hadoop03:9093,3@hadoop04:9093

 ############################# Socket Server Settings #############################

@@ -46,7 +46,7 @@

 # Listener name, hostname and port the broker will advertise to clients.
 # If not set, it uses the value for "listeners".
-advertised.listeners=PLAINTEXT://localhost:9092
+advertised.listeners=PLAINTEXT://hadoop03:9092

 # A comma-separated list of the names of the listeners used by the controller.
 # If no explicit mapping set in `listener.security.protocol.map`, default will be using PLAINTEXT protocol
@@ -75,12 +75,13 @@
 ############################# Log Basics #############################

 # A comma separated list of directories under which to store log files
-log.dirs=/tmp/kraft-combined-logs
+log.dirs=/hadoop/logs/kafka/data/kraft-combined-logs
+metadata.log.dir=/hadoop/logs/kafka/data/metadata-logs

 # The default number of log partitions per topic. More partitions allow greater
 # parallelism for consumption, but this will also result in more files across
 # the brokers.
-num.partitions=1
+num.partitions=3

 # The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
 # This value is recommended to be increased for installations with data dirs located in RAID array.
@@ -89,8 +90,8 @@
 ############################# Internal Topic Settings  #############################
 # The replication factor for the group metadata internal topics "__consumer_offsets" and "__transaction_state"
 # For anything other than development testing, a value greater than 1 is recommended to ensure availability such as 3
.
-offsets.topic.replication.factor=1
-transaction.state.log.replication.factor=1
+offsets.topic.replication.factor=3
+transaction.state.log.replication.factor=3
 transaction.state.log.min.isr=1

 ############################# Log Flush Policy #############################
```

* `node.id` 为 kafka 节点 ID，**每个节点独立编号，必须互不相同**；
* `controller.quorum.voters` 为 controller quorum 列表，所有 controller 都必须在此列出。列表成员格式为 `id@hostname:port`，`id` 为节点 ID，`hostname` 为主机名，`port` 为端口号，成员间以 `,` 分隔；
* `advertised.listeners` 为客户端提供服务的 url；
* `log.dirs` 为数据存储目录列表，如果指定了多个目录，目录之间以 `,` 分隔；
* `metadata.log.dir` 为元数据存储目录；
* `num.partitions` 为分区数。
* `offsets.topic.replication.factor` 和 `transaction.state.log.replication.factor` 设置为与 broker 数相匹配，以避免 group coordinator 不可用的问题；

执行以下命令，生成集群 ID，**仅在任一 kafka 节点上执行一次**，

```bash
kafka-storage.sh random-uuid
```

记录生成的 ID，后续操作会用到，本次得到的 ID 是 `3WR1JBtdRda7rfOUBWvAoA`。

格式化存储，**在每个 kafka 节点上执行**：

```bash
kafka-storage.sh format -t 3WR1JBtdRda7rfOUBWvAoA -c  /usr/local/kafka/config/kraft/server.properties
```

启动 kafka 集群，**在任一可通过 ssh 连接到每个 kafka 节点的机器上执行一次**：

```bash
ssh hadoop02 "LOG_DIR=/hadoop/logs/kafka/logs kafka-server-start.sh -daemon /usr/local/kafka/config/kraft/server.properties"
ssh hadoop03 "LOG_DIR=/hadoop/logs/kafka/logs kafka-server-start.sh -daemon /usr/local/kafka/config/kraft/server.properties"
ssh hadoop04 "LOG_DIR=/hadoop/logs/kafka/logs kafka-server-start.sh -daemon /usr/local/kafka/config/kraft/server.properties"
```

创建 topic，用于传输 nginx 日志，**仅在任一 kafka 节点上执行一次**:

```bash
kafka-topics.sh --bootstrap-server hadoop02:9092,hadoop03:9092,hadoop04:9092 --if-not-exists --create --topic web01-nginx-log
```

#### 使用 flume 采集并存储日志

本节在 `web01` 上部署 flume 以采集 nginx 日志，并通过 kafka 将其存储到 HDFS 上。

以下操作在 `web01` 上进行。

下载安装：

```bash
cd
wget -c https://dlcdn.apache.org/flume/1.11.0/apache-flume-1.11.0-bin.tar.gz
tar xf apache-flume-1.11.0-bin.tar.gz
sudo mv apache-flume-1.11.0-bin /usr/local/flume
```

添加 `/usr/local/flume/bin` 目录到 `PATH` 环境变量：

```bash
sudo sed -i 's/"$/:\/usr\/local\/flume\/bin"/' /etc/environment
```

退出登录：

```bash
logout
```

重新登录后执行以下命令测试 `PATH` 设置是否成功：

```bash
which flume-ng
```

如果打印出 `flume-ng` 的绝对路径 `/usr/local/flume/bin/flume-ng`，则表示设置成功。

Flume 存取 HDFS 需要依赖 hadoop 的 jar 包。将任一 hadoop 节点的 `HADOOP_HOME` 环境变量指示的目录复制为 `web01` 上的 `/usr/local/hadoop` 目录：

```bash
cd
scp -r hadoop04:/usr/local/hadoop .
sudo mv hadoop /usr/local
```

生成 flume 运行环境配置文件：

```bash
cp /usr/local/flume/conf/flume-env.sh.template /usr/local/flume/conf/flume-env.sh
```

修改 flume 运行环境配置文件：

```diff
--- /usr/local/flume/conf/flume-env.sh.template 2022-01-21 01:28:29.000000000 +0800
+++ /usr/local/flume/conf/flume-env.sh  2023-07-25 09:50:21.799484505 +0800
@@ -19,7 +19,14 @@

 # Enviroment variables can be set here.

-# export JAVA_HOME=/usr/lib/jvm/java-8-oracle
+export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
+
+export HADOOP_HOME=/usr/local/hadoop
+export HADOOP_COMMON_HOME=$HADOOP_HOME
+export HADOOP_HDFS_HOME=$HADOOP_HOME
+export HADOOP_MAPRED_HOME=$HADOOP_HOME
+export HADOOP_YARN_HOME=$HADOOP_HOME
+export HADOOP_CONF_DIR=${HADOOP_HOME}/etc/hadoop

 # Give Flume more memory and pre-allocate, enable remote monitoring via JMX
 # export JAVA_OPTS="-Xms100m -Xmx2000m -Dcom.sun.management.jmxremote"
```

建立 flume 日志采集与存储配置文件：

```bash
mkdir -p ~/Projects/comprehensive-case-study/flume
cd ~/Projects/comprehensive-case-study/flume
touch nginx-hdfs.conf
```

`nginx-hdfs.conf` 内容为：

```cnof
nginx-hdfs-agent.sources = nginx-log-source
nginx-hdfs-agent.sinks = hdfs-sink
nginx-hdfs-agent.channels = kafka-channel

nginx-hdfs-agent.sources.nginx-log-source.channels = kafka-channel
nginx-hdfs-agent.sources.nginx-log-source.type = exec
nginx-hdfs-agent.sources.nginx-log-source.command = tail -F /var/log/nginx/access.log

nginx-hdfs-agent.sinks.hdfs-sink.channel = kafka-channel
nginx-hdfs-agent.sinks.hdfs-sink.type = hdfs
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.path = hdfs://hadoop01:9000/flume/web01/nginx_log/%Y/%m/
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.filePrefix = nginx-log
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.rollInterval = 0
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.rollCount = 100000
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.rollSize = 0
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.writeFormat = Text
nginx-hdfs-agent.sinks.hdfs-sink.hdfs.useLocalTimeStamp = true

nginx-hdfs-agent.channels.kafka-channel.type = org.apache.flume.channel.kafka.KafkaChannel
nginx-hdfs-agent.channels.kafka-channel.kafka.bootstrap.servers = hadoop02:9092,hadoop03:9092,hadoop04:9092
nginx-hdfs-agent.channels.kafka-channel.kafka.topic = web01-nginx-log
nginx-hdfs-agent.channels.kafka-channel.kafka.consumer.group.id = web01.nginx.log.flume
```

该文件定义了名为 `nginx-hdfs-agent` 的 agent，该 agent 从 `nginx-log-source` 数据源中采集 nginx 日志，通过 `kafka-channel` 将日志传输到 `hdfs-sink` 上。

执行日志采集与存储：

```bash
flume-ng agent --name nginx-hdfs-agent \
               --conf /usr/local/flume/conf/ \
               --conf-file ~/Projects/comprehensive-case-study/flume/nginx-hdfs.conf \
               -Xmx1g
```

### 构建日志分析功能

系统以 flink 从 Kafka 中实时获取日志信息，计算服务表现，并将其存入 PostgreSQL 中。

#### Postgresql 安装设置

如果没有特别说明，本节操作均在 `dbb01` 上进行。

安装 PostgreSQL：

```bash
sudo apt install postgresql
```

系统需要使用特定用户 `log_analyzer` 在内网同一网段远程访问  `log_analysis`，为获得远程访问权限，做如下配置。

首先，修改监听地址，新建 `/etc/postgresql/14/main/conf.d/listen_all.conf` 文件：

```bash
sudo touch /etc/postgresql/14/main/conf.d/listen_all.conf
sudo chown postgres:postgres /etc/postgresql/14/main/conf.d/listen_all.conf
```

`/etc/postgresql/14/main/conf.d/listen_all.conf` 内容如下：

```conf
listen_addresses = '*'
```

然后，为 `log_analyzer` 用户添加在内网同一网段远程访问 `log_analysis` 数据库的权限，修改 `/etc/postgresql/14/main/pg_hba.conf` 文件：

```diff
--- /etc/postgresql/14/main/pg_hba.conf.bak     2023-07-25 01:36:20.154579148 +0800
+++ /etc/postgresql/14/main/pg_hba.conf 2023-07-25 01:36:41.918248629 +0800
@@ -102,3 +102,6 @@
 local   replication     all                                     peer
 host    replication     all             127.0.0.1/32            scram-sha-256
 host    replication     all             ::1/128                 scram-sha-256
+
+# Allow internal network access to database log_analysis for user log_analyzer
+host    log_analysis    log_analyzer    192.168.122.1/24        scram-sha-256
```

重启服务：

```bash
sudo service postgresql restart
```

最后新建 `log_analyzer` 用户和 `log_analysis` 数据库及表结构：

把 `log_analysis/postgresql` 子目录下的 `create_db.sh` 和 `log_analysis_db.sql` 文件复制到 `dbb01` 的 `~/db_scripts` 下，在 `db01` 上执行以下命令：

```bash
cd ~/db_scripts
chmod a+x create_db.sh
./create_db.sh
```

`lazy_analyzer` 用户密码为 `log_analyzer_pass`，此时，已经可以通过 `log_analyzer` 用户在内网同一网段远程访问 `log_analysis` 数据库了。

`log_analysis` 包含一张名为 `web01_nginx_log_analysis` 的表，结构参见 `log_analysis_db.sql` 中的建表语句（无需单独执行，表结构已经由上面的命令创建完毕）：

```sql
CREATE TABLE IF NOT EXISTS web01_nginx_log_analysis
(
  id serial,
  start_timestamp timestamp with time zone NOT NULL,
  end_timestamp timestamp with time zone NOT NULL,
  request_count integer NOT NULL,
  min_request_time double precision NOT NULL,
  max_request_time double precision NOT NULL,
  mean_request_time double precision NOT NULL,
  std_dev_request_time double precision NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT web01_nginx_log_analysis_duration_unique UNIQUE (start_timestamp, end_timestamp)
);
```

登录 `web01` 测试远程数据库访问是否正常，执行以下命令：

```bash
psql -U log_analyzer -h db01 -p 5432 -d log_analysis
```

输入密码 `log_analyzer_pass`，成功登录数据库即为正常，如图所示：

![postgresql_remote_access_test](./image/postgresql_remote_access_test.png)

输入 `\q` 后按回车键可退出登录。

#### 编写 flink 日志分析程序

详细代码参见 `log_analysis/flink/comprehensive-case-flink` 子目录，使用的主要技术栈如下：

* 程序语言：Java 11；
* 工程管理：Maven；
* 流式计算框架：Apache flink.

以下仅对需要注意的部分细节作简要说明：

* 使用 `maven-shade-plugin` 将依赖打包到最终 jar 包中以简化部署；
* Flink 无法直接序列化 `ZonedDateTime` 类型，可显式指定 kyro 相关的序列化类，参见 `LogHandler.java`：

```java
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
env.getConfig().registerTypeWithKryoSerializer(ZonedDateTime.class, TimeSerializers.ZonedDateTimeSerializer.class);
```

* 使用 lombok 注解时需要考虑 flink 的序列化机制，参见 `LogEntry.java` 与 `LogAnalysisResult.java`.

#### 生成 flink 程序 jar 包

在 `log_analysis/flink/comprehensive-case-flink` 子目录下执行以下命令，生成 jar 包：

```bash
maven clean package
```

jar 包生成在 `log_analysis/flink/comprehensive-case-flink/target` 目录下，名为 `log-analysis-0.1.jar`，将其复制到 `hadoop04` 的 `~` 目录下以供后续使用。

#### 部署 flink

Flink 部署在 `hadoop04` 上，如果没有特别说明，本节操作均在 `hadoop04` 上进行。

下载安装：

```bash
cd
wget -c https://dlcdn.apache.org/flink/flink-1.17.1/flink-1.17.1-bin-scala_2.12.tgz
tar xf flink-1.17.1-bin-scala_2.12.tgz
sudo mv flink-1.17.1 /usr/local/flink
```

配置 flink 运行环境，新建 `/etc/profile.d/flink-env.sh` 文件，其内容为：

```bash
export HADOOP_CLASSPATH=$(hadoop classpath)

export FLINK_HOME=/usr/local/flink
```

执行 `logout` 命令后重新登录以使设置生效。

#### 提交 flink 日志分析作业

本节操作在 `hadoop04` 上进行。

```bash
cd $FLINK_HOME
./bin/yarn-session.sh --detached
./bin/flink run ~/log-analysis-0.1.jar
```
