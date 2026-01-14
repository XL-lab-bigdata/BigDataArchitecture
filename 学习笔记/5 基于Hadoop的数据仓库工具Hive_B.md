## Hive架构概述

- **Hive核心组件**：
  - 用户接口（UI）
  - 跨语言服务
  - 驱动程序（Driver）
  - 元数据存储（Metastore）
- **用户接口**：
  - 提供用户与系统交互，包括命令行界面（CLI）、Web接口、JDBC/ODBC接口等。
  - CLI适合简单操作和快速验证。
  - Web接口提供直观的Web操作控制台。
  - JDBC/ODBC适合复杂应用和自动化任务。
- **跨语言服务**：
  - 使用Apache Thrift作为跨语言服务框架。
  - 支持多种编程语言（Java、C++、Python）通过API与Hive通信。
- **驱动程序**：
  - 包含解析器、编译器、优化器、执行器。
  - 处理HQL查询语句，包括词法分析、语法分析、编译、优化和生成查询计划。
- **元数据存储**：
  - 存储表名、表结构、字段名、字段类型等元数据。
  - 默认使用关系型数据库（如Derby或MySQL）管理元数据。
  - 注意：生产环境中通常使用MySQL代替Derby。

## Hive组件详解

- **Apache Thrift**：
  - 开源跨语言服务开发框架。
  - 根据IDL生成API代码，实现不同编程语言间的无缝通信。
  - 支持多种编程语言，通过引入Thrift客户端库与Hive交互。
- **驱动程序详解**：
  - 包括解析器、编译器、优化器、执行器。
  - 协同工作完成HQL查询语句处理：词法分析、语法分析、编译、优化、生成查询计划。
- **元数据存储**：
  - 存储描述数据特征和属性。
  - 使用关系型数据库（如Derby或MySQL）维护元数据。
  - 注意：生产环境中通常使用MySQL代替Derby。

## Hive工作流程及与Hadoop交互

**概述：** 这几页内容详细介绍了Hive在Hadoop生态系统中的作用、工作流程以及与Hadoop各组件的交互方式。Hive作为一个数据仓库基础设施，提供了一种更直观和灵活的数据处理方式，使用户可以通过类SQL语言（HQL）在Hadoop上执行各种数据操作。

**Hive架构与组件：**

- Hive包含四个核心组件：用户接口（UI）、跨语言服务、驱动程序（Driver）和元数据存储（Metastore）。
- 用户接口：包括命令行界面（CLI）、Web接口、JDBC/ODBC接口，用于用户与系统的交互。
- 跨语言服务：使用Apache Thrift支持多种编程语言与Hive通信。
- 驱动程序：包括解析器、编译器、优化器和执行器，协同工作完成HQL查询语句的处理。
- 元数据存储：存储表名、表结构、字段名、字段类型等元数据，通常使用关系型数据库（如Derby或MySQL）管理。

**Hive工作流程及与Hadoop交互：**

1. 用户通过用户接口向驱动程序提交查询操作。
2. 驱动程序处理查询操作，包括：
   - 解析器将查询操作转换为抽象语法树（AST）。
   - 编译器将AST转换为逻辑查询计划。
   - 优化器优化逻辑查询计划并转换为物理查询计划。
   - 执行器对物理计划进行优化并提交给执行引擎。
3. 任务生成后，驱动程序将其提交给资源管理器YARN，YARN在Hadoop集群的各个节点上调度这些作业的执行。
4. 执行完成后，驱动程序获取查询结果并将其返回给用户。

## Hive数据类型

**概述：** Hive支持多种数据类型，包括基本数据类型和复杂的集合数据类型，以适应不同类型的数据处理需求。

**基本数据类型：**

- **整数类型**：Tinyint、Smallint、Int/Integer、Bigint，分别对应1、2、4、8字节的有符号整数。
- **浮点数类型**：Float（单精度）、Double（双精度）。
- **定点数**：Decimal，用户自定义精度和标度。
- **字符串类型**：String（指定字符集）、Varchar（最大长度限制）、Char（固定长度）。
- **日期/时间类型**：Timestamp（时间戳）、Date（日期）、Interval（时间间隔）、Boolean（True/False）。
- **其他类型**：Binary（二进制数据类型，用于存储图像、音频、视频等多媒体数据）。

**示例**：

