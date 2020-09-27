package com.uli28.app

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.uli28.wireflowcreator.app.R
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

//        EspressoIdlingResource.increment()
//        val tv1: TextView = findViewById(R.id.activity_secondary_title)
//        tv1.text = "Still loading"
//        // again we use a kotlin coroutine to simulate a 3 second network request:
//        val job = GlobalScope.launch {
//            // our network call starts
//            delay(3000)
//        }
//        job.invokeOnCompletion {
//            runOnUiThread { tv1.text = "Second Activity" }
//
//            // our network call ended!
//            EspressoIdlingResource.decrement()
//        }

        button_back.setOnClickListener {
            onBackPressed()
        }
    }
}
