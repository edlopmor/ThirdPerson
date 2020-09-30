package com.androidavanzado.thirdperson

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    enum class ProviderType {
        BASIC
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("Message","Integracion de firebase Completa")
        analytics.logEvent("InitScreen",bundle)


        //Setup
        Setup();
    }

    private fun Setup() {
        textViewRegistro.setOnClickListener {
            if (editTextEmail.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editTextEmail.text.toString(),editTextTextPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email?:"",ProviderType.BASIC)

                        }else{
                            ShowAlert()
                        }
                    }
            }
        }
        buttonAcceder.setOnClickListener {
            if (editTextEmail.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()){
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(editTextEmail.text.toString(),editTextTextPassword.text.toString()).addOnCompleteListener {
                        if (it.isSuccessful){
                            showHome(it.result?.user?.email?:"",ProviderType.BASIC)

                        }else{
                            ShowAlert()
                        }
                    }
            }
        }
    }
    private fun ShowAlert (){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog:AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome (email: String, provider: ProviderType){
        val homeIntent:Intent = Intent (this,HomeActivity::class.java).apply {
            putExtra("email",email)
            putExtra("proveedor",provider.name)
        }
        startActivity(homeIntent)

    }
}