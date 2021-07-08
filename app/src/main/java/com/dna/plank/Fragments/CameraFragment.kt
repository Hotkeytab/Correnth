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

package com.dna.plank.Fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.dna.plank.*
import com.dna.plank.Activities.MainActivity
import com.dna.plank.DB.DBscore
import com.dna.plank.Modeles.DrawView
import com.dna.plank.Modeles.Historique
import com.dna.plank.extras.CustomOrientationEventListener
import com.dna.plank.lib.Device
import com.dna.plank.lib.ImageDetector
import com.dna.plank.lib.KeyPointClassifier
import com.dna.plank.lib.Posenet
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit


/**
 * Basic fragments for the Camera.
 */
class CameraFragment : Fragment(), ActivityCompat.OnRequestPermissionsResultCallback {

  private val lock = Any()
  private var runClassifier = false
  private var runVocie = false
  private var textureView: TextureView? = null
  private  lateinit var chrono2: Chronometer
  private var drawView: DrawView? = null
  private var radiogroup: RadioGroup? = null
  private lateinit var detector: ImageDetector
  private  lateinit var classifier: KeyPointClassifier
  private var textView1: TextView? = null
  private var image: ImageView? = null
  private lateinit var chrono : Chronometer
  private var running = false;
  private var running2 = false;
  private var lastPause: Long = 0
  private var lastPause1: Long = 0

  private var btnstop: ImageView? = null
  private var textView: TextView? = null
  private lateinit var mp:MediaPlayer;
  private   var checkposeseAudio: Boolean = false;
  private   var checkposetrue: Boolean = false;
  private   var compteur_chrono: Int = 0;


  private var customOrientationEventListener: CustomOrientationEventListener? = null
  val ROTATION_O = 1
  val ROTATION_90 = 2
  val ROTATION_180 = 3
  val ROTATION_270 = 4
  private lateinit var panneau: RelativeLayout
  private lateinit var  layoutscore: LinearLayout;
  private lateinit var  sigpos: LinearLayout;
  private lateinit var  chronoo: LinearLayout;
  private lateinit var  btn_fin: LinearLayout;
  private val handler = Handler()


  /**
   * ID of the current [CameraDevice].
   */
  private var cameraId: String? = null

  /**
   * A [CameraCaptureSession] for camera preview.
   */
  private var captureSession: CameraCaptureSession? = null

  /**
   * A reference to the opened [CameraDevice].
   */
  private var cameraDevice: CameraDevice? = null

  /**
   * The [android.util.Size] of camera preview.
   */
  private var previewSize: Size? = null

  /**
   * An additional thread for running tasks that shouldn't block the UI.
   */
  private var backgroundThread: HandlerThread? = null

  private var backgroundvoice: HandlerThread? = null


  /**
   * A [Handler] for running tasks in the background.
   */
  private var backgroundHandler: Handler? = null
  private var backgroundVoiceHandler: Handler? = null


  /**
   * An [ImageReader] that handles image capture.
   */
  private var imageReader: ImageReader? = null

  /**
   * [CaptureRequest.Builder] for the camera preview
   */
  private var previewRequestBuilder: CaptureRequest.Builder? = null

  /**
   * [CaptureRequest] generated by [.previewRequestBuilder]
   */
  private var previewRequest: CaptureRequest? = null

  /**
   * A [Semaphore] to prevent the app from exiting before closing the camera.
   */
  private val cameraOpenCloseLock = Semaphore(1)

  /**
   * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
   */
  private val stateCallback = object : CameraDevice.StateCallback() {

    override fun onOpened(currentCameraDevice: CameraDevice) {
      // This method is called when the camera is opened.  We start camera preview here.
      cameraOpenCloseLock.release()
      cameraDevice = currentCameraDevice
      createCameraPreviewSession()
    }

    override fun onDisconnected(currentCameraDevice: CameraDevice) {
      cameraOpenCloseLock.release()
      currentCameraDevice.close()
      cameraDevice = null
    }

    override fun onError(currentCameraDevice: CameraDevice, error: Int) {
      onDisconnected(currentCameraDevice)
      activity?.finish()
    }
  }

