package com.example.asadordelchapulin;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class SendNotification {
    public void sendMessage(String token,String titulo,String mensaje) {
        new NetworkTask().execute("https://fcm.googleapis.com/fcm/send",token,titulo,mensaje);
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];
            String token =urls[1];
            String titulo=urls[2];
            String mensaje=urls[3];

            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            //INICIALIZAMOS EL CONTENEDOR DEL ENVIO
            HttpURLConnection httpConn;

            {
                try {
                    httpConn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //EL TIPO DE ENVIO DE DATOS VA A SER VIA POST
            try {
                httpConn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
            //CODIGO DE AUTORIZACION DE JAVA
            httpConn.setRequestProperty("Authorization", "key=" + "AAAAjdd6U3Q:APA91bH9_kJInTwLXOT7DfS4ScbNUrE8KyTE36bjx0gGjGpxlx0Px8TGkkp1eRxqY_dxnqZ_xIJTNsuA2sWevyqRHdN9Hcj_AHjfo394gfrbrIY050e5gKKtu9HLoPHSPOhtucKjcbke");
            //DEFINIMOS QUE LOS DATOS SERAN TRATADOS COMO JSON
            httpConn.setRequestProperty("Content-Type", "application/json; application/x-www-form-urlencoded; charset=UTF-8");
            //PREPARAMOS Y ENVIAMOS EL JSON
            httpConn.setDoOutput(true);
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(httpConn.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                writer.write("{" +
                        "\"to\":\""+token+"\"," +
                        "\"notification\": {" +
                        "\"title\": \""+titulo+"\"," +
                        "\"body\": \""+mensaje+"\"" +
                        "}," +
                        "\"data\": {" +
                        "}" +
                        "}");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //LIMPIAMOS LOS DATOS
            try {
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //CERRAMOS LOS DATOS
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //CERRAMOS LA CONEXION
            try {
                httpConn.getOutputStream().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //RECIBIMOS EL RESULTADO DEL ENVIO
            InputStream responseStream = null;
            try {
                responseStream = httpConn.getResponseCode() / 100 == 2
                        ? httpConn.getInputStream()
                        : httpConn.getErrorStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            //OBTENEMOS LOS RESULTADOS
            String respuesta = s.hasNext() ? s.next() : "";
            return respuesta;
        }


        @Override
        protected void onPostExecute(String result) {
            // Aquí puedes manejar el resultado de la solicitud de red
            if (result != null) {
                // Procesar la respuesta aquí
            }
        }
    }
}
