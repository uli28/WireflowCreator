package com.uli28.wireflowcreator.wireflows.extensions

import android.graphics.Bitmap
import android.os.Environment.DIRECTORY_PICTURES
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import com.uli28.wireflowcreator.wireflows.entities.ImageType
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream

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

class ScreenshotRecorder {
    fun createScreenshot(): ImageType {
        val imageType = ImageType()
        val filename = System.currentTimeMillis().toString()

        val capture = Screenshot.capture()
        capture.name = filename
        capture.format = Bitmap.CompressFormat.PNG

        imageType.width = capture.bitmap.width
        imageType.height = capture.bitmap.height
        imageType.filename =
            capture.name + "." + capture.format

//        val processors = HashSet<ScreenCaptureProcessor>()
//        processors.add(IDTScreenCaptureProcessor())
//
//        try {
//            capture.process(processors)
//        } catch (ioException: IOException) {
//            ioException.printStackTrace()
//        }

        val outFile: OutputStream?
        val file =
            File(
                getPathName() + File.separator + capture.name + "." + capture.format
            )
        try {
            outFile = FileOutputStream(file)
            val bitmap = capture.bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 26, outFile)
            outFile.flush()
            outFile.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return imageType
    }

    private fun getPathName(): String {
        return getInstrumentation().targetContext.applicationContext
            .getExternalFilesDir(DIRECTORY_PICTURES).toString()
    }
}