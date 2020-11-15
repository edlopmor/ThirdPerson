package com.androidavanzado.thirdperson

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val bundle = android.os.Bundle()
    private val callbackManager = com.facebook.CallbackManager.Factory.create()

    private val GOOGLE_SIG_IN = 100



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        firebaseAnalytics = Firebase.analytics
        //Setup
        Setup()

        //Si el inicio viene con extras puede haber sido lanzado desde una notificacion
        var extras = intent.extras

        if (extras!= null){
            var documentRefActivity = extras.getString("documentRefActivity",null)
            GoRealizandoActividad(documentRefActivity)
        }else{
            ConprobarUsuarioSession()
        }

    }

    override fun onStart() {
        super.onStart()
        authLayout.visibility = View.VISIBLE
    }

    private fun Setup() {
        buttonFacebook.setOnClickListener{
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email"))
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {

                    override fun onSuccess(result: LoginResult?) {
                        result?.let {
                            val token = it.accessToken
                            val credencial = FacebookAuthProvider.getCredential(token.token)
                            FirebaseAuth.getInstance().signInWithCredential(credencial)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        ShowHome(it.result?.user?.email ?: "")
                                    } else {
                                        ShowAlert()
                                    }

                                }

                        }
                    }

                    override fun onCancel() {


                    }

                    override fun onError(error: FacebookException?) {
                        ShowAlert()
                    }

                })
        }

        buttonRegistrar.setOnClickListener {
            ShowRegisterActivity()
            firebaseAnalytics.logEvent("BotonRegistrar", bundle)
        }
        buttonAcceder.setOnClickListener {
           // var email : String = editTextEmail.text.toString()
           // var password :String = editTextTextPassword.text.toString()
            firebaseAnalytics.logEvent("BotonAcceder", bundle)
           if (editTextEmail.text.isNotEmpty() && editTextTextPassword.text.isNotEmpty()) {
               var email : String = editTextEmail.text.toString()
               var password :String = editTextTextPassword.text.toString()
                    Acceder(email, password)

            } else {
                if (editTextEmail.text.isEmpty())
                    Toast.makeText(this, "Rellene el campo de email", Toast.LENGTH_LONG).show()
                if (comprobarEmail(editTextEmail.text.toString()))
                    Toast.makeText(this, "El campo email no es correcto", Toast.LENGTH_LONG).show()
                if (editTextTextPassword.text.isEmpty())
                    Toast.makeText(this, "Por favor rellene el campo password", Toast.LENGTH_LONG)
                        .show()

            }
        }
        textViewLostPassword.setOnClickListener {
            firebaseAnalytics.logEvent("Perdidadecontraseña", bundle)
            ShowLostPasswordActivity()
        }
        buttonGoogle.setOnClickListener {
            firebaseAnalytics.logEvent("BotonAccesoconGoogle", bundle)
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
    private fun Acceder(email: String, password: String) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(
                email, password
            ).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Acceso exitoso", Toast.LENGTH_LONG).show()
                    ShowHome(it.result?.user?.email ?: "")
                } else {
                    if (comprobarEmail(email)){
                        Toast.makeText(this, "El email es incorrecto", Toast.LENGTH_LONG).show()
                    }
                    Toast.makeText(this, "Revise la contraseña", Toast.LENGTH_LONG).show()
                }

            }
    }

    /**
     * Funcion que navega hasta la pantalla de usuario y envia los datos.
     *
     */
    private fun ShowHome(email: String){

        val homeIntent: Intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)

        }
        startActivity(homeIntent)
    }
    private fun GoRealizandoActividad(documentRefActivity : String){
        val realizadoActividadIntent : Intent = Intent(this, RealizandoActividad::class.java).apply {
            putExtra("documentRefActividad",documentRefActivity)
        }
        startActivity(realizadoActividadIntent)
    }
    /**
     * Metodo que muestra un aviso de error.
     */
    private fun ShowAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Error autenticado al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    /**
     * Metodo para ir a la actividdad de registro.
     */
    private fun ShowRegisterActivity() {
        val registerActivity: Intent = Intent(this, RegisterActivity::class.java).apply {
        }
        startActivity(registerActivity)

    }
    /**
     * Metodo para ir a la actividad de recuperacion de contraseña .
     */
    private fun ShowLostPasswordActivity() {
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
            ShowHome(email)

        }
    }
    //Funcion que compara el campo email con una expresion regular para comprobar si es correcto.
    private fun  comprobarEmail(emailAcomprobar: String): Boolean {
        var emailCorrecto : Boolean = false
        val emailPattern ="[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+"
        if (emailAcomprobar.trim(){it <= ' '}.matches(emailPattern.toRegex())){
            emailCorrecto = true
        }
        return emailCorrecto

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
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
                                ShowHome(it.result?.user?.email ?: "")
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