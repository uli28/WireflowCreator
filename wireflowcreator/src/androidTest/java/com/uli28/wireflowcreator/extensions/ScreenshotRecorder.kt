package com.uli28.wireflowcreator.extensions

import android.graphics.Bitmap
import android.os.Environment.DIRECTORY_PICTURES
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import java.io.File
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.ScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import com.uli28.wireflowcreator.wireflows.entities.ImageType
import java.io.IOException

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
        imageType.filename = "/resources/" + capture.name + "." + capture.format

        val processors = HashSet<ScreenCaptureProcessor>()
        processors.add(IDTScreenCaptureProcessor())

        try {
            capture.process(processors)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }

        return imageType
    }
}