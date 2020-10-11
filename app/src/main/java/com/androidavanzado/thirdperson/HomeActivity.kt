package com.androidavanzado.thirdperson

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

enum class ProviderType {
    BASIC,
    GOOGLE
}

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup
        val bundle: Bundle? = intent.extras
        val email: String? = bundle?.getString("email")
        val provider: String? = bundle?.getString("provider")
        setup(email ?: "", provider ?: "")

        //Guardar los datos de los usuarios autentificados en el dispositivo, de esta forma no es necesario que cada vez que entren a la aplicacion se autentifiquen.

        val preferencias: SharedPreferences.Editor? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        preferencias?.putString("email", email)
        preferencias?.putString("provider", provider)
        preferencias?.apply()

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }

    }

    private fun setup(email: String, provider: String) {
        title = "Inicio"
        textViewEmail.text = email
        textViewProvider.text = provider

        //Programar el botton de log-out.


    }

    /**
     * Boton que cierra la sesion.
     *
     */
    private fun cerrarSesion() {
        val preferencias: SharedPreferences.Editor? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        preferencias?.clear()
        preferencias?.apply()
        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }
}