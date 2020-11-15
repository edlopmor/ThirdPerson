package Modelos;

import java.util.Date;

public class Actividad {
    String codigoRegistro;
    String emailContacto;
    Date horaFinal;
    Date horaInicio;
    String matriculaCoche;
    String nombreActividad;
    int numeroPersonas;
    String ubicacion;

    public Actividad(String codigoRegistro, String emailContacto, Date horaFinal, Date horaInicio, String matriculaCoche, String nombreActividad, int numeroPersonas, String ubicacion) {
        this.codigoRegistro = codigoRegistro;
        this.emailContacto = emailContacto;
        this.horaFinal = horaFinal;
        this.horaInicio = horaInicio;
        this.matriculaCoche = matriculaCoche;
        this.nombreActividad = nombreActividad;
        this.numeroPersonas = numeroPersonas;
        this.ubicacion = ubicacion;
    }

    public String getCodigoRegistro() {
        return codigoRegistro;
    }

    public void setCodigoRegistro(String codigoRegistro) {
        this.codigoRegistro = codigoRegistro;
    }

    public String getEmailContacto() {
        return emailContacto;
    }

    public void setEmailContacto(String emailContacto) {
        this.emailContacto = emailContacto;
    }

    public Date getHoraFinal() {
        return horaFinal;
    }

    public void setHoraFinal(Date horaFinal) {
        this.horaFinal = horaFinal;
    }

    public Date getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(Date horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getMatriculaCoche() {
        return matriculaCoche;
    }

    public void setMatriculaCoche(String matriculaCoche) {
        this.matriculaCoche = matriculaCoche;
    }

    public String getNombreActividad() {
        return nombreActividad;
    }

    public void setNombreActividad(String nombreActividad) {
        this.nombreActividad = nombreActividad;
    }

    public int getNumeroPersonas() {
        return numeroPersonas;
    }

    public void setNumeroPersonas(int numeroPersonas) {
        this.numeroPersonas = numeroPersonas;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
