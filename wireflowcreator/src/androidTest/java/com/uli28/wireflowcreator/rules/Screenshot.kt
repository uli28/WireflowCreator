package com.uli28.wireflowcreator.rules

import android.graphics.Bitmap
import android.os.Environment.DIRECTORY_PICTURES
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.runner.screenshot.BasicScreenCaptureProcessor
import androidx.test.runner.screenshot.ScreenCaptureProcessor
import androidx.test.runner.screenshot.Screenshot
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.IOException

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

class ScreenshotTestRule : TestWatcher() {
    override fun finished(description: Description?) {
        super.finished(description)

        val className = description?.testClass?.simpleName ?: "NullClassname"
        val methodName = description?.methodName ?: "NullMethodName"
        val filename = "$className - $methodName"

        val capture = Screenshot.capture()
        capture.name = filename
        capture.format = Bitmap.CompressFormat.PNG

        val processors = HashSet<ScreenCaptureProcessor>()
        processors.add(IDTScreenCaptureProcessor())

        try {
            capture.process(processors)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }
}