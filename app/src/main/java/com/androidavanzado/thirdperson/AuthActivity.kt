package com.androidavanzado.thirdperson

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private val bundle = android.os.Bundle()

    enum class ProviderType {
        BASIC
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(3000)//QUITAR ESTO CUANDO FUNCIONE
        setTheme(R.style.AppTheme)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        bundle.putString("Message", "Integracion de firebase Completa")
        analytics.logEvent("InitScreen", bundle)


        //Setup
        Setup()
    }

    private fun Setup() {
        buttonRegistrar.setOnClickListener {
            showRegisterActivity()
            analytics.logEvent("BotonRegistrar", bundle)
        }
        buttonAcceder.setOnClickListener {
            if (editTextEmail.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        editTextEmail.text.toString(),
                        editTextTextPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Acceso exitoso", Toast.LENGTH_LONG).show()
                            val intentToHome: Intent = Intent(this, HomeActivity::class.java)
                            startActivity(intentToHome)

                        } else {
                            ShowAlert()
                        }
                    }
            } else {
                if (editTextEmail.text.isEmpty())
                    Toast.makeText(this, "Rellene el campo de email", Toast.LENGTH_LONG).show()
                if (editTextTextPassword.text.isEmpty())
                    Toast.makeText(this, "Por favor rellene el campo password", Toast.LENGTH_LONG)
                        .show()
            }
        }
        textViewLostPassword.setOnClickListener {
            showLostPasswordActivity()
        }
    }

    private fun ShowAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showRegisterActivity() {
        val homeIntent: Intent = Intent(this, RegisterActivity::class.java).apply {
        }
        startActivity(homeIntent)

    }

    private fun showLostPasswordActivity() {
        val lostPasswordIntent: Intent = Intent(this, RetrievePasswordActivity::class.java).apply {
        }
        startActivity(lostPasswordIntent)

    }
}