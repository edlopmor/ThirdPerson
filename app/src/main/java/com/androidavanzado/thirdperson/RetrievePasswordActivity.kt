package com.androidavanzado.thirdperson

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_retrieve_password.*

class RetrievePasswordActivity : AppCompatActivity() {
    var mAuth :FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_password)

        buttonSendEmail.setOnClickListener {
            var emailUsuario : String = editTextEmailRetrievePassword.text.toString()
            if (emailUsuario.isNotEmpty()){
                    //TODO operacion when que acepte distintos idiomas.
                   mAuth.setLanguageCode("es")
                   mAuth.sendPasswordResetEmail(emailUsuario).addOnCompleteListener(this){
                       task ->
                       if (task.isSuccessful){
                            Toast.makeText(this,"Se ha enviado un correo para restablecer su contraseña",Toast.LENGTH_LONG).show()
                       }else {
                            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                       }
                   }
            }else{
                editTextEmailRetrievePassword.hint = "Introduzca un email correcto"
            }

        }

    }
    //Funcion que compara el campo email con una expresion regular para comprobar si es correcto.
    private fun  ComprobarEmail (emailAcomprobar :String ): Boolean {
        var emailCorrecto : Boolean = false
        val emailPattern ="[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"
        if (emailAcomprobar.trim(){it <= ' '}.matches(emailPattern.toRegex())){
            emailCorrecto = true
        }
        return emailCorrecto

    }
}