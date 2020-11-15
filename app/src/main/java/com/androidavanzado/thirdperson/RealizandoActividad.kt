package com.androidavanzado.thirdperson

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_realizando_actividad.*

class RealizandoActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizando_actividad)
        val database = FirebaseFirestore.getInstance()
        val TAG: String = "REF"
        val bundle: Bundle? = intent.extras
        var documentRefActividad = bundle?.getString("documentRefActividad").toString()
        val preferencias: SharedPreferences? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email: String? = preferencias?.getString("email", null)
        val ultimaActividad: String? = preferencias?.getString("UltimaActividadRef", null)

        if (ultimaActividad != null) documentRefActividad = ultimaActividad

        //val docRef = database.collection("cities").document("SF")
        var docRef =
            database.collection("USUARIOS").document(email.toString()).collection("ACTIVIDAD")
                .document(documentRefActividad)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    textViewNombreActividad.text = document.getString("nombreActividad")
                    textViewCoordenadasGps.text = document.getString("ubicacion")
                    textViewMatriculaCoche.text = document.getString("matriculaCoche")
                    textViewHoraFin.text = document.getString("horaFinal")
                    texViewTelefonoContacto.text = document.getString("emailContacto")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        buttonFinalizarActividad.setOnClickListener {
            var cambiarPantalla = false
            docRef.update("actividadAcabada", true)
                .addOnSuccessListener { document ->
                    Log.d(TAG, "Documento actualizado")
                    var preferenciasEditor: SharedPreferences.Editor? =
                        getSharedPreferences(
                            getString(R.string.prefs_file),
                            Context.MODE_PRIVATE
                        ).edit()
                    preferenciasEditor?.putString("UltimaActividadRef", null)
                    preferenciasEditor?.apply()
                    var goHomeIntent = Intent(this, HomeActivity::class.java).apply {
                    }
                    startActivity(goHomeIntent)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error actualizando el documento", e)
                }
        }
    }
}








