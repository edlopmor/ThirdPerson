package com.androidavanzado.thirdperson


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.activity_go_canyon.*

import java.text.SimpleDateFormat

import java.time.LocalDate
import java.time.LocalTime
import java.util.*


class  GoCanyonActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener , OnTimeSetListener {
    //Abrir una instancia con la base de datos
    private val database = FirebaseFirestore.getInstance()

    //private val referenceActividades  = database.collection("USUARIOS").document(email).collection("ACTIVIDAD").document("IDACTIVIDAD")
    //Obtener una instancia del calendario
    var calendar = Calendar.getInstance()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_canyon)

        var preferencias: SharedPreferences? =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        var documentRefActividad : String? = preferencias?.getString("UltimaActividadRef",null)
        if (documentRefActividad!=null){
            GoActivityRealizandoActividad(documentRefActividad)
        }
        //Variabl bundle para capturar los elementos entrantes desde otras activitys.
        val bundle: Bundle? = intent.extras
        var email: String? = preferencias?.getString("email", null)
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
        //Comprobamos el campo de texto en tiempo de ejecucion.
        editTextEmailContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (regexEmail(editTextEmailContact.text.toString())) {
                } else {
                    editTextEmailContact.setError("Invalid mail")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        editTextDay.setOnClickListener {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
                    var dateCogido = LocalDate.of(year, month + 1, day)
                    var dateActual = LocalDate.now()
                    if (dateCogido.isAfter(dateActual) or dateCogido.isEqual(dateActual))
                        editTextDay.setText(String.format("%d/%d/%d", day, month + 1, year))
                    else {
                        Toast.makeText(
                            this,
                            "La fecha debe ser el dia de hoy o posterior",
                            Toast.LENGTH_LONG
                        ).show()
                        editTextDay.setText("")
                    }
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }
        editTextTimeStart.setOnClickListener {
            editTextTimeStart.setText("")
            val timeSetListener =
                OnTimeSetListener { timePicker, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    editTextTimeStart.setText(SimpleDateFormat("HH:mm").format(calendar.time))
                }
            TimePickerDialog(
                        this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
            ).show()



        }
        editTextTimeFinish.setOnClickListener {
            if (editTextTimeStart.text.toString().isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor introduzca primero la hora de inicio",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                    var tiempoInicial = LocalTime.parse(editTextTimeStart.text.toString())
                    val timeSetListener =
                        OnTimeSetListener { timePicker, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            var tiempoFinal = LocalTime.of(hourOfDay, minute)
                            if (tiempoInicial.isBefore(tiempoFinal)) {
                                editTextTimeFinish.setText(SimpleDateFormat("HH:mm").format(calendar.time))
                            } else {
                                Toast.makeText(
                                    this,
                                    "La hora de inicio debe ser menor que la hora final",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    TimePickerDialog(
                        this,
                        timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
            }
        }


    }

    private fun guardarActividad(email: String, codigoRegistro: String) {
        var documentRefActividad: String?
        //El numero de actividad lo crea de manera automatica siendo este un campo unico .
        if (editTextActivityName.text.isNotEmpty() && editTextNumberPeople.text.isNotEmpty() && editTextMatriculaCoche.text.isNotEmpty() && editTextEmailContact.text.isNotEmpty()
            && editTextTimeFinish.text.isNotEmpty()
        ) {
            var actividad = Actividad()
            actividad.codigoRegistro = codigoRegistro
            actividad.nombreActividad = editTextActivityName.text.toString()
            if (isNumeric(editTextNumberPeople.text.toString())) {
                actividad.numeroPersonas = editTextNumberPeople.text.toString()
            } else {
                Toast.makeText(
                    this,
                    "Introduzca un numero de personas correcto",
                    Toast.LENGTH_SHORT
                ).show()
            }
            actividad.matriculaCoche = editTextMatriculaCoche.text.toString();
            actividad.emailContacto = editTextEmailContact.text.toString()
            actividad.horainicio = editTextTimeStart.text.toString()
            actividad.horaFinal = editTextTimeFinish.text.toString()
            actividad.ubicacion = textViewCoordenasGPS.text.toString()
            actividad.actividadAcabada = false
            actividad.avisoRealizadoActividad = false

            database.collection("USUARIOS").document(email).collection("ACTIVIDAD")
                .add(actividad)
                .addOnSuccessListener { documentReference ->

                    Log.d("GCA", "DocumentSnapShot written ID: ${documentReference.id}")
                    var documentRefActividad = documentReference.id
                    //Añadimos el documentRefActividad de nuestra actividad a nuestra referencias para poder recuperarla.
                    var preferencias: SharedPreferences.Editor? =
                        getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                    preferencias?.putString("UltimaActividadRef", documentRefActividad)
                    preferencias?.apply()
                    GoActivityRealizandoActividad(documentRefActividad)
                }
                .addOnFailureListener {
                    Log.w("GCA", "Error adding document")
                }

        }

    }

    private fun GoActivityRealizandoActividad(documentRefActividad: String) {
        val realizandoActividadIntent: Intent =
            Intent(this, RealizandoActividad::class.java).apply {
                putExtra("documentRefActividad", documentRefActividad)
            }
        startActivity(realizandoActividadIntent)

    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {

    }

    data class Actividad(
        var codigoRegistro: String? = null,
        var nombreActividad: String? = null,
        var numeroPersonas: String? = null,
        var matriculaCoche: String? = null,
        var emailContacto: String? = null,
        var horainicio: String? = null,
        var horaFinal: String? = null,
        var ubicacion: String? = null,
        var actividadAcabada: Boolean? = null,
        var avisoRealizadoActividad: Boolean? = null

    )

    private fun isNumeric(cadena: String): Boolean {
        return try {
            cadena.toInt()
            true
        } catch (nfe: NumberFormatException) {
            false
        }
    }

    private fun regexEmail(textEmail: String): Boolean {
        var emailAcomprobar = textEmail
        var emailCorrecto: Boolean = false
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAcomprobar).matches()) {
            emailCorrecto = true
        } else {
            emailCorrecto = false
        }
        return emailCorrecto
    }

    private fun MatriculaCorrecta(matriculaCoche: EditText): Boolean {
        var matriculaCorrecta = false
        //Matriculas correcta segun 4numeros y 3letras.
        val matriculaPattern = "^[0-9]{4}[a-zA-Z]{3}$".toRegex()
        //Matriculas correcta se numeracion vieja
        val matriculaPatterVieja = "^[a-zA-Z]{2}[0-9]{4}[a-zA-Z]".toRegex()
        if (matriculaPattern.matches(matriculaCoche.text.toString())){
        matriculaCorrecta = true
        }else{
        }
        if(matriculaPatterVieja.matches(matriculaCoche.text.toString())){
        matriculaCorrecta = true
        }
        return matriculaCorrecta
    }
}


























