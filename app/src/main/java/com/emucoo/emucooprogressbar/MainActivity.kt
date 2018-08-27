package com.emucoo.emucooprogressbar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    lateinit var progressView:EmucooProgressView
    lateinit var bt:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressView =findViewById<EmucooProgressView>(R.id.progress)
        bt=findViewById<Button>(R.id.bt)


        progressView.setProgress(100,true)

        progressView.postDelayed({progressView.setProgress(80,true)},1000)
        progressView.postDelayed({progressView.setProgress(50,true)},1500)

        bt.setOnClickListener {
            progressView.setProgress(100,true)
            progressView.postDelayed({progressView.setProgress(80,true)},1000)
            progressView.postDelayed({progressView.setProgress(20,true)},1500)
        }




//        ObjectAnimator.ofInt(progressView,"progress",0,100).setDuration(2500).start()
    }

    override fun onStart() {
        super.onStart()
    }
}
