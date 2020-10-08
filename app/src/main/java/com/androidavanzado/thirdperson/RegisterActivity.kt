package com.androidavanzado.thirdperson

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
/*
        if (editTextEmail.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editTextEmail.text.toString(),editTextTextPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            showRegisterActivity()

                        }else{
                            ShowAlert()
                        }
                    }*/
    }
}