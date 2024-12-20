package com.upiiz.exameniii_jscb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Inicio : AppCompatActivity() {

    private lateinit var Btn_ir_login: Button
    private lateinit var Btn_ir_registros: Button


    var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        val btnCreditos = findViewById<Button>(R.id.Btn_creditos)
        btnCreditos.setOnClickListener {
            val intent = Intent(this, CreditosActivity::class.java)
            //Toast.makeText(applicationContext, "Creditos", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        Btn_ir_login = findViewById(R.id.Btn_ir_login)
        Btn_ir_registros = findViewById(R.id.Btn_ir_registros)


        Btn_ir_login.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            //Toast.makeText(applicationContext, "Ingresar", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }


        Btn_ir_registros.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            //Toast.makeText(applicationContext, "Registrar", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        //supportActionBar?.title = ""
        supportActionBar?.hide()

    }

    private fun ComprobarSesion() {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            //Toast.makeText(applicationContext, "Sesion iniciada", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        ComprobarSesion()
        super.onStart()
    }
}