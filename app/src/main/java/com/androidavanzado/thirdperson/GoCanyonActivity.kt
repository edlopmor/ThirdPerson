package com.androidavanzado.thirdperson

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_go_canyon.*
import java.util.*

class  GoCanyonActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{
     private val database = FirebaseFirestore.getInstance()
     val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_canyon)
        val preferencias: SharedPreferences? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)

        val bundle: Bundle? = intent.extras
        val email: String = bundle?.getString("email").toString()
        var latLong :String? = bundle?.getString("LatLongitudAparcamiento").toString()


        if (latLong=="null"){
            textViewCoordenasGPS.text = "Coordenadas GPS"
        }
        else {
            textViewCoordenasGPS.text = latLong
        }

        btnObtenerUbicacionVehiculo.setOnClickListener{
            val intentToMapsActivity : Intent = Intent(this, MapsActivity::class.java).apply {
            }
            startActivity(intentToMapsActivity)
        }
        buttonGuardarActividad.setOnClickListener {
                //TODO CAPTURAR CAMPOS VACIOS.
            guardarActividad(email)
        }
        editTextDay.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                editTextDay.setText(String.format("%d/%d/%d",day,month,year))
            },year,month,day)
            datePickerDialog.show()
            }
        editTextTimeStart.setOnClickListener{
            guardarHora(editTextTimeStart)
        }
        editTextTimeFinish.setOnClickListener{
            guardarHora(editTextTimeFinish)
        }

    }
    private fun guardarActividad(email :String) {
        //TODO CAMBIAR ESTE METODO PARA CREAR UNA NUEVA ACTIVIDAD Y ENVIAR UNA COPIA A LA BASE DE DATOS.
        database.collection("Actividad").document(email).set(
            hashMapOf("NOMBRE_ACTIVIDAD" to editTextActivityName.text.toString(),
                "NUMERO_PERSONAS" to editTextNumberPeople.text.toString(),
                "MATRICULA_COCHE" to editTextMatriculaCoche.text.toString(),
                "EMAIL_CONTACTO" to editTextEmailContact.text.toString(),
                "HORA_INICIO" to editTextTimeStart.text.toString(),
                "HORA_FINAL" to editTextTimeFinish.text.toString(),
                "UBICACION" to textViewCoordenasGPS.text.toString()) )

    }
    private fun guardarHora(campoHora : EditText){
        var hora = calendar.get(Calendar.HOUR_OF_DAY)
        var minuto = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,object: TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hora: Int, minute: Int) {
                if (minute<10)
                campoHora.setText(String.format("%d:0%d", hora, minute))
                else{
                    campoHora.setText(String.format("%d:%d", hora, minute))
                }
            }
        }, hora, minuto,false)
        timePickerDialog.show()
    }

     override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
         TODO("Not yet implemented")
     }

     override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
         TODO("Not yet implemented")
     }
 }













