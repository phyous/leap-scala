package com.phyous.leap

import scala.collection.JavaConverters._
import java.lang.Math
import com.leapmotion.leap._
import com.leapmotion.leap.Gesture.State

class SampleListener extends Listener {

  override def onInit(controller: Controller) {
    println("Initialized")
  }

  override def onConnect(controller: Controller) {
    println("Connected")
    controller.enableGesture(Gesture.Type.TYPE_SWIPE)
    controller.enableGesture(Gesture.Type.TYPE_CIRCLE)
    controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP)
    controller.enableGesture(Gesture.Type.TYPE_KEY_TAP)
  }

  override def onDisconnect(controller: Controller) {
    println("Disconnected")
  }

  override def onExit(controller: Controller) {
    println("Exited")
  }

  override def onFrame(controller: Controller) {
    val frame: Frame = controller.frame
    println("Frame id: " + frame.id + ", timestamp: " + frame.timestamp + ", hands: " + frame.hands.count + ", fingers: " + frame.fingers.count + ", tools: " + frame.tools.count + ", gestures " + frame.gestures.count)
    if (!frame.hands.empty) {
      val hand: Hand = frame.hands.get(0)
      val fingers: FingerList = hand.fingers
      if (!fingers.empty) {
        var avgPos: Vector = Vector.zero
        import scala.collection.JavaConversions._
        for (finger <- fingers) {
          avgPos = avgPos.plus(finger.tipPosition)
        }
        avgPos = avgPos.divide(fingers.count)
        println("Hand has " + fingers.count + " fingers, average finger tip position: " + avgPos)
      }
      println("Hand sphere radius: " + hand.sphereRadius + " mm, palm position: " + hand.palmPosition)
      val normal: Vector = hand.palmNormal
      val direction: Vector = hand.direction
      println("Hand pitch: " + Math.toDegrees(direction.pitch) + " degrees, " + "roll: " + Math.toDegrees(normal.roll) + " degrees, " + "yaw: " + Math.toDegrees(direction.yaw) + " degrees")
    }
    for (gesture <- frame.gestures().asScala) {
      gesture.`type` match {
        case Gesture.Type.TYPE_CIRCLE =>
          val circle = new CircleGesture(gesture)
          var clockwiseness = "counterclockwise"
          if (circle.pointable.direction.angleTo(circle.normal) <= Math.PI / 4) {
            clockwiseness = "clockwise"
          }

          var sweptAngle: Double = 0
          if (circle.state ne State.STATE_START) {
            val previousUpdate = new CircleGesture(controller.frame(1).gesture(circle.id))
            sweptAngle = (circle.progress - previousUpdate.progress) * 2 * Math.PI
          }
          println("Circle id: " + circle.id + ", " + circle.state + ", progress: " + circle.progress + ", radius: " + circle.radius + ", angle: " + Math.toDegrees(sweptAngle) + ", " + clockwiseness)
        case Gesture.Type.TYPE_SWIPE =>
          val swipe = new SwipeGesture(gesture)
          println("Swipe id: " + swipe.id + ", " + swipe.state + ", position: " + swipe.position + ", direction: " + swipe.direction + ", speed: " + swipe.speed)
        case Gesture.Type.TYPE_SCREEN_TAP =>
          val screenTap = new ScreenTapGesture(gesture)
          println("Screen Tap id: " + screenTap.id + ", " + screenTap.state + ", position: " + screenTap.position + ", direction: " + screenTap.direction)
        case Gesture.Type.TYPE_KEY_TAP =>
          val keyTap = new KeyTapGesture(gesture)
          println("Key Tap id: " + keyTap.id + ", " + keyTap.state + ", position: " + keyTap.position + ", direction: " + keyTap.direction)
        case _ =>
          println("Unknown gesture type.")
      }
    }
  }
}
