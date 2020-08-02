package com.mloe.reciever

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import com.mloe.reciever.services.Listener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val switch: Switch = findViewById(R.id.listenerswitch)
        switch.setOnCheckedChangeListener{ _, isChecked ->
            if  (isChecked){
                startListener()
            }else{
                stopListener()
            }
        }
    }
    fun startListener(){
        val intent: Intent = Intent(this, Listener::class.java)
        startService(intent)
    }
    fun stopListener(){
        val intent: Intent = Intent(this, Listener::class.java)
        stopService(intent)
    }
}