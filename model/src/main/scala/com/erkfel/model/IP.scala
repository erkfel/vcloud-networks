package com.erkfel.model

/**
 * @author Oleg Aravin
 */
class IP(ipString: String) {

  val ip = parse(ipString)

  def parse(ipString: String): Array[Int] = ipString.split("\\.").map(_.toInt)

  def >(ip: IP): Boolean = {
    var decision = false
    var isStop = false
    var i = 0
    while (!isStop && i < 4) {
      if (ip.ip(i) != this.ip(i)) {
        decision = this.ip(i) > ip.ip(i)
        isStop = true
      }
      i = i + 1
    }
    decision
  }

  def <(ip: IP): Boolean = {
    val eq = !(ip == this)
    val less = !(this > ip)
    eq && less
  }

  def ==(ip: IP) = ip.ip.sameElements(this.ip)

  override def toString: String = ip.map(_.toString).reduceLeft(_ + "." + _)
}

object IP {
  def apply(ipString: String) = new IP(ipString)
}

