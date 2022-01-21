package com.lb.unicron.scala.jdbc

/**
  * @ClassName JdbcColumnType
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/25 19:34
  * @Version 1.0
  **/
object JdbcColumnType extends Enumeration{

  //这行是可选的，类型别名，在使用import语句的时候比较方便，建议加上
  type JdbcColumnType = Value

  //枚举的定义
  val StringColumn,
  LongTextColumn,
  IntColumn,
  LongColumn,
  DoubleColumn,
  BooleanColumn,
  TimestampColumn,
  BigDecimalColumn,
  DateColumn
  = Value


}



