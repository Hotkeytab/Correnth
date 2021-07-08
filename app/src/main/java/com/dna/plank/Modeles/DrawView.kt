/*
 * Copyright 2018 Zihua Zeng (edvard_hua@live.com), Lang Feng (tearjeaker@hotmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dna.plank.Modeles

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Style.FILL
import android.util.AttributeSet
import android.view.View
import com.dna.plank.R
import com.dna.plank.lib.Person
import com.dna.plank.lib.BodyPart

/**
 * Created by edvard on 18-3-23.
 */

class DrawView : View {

  private var mPerson: Person?= null
  private var mRatioWidth: Float = 1F
  private var mRatioHeight: Float = 1F
  private val mMinPersonScore: Float = 0.3F

  private val mColorArray: HashMap<BodyPart, Int> = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
    hashMapOf(
      BodyPart.TOP to resources.getColor(R.color.color_top, null),
      BodyPart.NECK to resources.getColor(R.color.color_neck, null),
      BodyPart.NOSE to resources.getColor(R.color.color_nose, null),
      BodyPart.LEFT_EYE to resources.getColor(R.color.color_left_eye, null),
      BodyPart.RIGHT_EYE to resources.getColor(R.color.color_right_eye, null),
      BodyPart.LEFT_EAR to resources.getColor(R.color.color_left_ear, null),
      BodyPart.RIGHT_EAR to resources.getColor(R.color.color_right_ear, null),
      BodyPart.LEFT_SHOULDER to resources.getColor(R.color.color_l_shoulder, null),
      BodyPart.LEFT_ELBOW to resources.getColor(R.color.color_l_elbow, null),
      BodyPart.LEFT_WRIST to resources.getColor(R.color.color_l_wrist, null),
      BodyPart.RIGHT_SHOULDER to resources.getColor(R.color.color_r_shoulder, null),
      BodyPart.RIGHT_ELBOW to resources.getColor(R.color.color_r_elbow, null),
      BodyPart.RIGHT_WRIST to resources.getColor(R.color.color_r_wrist, null),
      BodyPart.LEFT_HIP to resources.getColor(R.color.color_l_hip, null),
      BodyPart.LEFT_KNEE to resources.getColor(R.color.color_l_knee, null),
      BodyPart.LEFT_ANKLE to resources.getColor(R.color.color_l_ankle, null),
      BodyPart.RIGHT_HIP to resources.getColor(R.color.color_r_hip, null),
      BodyPart.RIGHT_KNEE to resources.getColor(R.color.color_r_knee, null),
      BodyPart.RIGHT_ANKLE to resources.getColor(R.color.color_r_ankle, null)
    )
  } else {
    TODO("VERSION.SDK_INT < M")
  }
  private val mBodyParts = listOf(
    BodyPart.LEFT_ELBOW,
    BodyPart.RIGHT_ELBOW,
    BodyPart.LEFT_SHOULDER,
    BodyPart.RIGHT_SHOULDER,
    BodyPart.LEFT_HIP,
    BodyPart.RIGHT_HIP,
    BodyPart.LEFT_KNEE,
    BodyPart.RIGHT_KNEE,
    BodyPart.LEFT_ANKLE,
    BodyPart.RIGHT_ANKLE
  )
  private val mBodyJoints = listOf(
    //Pair(BodyPart.LEFT_WRIST, BodyPart.LEFT_ELBOW),
    Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_SHOULDER),
    Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
    Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
    //Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
    Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
    Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
    Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_SHOULDER),
    Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
    Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
    Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
    Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
  )

  private val circleRadius: Float by lazy {
    (3 * resources.displayMetrics.density)
  }

  private val mPaint: Paint by lazy {
    Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG).apply {
      style = FILL
      strokeWidth = (2 * resources.displayMetrics.density)
      textSize = (13 * resources.displayMetrics.scaledDensity)
    }
  }

  constructor(context: Context) : super(context)

  constructor(
    context: Context,
    attrs: AttributeSet?
  ) : super(context, attrs)

  constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
  ) : super(context, attrs, defStyleAttr)

  fun setDrawPerson(person: Person, ratioWidth: Float, ratioHeight: Float){
    mPerson = person
    mRatioWidth = ratioWidth
    mRatioHeight = ratioHeight
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (mPerson == null) return
    if (mPerson!!.score < mMinPersonScore) return
    for (bodyPart in mBodyParts) {
      val keyPoint = mPerson!!.keyPoints[bodyPart]!!
      mPaint.color = mColorArray[bodyPart]!!
      canvas.drawCircle(
              mRatioWidth * keyPoint.position.x,
              mRatioHeight * keyPoint.position.y,
              circleRadius, mPaint
      )
    }
    mPaint.color = 0xff6fa8dc.toInt()
    for (bodyJoint in mBodyJoints) {
      val keyPoint1 = mPerson!!.keyPoints[bodyJoint.first]
      val keyPoint2 = mPerson!!.keyPoints[bodyJoint.second]
      canvas.drawLine(
              mRatioWidth * keyPoint1!!.position.x,
              mRatioHeight * keyPoint1.position.y,
              mRatioWidth * keyPoint2!!.position.x,
              mRatioHeight * keyPoint2.position.y, mPaint
      )
    }
  }
}
