package com.androidavanzado.thirdperson;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap myMapa;
    Marker myUbicacionAparcamiento ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMapa = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            myMapa.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE
                );
            }
        }
        myMapa.getUiSettings().setZoomControlsEnabled(true);
        //Crear un marcador cuando pulsamos de forma prolongada.
        final LatLng[] myPosition = new LatLng[1];
        myMapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                myUbicacionAparcamiento = myMapa.addMarker((new MarkerOptions()
                        .position(latLng)
                        //Aumentar el tamaño de los aparcamientos.
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.lugaraparcamientocoche))
                        .title(String.valueOf(latLng))));

                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("Selecciona esta ubicacion")
                        .setMessage("Desea que esta sea la posicion de su vehiculo")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("MapsActivity","Pulsacion ok");
                                        Intent intentToGoCanyonActivity = new Intent(MapsActivity.this, GoCanyonActivity.class);
                                        String latitudLongitudEnviar = (latLng.toString());
                                        intentToGoCanyonActivity.putExtra("LatLongitudAparcamiento",latitudLongitudEnviar);

                                        startActivity(intentToGoCanyonActivity);

                                    }
                                }

                        )
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d("MapsActivity","Pulsacion No");
                               myUbicacionAparcamiento.remove();
                                //TODO destruir el marker y dejar el mapa en blanco.
                            }
                        })
                        .show();




            }
        });

        myMapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText( getApplicationContext(),"Has pulsado la marca",Toast.LENGTH_LONG).show();
                return false;
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            //Permisos Asignados
            if (permissions.length > 0 && permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                myMapa.setMyLocationEnabled(true);
            }else{
                Toast.makeText(this,"Error en los permisos",Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private Boolean MostrarCuadroDialogEnMapa(){
        final Boolean[] esElPuntoCorrecto = {false};
        new AlertDialog.Builder(this)
                .setTitle("Selecciona esta ubicacion")
                .setMessage("Desea ques esta sea la posicion de su vehiculo")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("MapsActivity","Pulsacion ok");
                        esElPuntoCorrecto[0] = true;
                    }
                }

                )
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("MapsActivity","Pulsacion No");
                        esElPuntoCorrecto[0] = false;
                        //TODO destruir el marker y dejar el mapa en blanco.
                    }
                })
                .show();

        return esElPuntoCorrecto[0];
    }
}