- Tinyint：45Y（1字节，范围从-2^7到2^7-1）
- Smallint：12S（2字节，范围从-2^15到2^15-1）
- Int：10（4字节，范围从-2^31到2^31-1）
- Bigint：244L（8字节，范围从-2^63到2^63-1）
- Float：3.14159
- Double：3.14159
- Decimal(4,2)：99.99（指定小数位数为2）
- String："today", "long time no see"
- Timestamp：'2022-10-01 23:59:59.999'
- Date：'2016-07-03'
- Interval：1'DAY
- Binary：10111001（二进制数据）

**集合数据类型：**

- **STRUCT**：封装一组任意基本数据类型的命名字段。
  - 示例：`person STRUCT<name: STRING, age: INT, address: ARRAY<STRING>>`
- **MAP**：键值对元组的集合，格式为“键→值”。
  - 示例：`properties MAP<STRING, STRING>`
- **ARRAY**：具有相同数据类型的变量集合，元素有下标编号。
  - 示例：`hobbies ARRAY<STRING>`

**注释**：

- 一个字节由8个二进制位组成，可表示2^8个不同的数值。在有符号整数的情况下，通常将一半的值用于表示负数，因此一个字节的数据范围通常是从-128到127。

## Hive数据库管理

**概述：** Hive支持对数据库的创建、删除、重命名、切换等操作，提供了灵活的数据库管理功能。

**新建数据库：**

- 在Hive中创建数据库实际上是在HDFS中创建一个新目录，用于存储该数据库的所有表和命名空间。
- 语法：`CREATE (DATABASE|SCHEMA) [IF NOT EXISTS] database_name [COMMENT database_comment] [LOCATION hdfs_path] [WITH DBPROPERTIES (property_name=value,...)];`
  - 参数说明：
    - `database_name`：数据库名称。
    - `IF NOT EXISTS`：判断数据库是否存在。
    - `COMMENT database_comment`：数据库描述。
    - `LOCATION hdfs_path`：数据库在HDFS上的存储位置。
    - `WITH DBPROPERTIES`：设置数据库属性。

**查询数据库：**

- 使用`SHOW DATABASES [LIKE 'pattern']`：列出匹配特定模式的数据库。
- 示例：`SHOW DATABASES LIKE 'U*';`（列出名称以U开头的数据库）

**使用数据库：**

- `USE database_name;`：切换当前工作的数据库。

**查看数据库信息：**

- `DESC|DESCRIBE DATABASE [EXTENDED] database_name;`：查看数据库基本信息。
- `EXTENDED`：显示更详细的信息，包括所有者、创建时间、参数设置等。

**修改数据库：**

- `ALTER (DATABASE|SCHEMA) database_name SET DBPROPERTIES (property_name=value,...);`：修改数据库属性。
- `ALTER (DATABASE|SCHEMA) database_name SET OWNER [USER|ROLE] user_or_role;`：修改数据库所有者属性。
- 示例：修改数据库描述和所有者。

**删除数据库：**

- `DROP (DATABASE|SCHEMA) [IF EXISTS] database_name [RESTRICT|CASCADE];`
- 参数说明：
  - `IF EXISTS`：判断数据库是否存在。
  - `RESTRICT`：如果存在表，无法删除。
  - `CASCADE`：即使存在表，也删除数据库和表。

**示例：**

- 创建数据库：`CREATE DATABASE IF NOT EXISTS UserBehavior_DB;`
- 查询数据库：`SHOW DATABASES LIKE 'U*';`
- 使用数据库：`USE UserBehavior_DB;`
- 查看数据库信息：`DESCRIBE DATABASE UserBehavior_DB;`
- 修改数据库：`ALTER DATABASE UserBehavior_DB SET DBPROPERTIES ('description'='Database for storing user behavior data, including user interactions, posts, comments, etc., and supporting data analysis and query operations.');`
- 删除数据库：`DROP DATABASE IF EXISTS UserBehavior_DB;`

**总结：** 通过这些操作，用户可以灵活地管理Hive中的数据库，包括创建、查询、使用、查看信息、修改和删除等。这些功能对于维护和优化数据库结构非常重要。

## Hive表管理

**概述：** Hive中的表是数据组织和存储的基本单位，类似于关系数据库中的表格。Hive支持两种主要的表类型：内部表（Managed table）和外部表（External table）。这两种表在元数据的组织方式上相同，但在数据存储方式上有所不同。

**内部表（Managed table）：**

