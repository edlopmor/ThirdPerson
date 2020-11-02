package com.androidavanzado.thirdperson

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private val analytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    private val bundle = android.os.Bundle()

    private val GOOGLE_SIG_IN = 100

    enum class ProviderType {
        BASIC,
        GOOGLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val analytics:FirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        bundle.putString("Message", "Integracion de firebase Completa")
        analytics.logEvent("InitScreen", bundle)


        //Setup
        Setup()
        ConprobarUsuarioSession()
    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun Setup() {

        buttonRegistrar.setOnClickListener {
            showRegisterActivity()
            analytics.logEvent("BotonRegistrar", bundle)
        }
        buttonAcceder.setOnClickListener {
            analytics.logEvent("BotonAcceder", bundle)
            if (editTextEmail.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()) {
                Acceder()

            } else {
                if (editTextEmail.text.isEmpty())
                    Toast.makeText(this, "Rellene el campo de email", Toast.LENGTH_LONG).show()
                if (editTextTextPassword.text.isEmpty())
                    Toast.makeText(this, "Por favor rellene el campo password", Toast.LENGTH_LONG)
                        .show()
            }
        }
        textViewLostPassword.setOnClickListener {
            analytics.logEvent("Perdidadecontraseña", bundle)
            showLostPasswordActivity()
        }
        buttonGoogle.setOnClickListener {
            analytics.logEvent("BotonAccesoconGoogle", bundle)
            //Configuracion de acceso con cuenta google.
            val googleAuthConfiguracion =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
            val googleCliente = GoogleSignIn.getClient(this, googleAuthConfiguracion)
            googleCliente.signOut()

            startActivityForResult(googleCliente.signInIntent, GOOGLE_SIG_IN)
        }
    }
    /**
     * Funcion que realiza la autentificacion a traves de los campos de texto mediante el usuario y la contraseña.
     */
    private fun Acceder() {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(
                editTextEmail.text.toString(), editTextTextPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Acceso exitoso", Toast.LENGTH_LONG).show()
                    showHome(it.result?.user?.email ?: "")

                } else {
                    ShowAlert()
                }

            }
    }

    /**
     * Funcion que navega hasta la pantalla de usuario y envia los datos.
     *
     */
    private fun showHome(email: String ){

        val homeIntent: Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)

        }
        startActivity(homeIntent)
    }

    /**
     * Metodo que muestra un aviso de error.
     */
    private fun ShowAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    /**
     * Metodo para ir a la actividdad de registro.
     */
    private fun showRegisterActivity() {
        val registerActivity: Intent = Intent(this, RegisterActivity::class.java).apply {
        }
        startActivity(registerActivity)

    }

    /**
     * Metodo para ir a la actividad de recuperacion de contraseña .
     */
    private fun showLostPasswordActivity() {
        val lostPasswordIntent: Intent = Intent(this, RetrievePasswordActivity::class.java).apply {
        }
        startActivity(lostPasswordIntent)

    }

    /**
     * Funcion que ve si tenemos algun usuario saltandose la primera pantalla en caso de que el usuario se haya registrado anteriormente.
     */
    private fun ConprobarUsuarioSession() {
        val preferencias: SharedPreferences? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String? = preferencias?.getString("email", null)
        // val provider: String? = preferencias?.getString("provider", null)

        if (email != null) {
            authLayout.visibility = View.INVISIBLE
            showHome(email)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIG_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credencial = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(it.result?.user?.email ?: "" )
                            } else {
                                ShowAlert()
                            }
                        }
                }
            } catch (e: ApiException) {
                ShowAlert()
            }


        }
    }


}