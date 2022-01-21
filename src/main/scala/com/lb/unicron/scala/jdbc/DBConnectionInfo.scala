package com.lb.unicron.scala.jdbc

import java.sql.Connection
import java.util.Properties
import javax.sql.DataSource

import com.alibaba.druid.pool.DruidDataSourceFactory
import com.lb.unicron.scala.fileutil.ConfigFiles
import org.slf4j.LoggerFactory

/**
  * @ClassName DBConnectionInfo
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/25 16:11
  * @Version 1.0
  **/
class DBConnectionInfo(pro: Properties) extends java.io.Serializable {

  val log = LoggerFactory.getLogger(classOf[DBConnectionInfo])

  val dataSource: Option[DataSource] = {
    try {
      Some(DruidDataSourceFactory.createDataSource(pro))
    } catch {
      case error: Exception =>
        log.error("Error Create Mysql Connection", error.getMessage)
        None
    }
  }

  // 连接方式
  def getConnection: Option[Connection] = {
    dataSource match {
      case Some(ds) => Some(ds.getConnection())
      case None => None
    }
  }
}



object DBConnectionInfo {

  def main(args: Array[String]): Unit = {
    val con = new DBConnectionInfo(new ConfigFiles("jdbc.properties").getProperties().get).getConnection.get

    val dm = con.getMetaData
    System.out.println(dm.getDriverName)
    System.out.println(dm.getDatabaseMajorVersion + "." + dm.getDatabaseMinorVersion)
    System.out.println(dm.getMaxStatements)
    System.out.println(dm.getJDBCMajorVersion) //jdbc4.0

    System.out.println("--------------------------")


    import java.sql.ResultSet
    //返回所有数据库的名字//返回所有数据库的名字

    val rsa = dm.getCatalogs
    while (rsa.next) {
      val name = rsa.getString("TABLE_CAT")
      val name2 = rsa.getString(1)
      System.out.println(name + "," + name2)
    }
    System.out.println("--------------------------")


    import java.sql.ResultSet
    //返回某个数据库的表名//返回某个数据库的表名

    //参数解析: 第1和第2个都是数据库的名字(2个，是为兼容不同数据库), 第3个参数是查询表名的过滤模式(null为不过滤即查所有，"%a%"为表名中包含字母'a'),最后一个参数是表类型如"TABLE"、"VIEW"等(这些值可查看API中getTableTypes()方法)
    val rs = dm.getTables("finup_ecology", "finup_ecology", null, Array[String]("TABLE", "VIEW"))
    while ( {
      rs.next
    }) {
      val name = rs.getString("TABLE_NAME")
      //字符串参数的具体取值参看API中getTables()
      val name2 = rs.getString("TABLE_TYPE")
      System.out.println(name + "," + name2)
    }
    System.out.println("--------------------------")

    //如果已知数据库的名字，打开该数据库。如果还知道某个表的名字，那么可以操纵这个表
    val s1 = "finup_ecology"
    con.createStatement.execute("use " + s1) //该方法能够执行所有SQL语句，包括: use hncu; drop database hncu

    val s2 = "zoology_store_order"
    val rs2 = con.createStatement.executeQuery("select * from " + s2)
    while ( {
      rs2.next
    }) { //如果已经列数n，就可输出表格的所有数据
      val n = 2
      var i = 1
      while ( {
        i <= n
      }) {
        val obj = rs2.getObject(i)
        System.out.print(obj + " ")

        {
          i += 1;
          i - 1
        }
      }
      System.out.println()
    }

  }


}
