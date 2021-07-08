package com.dna.plank.lib

enum class BodyPart {
    TOP,
    NECK,
    NOSE,
    LEFT_EYE,
    RIGHT_EYE,
    LEFT_EAR,
    RIGHT_EAR,
    LEFT_SHOULDER,
    LEFT_ELBOW,
    LEFT_WRIST,
    LEFT_HIP,
    LEFT_KNEE,
    LEFT_ANKLE,
    RIGHT_SHOULDER,
    RIGHT_ELBOW,
    RIGHT_WRIST,
    RIGHT_HIP,
    RIGHT_KNEE,
    RIGHT_ANKLE
}

class Position(var x: Int, var y: Int)

class KeyPoint(var position: Position, var score: Float)

class Person {
    var keyPoints = HashMap<BodyPart, KeyPoint>()
    var score: Float = 0.0f
}