- Hive默认创建时，Hive会在HDFS中配置的仓库目录下为表自动分配存储空间。
- Hive完全控制内部表数据的存储、管理和清理。
- 删除内部表时，Hive会删除表的元数据和所有数据文件。

**外部表（External table）：**

- Hive只记录表的元数据，不负责实际的数据存储。
- 数据存储位置由建表语句中的LOCATION指定，未指定时，Hive会在HDFS中配置的仓库目录下创建与外部表名同名的目录。
- 删除外部表时，Hive只删除表的元数据，不影响实际的数据文件。

**创建表：**

- 使用`CREATE TABLE`语句创建新表，包含多个可选参数，允许定制表的各种属性。
- 语法：`CREATE [TEMPORARY|EXTERNAL] TABLE [IF NOT EXISTS] [db_name.]table_name [(col_name data_type [COMMENT column_comment],...)] [COMMENT table_comment] [ROW FORMAT row_format] [STORED AS file_format] [LOCATION hdfs_path] [TBLPROPERTIES (property_name=property_value,...)] [AS select_statement] [LIKE existing_table_or_view_name]`
- 示例：创建表`purchase_records`，包含用户ID、商品尺寸、价格、性别、类别和颜色字段，行格式为DELIMITED，字段之间用逗号分隔，行之间用换行符分隔，数据以TEXTFILE格式存储。

**修改表：**

- 使用`ALTER TABLE`语句修改表结构，包括添加列、重命名表等。
- 语法：`ALTER TABLE tablename [ADD|REPLACE COLUMNS(col_name data_type [COMMENT col_comment],...)][CHANGE COLUMN col_old_name col_new_name col_data_type [COMMENT col_comment]][RENAME TO new_tablename][PARTITIONED BY(col_name data_type [COMMENT col_comment],...)][CLUSTERED BY(col_name, col_name,...)[SORTED BY(col_name [ASC|DESC],...)] INTO num_buckets BUCKETS][SKEWED BY(col_name1, col_name2,...) ON((col_name1_value, col_name2_value,...),(col_name1_value, col_name2_value,...),...)[STORED AS DIRECTORIES][TBLPROPERTIES (property_name=value,...)];`
- 示例：修改表`purchase_records`，添加新列`brand`，修改列名`size`为`product_size`。

**删除表：**

- 使用`DROP TABLE`语句删除表的元数据和数据。
- 语法：`DROP TABLE [IF EXISTS] tablename [PURGE];`
- `PURGE`参数：指定时，表数据不会放入回收站，后续无法通过回收站恢复表数据；反之，表数据会放入回收站。
- 示例：删除表`purchase_records`。

**总结：** 通过这些操作，用户可以灵活地管理Hive中的表，包括创建、查询、使用、查看信息、修改和删除等。这些功能对于维护和优化数据库结构非常重要。Hive提供了丰富的表管理功能，使得用户能够根据需要对表进行各种操作。

## Hive视图管理

**概述：** Hive视图管理允许用户基于现有表或其他视图创建虚拟表，用于管理和组织数据。视图本身不存储数据，而是通过查询逻辑进行操作。

**创建视图：**

- 使用`CREATE VIEW`语句创建视图。
- 语法：`CREATE VIEW [IF NOT EXISTS] [db_name.]view_name [(column_list)] AS SELECT column1, column2, ... FROM tablename WHERE condition;`
- `view_name`：指定视图名称。
- `column_list`：列出视图中需要包含的列名，若不提供，则包含SELECT语句中选择的所有列。
- `WHERE condition`：指定筛选条件，用于过滤视图中的数据。

**重命名视图：**

- 使用`ALTER VIEW`语句更改视图名称。
- 语法：`ALTER VIEW old_view_name RENAME TO new_view_name;`

**查看视图：**

- `DESCRIBE your_view_name;`：查看某个视图。
- `SHOW VIEWS;`：查看所有视图。
- `SHOW VIEWS IN your_database_name;`：查看某个特定数据库中的视图信息。

**删除视图：**

- 使用`DROP VIEW`语句删除视图。
- 语法：`DROP VIEW [IF EXISTS] <view_name> [RESTRICT|CASCADE];`
- `RESTRICT`：存在依赖时不允许删除。
- `CASCADE`：删除视图及其依赖对象。

## Hive 索引管理

#### 索引概述

