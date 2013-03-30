package com.phyous.leap

import java.io.IOException
import com.leapmotion.leap._

object App {

  def main(args: Array[String]) {
    val listener = new SampleListener
    val controller = new Controller
    controller.addListener(listener)
    println("Press Enter to quit...")
    try {
      System.in.read
    }
    catch {
      case e: IOException => {
        e.printStackTrace
      }
    }
    controller.removeListener(listener)
  }

}