  /**
   * A [CameraCaptureSession.CaptureCallback] that handles events related to capture.
   */
  private val captureCallback = object : CameraCaptureSession.CaptureCallback() {

    override fun onCaptureProgressed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            partialResult: CaptureResult
    ) {
    }

    override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
    ) {
    }
  }

  /**
   * Shows a [Toast] on the UI thread for the detection results.
   *
   * @param text The message to show
   */

  private  fun Voice(check: Boolean) {
    mp = MediaPlayer.create(context, R.raw.mauvaisepos);

  //  mp.start()
  /*

    val activity = activity
    activity?.runOnUiThread {

      if (!check) {
        Timer().scheduleAtFixedRate(object : TimerTask() {
          override fun run() {
            mp.start()
          }
        }, 0, 10000)


      }



    }*/
  }



  private fun showToast(text: String, text1: String, cls: Boolean) {
    val activity = activity
    activity?.runOnUiThread {
      //textView!!.text = text
      drawView!!.invalidate()

        chrono2!!.setTextColor(Color.GREEN)

        if(cls) {

          image?.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                    it.applicationContext, // Context
              R.drawable.correct
            )
          })
          textView1!!.setTextColor(Color.GREEN)
          textView1!!.text = text1
          //Starchrono(this.context)
          resumechrono1(this.context)
          resumerchrono2(this.context)


        }else {

          image?.setImageDrawable(context?.let {
            ContextCompat.getDrawable(
                    it.applicationContext, // Context
              R.drawable.incorrecte
            )
          })
          Pauserchrono2(this.context);

          textView1!!.setTextColor(Color.RED)

          textView1!!.text = text1 } } }


  /**
   * Layout the preview and buttons.
   */
  override fun onCreateView(
          inflater: LayoutInflater,
          container: ViewGroup?,
          savedInstanceState: Bundle?
  ): View? {

    return inflater.inflate(R.layout.fragment_camera, container, false) }

  /**
   * Connect the buttons to their event handler.
   */
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    textureView = view.findViewById(R.id.texture)
   btnstop = view.findViewById(R.id.FiBn)
    chrono2 = view.findViewById(R.id.chrono2)
    textView1 = view.findViewById(R.id.text1)
    image = view.findViewById(R.id.oknok);
    chrono = view.findViewById(R.id.chrono);
    textView = view.findViewById(R.id.text)

    btnstop!!.setOnClickListener {
      val builder: AlertDialog.Builder

      builder = AlertDialog.Builder(context)
      val cro1: Long = SystemClock.elapsedRealtime() - chrono.getBase()
      var cro2: Long = SystemClock.elapsedRealtime() - chrono2.getBase()
      var rate =0
      var stoppedMilliseconds = 0
      var stoppedMilliseconds1= 0
      val chronoText: String = chrono.getText().toString()
      val array = chronoText.split(":").toTypedArray()
      if (array.size == 2) {
        stoppedMilliseconds = (array[0].toInt() * 60 * 1000
                + array[1].toInt() * 1000)
      } else if (array.size == 3) {
        stoppedMilliseconds = array[0].toInt() * 60 * 60 * 1000 + array[1].toInt() * 60 * 1000 + array[2].toInt() * 1000
      }

      val chronoText2: String = chrono2.getText().toString()
      val array2 = chronoText2.split(":").toTypedArray()
      if (array2.size == 2) {
        stoppedMilliseconds1 = (array2[0].toInt() * 60 * 1000
                + array2[1].toInt() * 1000)
      } else if (array2.size == 3) {
        stoppedMilliseconds1 = array2[0].toInt() * 60 * 60 * 1000 + array2[1].toInt() * 60 * 1000 + array2[2].toInt() * 1000
      }
      //val bmi = stoppedMilliseconds1 as Float / stoppedMilliseconds as Float


      //if(stoppedMilliseconds1==0){

        //rate=0
      //}
var  t =  (stoppedMilliseconds1.toDouble() / stoppedMilliseconds.toDouble())
      t = t*100

      builder.setTitle("Synthèse exercice"+t.toUInt()+" %")
      //set message for alert dialog
      val db = DBscore(view.context)
      val histo = Historique()
      histo.setDureeEx(chrono.getText().toString())
      histo.setNomEx("Planche")
      histo.setDureepose(chrono2.getText().toString())
      histo.setRatio(t.toUInt().toString())
      //"Taux de réussite : " + rate.toString() + "%"
      builder.setMessage("Durée en position correcte :" + chrono2.getText().toString() +
              "\n" + "Durée de l'exercice :" + chrono.getText().toString()+ "\n")
      builder.setIcon(android.R.drawable.ic_dialog_alert)

      //performing positive action
      /*
      builder.setPositiveButton("Historique") { dialogInterface, which ->
        Toast.makeText(context, "", Toast.LENGTH_LONG).show()

      }  */
      //performing cancel action
      builder.setNeutralButton("Annuler") { dialogInterface, which ->
       // Toast.makeText(context, "", Toast.LENGTH_LONG).show()
      }
      //performing negative action
      builder.setNegativeButton("Menu principal") { dialogInterface, which ->
        //Toast.makeText(context,"", Toast.LENGTH_LONG).show()
        db.saveNewPerson(histo)


        val intent = Intent(view.context, MainActivity::class.java)
        startActivity(intent)

      }
      // Create the AlertDialog
      val alertDialog: AlertDialog = builder.create()
      // Set other dialog properties
      alertDialog.setCancelable(false)
      alertDialog.show()
    }





    drawView = view.findViewById(R.id.drawview)
      radiogroup = view.findViewById(R.id.radiogroup)
      radiogroup!!.setOnCheckedChangeListener { group, checkedId ->
        backgroundHandler!!.post {
          detector.setDevice(if (checkedId == R.id.radio_gpu) Device.GPU else Device.CPU)
        }
      }

    layoutscore=view.findViewById(R.id.layoutScore)
    sigpos=view.findViewById(R.id.signalpos)
    chronoo=view.findViewById(R.id.Layoutchrono)
    btn_fin=view.findViewById(R.id.btnlayout)


    customOrientationEventListener = object : CustomOrientationEventListener(this.context)  {
      override fun onSimpleOrientationChanged(orientation: Int) {
        when (orientation) {
          ROTATION_O -> {                      //rotate as on portrait
            layoutscore.animate().rotation(0F).setDuration(500).start()
            chronoo.animate().rotation(0F).setDuration(500).start()
            sigpos.animate().rotation(0F).setDuration(500).start()
            btn_fin.animate().rotation(0F).setDuration(500).start()


          }
          ROTATION_90 -> {                      //rotate as left on top
            layoutscore.animate().rotation((-90.0).toFloat()).setDuration(500).start()
            chronoo.animate().rotation(-90F).setDuration(500).start()
            sigpos.animate().rotation(-90F).setDuration(500).start()
            btn_fin.animate().rotation(-90F).setDuration(500).start()

          }
          ROTATION_270 -> {                      //rotate as right on top
            layoutscore.animate().rotation(90F).setDuration(500).start()
            chronoo.animate().rotation(90F).setDuration(500).start()
            sigpos.animate().rotation(90F).setDuration(500).start()
            btn_fin.animate().rotation(90F).setDuration(500).start()

          }
          ROTATION_180 -> {                      //rotate as upside down
            layoutscore.animate().rotation(180F).setDuration(500).start()
            chronoo.animate().rotation(180F).setDuration(500).start()
            sigpos.animate().rotation(180F).setDuration(500).start()
            btn_fin.animate().rotation(180F).setDuration(500).start()

          }

        }
      }
    }
  }

  private fun Starchrono(view: Context?){

    if(!running ){


        chrono.setBase(SystemClock.elapsedRealtime());
      //  Toast.makeText(view, checkposetrue.toString() + compteur_chrono.toString(), Toast.LENGTH_LONG).show()
        chrono.start()
        running = true



    }
  }
  private fun Starchrono2(view: Context?){
      chrono2.setBase(SystemClock.elapsedRealtime());
      chrono2.start()
      running2=true }

  private fun Pauserchrono2(view: Context?){
    if(running2){
      lastPause = chrono2.getBase() - SystemClock.elapsedRealtime();
      chrono2.stop()
      running2=false
    }}

  private  fun resumechrono1(view: Context?){
    if(!running) {
      if(lastPause1==0.toLong()){
        Starchrono(this.context)
      }
      chrono.setBase(SystemClock.elapsedRealtime() + lastPause1);
      chrono.start();
      running = true }
  }
   private fun resumerchrono2(view: Context?){
     if(!running2) {
       if(lastPause==0.toLong()){
         Starchrono2(this.context)
       }
       chrono2.setBase(SystemClock.elapsedRealtime() + lastPause);
       chrono2.start();
       running2 = true }}

  private fun Resetchrono(view: Context?){}
  private fun classifyFrame() {
    if (cameraDevice == null) return
    val estimationStartTime = SystemClock.elapsedRealtime()
    val size = detector.getInputSize()
    val bitmap = textureView!!.getBitmap(size.width, size.height)
    val person = detector.run(bitmap)
    val (direction, value, cls) = classifier.run(person)
    checkposeseAudio=cls
    bitmap.recycle()
    drawView!!.setDrawPerson(
            person, textureView!!.width.toFloat() / size.width,
            textureView!!.height.toFloat() / size.height
    )
    val lastInferenceTime = SystemClock.elapsedRealtime() - estimationStartTime
    val text = String.format(/*
            "Elapsed time %.2f s\n%s %.2f %s\n%.2f", 1.0f * lastInferenceTime / 1_000,
            direction.toString(), value, if(cls) "ok" else "nok",*/ person.score.toString())
    var text1 ="Rien à Capter"
    if (cls) {
      text1 = "Correcte"
      checkposetrue = true
    }else {
      text1 = "Incorrecte"
    }
    showToast(text, text1, cls)
  }

  /**
   * Takes photos and classify them periodically.
   */
  private val periodicClassify = object : Runnable {
    override fun run() {
      synchronized(lock) {
        if (runClassifier) {
          classifyFrame() } }
      backgroundHandler!!.post(this) } }


  private val periodVoice = object : Runnable {
    override fun run() {
      synchronized(lock) {
        if (!checkposeseAudio) {
          mp = MediaPlayer.create(context, R.raw.mauvaisepos)
          mp.start()
        }else{
          mp = MediaPlayer.create(context, R.raw.bonee)
          mp.start() } }
     // backgroundVoiceHandler!!.post(this)
      backgroundVoiceHandler!!.postDelayed(this, 15000) }}


  @Synchronized
  override fun onResume() {
    super.onResume()
    startBackgroundThread()
    startvoice() //
    runClassifier = true
    runVocie=true
    backgroundHandler!!.post {
      detector = Posenet(this.context!!, "posenet_mv1_075_float_from_checkpoints.tflite")
      //detector = Cpm(this.context!!)
      //detector = Hourglass(this.context!!)

      classifier = KeyPointClassifier() }
    backgroundHandler!!.post(periodicClassify)

    if (textureView!!.isAvailable) {
      openCamera()
    } else {
      textureView!!.surfaceTextureListener = surfaceTextureListener }

    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
    customOrientationEventListener?.enable();

    backgroundVoiceHandler!!.post(periodVoice) }

  override fun onPause() {
    closeCamera()
   // mp.pause()
    stopBackgroundThread()
    stopVoiceThread()
    synchronized(lock) {
      runClassifier = false
    }
    super.onPause()
  }

  override fun onDestroy() {
    detector.close()
    super.onDestroy()
  }

  private fun requestCameraPermission() {
    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
      ConfirmationDialog().show(childFragmentManager, FRAGMENT_DIALOG)
    } else {
      requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
    }
  }

  private fun allPermissionsGranted(grantResults: IntArray) = grantResults.all {
    it == PackageManager.PERMISSION_GRANTED
  }

  override fun onRequestPermissionsResult(
          requestCode: Int,
          permissions: Array<String>,
          grantResults: IntArray
  ) {
    if (requestCode == REQUEST_CAMERA_PERMISSION) {
      if (allPermissionsGranted(grantResults)) {
        ErrorDialog.newInstance(getString(R.string.request_permission))
                .show(childFragmentManager, FRAGMENT_DIALOG)
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
  }

  /**
   * Sets up member variables related to camera.
   */
  private fun setUpCameraOutputs() {
    val activity = activity
    val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    try {
      for (cameraId in manager.cameraIdList) {
        val characteristics = manager.getCameraCharacteristics(cameraId)

        // We don't use a front facing camera in this sample.
        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
        if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
          continue
        }

        previewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)

        this.cameraId = "1"
        return
      }
    } catch (e: CameraAccessException) {
      Log.e(TAG, e.toString())
    } catch (e: NullPointerException) {
      // Currently an NPE is thrown when the Camera2API is used but not supported on the
      // device this code runs.
      ErrorDialog.newInstance(getString(R.string.camera_error))
              .show(childFragmentManager, FRAGMENT_DIALOG)
    }
  }

  /**
   * Opens the camera specified by [CameraFragment.cameraId].
   */
  private fun openCamera() {
    val permissionCamera = getContext()!!.checkPermission(
            Manifest.permission.CAMERA, Process.myPid(), Process.myUid()
    )
    if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
      requestCameraPermission()
    }
    setUpCameraOutputs()
    val manager = activity!!.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    try {
      // Wait for camera to open - 2.5 seconds is sufficient
      if (!cameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
        throw RuntimeException("Time out waiting to lock camera opening.")
      }
      manager.openCamera("1"!!, stateCallback, backgroundHandler)
    } catch (e: CameraAccessException) {
      Log.e(TAG, e.toString())
    } catch (e: InterruptedException) {
      throw RuntimeException("Interrupted while trying to lock camera opening.", e)
    }
  }

  /**
   * Closes the current [CameraDevice].
   */
  private fun closeCamera() {
    if (captureSession == null) {
      return
    }

    try {
      cameraOpenCloseLock.acquire()
      captureSession!!.close()
      captureSession = null
      cameraDevice!!.close()
      cameraDevice = null
      imageReader?.close()
      imageReader = null
    } catch (e: InterruptedException) {
      throw RuntimeException("Interrupted while trying to lock camera closing.", e)
    } finally {
      cameraOpenCloseLock.release()
    }
  }

  /**
   * Starts a background thread and its [Handler].
   */
  private fun startBackgroundThread() {
    backgroundThread = HandlerThread("surfaceTextureListener").also { it.start() }
    backgroundHandler = Handler(backgroundThread!!.looper)

  }

  private fun startvoice() {
    backgroundvoice = HandlerThread("voiceListner").also { it.start() }
    backgroundVoiceHandler = Handler(backgroundvoice!!.looper)
  }
  /**
   * Stops the background thread and its [Handler].
   */
  private fun stopBackgroundThread() {
    backgroundThread?.quitSafely()
    try {
      backgroundThread?.join()
      backgroundThread = null
      backgroundHandler = null
    } catch (e: InterruptedException) {
      Log.e(TAG, e.toString())
    }
  }

  private fun stopVoiceThread() {
    backgroundvoice?.quitSafely()
    try {
      mp.pause()
      backgroundvoice?.join()
      backgroundvoice = null
      backgroundVoiceHandler = null
    } catch (e: InterruptedException) {
      Log.e(TAG, e.toString())
    }
  }





  /**
   * [TextureView.SurfaceTextureListener] handles several lifecycle events on a [ ].
   */
  private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {

    override fun onSurfaceTextureAvailable(
            texture: SurfaceTexture,
            width: Int,
            height: Int
    ) {
      openCamera()
    }

    override fun onSurfaceTextureSizeChanged(
            texture: SurfaceTexture,
            width: Int,
            height: Int
    ) {
    }

    override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
      return true
    }

    override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}
  }

  /**
   * Creates a new [CameraCaptureSession] for camera preview.
   */
  private fun createCameraPreviewSession() {
    try {
      // We capture images from preview
      val texture = textureView!!.surfaceTexture!!
      // We configure the size of default buffer to be the size of camera preview we want.
      texture.setDefaultBufferSize(previewSize!!.width, previewSize!!.height)
      // This is the output Surface we need to start preview.
      val previewSurface = Surface(texture)

      // We set up a CaptureRequest.Builder with the output Surface.
      previewRequestBuilder = cameraDevice!!.createCaptureRequest(
              CameraDevice.TEMPLATE_PREVIEW
      )
      previewRequestBuilder!!.addTarget(previewSurface)

      // Here, we create a CameraCaptureSession for camera preview.
      cameraDevice!!.createCaptureSession(
              listOf(previewSurface),
              object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
                  // The camera is already closed
                  if (cameraDevice == null) return

                  // When the session is ready, we start displaying the preview.
                  captureSession = cameraCaptureSession
                  try {
                    // Auto focus should be continuous for camera preview.
                    previewRequestBuilder!!.set(
                            CaptureRequest.CONTROL_AF_MODE,
                            CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                    )

                    // Finally, we start displaying the camera preview.
                    previewRequest = previewRequestBuilder!!.build()
                    captureSession!!.setRepeatingRequest(
                            previewRequest!!, captureCallback, backgroundHandler
                    )
                  } catch (e: CameraAccessException) {
                    Log.e(TAG, e.toString())
                  }
                }

                override fun onConfigureFailed(cameraCaptureSession: CameraCaptureSession) {
                  showToast("Failed", "rien à capter", false)
                }
              },
              null
      )
    } catch (e: CameraAccessException) {
      Log.e(TAG, e.toString())
    }
  }

  /**
   * Shows OK/Cancel confirmation dialog about camera permission.
   */
  class ConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                    .setMessage(R.string.request_permission)
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                      parentFragment!!.requestPermissions(
                              arrayOf(Manifest.permission.CAMERA),
                              REQUEST_CAMERA_PERMISSION
                      )
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                      parentFragment!!.activity?.finish()
                    }
                    .create()
  }

  /**
   * Shows an error message dialog.
   */
  class ErrorDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                    .setMessage(arguments!!.getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok) { _, _ -> activity!!.finish() }
                    .create()

    companion object {

      @JvmStatic
      private val ARG_MESSAGE = "message"

      @JvmStatic
      fun newInstance(message: String): ErrorDialog = ErrorDialog().apply {
        arguments = Bundle().apply { putString(ARG_MESSAGE, message) }
      }
    }
  }

  companion object {
    private const val REQUEST_CAMERA_PERMISSION = 1
    private const val FRAGMENT_DIALOG = "dialog"
    private const val PREVIEW_WIDTH = 640
    private const val PREVIEW_HEIGHT = 480

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private val ORIENTATIONS = SparseIntArray()

    init {
      ORIENTATIONS.append(Surface.ROTATION_0, 90)
      ORIENTATIONS.append(Surface.ROTATION_90, 0)
      ORIENTATIONS.append(Surface.ROTATION_180, 270)
      ORIENTATIONS.append(Surface.ROTATION_270, 180)
    }

    /**
     * Tag for the [Log].
     */
    private const val TAG = "CameraFragment"
  }
}