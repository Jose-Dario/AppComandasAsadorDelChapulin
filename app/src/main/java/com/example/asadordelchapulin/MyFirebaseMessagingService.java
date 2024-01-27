package com.example.asadordelchapulin;

import android.media.MediaPlayer;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private MediaPlayer entrante,corregida;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // Este método se llama cuando se genera un nuevo token.
        // Puedes enviar este token al servidor si es necesario.
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        // Este método se llama cuando se recibe un mensaje.
        // Aquí puedes manejar la notificación según tus necesidades
        Notificacion notificacion=new Notificacion(getApplicationContext());
        notificacion.lanzarNotificacion(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
//        entrante=MediaPlayer.create(getApplicationContext(),R.raw.entrante);
        //corregida=MediaPlayer.create(getApplicationContext(),R.raw.corregida);
        //if(remoteMessage.getNotification().getTitle().equals("Comanda corregida")){
          //  corregida.start();
        //}
//        } else if (remoteMessage.getNotification().getTitle().equals("Comanda corregida")) {
//            corregida.start();
//        }
    }
}