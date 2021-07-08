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

package com.dna.plank.lib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import android.util.Size
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import java.nio.ByteBuffer

enum class Device {
  CPU,
  GPU,
  NNAPI,
}

abstract class ImageDetector(
        private val context: Context,
        private val filename: String
): AutoCloseable {

  /** An Interpreter for the TFLite model.   */
  protected var interpreter: Interpreter? = null
  private var device: Device? = null
  protected var mBodyParts: List<BodyPart>? = null

  init {
      setDevice(Device.CPU)
  }

  override fun close() {
    interpreter?.close()
    interpreter = null
  }

  fun setDevice(otherDevice: Device) {
    if (device == otherDevice) {
      return
    }
    val options = Interpreter.Options()
    options.setNumThreads(NUM_LITE_THREADS)
    when (otherDevice) {
      Device.CPU -> { }
      Device.GPU -> {
        val gpuDelegate = GpuDelegate()
        options.addDelegate(gpuDelegate)
      }
      Device.NNAPI -> options.setUseNNAPI(true)
    }
    interpreter = Interpreter(loadModelFile(filename, context), options)
    device = otherDevice
  }

  /** Preload and memory map the model file, returning a MappedByteBuffer containing the model. */
  private fun loadModelFile(path: String, context: Context): MappedByteBuffer {
    val fileDescriptor = context.assets.openFd(path)
    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
    return inputStream.channel.map(
            FileChannel.MapMode.READ_ONLY, fileDescriptor.startOffset, fileDescriptor.declaredLength
    )
  }

  fun getInputSize(): Size {
    val shape = interpreter!!.getInputTensor(0).shape()
    return Size(shape[2], shape[1])
  }

  protected abstract fun initInputArray(bitmap: Bitmap): ByteBuffer

  protected abstract fun initOutputMap(): HashMap<Int, Any>

  protected abstract fun decodeOutputMap(outputMap: HashMap<Int, Any>): Triple<IntArray, IntArray, FloatArray>

  private fun getPerson(
    xCoords: IntArray, yCoords: IntArray, confidenceScores: FloatArray
  ): Person {
    val person = Person()
    val keyPoints = HashMap<BodyPart, KeyPoint>()
    mBodyParts!!.forEachIndexed { idx, it ->
      keyPoints[it] = KeyPoint(Position(xCoords[idx], yCoords[idx]), confidenceScores[idx])
    }
    person.keyPoints = keyPoints
    person.score = confidenceScores.sum() / confidenceScores.size
    return person
  }

  /**
   * Estimates the pose for a single person.
   * args:
   *      bitmap: image bitmap of frame that should be processed
   * returns:
   *      person: a Person object containing data about keypoint locations and confidence scores
   */
  @Suppress("UNCHECKED_CAST")
  fun run(bitmap: Bitmap): Person {
    val estimationStartTime = SystemClock.elapsedRealtime()
    val matrix = Matrix()
    //matrix.postRotate(270.0F)
    matrix.preRotate(180.0F)
    val size = getInputSize()
    val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, size.width, size.height,
            matrix, true
    )
    val inputArray = arrayOf(initInputArray(rotatedBitmap))
    Log.i(TAG, String.format(
        "Scaling to [-1,1] took %.2f ms",
        1.0f * (SystemClock.elapsedRealtime() - estimationStartTime) / 1_000
      )
    )

    val outputMap = initOutputMap()

    val inferenceStartTime = SystemClock.elapsedRealtime()
    interpreter!!.runForMultipleInputsOutputs(inputArray, outputMap)
    val lastInferenceTime = SystemClock.elapsedRealtime() - inferenceStartTime
    Log.i(TAG, String.format("Interpreter took %.2f ms", 1.0f * lastInferenceTime / 1_000))

    val (xCoords, yCoords, confidenceScores) = decodeOutputMap(outputMap)
    /*yCoords.forEachIndexed { idx, y ->
      yCoords[idx] = size.width - y
    }*/
    xCoords.forEachIndexed { idx, x ->
      xCoords[idx] = size.width - x
    }
    yCoords.forEachIndexed { idx, y ->
      yCoords[idx] = size.height - y
    }
    //return getPerson(yCoords, xCoords, confidenceScores)
    return getPerson(xCoords, yCoords, confidenceScores)
  }

  companion object {

    /** Tag for the [Log].  */
    private const val TAG = "ImageDetector"

    private const val NUM_LITE_THREADS = 4

  }
}
