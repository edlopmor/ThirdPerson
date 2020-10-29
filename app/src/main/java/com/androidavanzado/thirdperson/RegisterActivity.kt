package com.androidavanzado.thirdperson

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonLanzarRegistro.setOnClickListener {
            if (editTextTextEmailAddress.text.isNotEmpty() && editTextTextPassword2.text.isNotEmpty() &&editTextTextPassword3.text.isNotEmpty()) {
                    if (editTextTextPassword2.text.toString().equals(editTextTextPassword3.text.toString())){
                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(
                            editTextTextEmailAddress.text.toString(),
                            editTextTextPassword2.text.toString()
                            ).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val email: String = editTextTextEmailAddress.toString()
                            showHome(email)
                                } else {
                                    Toast.makeText(this,"Error al crear el usuario",Toast.LENGTH_SHORT).show()
                                }
                            }
                    }else{
                        Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show()


                    }
            }else{
                Toast.makeText(this,"Alguno de los campos esta vacio",Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showHome(email: String) {

        val homeIntent: Intent = Intent(this, AuthActivity::class.java).apply {

        }
        startActivity(homeIntent)
    }
}



