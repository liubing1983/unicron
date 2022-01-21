package com.lb.unicron.scala.tools

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializeConfig
import org.apache.kafka.clients.producer.{KafkaProducer, Producer, ProducerRecord}
import org.slf4j.LoggerFactory

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Properties
import scala.util.Random

case class TestData(name: String, phone: Long, age: Int, salary: BigDecimal, status: Boolean, create_time: Timestamp)

/**
  * 生产kafka测试数据
  */
object KafkaRandomTestDataProducer extends App{

  val log = LoggerFactory.getLogger(KafkaRandomTestDataProducer.getClass)


  val props = new Properties()

  // 必须指定
  props.put("bootstrap.servers", "localhost:9092 ")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  // 可选配置
  props.put("acks", "1")
  // props.put("retries", 3)
  // props.put("batch.size", 323840)
  // props.put("linger.ms", 10)
  // props.put("buffer.memory", 33554432)
  // props.put("max.block.ms", 3000)

  val producer: Producer[String, String] = new KafkaProducer[String, String](props)

  for (i <- 0 until  10) {

    val localDateTime =  LocalDateTime.now()
    val bean = new TestData(
      DataMakerTools.getRandomChineseCharacters(5),
      DataMakerTools.getNumber(11).toLong,
      DataMakerTools.getNumber(2).toInt,
      BigDecimal.valueOf(Random.nextDouble() * Random.nextInt(10000)),
      Random.nextBoolean(),
      Timestamp.valueOf("2020-01-01 00:00:00")
    )
    // 使用Fastjson, 将case class转换json 因可变参数error, 需添加SerializeConfig.
    val conf = new SerializeConfig(true)
    val json_data = JSON.toJSONString(bean, conf)

    // println(json_data)
    // producer.send(new ProducerRecord[String, String]("flink-RandomTestData-topic", Integer.toString(i + 1), json_data.toString))
    producer.send(new ProducerRecord[String, String]("lb3", Integer.toString(i + 1), json_data.toString))
    Thread.sleep(5)
  }
  producer.close()




}
