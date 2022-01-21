package com.lb.unicron.scala.jdbc

import java.math.BigDecimal
import java.sql.{Connection, PreparedStatement, ResultSet, Timestamp}
import java.{sql, util}
import java.util.Date

import com.lb.unicron.scala.fileutil.ConfigFiles
import com.lb.unicron.scala.jdbc.JdbcColumnType.JdbcColumnType
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.ConfigFile

import scala.collection.JavaConversions._
import org.slf4j.LoggerFactory

/**
  * @ClassName MysqlDao
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/25 16:12
  * @Version 1.0
  **/
class MysqlDao(dBConnectionInfo: DBConnectionInfo) extends java.io.Serializable {

  val log = LoggerFactory.getLogger("MysqlDao")

  // ===================  insert  ========================

  /**
    * 保存单条数据, 并成功返回执行成功数
    *
    * @param sql
    * @return
    */
  def saveData(sql: String): Option[Int] = {
    log.debug(s"""save data sql : $sql""")
    var conn: Connection = null
    var ps: PreparedStatement = null

    try {
      conn = dBConnectionInfo.getConnection.get
      ps = conn.prepareStatement(sql)
      Some(ps.executeUpdate())
    } catch {
      case e => e.printStackTrace()
        log.error(s"sql: ${sql} ")
        None
    } finally {
      connClose(conn)
      psClose(ps)
    }
  }

  /**
    *
    * @param sql
    * @param columnNames
    * @param columnTypes
    * @param dataList
    * @param batchNum
    */
  def saveDataBatch(sql: String, columnNames: Seq[String], columnTypes: Map[String, JdbcColumnType],
                    dataList: util.ArrayList[util.HashMap[String, String]], batchNum: Int = 10000) = {
    log.debug(s"""save data sql : $sql""")
    var conn: Connection = null
    var ps: PreparedStatement = null


    log.debug(sql)
    log.debug(columnNames.mkString(","))
    log.debug(columnTypes.mkString("|"))

    var i = 0


    try {
      conn = dBConnectionInfo.getConnection.get
      ps = conn.prepareStatement(sql)
      conn.setAutoCommit(false)

      // 遍历数据
      dataList.foreach { dataMap =>
        var columnNames_index_value = 1
        // 遍历字段名
        columnNames.foreach { columnName =>
          // 根据字段类型, 向ps中添加数据
          columnTypes.getOrElse(columnName, "") match {
            case JdbcColumnType.StringColumn => ps.setString(columnNames_index_value, dataMap.getOrElse(columnName, ""))
            case JdbcColumnType.LongTextColumn => ps.setString(columnNames_index_value, dataMap.getOrElse(columnName, ""))
            case JdbcColumnType.IntColumn => ps.setInt(columnNames_index_value, dataMap.getOrElse(columnName, "").toInt)
            case JdbcColumnType.LongColumn => ps.setLong(columnNames_index_value, dataMap.getOrElse(columnName, "").toLong)
            case JdbcColumnType.DoubleColumn => ps.setDouble(columnNames_index_value, dataMap.getOrElse(columnName, "").toDouble)
            case JdbcColumnType.TimestampColumn => ps.setTimestamp(columnNames_index_value, Timestamp.valueOf(dataMap.getOrElse(columnName, "")))
            case JdbcColumnType.BooleanColumn => ps.setBoolean(columnNames_index_value, dataMap.getOrElse(columnName, "").toBoolean)
            case JdbcColumnType.BigDecimalColumn => ps.setBigDecimal(columnNames_index_value, BigDecimal.valueOf(dataMap.getOrElse(columnName, "").toDouble))
            // case JdbcColumnType.DateColumn => ps.setDate(1, new sql.Date() )
            case _ => log.error(s"[${columnName}]--${JdbcColumnType.values.toString()}")
          }
          columnNames_index_value = columnNames_index_value + 1
        }
        ps.addBatch()

        if (i >= batchNum) {
          ps.executeBatch()
          //conn.commit()
          i = 0
        }
      }

      ps.executeBatch()
      conn.commit()

    }

    catch {
      case e => e.printStackTrace(); log.error(s"sql: ${sql} ")
    } finally {
      connClose(conn)
      psClose(ps)
    }
  }


  // ===================  select  ========================

  /**
    * 查询数据, 根据传入的column将数据封装成map.
    *
    * @param sql    查询数据sql
    * @param column 查询需返回的字段
    * @return
    */
  def getDataList(sql: String, column: String): Option[util.ArrayList[Map[String, String]]] = {
    var conn: Connection = null
    var ps: PreparedStatement = null
    var rs: ResultSet = null
    val list = new util.ArrayList[Map[String, String]]()

    val s1 = System.currentTimeMillis()


    try {
      conn = dBConnectionInfo.getConnection.get
      ps = conn.prepareStatement(sql)
      rs = ps.executeQuery()
      while (rs.next()) {
        list.add(column.split("\\,", -1).map {
          x => (x, rs.getString(x.trim))
        }.toMap)
      }
      Some(list)
    } catch {
      case e => e.printStackTrace();
        None
    } finally {
      connClose(conn)
      psClose(ps)
      rsClose(rs)
      log.debug("select sql time: " + ((System.currentTimeMillis() - s1) / 1000))
    }
  }


