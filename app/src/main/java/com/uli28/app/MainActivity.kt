package com.uli28.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.uli28.wireflowcreator.app.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        EspressoIdlingResource.increment()
        val tv1: TextView = findViewById(R.id.activity_main_title)
        tv1.text = "Still loading"
        // again we use a kotlin coroutine to simulate a 3 second network request:
        val job = GlobalScope.launch {
            // our network call starts
            delay(3000)
        }
        job.invokeOnCompletion {
            runOnUiThread { tv1.text = "Main Activity" }

            // our network call ended!
            EspressoIdlingResource.decrement()
        }



        button_next_activity.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            EspressoIdlingResource.increment()
            // again we use a kotlin coroutine to simulate a 3 second network request:
            val job = GlobalScope.launch {
                // our network call starts
                delay(3000)
            }
            job.invokeOnCompletion {
                // our network call ended!
                EspressoIdlingResource.decrement()
                startActivity(intent)

            }
            startActivity(intent)

        }
    }
}