在Hive中，索引是一种可选的优化机制，通过在表的某些列上创建一个或多个索引，可以更快地定位和访问符合条件的行，从而提高查询效率。与传统关系型数据库不同，Hive中创建的索引是只读的，这意味着一旦创建了索引，则只能查询索引而不能修改索引。此外，对表数据进行更新和删除操作时，索引会带来额外的开销。因此，在创建索引时需要谨慎选择索引的列、类型和数量，以避免不必要的索引，从而提高性能并减少存储成本。

#### 创建索引

使用`CREATE INDEX`语句用于创建索引，具体语法如下：

- **语法：**

  sql复制

  ```sql
  CREATE [UNIQUE] INDEX index_name ON TABLE tablename (column1, column2, ...) AS index_type [WITH DEFERRED REBUILD];
  ```

  - `index_name`：指定索引名称。
  - `tablename`：指创建索引的表名。
  - `column_name1, column_name2`：指创建索引的列名。
  - `UNIQUE`：关键字表示创建唯一索引，省略则创建非唯一索引。
  - `WITH DEFERRED REBUILD`：表示创建索引时延迟重建索引。

#### 查看表索引信息

使用`SHOW INDEXES`语句用于查看某特定表的索引信息，后接表名，用`ON`关键字连接，具体语法如下：

- **语法：**

  sql复制

  ```sql
  SHOW INDEXES ON tablename;
  ```

#### 删除索引

使用`DROP INDEX`语句用于删除某特定表上的指定索引，具体语法如下：

- **语法：**

  sql复制

  ```sql
  DROP INDEX index_name ON TABLE tablename;
  ```

#### 总结

Hive的索引管理功能包括创建、查看和删除索引。创建索引时，可以选择创建唯一索引或非唯一索引，并可以选择延迟重建索引以减少初始创建时的开销。查看索引信息可以帮助用户了解表上的索引情况，而删除索引则可以用于移除不再需要的索引，从而节省存储空间和减少维护开销。通过这些操作，用户可以有效地管理Hive中的索引，以优化查询性能和资源使用。

## Hive 分区与分区表

#### 分区概述

在Hive中，分区是将表中数据按照某一列或多列的值划分成若干个逻辑分区的过程。分区键是用于划分数据并创建逻辑分区的列。每个逻辑分区对应HDFS中的一个独立文件夹，该文件夹下是该分区所有的数据文件。分区表是划分了逻辑分区的表，可以在创建新表时通过指定分区键创建，也可以通过使用`ALTER TABLE`命令来给未分区表添加分区列，从而将其转换为分区表。

#### 创建分区表

创建分区表的语法与创建表的语法类似，主要差别在于后者添加了`PARTITIONED BY`语句，用于定义分区键，具体语法如下：

- **语法：**

  sql复制

  ```sql
  CREATE [EXTERNAL] TABLE [IF NOT EXISTS] tablename
  (
    column1 data_type [COMMENT column_comment],
    column2 data_type [COMMENT column_comment],
    ...
  )
  [COMMENT table_comment]
  PARTITIONED BY (partition_column1 data_type [COMMENT column_comment], partition_column2 data_type [COMMENT column_comment], ...)
  [ROW FORMAT row_format]
  [STORED AS file_format]
  [LOCATION hdfs_path]
  ```

- **示例：** 创建名为“purchase_records_partition”的分区表，分区键为“商品对应性别”“商品类别”“商品颜色”。

#### 增加分区

在Hive中，可以通过执行以下命令将`partition_column`的值等于`partition_value`的分区添加到`tablename`表中。

- **语法：**

  sql复制

  ```sql
  ALTER TABLE tablename ADD [IF NOT EXISTS] PARTITION (partition_column=partition_value, ...) [LOCATION 'location'];
  ```

- **示例：** 向`purchase_records_partition`表中添加一个新的分区，对应性别为男性、商品类别为配饰、颜色为黄色。

#### 删除分区

若不再需要表中的某些分区，或分区需要进行更新时，用户可以执行以下命令以删除这些分区。

- **语法：**

  sql复制

  ```sql
  ALTER TABLE tablename DROP PARTITION (partition_column='partition_value');
  ```

- **示例：** 删除`purchase_records_partition`表中对应性别为女性、商品类别为背心、颜色为黑色的分区。

#### 查看分区表的所有分区

在Hive中，可以通过执行以下命令查看指定分区表中的所有分区，以了解表中数据的分布情况。

- **语法：**

  sql复制

  ```sql
  SHOW PARTITIONS tablename;
  ```

- **示例：** 查看`purchase_records_partition`表的所有分区。

#### 总结

