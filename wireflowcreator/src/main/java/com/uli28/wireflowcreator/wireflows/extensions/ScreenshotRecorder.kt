package com.uli28.wireflowcreator.wireflows.extensions

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment.DIRECTORY_PICTURES
import androidx.annotation.RequiresApi
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.uli28.wireflowcreator.wireflows.config.ConfigParameter
import com.uli28.wireflowcreator.wireflows.entities.ImageType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.CountDownLatch


// https://stackoverflow.com/questions/38519568/how-to-take-screenshot-at-the-point-where-test-fail-in-espresso
class IDTScreenCaptureProcessor : BasicScreenCaptureProcessor() {
    init {
        mTag = "IDTScreenCaptureProcessor"
        mFileNameDelimiter = "-"
        mDefaultFilenamePrefix = "Giorgos"
        mDefaultScreenshotPath = getNewFilename()
    }

    private fun getNewFilename(): File? {
        val context = getInstrumentation().targetContext.applicationContext
        return context.getExternalFilesDir(DIRECTORY_PICTURES)
    }
}

class ScreenshotRecorder(private val buildDate: String, private val initialScreenshot: Boolean) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun createScreenshot(): ImageType {
        val imageType = ImageType()
        val filename = System.currentTimeMillis().toString()

        var capture = Screenshot.capture()
        if (initialScreenshot) {
            capture = Screenshot.capture()
        }
        capture.name = filename

        imageType.width = capture.bitmap.width
        imageType.height = capture.bitmap.height
        val formatter = DateTimeFormatter.ofPattern(ConfigParameter.DEFAULT_DATETIME_FORMAT)
        val buildDate = LocalDateTime.parse(buildDate, formatter)
        val idFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
        val formattedDate = buildDate.format(idFormatter)
        imageType.filename =
            formattedDate + "/" + capture.name + "." + capture.format

        try {
            val bitmap = capture.bitmap
            val baos = ByteArrayOutputStream()
            val options = BitmapFactory.Options()
            // https://stackoverflow.com/questions/47717299/compressing-a-jpg-image-to-a-given-size-in-kb
            options.inSampleSize = 3 // If you want an image four times smaller than the original

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val decoded =
                BitmapFactory.decodeByteArray(
                    baos.toByteArray(),
                    0,
                    baos.toByteArray().size,
                    options
                )

            val compressedBaos = ByteArrayOutputStream()
            decoded.compress(Bitmap.CompressFormat.JPEG, 85, compressedBaos)
            uploadImage(
                compressedBaos.toByteArray(),
                imageType.filename
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return imageType
    }


    private fun uploadImage(data: ByteArray, filename: String) {
        // Create a storage reference from our app
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val mAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = mAuth.currentUser
        if (user != null) {
            // do your stuff
        } else {
            signInAnonymously(mAuth)
        }

        val mountainsRef = storageRef.child(filename)
        val uploadTask = mountainsRef.putBytes(data)
        ScreenshotCounter.notUploadedScreenshotCounter.incrementAndGet()
        uploadTask.addOnFailureListener {
            println("could not upload image, trying again")

            val retriedMountainsRef = storageRef.child(filename)
            val retriedUploadTask = retriedMountainsRef.putBytes(data)

            retriedUploadTask.addOnSuccessListener {
                ScreenshotCounter.notUploadedScreenshotCounter.decrementAndGet()
            }
                .addOnFailureListener {
                    println("image could not be uploaded")
                    ScreenshotCounter.notUploadedScreenshotCounter.decrementAndGet()
                }
        }.addOnSuccessListener {
            ScreenshotCounter.notUploadedScreenshotCounter.decrementAndGet()
        }
    }

    private fun signInAnonymously(mAuth: FirebaseAuth) {
        mAuth.signInAnonymously()
            .addOnSuccessListener {
                println("worked")
            }
            .addOnFailureListener {
                // Handle unsuccessful uploads
                println("didn't work")
            }
    }

    private fun getPathName(): String {
        return getInstrumentation().targetContext.applicationContext
            .getExternalFilesDir(DIRECTORY_PICTURES).toString()
    }
}