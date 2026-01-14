# 第七章 分布式计算框架MapReduce

MapReduce将一个大规模数据集拆分为若干子数据集；然后，在Map阶段，每个节点对其分配的数据子集进行处理，并以`<Key, Value>`键值对的形式输出到本地磁盘。最后，在Reduce阶段，系统读取Map阶段的输出的键值对，进行聚合处理，并将最终结果输出到HDFS上，示例如下：

![MapReduce原理示例](./assets/MapReduce原理示例.jpg)

## 1 工作流程

### Map任务特点

- Map任务的数量影响数据处理效率
- Map任务具有数据本地化优势：指尽量在存储有数据的节点上执行计算任务，这样可以避免在集群中跨节点传输大量数据，减少网络开销，提升计算效率
- Map任务的输出结果：Map任务的输出结果是一系列`<Key, Value>`键值对，这些`<Key, Value>`键值对**存储在执行Map任务的节点的本地磁盘上**
- Map任务失败重启：如果Map任务在执行过程中失败，那么Hadoop将会在另一台空闲的节点重新启动Map任务再次执行上次失败的任务

### Reduce任务特点

- Reduce任务**不具备**本地化优势：由于Reduce任务和Map任务通常在不同的节点执行，Map阶段的输出结果需要通过网络跨节点传输到Reduce端归并汇总
- Reduce任务的输出结果：为提高数据的可靠性和容错能力，Reduce任务的输出结果不仅会保存一份在执行Reduce任务的节点上，还会将此文件的副本通过网络发送到其他节点上备份

### Map-Reduce阶段数据处理过程

![Map-Reduce阶段数据处理过程](./assets/Map-Reduce阶段数据处理过程.jpg)

### Combiner类

在MapReduce工作流程中，Combiner类是一个可选的中间处理步骤，位于Map任务之后，对Map任务的输出结果进行局部聚合，减少从Map节点到Reduce节点的数据传输量,提高处理效率

---



## 2 执行步骤

**输入阶段：**

1. 定位输入数据的存储路径S
2. 循环遍历存储路径下的每一个文件$f_{i=1,2,\dots,n}$
3. 对每个文件进行分片，默认情况下分片大小为HDFS的块大小`Block_size`。注意这里仅记录分片的元数据信息，并没有真正对数据进行分块

**Map阶段：**

1. 向YARN申请资源，根据分片数开启相应的Map任务个数$N_m$
2. 每个Map任务处理相应分片的数据，默认按行读取，送入Map函数中处理，Map函数输出一系列`<Key, Value>`键值对作为中间结果

**Shuffle阶段：**

1. 环形缓冲区收集Map函数输出的`<Key, Value>`键值对，达到一定阈值时溢写到磁盘
2. 溢写文件达到一定数量时，将溢写文件合并为一个大文件$F_{i=1,2,\dots,j}$
3. Reduce任务复制Map阶段的数据

**Reduce阶段：**

1. Reduce任务对数据进行归并排序
2. 执行用户编写的Reduce函数逻辑

**输出阶段：**

1. 对Reduce函数的输出结果进行`OutputFormat`验证，最后将输出结果写到HDFS

![执行步骤](./assets/执行步骤.jpg)

---



## 3 数据类型与格式

### 数据类型

|  函数  |       输入       |       输出       |
| :----: | :--------------: | :--------------: |
|  Map   |    `<K1, V1>`    | `List(<K2, V2>)` |
| Reduce | `<K2, List(V2)>` |    `<K3, V3>`    |

Map函数输出的键值类型（K2和V2）和Reduce函数输入的键值类型（K2和V2）必须相同，其他键值类型可以不同

- `<K1, V1>`键值对数据类型由输入格式`InputFromat`进行设置，在`InputFromat`中，默认的输入格式是`TextInputFromat`，键类型是`LongWritable`（偏移量），值类型是`Text`（一行内容）
- `<K2, V2>`键值对数据类型由用户自定义，用户可以通过调用`job.setMapOutputKClass()`方法和`job.setMapOutputVClass()`方法分别进行设置
- `<K3, V3>`键值对数据类型由输出格式`OutputFormat`决定。`OutputFormat`默认的输出格式是`TextOutputFormat`，键值是任意类型，可以通过调用`job.setOutputKClass()`方法和`job.setOutputVClass()`方法设置最终输出的键值类型

### 输入格式

在运行MapReduce程序时，输入数据格式主要为文本文件、二进制格式文件、数据库表等。而Map函数的输入要求是`<Key, Value>`键值对，因此输入数据并不能直接交给Map函数处理，在此之前需要先使用`InputFormat`类把输入数据转换为Map函数可以处理的格式。

**`InputFormat`类在 MapReduce中主要负责两个核心功能：**

1. 根据设定的分片大小和文件的切分规则，逻辑上对输入数据进行分片，并确定分片数量，其中每个输入分片对应一个由单个Map任务处理的数据块
2. `InputFormat`类负责将每个输入分片进一步分解为若干个`<Key, Value>`键值对，这些键值对是送入 Map 函数中进行处理的记录

![InputFormat类的部分层次结构](./assets/InputFormat类的部分层次结构.jpg)

### 输出格式

在MapReduce程序中，输出数据格式可以是文本文件、二进制格式文件、数据库表等。因此，MapReduce提供了`OutputFormat`类控制输出数据的输出格式

![OutputFormat类的部分层次结构](./assets/OutputFormat类的部分层次结构.jpg)

**常用OutoutFormat实现类：**

| 输出格式                  | 描述                                         | 示例             |
| ------------------------- | -------------------------------------------- | ---------------- |
| `TextOutputFormat`        | 默认输出格式，以`“Key \t Value”`的格式输出行 | hello 2          |
| `SequenceFileInputFormat` | 输出二进制文件                               | key-0 => value-0 |
| `DBOutputFormat`          | 将输出数据存储到数据库中                     |                  |