分区是Hive中重要的数据组织方式，通过分区可以显著提高查询效率和数据检索速度。用户可以通过创建、增加、删除和查看分区来灵活地管理Hive表中的数据。这些操作对于维护和优化数据库结构非常重要，使得用户能够根据需要对表进行各种操作。

## Hive 分桶与分桶表

#### 分桶概述

分桶是一种在Hive中用于优化数据存储和查询性能的技术。与分区不同，分桶不是基于存储路径的组织方式，而是基于数据文件内容的逻辑分组。分桶是根据指定的某一列将数据文件划分为固定数量桶的技术，每个桶包含一部分数据，并存储在表的文件中。通过将数据文件划分到不同的桶中，可以改善数据访问的局部性，并进一步优化查询性能。

#### 创建分桶表

创建分桶表的语法与创建表的语法类似，主要差别在于后者添加了`CLUSTERED BY`语句，用于指定分桶表的分桶键和分桶数目。分桶表只能根据单列进行分桶，具体语法如下：

- **语法：**

  sql复制

  ```sql
  CREATE [EXTERNAL] TABLE [IF NOT EXISTS] tablename
  (
    column1 data_type [COMMENT column_comment],
    column2 data_type [COMMENT column_comment],
    ...
  )
  CLUSTERED BY (column1) SORTED BY (column1|column2 ASC|DESC) INTO num_buckets BUCKETS
  [COMMENT table_comment]
  [PARTITIONED BY (partition_column1 data_type [COMMENT column_comment], partition_column2 data_type [COMMENT column_comment], ...)]
  [ROW FORMAT row_format]
  [STORED AS file_format]
  [LOCATION hdfs_path]
  [TBLPROPERTIES (property_name=value, ...)]
  ```

  - `CLUSTERED BY`语句指定列作为分桶键，并按照该列或者另一列进行升序（ASC）或降序（DESC）排列，桶个数为指定的数值（个数可自行设置）。

#### 示例：创建分桶表

针对用户交易数据，若想根据商品价格`price`进行数据划分，可以创建名为“purchase_records_bucket”的分桶表，分区键为“商品对应性别”“商品类别”“商品颜色”，分桶键为“商品价格”，桶数量为50。

- **命令：**

  sql复制

  ```sql
  CREATE TABLE purchase_records_bucket (user_id string, brand string, size string, price float)
  PARTITIONED BY (gender string, category string, color string)
  CLUSTERED BY (price) INTO 50 BUCKETS;
  ```

执行该命令后，不同分区中的数据会根据商品价格进行哈希取模并分配进各个桶中。

#### 总结

分桶是Hive中用于优化数据存储和查询性能的重要技术。通过将数据文件划分到不同的桶中，可以改善数据访问的局部性，并进一步优化查询性能。分桶表的创建语法与创建表的语法类似，主要差别在于添加了`CLUSTERED BY`语句，用于指定分桶键和分桶数目。分桶表可以与分区表同时使用，以实现更细粒度的数据组织和优化。

## Hive 数据操作语言

#### 加载文件

加载文件语句用于将本地磁盘或者HDFS文件中的结构化数据加载到指定的Hive数据表或分区中。

- **语法：**

  sql复制

  ```sql
  LOAD DATA [LOCAL] INPATH 'filepath' [OVERWRITE] INTO TABLE tablename [PARTITION (partcol1=val1, partcol2=val2 ...)]
  ```

  - `LOCAL`关键字代表从本地文件系统加载文件，省略则代表从HDFS上加载文件。
  - `filepath`可以是绝对路径或相对路径，从HDFS加载文件时，`filepath`须为文件完整的URL地址。
  - `OVERWRITE`关键字用于指定数据的加载方式是覆盖还是追加。

#### 查询插入

数据的查询插入可分为单表插入、多表插入和本地插入。

##### 单表插入

单表插入指将查询的单个结果集插入到一张表中。

- **语法：**

  sql复制

  ```sql
  INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2...)] [IF NOT EXISTS] select_statement1 FROM from_statement;
  ```

  - `INSERT OVERWRITE TABLE`语句表示覆盖目标表中的数据，若不希望覆盖原有数据，可以使用`INSERT INTO TABLE`语句进行插入操作。
  - `PARTITION (partcol1[=val1], partcol2[=val2]...)`语句用于将数据插入分区表的指定分区。
  - `select_statement1`中，可以编写适当的查询语句来选择要插入的数据。
  - `from_statement`则用于指定数据来源，可以是单个表、子查询、JOIN操作等。

