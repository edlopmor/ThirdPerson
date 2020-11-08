package com.androidavanzado.thirdperson

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_go_canyon.*
import java.util.*



class  GoCanyonActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener {
    //Abrir una instancia con la base de datos
    private val database = FirebaseFirestore.getInstance()
    //private val referenceActividades  = database.collection("USUARIOS").document(email).collection("ACTIVIDAD").document("IDACTIVIDAD")
    //Obtener una instancia del calendario
    val calendar = Calendar.getInstance()
    //Variable final que indicara el inicio de la actividad.
    val ACTIVIDADCANYON : String = "CAN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_canyon)
        val preferencias: SharedPreferences? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        //Variabl bundle para capturar los elementos entrantes desde otras activitys.
        val bundle: Bundle? = intent.extras
        val email: String? = preferencias?.getString("email", null)
        var latLong: String? = bundle?.getString("LatLongitudAparcamiento").toString()
        var codigoRegistro: String = ""
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            it.result.let {
                //println("este es el codigo registro desde GoCanyOn : ${it}")
                if (it != null) {
                    codigoRegistro = it.token.toString()
                }
            }
        }
        email?.let { crearIdActividad(it) }
        if (latLong == "null") {
            textViewCoordenasGPS.text = "Coordenadas GPS"
        } else {
            textViewCoordenasGPS.text = latLong
        }

        btnObtenerUbicacionVehiculo.setOnClickListener {
            var intentToMapsActivity: Intent = Intent(this, MapsActivity::class.java).apply {
            }
            startActivity(intentToMapsActivity)
        }
        buttonGuardarActividad.setOnClickListener {
            if (email != null) {
                guardarActividad(email, codigoRegistro)
            }

        }
        editTextDay.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                    editTextDay.setText(String.format("%d/%d/%d", day, month, year))
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        editTextTimeStart.setOnClickListener {
            guardarHora(editTextTimeStart)
        }
        editTextTimeFinish.setOnClickListener {
            guardarHora(editTextTimeFinish)
        }

    }
    private fun crearIdActividad(email: String){
        // LA ID de actividad contara de 2 partes un texto unico proviniente desde el boton . Un campo int que sera = al numero + 1
        //Lectura de la base de datos a fin de obtener la ultima IDactividad.
        val docRef = database.collection("Usuarios").document(email).collection("Actividades").document(
            "IDACTIVIDAD"
        )
        docRef.get().addOnCompleteListener { document ->
            println("DocumentSnapShot data : $ {document.data}")
        }



    }

    private fun guardarActividad(email: String, codigoRegistro: String) {
        //TODO desbloquear esto
        /*if (editTextActivityName.text.isNotEmpty() && editTextNumberPeople.text.isNotEmpty() && editTextMatriculaCoche.text.isNotEmpty() && editTextEmailContact.text.isNotEmpty()
            && editTextTimeFinish.text.isNotEmpty())*/
            //Si no introducimos nosotros el campo del nombre del documento crea un campo unico aleatorio y lo ingresa con ese nombre. Crear o reemplazar un documento
            val actividad = Actividad(codigoRegistro,
                editTextActivityName.text.toString(),
                editTextNumberPeople.text.toString(),
                editTextMatriculaCoche.text.toString(),
                editTextEmailContact.text.toString(),
                editTextTimeStart.text.toString(),
                editTextTimeFinish.text.toString(),
                textViewCoordenasGPS.text.toString(),
                false,
                false,
            )
            database.collection("USUARIOS").document(email).collection("ACTIVIDAD")
                .add(actividad)
                .addOnSuccessListener { documentReference->
                    Log.d("GCA","DocumentSnapShot written ID: ${documentReference.id}")
                    val documentRefActividad = documentReference.id
                    GoActivityRealizandoActividad(documentRefActividad)
                }
                .addOnFailureListener{
                    Log.w("GCA","Error adding document")
                }



    }

    private fun guardarHora(campoHora: EditText) {
        var hora = calendar.get(Calendar.HOUR_OF_DAY)
        var minuto = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hora: Int, minute: Int) {
                if (minute < 10)
                    campoHora.setText(String.format("%d:0%d", hora, minute))
                else {
                    campoHora.setText(String.format("%d:%d", hora, minute))
                }
            }
        }, hora, minuto, false)
        timePickerDialog.show()
    }
    private fun GoActivityRealizandoActividad (documentRefActividad : String){
        val realizandoActividadIntent : Intent = Intent(this, RealizandoActividad::class.java).apply {
            putExtra("documentRefActividad",documentRefActividad)
        }
        startActivity(realizandoActividadIntent)

    }
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        TODO("Not yet implemented")
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    data class Actividad (
       val codigoRegistro: String? = null,
        val nombreActividad : String? = null,
        var numeroPersonas : String? = null,
        val matriculaCoche : String? = null,
        val emailContacto : String?= null,
        val horainicio : String?= null,
        val horaFinal : String?= null,
        val ubicacion : String?= null,
        val actividadAcabada : Boolean? = null ,
        val avisoRealizadoActividad : Boolean? = null

    )



}


















