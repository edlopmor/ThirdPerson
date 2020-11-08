package com.androidavanzado.thirdperson

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_realizando_actividad.*

class RealizandoActividad : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizando_actividad)
        val database = FirebaseFirestore.getInstance()
        val TAG : String = "REF"
        val bundle: Bundle? = intent.extras
        val documentRefActividad = bundle?.getString("documentRefActividad").toString()

        //val docRef = database.collection("cities").document("SF")
        val docRef = database.collection("USUARIOS").document("edlopmor@gmail.com").collection("ACTIVIDAD").document(documentRefActividad)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    textViewNombreActividad.text= document.getString("nombreActividad")
                    textViewCoordenadasGps.text= document.getString("ubicacion")
                    textViewMatriculaCoche.text= document.getString("matriculaCoche")
                    textViewHoraFin.text= document.getString("horaFinal")
                    texViewTelefonoContacto.text= document.getString("emailContacto")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        buttonFinalizarActividad.setOnClickListener {
            docRef.update("actividadAcabada",true)
                .addOnSuccessListener { Log.d(TAG, "Documento actualizado") }
                .addOnFailureListener { e -> Log.w(TAG, "Error actualizando el documento", e) }
            }
        }

    }