##### 多表插入

多表插入则指将查询的多个结果集插入多张表中。

- **语法：**

  sql复制

  ```sql
  FROM from_statement
  INSERT OVERWRITE TABLE tablename1 [PARTITION (partcol1=val1, partcol2=val2...) [IF NOT EXISTS]] select_statement1
  [INSERT OVERWRITE TABLE tablename2 [PARTITION ... [IF NOT EXISTS]] select_statement2];
  ```

  - 使用多个`INSERT`语句将数据插入到多个表中。

##### 本地插入

本地插入是将查询的单个结果集插入本地文件系统或HDFS文件系统。

- **语法：**

  sql复制

  ```sql
  INSERT OVERWRITE [LOCAL] DIRECTORY 'directory' [ROW FORMAT row_format] [STORED AS file_format] SELECT ... FROM ...;
  ```

  - `LOCAL`表示插入HDFS文件系统，反之插入本地文件系统。
  - `directory`用于指定文件系统的路径。
  - `ROW FORMAT row_format`用于指定输出文件的行格式，可以指定字段分隔符和字符编码等内容。
  - `STORED AS file_format`用于指定文件存储格式。

#### 数据迁移

在Hive中，数据迁入和迁出通过`IMPORT`和`EXPORT`命令来完成。

- **IMPORT命令：** 用于将外部数据迁入Hive表。

- **EXPORT命令：** 将表或分区的数据及其元数据迁出到指定的输出位置。

- **语法：**

  sql复制

  ```sql
  IMPORT [[EXTERNAL] TABLE new_or_original_tablename [PARTITION (part_column="value"[, ...])]] FROM 'source_path' [LOCATION 'import_target_path']
  EXPORT TABLE tablename [PARTITION (part_column="value"[, ...])] TO 'export_target_path' [FOR replication('eventid')]
  ```

  - `FROM 'source_path'`指定要从中导入数据的源路径。
  - `LOCATION 'import_target_path'`指定导入目标路径。
  - `TO 'export_target_path'`指定数据导出的目标路径。
  - 如果需要支持数据复制，则可以通过`FOR replication`子句指定事件ID。

#### 总结

Hive数据操作语言提供了加载文件、查询插入和数据迁移等操作，使得用户能够灵活地管理Hive中的数据。加载文件操作可以将本地或HDFS上的文件数据加载到Hive表中，查询插入操作可以将查询结果插入到表中，而数据迁移操作则可以在不同Hadoop集群之间进行Hive表的迁移。这些操作对于维护和优化数据库结构非常重要，使得用户能够根据需要对数据进行各种操作。

## Hive 运算符

Hive 内置的运算符主要分为四类：

1. **关系运算符**：用于比较两个值，包括等值（`=`）、不等值（`!=`）、小于（`<`）、大于（`>`）、小于等于（`<=`）、大于等于（`>=`）、空值判断（`IS NULL`）、非空判断（`IS NOT NULL`）、LIKE 比较（`LIKE`）。
2. **算术运算符**：包括加法（`+`）、减法（`-`）、乘法（`*`）、除法（`/`）、取余（`%`）、按位与（`&`）、按位或（`|`）、按位异或（`^`）、按位取反（`~`）。
3. **逻辑运算符**：包括逻辑与（`AND`、`&&`）、逻辑或（`OR`、`||`）、逻辑非（`NOT`、`!`）。
4. **复杂运算符**：用于处理数组（`A[n]`）、映射（`M[key]`）、结构体（`S.x`）等复杂数据类型。

## 数据的查询、过滤与分组聚合

数据查询、过滤和分组聚合是数据分析和处理过程中的重要操作。在 Hive 中，常用以下命令来实现这些操作：

- **SELECT**：选择数据。
- **FROM**：指定数据来源。
- **WHERE**：过滤数据。
- **GROUP BY**：对数据进行分组。
- **HAVING**：对分组后的数据进行过滤。
- **JOIN**：连接多个表。
- **ORDER BY**：对结果进行排序。
- **SORT BY**：对数据进行排序。
- **DISTRIBUTE BY**：控制数据分布。
- **CLUSTER BY**：对数据进行聚类。
- **TABLESAMPLE**：对表进行抽样。
- **LIMIT**：限制返回的行数。

### JOIN 语句

Hive 支持多种类型的 JOIN 操作：

