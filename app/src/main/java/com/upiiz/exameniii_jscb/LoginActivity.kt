package com.upiiz.exameniii_jscb

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var L_Et_correo: EditText
    private lateinit var L_Et_contrasena: EditText
    private lateinit var Btn_ingresar: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        //supportActionBar?.title = "Login"
        supportActionBar?.hide()
        InicializarVariables()

        Btn_ingresar.setOnClickListener {
            ValidarDatos()
        }
    }

    private fun InicializarVariables() {
        L_Et_correo = findViewById(R.id.L_Et_correo)
        L_Et_contrasena = findViewById(R.id.L_Et_contrasena)
        Btn_ingresar = findViewById(R.id.Btn_ingresar)
        auth = FirebaseAuth.getInstance()

    }

    private fun ValidarDatos() {
        val correo: String = L_Et_correo.text.toString()
        val contrasena: String = L_Et_contrasena.text.toString()
        if (correo.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese su correo", Toast.LENGTH_SHORT).show()
        } else if (contrasena.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese su contraseña", Toast.LENGTH_SHORT).show()
        } else {
            IngresarUsuario(correo, contrasena)

        }
    }

    private fun IngresarUsuario(correo: String, contrasena: String) {
        auth.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    Toast.makeText(applicationContext, "Inicio de sesión", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Correo/contraseña inválidos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}