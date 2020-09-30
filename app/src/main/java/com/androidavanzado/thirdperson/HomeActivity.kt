package com.androidavanzado.thirdperson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bundle:Bundle? = intent.extras
        val email:String? =bundle?.getString("email")
        val provider:String? = bundle?.getString("proveedor")

        //Setup
        setup(email?:"",provider?:"")
    }

    private fun setup(email: String,provider: String) {
        textViewUser.text = email
        textViewPassword.text = provider
    }
}