1. **内连接（INNER JOIN）**：连接两个表中满足条件的数据。
2. **左连接（LEFT JOIN）**：包含左表的所有记录和满足条件的右表的部分记录。
3. **右连接（RIGHT JOIN）**：包含右表的所有记录和满足条件的左表的部分记录。
4. **全连接（FULL JOIN）**：包含两个表的所有记录，无对应数据则填充 NULL。
5. **左半连接（LEFT SEMI JOIN）**：只包含左表的列，用于筛选出左表中存在的符合连接条件的记录。
6. **交叉连接（CROSS JOIN）**：将两个表中的所有记录进行组合，形成笛卡尔积。

### 排序语句

Hive 提供多种排序语句：

1. **ORDER BY**：全局排序，将结果集按照一个或多个字段进行升序或降序排序。
2. **SORT BY**：内部排序，只在每个 Reduce 中对数据进行排序，保证局部有序，但不保证全局有序。
3. **DISTRIBUTE BY**：分区排序，控制 Map 的输出按照指定的字段将数据划分到不同的 Reduce 输出文件中。
4. **CLUSTER BY**：兼具 DISTRIBUTE BY 和 SORT BY 的功能，但排序只能是升序排序，无法指定排序规则。

### LIMIT 语句

LIMIT 语句用于限制查询结果的行数：

- 接受一个或两个非负整数常量作为参数。
- 第一个参数指定返回结果集中的第一行偏移量。
- 第二个参数指定要返回的最大行数。

### 总结

这些内容涵盖了 Hive 中的运算符、数据查询、过滤、分组聚合、JOIN 操作、排序语句和 LIMIT 语句的基本概念和使用方法。希望这些信息对您撰写读书笔记有所帮助。

## Hive 内置函数

### 字符串函数

1. **字符串长度函数**：`Length(Str)` 返回字符串 `Str` 的长度。
2. **字符串转大/小写函数**：`Upper(Str)` 和 `Lower(Str)` 分别将字符串 `Str` 转换为大写和小写。
3. **字符串截取函数**：`Substr(Str, Start, Len)` 返回字符串 `Str` 从位置 `Start` 开始长度为 `Len` 的子串。
4. **字符串连接函数**：`Concat(Str1, Str2, ...)` 连接多个字符串为一个字符串。
5. **去空格函数**：`Trim(Str)` 去除字符串 `Str` 的前后空格。

### 条件函数

1. **If函数**：`If(Condition, a, b)` 如果条件 `Condition` 为真（True），则返回 `a`；否则返回 `b`。
2. **条件判断函数**：`Case a When b Then c [When d Then e]* [Else f] End` 如果 `a` 等于 `b`，则返回 `c`；如果 `a` 等于 `d`，则返回 `e`；否则返回 `f`。
3. **条件判断函数**：`Case When a Then b [When c Then d]* [Else e] End` 如果 `a` 为真（True），则返回 `b`；如果 `c` 为真（True），则返回 `d`；否则返回 `e`。

### 日期时间函数

1. **指定格式日期转UNIX时间戳函数**：`Unix_TimeStamp(Date, String Pattern)` 转换 `Pattern` 格式的日期到 UNIX 时间戳。
2. **日期增加函数**：`Date_Add(Startdate, Days)` 返回开始日期 `Startdate` 增加 `Days` 天后的日期。
3. **日期减少函数**：`Date_Sub(Startdate, Days)` 返回开始日期 `Startdate` 减少 `Days` 天后的日期。

这些函数涵盖了字符串处理、条件判断和日期时间处理等方面，是 Hive 中常用的内置函数，用于数据处理和分析。

## Hive 安装与配置

#####  修改 Hadoop 相关参数

Hive 的安装和部署建立在已有的 Hadoop 生态系统基础之上，包括 Hadoop 分布式文件系统 HDFS、MapReduce 以及 YARN 资源管理系统。在这些基础平台和组件正确安装并能够正常运行的前提下，可以开始 Hive 的安装过程。而在安装部署 Hive 前，需要对 Hadoop 的底层依赖进行若干参数调整。这些调整涉及到 Hadoop 的核心配置文件，包括 `core-site.xml` 以及 `yarn-site.xml`。

###### 修改 `core-site.xml`

1. 配置 `bigdata(superUser)` 允许通过代理访问的主机节点。
2. 配置 `bigdata(superUser)` 允许通过代理用户所属组。
3. 配置 `bigdata(superUser)` 允许通过代理的用户。

###### 配置 `yarn-site.xml`

