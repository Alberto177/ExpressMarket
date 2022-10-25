package com.example.expressmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Locale;

public class RegistroVendedor extends AppCompatActivity implements LocationListener {

    private ImageButton back,gps;
    private ImageView perfil;
    private EditText name, shopname, phone, envio, estado, ciudad, direccion, email,
            pass, cpass;
    private Button registro;

    //Constantes
    private static final int LOCATION_REQUEST_CODE=100;

    //permisos array
    private String[] locationPermissions;

    private double latitud, longitud;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_vendedor);

        back= findViewById(R.id.back);
        gps= findViewById(R.id.gps);
        perfil= findViewById(R.id.perfilIv);
        name= findViewById(R.id.nameEt);
        shopname= findViewById(R.id.nameTiendEt);
        phone= findViewById(R.id.phoneEt);
        envio= findViewById(R.id.gastoEnvioEt);
        estado= findViewById(R.id.EstadoEt);
        ciudad= findViewById(R.id.ciudadEt);
        direccion= findViewById(R.id.addresEt);
        email= findViewById(R.id.emailEt);
        pass= findViewById(R.id.passwordEt);
        cpass= findViewById(R.id.cpasswordEt);
        registro= findViewById(R.id.registroBtn);

        //init permisos array
        locationPermissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //detecta la posicion
                if (checkLocationPermission()){
                    //iniciar localizacion
                    detecLocation();

                }else{
                    //no seguir
                    requestLocationPermission();
                }
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imagen
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registro de usuario
            }
        });

    }

    private void detecLocation() {
        Toast.makeText(this, "Por favor espera...", Toast.LENGTH_SHORT).show();
        locationManager= (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
    }

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Localizacion detectada
        latitud= location.getLatitude();
        longitud= location.getLongitude();

        findDireccion();

    }

    private void findDireccion() {
        //Encontrar estado y ciudad
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder((this, Locale.getDefault()));

        try {
            addresses= geocoder.getFromLocation(latitud,longitud,1);

            String direccion= addresses.get(0).getAddressLine(0); //direccion completa
            String ciudad= addresses.get(0).getLocality();
            String estado= addresses.get(0).getAdminArea();

            //Colocar direccion
            estado.setText(estado);
            ciudad.setText(ciudad);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        //gps//localizacion desactivada
        Toast.makeText(this, "Por favor habilita la localizacion..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean locationAccepted= grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        //permiso aceptados
                        detecLocation();
                    }else{
                        //permisos denegados
                        Toast.makeText(this, "Los permisos de localizacion son necesarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


}