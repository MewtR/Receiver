package com.mloe.reciever.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.util.Log.ERROR
import com.mloe.reciever.services.Notifyier
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.ClassCastException
import java.lang.NumberFormatException
import java.net.BindException
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

private const val TAG = "Listener"
class Listener : Service() {

    // what's the difference between a context and a dispatcher.
    private val listenerScope = CoroutineScope(Dispatchers.IO)
    private var serverSocket: ServerSocket = ServerSocket(9999)


    override fun onBind(intent: Intent): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // open socket on port in an aync way (coroutine)
        listenerScope.launch {
            // listen for data.
            // When you get data start service that show notification
            listen()
        }
        return START_STICKY
    }

    fun listen(){
        while(!serverSocket.isClosed) {
            try {
                val socket: Socket = serverSocket.accept()
                handleConnection(socket)
            }catch(e: SocketException){
                //Socket is closed, don't do anything
            }
        }
    }
    fun handleConnection(socket: Socket){
        val socketInput: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))
        var count = 1;
        var title: String = "";
        var text: String = "";
        var priority: Int = 0;

        for(x in 1..3){
            when(count){
                1 -> title = socketInput.readLine()
                2 -> text = socketInput.readLine()
                3 -> {
                    val input = socketInput.readLine()
                    try {
                        priority = input.trim().toInt()
                    }catch(e: TypeCastException){
                        Log.e(TAG, "Could not convert third line to int: "+e.localizedMessage)
                    }catch(e: ClassCastException){
                        Log.e(TAG, "Could not convert third line to int: "+e.localizedMessage)
                    }catch(e: NumberFormatException){
                        Log.e(TAG, "Could not convert third line to int: "+e.localizedMessage)
                    }
                }
            }
            count++;
        }
        displayNotification(title,text,priority)
        socketInput.close()
        socket.close()
    }
    fun displayNotification(title: String, text: String, priority: Int){
        val intent: Intent = Intent(this, Notifyier::class.java).apply {
            putExtra("title", title)
            putExtra("text", text)
            putExtra("priority", priority)
        }
        startService(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        listenerScope.cancel()
        serverSocket.close()
    }
}
//Next step need to figure out how to handle multiple clients (simultaneously)
/*
test strings (all lines after the third one are ignored):
echo -n 'hello\nhowdy\nhow are you?\n 1 \n and another' | nc -q 1 localhost 9999 -> number format exception (priotity set to 0)
echo -n 'hello\nhowdy\n1\n 1 \n and another' | nc -q 1 localhost 9999 -> works cleanly
echo -n 'hello\nhowdy\n 1 \n 1 \n and another' | nc -q 1 localhost 9999 -> works cleanly after adding the trim function

 */