1. 设置 YARN 容器允许管理的物理内存大小。
2. 设置 YARN 容器允许分配的最大最小内存。
3. 关闭 YARN 对虚拟内存的限制检查。

将配置文件分发至所有节点并重启集群。在完成 Hadoop 相关参数的修改后，需要将这些新的配置文件分发到集群中的所有节点上。这一步骤确保整个集群的配置保持一致，从而避免因配置不一致而导致的故障或性能问题。分发完成后，需要重启整个 Hadoop 集群，以使新的配置生效。

#####  Hive 安装部署

在本地部署的虚拟服务器节点 node01 上安装部署 Hive。

1. 下载 Hive 安装包。本案例使用的 Hive 版本为 3.1.2，读者可以访问 Apache 资源网站下载使用。
2. 上传 Hive 安装包。使用 Xftp 文件传输工具连接虚拟服务器节点 node01，将 Hive 安装包 `apache-hive-3.1.2-bin.tar` 上传至 `/opt/software` 文件夹下。
3. 安装 Hive。通过解压缩的方式安装 Hive，将 Hive 安装到存放应用目录 `/opt/module` 文件夹下。解压后 `/opt/module` 文件夹下会新增 `apache-hive-3.1.2-bin`。
4. 修改名称为 `hive`。将名称 `apache-hive-3.1.2-bin` 修改为 `hive`。
5. 修改 `/etc/profile.d/my_env.sh` 文件，配置 JDK 环境变量。
6. 设置 `HIVE_HOME` 和 `PATH` 环境变量。
7. 执行 `source` 命令，让新的环境变量 `PATH` 生效。
8. 初始化元数据库（默认为 derby 数据库）。

#####  MySQL 安装部署

在本地部署的虚拟服务器节点 node01 上安装部署 MySQL。

1. 下载并上传 MySQL 安装包以及 MySQL 驱动 jar 包。本案例使用的 MySQL 安装包以及 MySQL 驱动 jar 包分别为：`mysql-5.7.28-1.el7.x86_64.rpm-bundle.tar` 和 `mysql-connector-java-5.1.37.jar`，读者可以访问官方资源网站下载使用。
2. 解压 MySQL 安装包。首先在文件夹 `/opt/software` 下新建 `mysql_lib` 文件夹并解压 MySQL 安装包至 `/opt/module/mysql_lib` 文件夹下。
3. 卸载系统自带的 mariadb。
4. 安装 MySQL 依赖。
5. 安装 mysql-client。
6. 安装 mysql-server。
7. 启动 MySQL。
8. 设置开机自启动。
9. 查看 MySQL 密码。
10. 配置 MySQL。

##### 配置元数据到 MySQL

配置元数据到 MySQL 是指将 Hive 的元数据存储由 Derby 数据库改为 MySQL 数据库，以提高元数据的可靠性和扩展性，可以更好地支持大规模数据处理和元数据管理，并且能够更好地与其他系统集成。

1. 登录 MySQL。
2. 新建 Hive 元数据库。
3. 将 MySQL 的 JDBC 驱动拷贝到 Hive 的 lib 目录下。
4. 在 `$HIVE_HOME/conf` 目录下新建 `hive-site.xml` 文件，并添加如下内容：

xml复制

```xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
  <!-- jdbc 连接的 URL -->
  <property>
    <name>javax.jdo.option.ConnectionURL</name>
    <value>jdbc:mysql://node01:3306/metastore?useSSL=false</value>
  </property>
  <!-- jdbc 连接的 Driver -->
  <property>
    <name>javax.jdo.option.ConnectionDriverName</name>
    <value>com.mysql.jdbc.Driver</value>
  </property>
  <!-- jdbc 连接的 username -->
  <property>
    <name>javax.jdo.option.ConnectionUserName</name>
    <value>root</value>
  </property>
  <!-- jdbc 连接的 password -->
  <property>
    <name>javax.jdo.option.ConnectionPassword</name>
    <value>000000</value>
  </property>
  <!-- Hive 默认在 HDFS 的工作目录 -->
  <property>
    <name>hive.metastore.warehouse.dir</name>
    <value>/user/hive/warehouse</value>
  </property>
</configuration>
```

1. 初始化 Hive 元数据库。

#####  Hive 常见属性配置

1. 修改 Hive 的 log 存放路径。
2. 修改 Hive 启动 JVM 堆内存配置。
3. Hive 窗口打印默认库和表头。