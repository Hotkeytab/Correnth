package com.dna.plank.lib

import kotlin.math.atan2
import kotlin.math.PI
import kotlin.math.abs


class KeyPointClassifier{

    enum class Direction {
        RIGHT, LEFT, FORWARD, BACKWARD, OTHER
    }

    private val mMinPersonScore: Float = 0.3F

    private fun computeDirection(
            shoulder: Position, hip: Position, knee: Position, ankle: Position
    ): Direction {
        return if ((abs(shoulder.x - hip.x) < EPS) and (abs(knee.x - hip.x) < EPS) and
                (abs(ankle.x - hip.x) < EPS)
        ){
            if (shoulder.y < hip.y){
                Direction.LEFT
            } else {
                Direction.RIGHT
            }
        } else {
            Direction.OTHER
        }
    }

    private fun computeAngle(a: Position, b: Position, c: Position): Float{
        val sp = (a.x - b.x) * (c.x - b.x) + (a.y - b.y) * (c.y - b.y)
        val det = (a.x - b.x) * (c.y - b.y) - (a.y - b.y) * (c.x - b.x)
        var angle = atan2(det.toFloat(), sp.toFloat())
        if (angle < 0) {
            angle += 2 * PI.toFloat()
        }
        return angle
    }

    private fun decisionTreeClassify(v: Float): Boolean{
        val vMin = 9.1 // 9.349
        val vMax = 9.95 // 9.855
        return (vMin <= v) and (v <= vMax)
    }

    fun run(person: Person): Triple<Direction, Float, Boolean> {
        if (person.score < mMinPersonScore)
            return Triple(Direction.OTHER, -1.0F, false)
        val lShoulder = person.keyPoints[BodyPart.LEFT_SHOULDER]!!.position
        val rShoulder = person.keyPoints[BodyPart.RIGHT_SHOULDER]!!.position
        val lHip = person.keyPoints[BodyPart.LEFT_HIP]!!.position
        val rHip = person.keyPoints[BodyPart.RIGHT_HIP]!!.position
        val lKnee = person.keyPoints[BodyPart.LEFT_KNEE]!!.position
        val rKnee = person.keyPoints[BodyPart.RIGHT_KNEE]!!.position
        val lAnkle = person.keyPoints[BodyPart.LEFT_ANKLE]!!.position
        val rAnkle = person.keyPoints[BodyPart.RIGHT_ANKLE]!!.position
        val shoulder = Position(
                (0.5 * (lShoulder.x + rShoulder.x)).toInt(),
                (0.5 * (lShoulder.y + rShoulder.y)).toInt()
        )
        val hip = Position(
                (0.5 * (lHip.x + rHip.x)).toInt(),
                (0.5 * (lHip.y + rHip.y)).toInt()
        )
        val knee = Position(
                (0.5 * (lKnee.x + rKnee.x)).toInt(),
                (0.5 * (lKnee.y + rKnee.y)).toInt()
        )
        val ankle = Position(
                (0.5 * (lAnkle.x + rAnkle.x)).toInt(),
                (0.5 * (lAnkle.y + rAnkle.y)).toInt()
        )
        when (computeDirection(shoulder, hip, knee, ankle)){
            Direction.RIGHT -> {
                val angleKneeHipShoulder = computeAngle(knee, hip, shoulder)
                val angleAnkleKneeHip = computeAngle(ankle, knee, hip)
                val v = 2 * angleKneeHipShoulder + angleAnkleKneeHip
                return Triple(Direction.RIGHT, v, decisionTreeClassify(v))
            }
            Direction.LEFT -> {
                val angleShoulderHipKnee = computeAngle(shoulder, hip, knee)
                val angleHipKneeAnkle = computeAngle(hip, knee, ankle)
                val v = 2 * angleShoulderHipKnee + angleHipKneeAnkle
                return Triple(Direction.LEFT, v, decisionTreeClassify(v))
            }
            else -> {
                return Triple(Direction.OTHER, -1.0f, false)
            }
        }
    }

    companion object {
        private const val EPS = 40
    }
}
