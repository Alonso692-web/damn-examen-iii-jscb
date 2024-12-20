package com.upiiz.exameniii_jscb

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth


import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : AppCompatActivity() {


    private lateinit var messageAdapter: MessageAdapter
    private lateinit var rv_mensajes: RecyclerView
    private lateinit var btn_enviarMensaje: Button
    private lateinit var et_mensaje: EditText
    private lateinit var tv_nombre_usuario: TextView


    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference


    val currentTimeMillis = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = dateFormat.format(Date(currentTimeMillis))


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Save token to your database
                saveTokenToDatabase(token)
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Check if user is logged in
        if (currentUser == null) {
            startActivity(Intent(this, Inicio::class.java))
            finish()
            return
        }

        // Initialize views
        rv_mensajes = findViewById(R.id.rv_mensajes)
        btn_enviarMensaje = findViewById(R.id.btn_enviarMensaje)
        et_mensaje = findViewById(R.id.et_mensaje)
        tv_nombre_usuario = findViewById(R.id.nombre_usuario)
        val enviarImagen = findViewById<ImageButton>(R.id.btn_enviar_Imagen)


        // Set up RecyclerView
        messageAdapter = MessageAdapter()
        rv_mensajes.layoutManager = LinearLayoutManager(this)
        rv_mensajes.adapter = messageAdapter

        // Set up database reference
        databaseReference = FirebaseDatabase.getInstance().reference.child("messages")
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference.child("images")

        // Set user name
        tv_nombre_usuario.text = currentUser.displayName ?: currentUser.email ?: "Usuario"

        // Send message
        btn_enviarMensaje.setOnClickListener {
            sendMessage()
            scrollToBottom()
            et_mensaje.text.clear()
        }

        // Listen for messages
        listenForMessages()


        // Edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets

        }
        enviarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Mandar Imagen"), 1)
        }







        supportActionBar?.title = "Chats"
        //supportActionBar?.hide()
    }

    private fun scrollToBottom() {
        rv_mensajes.post {
            rv_mensajes.scrollToPosition(messageAdapter.itemCount - 1)
        }
    }

    private fun sendMessage() {
        val messageText = et_mensaje.text.toString().trim()
        if (messageText.isEmpty()) return

        val currentUser = auth.currentUser ?: return
        val messageId = databaseReference.push().key ?: return

        // Create message object
        val message = Message(
            mensaje = messageText,

            nombre = currentUser.displayName ?: currentUser.email ?: "Usuario",
            hora = formattedTime

        )

        // Send message to Firebase
        databaseReference.child(messageId).setValue(message)
            .addOnSuccessListener {
                et_mensaje.text.clear()
                scrollToBottom()

                // Send push notification
                sendPushNotification(message)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendPushNotification(message: Message) {
        // You'll need a server or cloud function to send FCM messages
        // This is a simplified example
        val payload = mapOf(
            "to" to "/topics/chat", // Or specific user token
            "notification" to mapOf(
                "title" to message.nombre,
                "body" to message.mensaje
            ),
            "data" to mapOf(
                "sender" to message.nombre,
                "message" to message.mensaje
            )
        )
    }

    private fun listenForMessages() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    messageAdapter.addMessage(it)
                    scrollToBottom()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error al cargar mensajes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            storageReference.child(formattedTime).putFile(imageUri!!)
                .addOnSuccessListener {
                    it.storage.downloadUrl.addOnSuccessListener { uri ->
                        val message = Message(
                            mensaje = "Foto",
                            nombre = auth.currentUser?.email ?: "Usuario",
                            foto = uri.toString(),
                            type_mensaje = "image",
                            hora = formattedTime
                        )
                        databaseReference.push().setValue(message)
                    }

                }


        }
    }

    private fun saveTokenToDatabase(token: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(user.uid)
                .child("fcmToken")
                .setValue(token)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_salir -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, Inicio::class.java)
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}







