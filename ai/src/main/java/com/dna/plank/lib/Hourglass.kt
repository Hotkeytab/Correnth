package com.dna.plank.lib

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.exp


class Hourglass(
    context: Context, filename: String = "hourglass_model.tflite"
) : ImageDetector(context, filename) {

    init {
        mBodyParts = listOf(
            BodyPart.TOP,
            BodyPart.NECK,
            BodyPart.LEFT_SHOULDER,
            BodyPart.LEFT_ELBOW,
            BodyPart.LEFT_WRIST,
            BodyPart.LEFT_HIP,
            BodyPart.LEFT_KNEE,
            BodyPart.LEFT_ANKLE,
            BodyPart.RIGHT_SHOULDER,
            BodyPart.RIGHT_ELBOW,
            BodyPart.RIGHT_WRIST,
            BodyPart.RIGHT_HIP,
            BodyPart.RIGHT_KNEE,
            BodyPart.RIGHT_ANKLE
        )
    }

    /** Returns value within [0,1].   */
    private fun sigmoid(x: Float): Float {
        return (1.0f / (1.0f + exp(-x)))
    }

    /**
     * Scale the image to a byteBuffer.
     */
    override fun initInputArray(bitmap: Bitmap): ByteBuffer {
        val bytesPerChannel = 4
        val inputChannels = 3
        val batchSize = 1
        val inputBuffer = ByteBuffer.allocateDirect(
            batchSize * bytesPerChannel * bitmap.height * bitmap.width * inputChannels
        )
        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.rewind()

        val intValues = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        for (pixelValue in intValues) {
            inputBuffer.putFloat((pixelValue and 0xFF).toFloat())
            inputBuffer.putFloat((pixelValue shr 8 and 0xFF).toFloat())
            inputBuffer.putFloat((pixelValue shr 16 and 0xFF).toFloat())
        }
        return inputBuffer
    }

    /**
     * Initializes an outputMap of 1 * x * y * z FloatArrays for the model processing to populate.
     */
    override fun initOutputMap(): HashMap<Int, Any> {
        val outputMap = HashMap<Int, Any>()

        // 1 * 9 * 9 * 14 contains heatmaps
        val heatmapsShape = interpreter!!.getOutputTensor(0).shape()
        outputMap[0] = Array(heatmapsShape[0]) {
            Array(heatmapsShape[1]) {
                Array(heatmapsShape[2]) { FloatArray(heatmapsShape[3]) }
            }
        }

        return outputMap
    }

    override fun decodeOutputMap(outputMap: HashMap<Int, Any>): Triple<IntArray, IntArray, FloatArray> {
        val heatmaps = outputMap[0] as Array<Array<Array<FloatArray>>>

        val height = heatmaps[0].size
        val width = heatmaps[0][0].size
        val numKeyPoints = heatmaps[0][0][0].size

        // Finds the (row, col) locations of where the keypoints are most likely to be.
        val keypointPositions = Array(numKeyPoints) { Pair(0, 0) }
        for (keypoint in 0 until numKeyPoints) {
            var maxVal = heatmaps[0][0][0][keypoint]
            var maxRow = 0
            var maxCol = 0
            for (row in 0 until height) {
                for (col in 0 until width) {
                    if (heatmaps[0][row][col][keypoint] > maxVal) {
                        maxVal = heatmaps[0][row][col][keypoint]
                        maxRow = row
                        maxCol = col
                    }
                }
            }
            keypointPositions[keypoint] = Pair(maxRow, maxCol)
        }

        val xCoords = IntArray(numKeyPoints)
        val yCoords = IntArray(numKeyPoints)
        val confidenceScores = FloatArray(numKeyPoints)
        val inputSize = getInputSize()
        keypointPositions.forEachIndexed { idx, position ->
            val positionY = keypointPositions[idx].first
            val positionX = keypointPositions[idx].second
            yCoords[idx] = (
                    position.first / height.toFloat() * inputSize.height
                    ).toInt()
            xCoords[idx] = (
                    position.second / width.toFloat() * inputSize.width
                    ).toInt()
            confidenceScores[idx] = heatmaps[0][positionY][positionX][idx]
        }

        return Triple(xCoords, yCoords, confidenceScores)
    }

    companion object {

        /** Tag for the [Log].  */
        private const val TAG = "Hourglass"

    }
}