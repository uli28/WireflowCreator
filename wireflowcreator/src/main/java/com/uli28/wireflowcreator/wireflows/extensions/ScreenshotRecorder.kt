package com.uli28.wireflowcreator.wireflows.extensions

import android.graphics.Bitmap
import android.os.Environment.DIRECTORY_PICTURES
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.uli28.wireflowcreator.wireflows.entities.ImageType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException

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

class ScreenshotRecorder(private val buildDate:String, private val initialScreenshot: Boolean) {
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
        imageType.filename =
            buildDate + "/" + capture.name + "." + capture.format

//        val processors = HashSet<ScreenCaptureProcessor>()
//        processors.add(IDTScreenCaptureProcessor())
//
//        try {
//            capture.process(processors)
//        } catch (ioException: IOException) {
//            ioException.printStackTrace()
//        }

        try {
            val bitmap = capture.bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 26, baos)
            uploadImage(
                baos.toByteArray(),
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
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
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