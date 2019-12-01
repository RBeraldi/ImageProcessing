package com.example.macc.imageprocessing

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraCharacteristics.SENSOR_ORIENTATION
import android.hardware.camera2.CameraManager
import android.net.Uri

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.getBitmap
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.File


class MainActivity : AppCompatActivity() {



    lateinit var photo :Bitmap
    lateinit var cameraManager :CameraManager

    private val filename = "tmp"
    private val extension = "bmp"

    val MY_CAMERA_REQUEST_CODE = 1
    val RESULT_LOAD_IMAGE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        setContentView(R.layout.activity_main)


        cameraManager = getSystemService (CAMERA_SERVICE) as CameraManager


        var msg = ""

        val characteristics: CameraCharacteristics = cameraManager.getCameraCharacteristics("0")
        val sensorOrientation = characteristics[SENSOR_ORIENTATION]

        msg += sensorOrientation
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()



        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),MY_CAMERA_REQUEST_CODE)


        if (OpenCVLoader.initDebug()){
           Toast.makeText(this,"openCV Loaded. Version is: "+OpenCVLoader.OPENCV_VERSION,Toast.LENGTH_SHORT).show()
        }




    }

    fun takePhoto(v : View) {

        //This is used to get a thumbnail photo
      //  val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
     //   startActivityForResult(takePicture,MY_CAMERA_REQUEST_CODE)


        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,RESULT_LOAD_IMAGE);

    }


    fun applyFilter(v : View) {
            var  src :Mat = Mat()
            var  dst :Mat = Mat()
            photo = imageView.drawable.toBitmap()
            Utils.bitmapToMat(photo,src)
            val size = Size(45.0, 45.0)
            val point = Point(20.0, 30.0)
            Imgproc.blur(src, dst, size, point, Core.BORDER_DEFAULT)
            Utils.matToBitmap(dst,photo)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //This is used to get a thumbnail
        if (requestCode == MY_CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val extra = data?.extras
            val img =  extra?.get("data") as Bitmap
            val matrix = Matrix()
            matrix.postRotate(90f) //Rotate for
            photo = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            imageView.setImageBitmap(photo)
        }

        //Take the photo from the gallery
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            val url = data?.data
            Picasso.get().load(url).into(imageView)
            }

        }
}
