package com.lb.unicron.scala.fileutil

import java.util.Properties

import org.slf4j.LoggerFactory

import scala.io.Source

/**
  * @ClassName ConfigFiles
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/22 09:16
  * @Version 1.0
  **/
class ConfigFiles(filename: String) {

  import org.slf4j.LoggerFactory

  val log = LoggerFactory.getLogger("ConfigFiles")

  /**
    * 读取配置文件
    *
    * @return 配置文件对象
    */
  def getProperties(): Option[Properties] = {
    val props = new Properties()
    try {
      log.info(s"配置文件: $filename")
      props.load(classOf[ConfigFiles].getClassLoader.getResourceAsStream(filename))
      Some(props)
    } catch {
      case e: NullPointerException => log.error(s"$filename 不存在"); None
    }
  }

  def getLocalProperties(): Option[Properties] = {
    val props = new Properties()
    try {
      log.info(s"配置文件: $filename")
      props.load(Source.fromFile(this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath + filename).reader())

      Some(props)
    } catch {
      case e: NullPointerException => log.error(s"$filename 不存在"); None
    }
  }
}

object ConfigFiles {

  val log = LoggerFactory.getLogger("ConfigFiles")

  def main(args: Array[String]): Unit = {

    val path = this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath
    val path2 = classOf[ConfigFiles].getClassLoader.getResource("jdbc.properties")
    println("path:  " + path)
    println("path2:  " + path2)

    new ConfigFiles("jdbc.properties").getLocalProperties() match {
      case None => log.error("---")
      case Some(props) => println(props.getProperty("db", "======"))
    }
  }
}