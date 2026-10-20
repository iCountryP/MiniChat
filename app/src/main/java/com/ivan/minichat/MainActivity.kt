package com.ivan.minichat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI

class MainActivity : AppCompatActivity() {

    var webSocketClient: WebSocketClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }   

        val tvChat = findViewById<TextView>(R.id.tvChat)
        val etMensaje = findViewById<EditText>(R.id.etMensaje)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)
        // ejecutar en terminal para abrir el server
        // java -cp "lib/*;." WsServidor
        val serverUri = URI("ws://10.0.2.2:8888")
        webSocketClient = object : WebSocketClient(serverUri) {

            override fun onOpen(handshakedata: ServerHandshake?) {
                // se ejecuta cuando conseguimos conectarnos al servidor
                runOnUiThread {
                    tvChat.append("Conectado al servidor\n")
                }
            }

            override fun onMessage(message: String?) {
                // se ejecuta cuando recibimos un mensaje del servidor
                runOnUiThread {
                    tvChat.append("$message\n")
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                // se ejecuta cuando se pierde o se cierra la conexión
                runOnUiThread {
                    tvChat.append("Desconectado del servidor\n")
                }
            }

            override fun onError(ex: Exception?) {
                // se ejecuta cuando ocurre algun error de conexión
                Log.e("MiniChat", "Error de conexión WebSocket", ex)

                runOnUiThread {
                    tvChat.append("Error: ${ex?.message}\n")
                }
            }
        }


        webSocketClient?.connect()
        tvChat.append("Intentando conectar con el servidor...\n")



        btnEnviar.setOnClickListener {
            // obtenemos el mensaje que ha escrito el usuario
            val mensaje = etMensaje.text.toString()

            // comprobamos que el mensaje no esté vacío y que estemos conectados
            if (mensaje.isNotBlank() && webSocketClient?.isOpen == true) {

                // enviamos el mensaje al servidor WebSocket
                webSocketClient?.send(mensaje)

                // limpiamos el campo de texto despues de enviarlo
                etMensaje.setText("")
            }
        }



    }
}