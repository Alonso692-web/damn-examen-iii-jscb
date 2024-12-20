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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var R_Et_nombre_usuario: EditText
    private lateinit var R_Et_correo: EditText
    private lateinit var R_Et_contrasena: EditText
    private lateinit var R_Et_confirmar_contrasena: EditText
    private lateinit var Btn_registrar: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        //supportActionBar?.title = "Registro"
        supportActionBar?.hide()
        InicializarVariables()

        Btn_registrar.setOnClickListener{
            ValidarDatos()
        }

    }


    private fun InicializarVariables(){
        R_Et_nombre_usuario = findViewById(R.id.R_Et_nombre_usuario)
        R_Et_correo = findViewById(R.id.R_Et_correo)
        R_Et_contrasena = findViewById(R.id.R_Et_contrasena)
        R_Et_confirmar_contrasena = findViewById(R.id.R_Et_confirmar_contrasena)
        Btn_registrar = findViewById(R.id.Btn_registrar)

        auth = FirebaseAuth.getInstance()

    }

    private fun ValidarDatos() {
        val nombre_usuario: String = R_Et_nombre_usuario.text.toString()
        val correo: String = R_Et_correo.text.toString()
        val contrasena: String = R_Et_contrasena.text.toString()
        val confirmar_contrasena: String = R_Et_confirmar_contrasena.text.toString()

        if(nombre_usuario.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese un nombre de usuario", Toast.LENGTH_SHORT).show()
        }
        else if(correo.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese un correo", Toast.LENGTH_SHORT).show()
        }
        else if(contrasena.isEmpty()){
            Toast.makeText(applicationContext, "Ingrese una contraseña", Toast.LENGTH_SHORT).show()
        }
        else if(confirmar_contrasena.isEmpty()){
            Toast.makeText(applicationContext, "Confirme su contraseña", Toast.LENGTH_SHORT).show()
        }
        else if(contrasena != confirmar_contrasena){
            Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        }
        else{
            RegistrarUsuario(correo, contrasena)
         }
    }

    private fun RegistrarUsuario(correo: String, contrasena: String) {
        auth.createUserWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var uid: String = ""
                    uid = auth.currentUser!!.uid
                    reference =
                        FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid)

                    val hashmap = HashMap<String, Any>()
                    val h_nombre_usuario: String = R_Et_nombre_usuario.text.toString()
                    val h_correo: String = R_Et_correo.text.toString()

                    hashmap["uid"] = uid
                    hashmap["nombre_usuario"] = h_nombre_usuario
                    hashmap["correo"] = h_correo
                    hashmap["imagen"] = ""
                    hashmap["buscar"] = h_nombre_usuario.lowercase()

                    reference.updateChildren(hashmap)
                        .addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {
                            val intent = Intent(this, MainActivity::class.java)
                            Toast.makeText(
                                applicationContext,
                                "Registro exitoso",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(intent)
                            finish()

                        }
                    }
                        .addOnFailureListener { e ->
                            Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT)
                                .show()

                        }


                } else {
                    Toast.makeText(applicationContext, "Error al registrar", Toast.LENGTH_SHORT)
                        .show()
                }

            }.addOnFailureListener { e ->
                Toast.makeText(applicationContext, "${e.message}", Toast.LENGTH_SHORT).show()
            }

    }
}