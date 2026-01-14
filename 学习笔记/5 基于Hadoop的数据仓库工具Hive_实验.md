1.启动hdfs

![image-20250215072625230](.\img\tp\image-20250215072625230.png)

2.启动yarn

![image-20250215072848457](.\img\tp\image-20250215072848457.png)

3.打开hadoop web界面

![image-20250215072935883](.\img\tp\image-20250215072935883.png)

4.打开文件系统

![image-20250215072953894](.\img\tp\image-20250215072953894.png)

5.新建目录

![image-20250215073112775](.\img\tps\image-20250215073112775.png)

6.查看目录

![image-20250215073153792](.\img\tp\image-20250215073153792.png)

7.上传文件并查看文件

![image-20250215073249840](.\img\tp\image-20250215073249840.png)

![image-20250215073304952](.\img\tp\image-20250215073304952.png)

8.hive建库

![image-20250215073707608](.\img\tp\image-20250215073707608.png)

9.查看库

![image-20250215073832529](.\img\tp\image-20250215073832529.png)

10.切换数据库

![image-20250215073925211](.\img\tp\image-20250215073925211.png)

11.建表

![image-20250215074102489](.\img\tp\image-20250215074102489.png)

12.查看表信息

![image-20250215074240597](.\img\tp\image-20250215074240597.png)

13.加载数据

![image-20250215074439348](.\img\tp\image-20250215074439348.png)

14.执行查询

![image-20250215074900854](.\img\tp\image-20250215074900854.png)

![image-20250215074913041](.\img\tp\image-20250215074913041.png)



![image-20250215074956561](.\img\tp\image-20250215074956561.png)

![](.\img\tp\image-20250215074938926.png)

15.创建新表，删除原表，修改表名

![image-20250215075230493](.\img\tp\image-20250215075230493.png)

![image-20250215075258243](.\img\tp\image-20250215075258243.png)

![image-20250215075317111](.\img\tp\image-20250215075317111.png)

![image-20250215075331512](.\img\tp\image-20250215075331512.png)

16.统计数据记录总数，唯一直播间数，主播数。

![image-20250215075805722](.\img\tp\image-20250215075805722.png)

![image-20250215075811486](.\img\tp\image-20250215075811486.png)

![image-20250215075828666](.\img\tp\image-20250215075828666.png)

![image-20250215075907666](.\img\tp\image-20250215075907666.png)

![image-20250215080012748](.\img\tp\image-20250215080012748.png)

![image-20250215080021113](.\img\tp\image-20250215080021113.png)

17.数据去重

![image-20250215080136910](.\img\tp\image-20250215080136910.png)

![image-20250215080226245](.\img\tp\image-20250215080226245.png)

查看 `temporary_result` 表总行数：SELECT COUNT(*) AS total_after FROM temporary_result;

![image-20250215080322603](.\img\tp\image-20250215080322603.png)

检查 `temporary_result` 表中是否有重复的 `room_id`：

SELECT room_id, COUNT(*) AS duplicate_count
FROM temporary_result
GROUP BY room_id
HAVING COUNT(*) > 1;

![image-20250215080446192](.\img\tp\image-20250215080446192.png)

检查每个 `room_id` 是否保留最大 `online` 的记录：

SELECT tr.*
FROM temporary_result tr
JOIN (
    SELECT room_id, MAX(online) AS max_online
    FROM live_platform
    GROUP BY room_id
) max_online_per_room
ON tr.room_id = max_online_per_room.room_id AND tr.online < max_online_per_room.max_online;

![image-20250215080706761](.\img\tp\image-20250215080706761.png)

18.统计每个直播间数量

![image-20250215080807611](.\img\tp\image-20250215080807611.png)

![image-20250215080923072](.\img\tp\image-20250215080923072.png)

19.筛选top10热门直播间

![image-20250215081001206](.\img\tp\image-20250215081001206.png)

![image-20250215081103299](.\img\tp\image-20250215081103299.png)

20.筛选粉丝数前10直播间

![image-20250215081142914](.\img\tp\image-20250215081142914.png)

![image-20250215081233216](.\img\tp\image-20250215081233216.png)

21.统计直播间平均实时在线人数

![image-20250215081316493](.\img\tp\image-20250215081316493.png)

![image-20250215081415026](.\img\tp\image-20250215081415026.png)

22.输出粉丝数和实时在线人数的最大最小平均值

![image-20250215081509037](.\img\tp\image-20250215081509037.png)



![image-20250215081621373](.\img\tp\image-20250215081621373.png)



23.创建实时在线人数较高的直播间视图

![image-20250215081637050](.\img\tp\image-20250215081637050.png)

![image-20250215081648129](.\img\tp\image-20250215081648129.png)

24.按照粉丝数进行分桶

![image-20250215081830134](.\img\tp\image-20250215081830134.png)

![image-20250215081845216](.\img\tp\image-20250215081845216.png)

![image-20250215082016008](.\img\tp\image-20250215082016008.png)

![image-20250215082023939](.\img\tp\image-20250215082023939.png)

25.验证分桶表

从 `temporary_result_bucketed` 表中选择粉丝数为1000的所有记录：

![image-20250215082117791](.\img\tp\image-20250215082117791.png)

![image-20250215082124231](.\img\tp\image-20250215082124231.png)

从 `temporary_result_bucketed` 表中按 `fans` 分组，选择粉丝数、每个组的记录数以及每个组的在线人数总和：

sql

![image-20250215082145295](.\img\tp\image-20250215082145295.png)

![image-20250215082250462](.\img\tp\image-20250215082250462.png)

从 `temporary_result_bucketed` 表中选择所有记录，并按 `fans` 排序：

![image-20250215082411016](.\img\tp\image-20250215082411016.png)

![image-20250215082355612](.\img\tp\image-20250215082355612.png)