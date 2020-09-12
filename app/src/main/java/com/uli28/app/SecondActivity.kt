package com.uli28.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.uli28.wireflowcreator.app.R
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        button_back.setOnClickListener {
            onBackPressed()
        }
    }
}
