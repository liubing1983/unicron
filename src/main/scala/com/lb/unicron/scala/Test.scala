package com.lb.unicron.scala

import java.sql.Timestamp

/**
  * @ClassName Test
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/25 17:31
  * @Version 1.0
  **/
object Test {

  def main(args: Array[String]): Unit = {

    println(scala.util.Random.nextInt(2).toString)
    println(scala.util.Random.nextLong().toString)
    println(scala.util.Random.nextDouble() * scala.util.Random.nextInt(10000))
    println(scala.util.Random.nextString(10))

    println(Timestamp.valueOf("2020-11-11 12:23:34").getTime)

  }

}