  /**
    * 关闭数据库连接
    *
    * @param conn
    */
  private def connClose(conn: Connection): Unit = {
    if (conn != null) conn.close
  }

  private def psClose(ps: PreparedStatement): Unit = {
    if (ps != null) ps.close
  }

  private def rsClose(rs: ResultSet): Unit = {
    if (rs != null) rs.close
  }

  def x = (x: Int) => x + 1
}


object MysqlDao extends App {

  val pro = new ConfigFiles("jdbc.properties")

  val conn = new DBConnectionInfo(pro.getProperties().get)

  val dao = new MysqlDao(conn)

  /**
    * CREATE TABLE `user` (
    * `id` bigint NOT NULL,
    * `name` varchar(20) NOT NULL,
    * `idcard` varchar(20) NOT NULL,
    * `age` int NOT NULL,
    * `salary` double(8,2) NOT NULL,
    * `sex` int NOT NULL,
    * `isvalidate` int NOT NULL,
    * `create_time` timestamp NOT NULL,
    * `id_card` varchar(255) DEFAULT NULL,
    * PRIMARY KEY (`id`)
    * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
    */


  val sql = "insert into user ( name, idcard, age, salary, sex, isvalidate, create_time, id_card ) values (?,?,?,?,?,?,?,?)"
  val columnName: Seq[String] = Seq("name", "idcard", "age", "salary", "sex", "isvalidate", "create_time", "id_card")
  val columnType: Seq[JdbcColumnType] =
    Seq(JdbcColumnType.StringColumn,
      JdbcColumnType.StringColumn,
      JdbcColumnType.IntColumn,
      JdbcColumnType.BigDecimalColumn,
      JdbcColumnType.IntColumn,
      JdbcColumnType.IntColumn,
      JdbcColumnType.TimestampColumn,
      JdbcColumnType.StringColumn
    )

  val list = new util.ArrayList[util.HashMap[String, String]]

  val t1 = System.currentTimeMillis()
  for (i <- 1 to 10000) {
    val map = new util.HashMap[String, String]
    map.put("name", "name" + i)
    map.put("idcard", scala.util.Random.nextLong().toString)
    map.put("age", scala.util.Random.nextInt(100).toString)
    map.put("salary", s"${scala.util.Random.nextInt(20000)}.${scala.util.Random.nextInt(99)}")
    map.put("sex", scala.util.Random.nextInt(2) + 1.toString)
    map.put("isvalidate", scala.util.Random.nextInt(2) + "")
    map.put("create_time", "2022-11-25 23:12:12")
    map.put("id_card", scala.util.Random.nextLong().toString)
    list.add(map)
  }


  dao.saveDataBatch(sql, columnName, columnName.zip(columnType).toMap, list);
  println(System.currentTimeMillis() - t1)


  val t2 = System.currentTimeMillis()
  for (i <- 1 to 10000) {
    val sql =
      s"""insert into user ( name, idcard, age, salary, sex, isvalidate, create_time, id_card )
         |values
         |('name-${i}',
         |'${scala.util.Random.nextLong()}',
         |${scala.util.Random.nextInt(100)},
         |${scala.util.Random.nextInt(20000)}.${scala.util.Random.nextInt(99)},
         |${scala.util.Random.nextInt(2) + 1},
         |${scala.util.Random.nextInt(2)},
         |now(),
         |'${scala.util.Random.nextLong()}'
         |
         |)""".stripMargin
    dao.saveData(sql)
  }
  println(System.currentTimeMillis() - t2)


  val t3 = System.currentTimeMillis()
  val sql2 = s"""insert into user ( name, idcard, age, salary, sex, isvalidate, create_time, id_card  ) values """
  val sql3 = (1 to 10000).map { i =>

    s""" ('name-${i}',
       |'${scala.util.Random.nextLong()}',
       |${scala.util.Random.nextInt(100)},
       |${scala.util.Random.nextInt(20000)}.${scala.util.Random.nextInt(99)},
       |${scala.util.Random.nextInt(2) + 1},
       |${scala.util.Random.nextInt(2)},
       |now(),
       |'${scala.util.Random.nextLong()}'
       |
         |)""".stripMargin
  }

  // println(sql2+""+sql3)

  dao.saveData(sql2 + "" + sql3.mkString(","))
  println(System.currentTimeMillis() - t3)

}