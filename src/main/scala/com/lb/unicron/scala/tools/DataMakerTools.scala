package com.lb.unicron.scala.tools

import scala.util.Random

/**
  * @ClassName DataMakerTools  数据生成器
  * @Description @TODO
  * @Author liubing
  * @Date 2021/11/26 14:34
  * @Version 1.0
  **/
class DataMakerTools {

}

object DataMakerTools{

  /**
    * 随机生成一个汉字(全部汉字集)
    * 0x4e00是汉字在Unicode编码的起始位置
    * 0x9fa5是汉字在Unicode编码的结束位置
    * @return
    */
  private def chineseCharactersAll(): String = (0x4e00 + (Math.random() * (0x9fa5 - 0x4e00 + 1)).toInt).toChar.toString

  /**
    * 随机生成一个常用汉字
    * 原理是从汉字区位码找到汉字。在汉字区位码中分高位与底位， 且其中简体又有繁体。位数越前生成的汉字繁体的机率越大。
    * 所以在本例中高位从171取，底位从161取， 去掉大部分的繁体和生僻字。但仍然会有！
    * @return
    */
  private def chineseCharacters(): String = {
    val random = new Random()
    val hightPos = 176 + Math.abs(random.nextInt(39)) //获取高位值
    val lowPos = 161 + Math.abs(random.nextInt(93)) //获取低位值
    val b: Array[Byte] = new Array[Byte](2)
    b(0) = (new Integer(hightPos).byteValue())
    b(1) = (new Integer(lowPos).byteValue())
    new String(b, "GBK") //转成中文
  }


  // 全部字母
  private def englishCharactersAll(): String = (0x0041 + (Math.random() * (0x007a - 0x0041 + 1)).toInt).toChar.toString
  // 大写字母
  private def capitalEnglishCharacters: String = (0x0041 + (Math.random() * (0x005a - 0x0041 + 1)).toInt).toChar.toString
  // 小写字母
  private def lowercaseEnglishCharacters: String = (0x0061 + (Math.random() * (0x007a - 0x0061 + 1)).toInt).toChar.toString

  // 数字
  private def number: Int = (0x0030 + (Math.random() * (0x0039 - 0x0030 + 1)).toInt)

  /**
    * 返回一定数量的随机汉字
    * @param num  默认10个汉字
    * @return
    */
  def getRandomChineseCharacters(length: Int = 10): String = {
    val sb = new StringBuilder(length)
    for (i <- 1 to length) sb.append(chineseCharacters)
    sb.toString
  }


  /**
    * 返回英文字母
    * @param num
    * @param s
    * @return
    */
  def getRandomEngilshCharacters(length: Int = 10)(s: String = "all" ): String = {
    val sb = new StringBuilder(length)
    for (i <- 1 to length) sb.append(englishCharactersAll)
    sb.toString
  }

  def getNumber(length: Int = 3) : String = {
      val base = "0123456789"
      val  random = new Random()
      val  sb = new StringBuffer()
      for (i <- 1 to length) {
        val number = random.nextInt(base.length())
        sb.append(base.charAt(number))
      }
    sb.toString()
  }



  def main(args: Array[String]): Unit = {
    // println(getRandomChineseCharacters(20))
    // println(getRandomEngilshCharacters(1000))
  }

}
