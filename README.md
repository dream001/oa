### 服务器提交任务步骤：

###### 1. 打包spark-caa-1.0.0-classes.jar

```
# mvn clean install
```

###### 2. 同步代码

```
# scp spark-caa-1.0.0-classes.jar root@cdh-01,cdh-02:/opt/cloudera/parcels/SPARK2-2.2.0.cloudera2-1.cdh5.12.0.p0.232957/lib/spark2/jars
```

###### 3. cdh-03执行脚本

```
# cd /opt/cloudera/parcels/SPARK2
# ./bin/spark2-submit --master yarn  --executor-memory 15G --driver-memory 2g --class nc.starter.entrance.Starter /opt/cloudera/parcels/SPARK2-2.2.0.cloudera2-1.cdh5.12.0.p0.232957/lib/spark2/jars/spark-caa-1.0.0-classes.jar
```

###### 4. 日志信息

`info_log  sql的执行信息`

`error_log  错误信息输出`



##  修改的部分

1）修改jdbc 底层，insert updata 利用KuduClient 直接调用，select走sparkKudu
		delete   sparkKudu  KuduClient  都有实现，根据具体传入参数来决定
2) 修改公式计算过程，一次性获取一页数据需要计算的值
3）分页，计算分页数 做了缓存   Spark缓存，修改原来的二次调用问题
4）JEP 定义的时候少一层循环
5）反向获取单据类型，获取元数据信息表     做CAA缓存使用   （修改）
6）获取原始台帐数据做缓存   Spark
7) 获取KuduTable  做CAA缓存
8) 增加Mysql 记录信息和执行顺序，影响6) 中执行效率


caa_acc_mainbill    贷款收息
caa_acc_loanpro     贷款发放
caa_bm_meta         外部数据源
caa_doc_duebill     计提利息


UPDATE spark_caa set islock = true,hostname = "",costtime = 0,sparktime=0 where 1=1;
select * from spark_caa where  hostname <> "";

UPDATE spark_caa set islock = false where pkorg in("0001H110000000000IML","0001H110000000000IMO","0001H110000000000ILR","0001F1100000002IGYSD");

####  查询 数量



./bin/spark-shell --master spark://bigdata-01:7077 --total-executor-cores 48 --executor-memory 90G

查詢：

import org.apache.kudu.spark.kudu._
import org.apache.kudu.client._
import collection.JavaConverters._
val kuduContext = new KuduContext("bigdata-02:7051,bigdata-03:7051",spark.sparkContext)
val caa_acc_mainbill = spark.read.options(Map("kudu.master" -> "bigdata-02:7051,bigdata-03:7051","kudu.table" -> "caa_acc_mainbill")).kudu
caa_acc_mainbill.count

val caa_acc_loanpro = spark.read.options(Map("kudu.master" -> "bigdata-02:7051,bigdata-03:7051","kudu.table" -> "caa_acc_loanpro")).kudu
caa_acc_loanpro.count

val caa_doc_caatally = spark.read.options(Map("kudu.master" -> "bigdata-02:7051,bigdata-03:7051","kudu.table" -> "caa_doc_caatally")).kudu
caa_doc_caatally.count



刪除：

import org.apache.kudu.spark.kudu._
import org.apache.kudu.client._
import collection.JavaConverters._
val kuduContext = new KuduContext("bigdata-02:7051,bigdata-03:7051",spark.sparkContext)
val caa_acc_mainbill = spark.read.options(Map("kudu.master" -> "bigdata-02:7051,bigdata-03:7051","kudu.table" -> "caa_acc_mainbill")).kudu
kuduContext.deleteRows(caa_acc_mainbill.select("PK_MAINBILL"), "caa_acc_mainbill")
caa_acc_mainbill.count
val caa_acc_loanpro = spark.read.options(Map("kudu.master" -> "bigdata-02:7051,bigdata-03:7051","kudu.table" -> "caa_acc_loanpro")).kudu
kuduContext.deleteRows(caa_acc_loanpro.select("PK_LOANPRO"), "caa_acc_loanpro")
caa_acc_loanpro.count
val caa_doc_caatally = spark.read.options(Map("kudu.master" -> "bigdata-02:7051,bigdata-03:7051","kudu.table" -> "caa_doc_caatally")).kudu
kuduContext.deleteRows(caa_doc_caatally.select("PK_LOANTALLY"), "caa_doc_caatally")
caa_doc_caatally.count


./bin/spark-submit --master spark://bigdata-01:7077 --total-executor-cores 16 --executor-memory 30G --class nc.starter.entrance.Starter  /opt/spark-2.3.0-bin-hadoop2.7/jars/spark-caa-1.0.0-classes.jar


nohup ./bin/spark-submit --master spark://bigdata-01:7077 --total-executor-cores 16  --executor-memory 30G --class nc.starter.entrance.Starter  /opt/spark-2.3.0-bin-hadoop2.7/jars/spark-caa-1.0.0-classes.jar >/dev/null 2>&1 &







