package com.thebrains.utils

import org.rogach.scallop._

abstract class Config(args: Seq[String]) extends ScallopConf(args) {
  def init(): Unit = {
    verify()
  }
}
