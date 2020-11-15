package com.androidavanzado.thirdperson
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

class MyFireBaseMessagingService : FirebaseMessagingService() {


    //Recibir notificacion en primer plano .
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Looper.prepare()
        java.util.concurrent.Executor {
            Toast.makeText(baseContext, remoteMessage.notification?.title,Toast.LENGTH_LONG).show()
        }
        Looper.loop()
    }
}