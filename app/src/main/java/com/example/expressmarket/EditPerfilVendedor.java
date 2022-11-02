package com.example.expressmarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Locale;

public class EditPerfilVendedor extends AppCompatActivity implements LocationListener {

    private ImageButton back, gps;
    private ImageView perfil;
    private EditText name,nameTienda,phone,gasto,estado, ciudad, direccion;
    private SwitchCompat shopOpen;
    private Button up;

    //constates de permisos
    private static final  int LOCATION_REQUEST_CODE= 100;
    private static final  int CAMERA_REQUEST_CODE= 200;
    private static final  int STORAGE_REQUEST_CODE= 300;
    //imagen
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //Arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    private double latitud=0.0;
    private double longitud=0.0;

    //mensajes
    private ProgressDialog progressDialog;
    //firebase
    private FirebaseAuth firebaseAuth;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil_vendedor);

        back= findViewById(R.id.back);
        gps= findViewById(R.id.gps);
        perfil= findViewById(R.id.perfilIv);
        name= findViewById(R.id.nameEt);
        nameTienda= findViewById(R.id.nameTiendEt);
        phone= findViewById(R.id.phoneEt);
        gasto= findViewById(R.id.gastoEnvioEt);
        estado= findViewById(R.id.EstadoEt);
        ciudad= findViewById(R.id.ciudadEt);
        direccion= findViewById(R.id.addresEt);
        shopOpen= findViewById(R.id.tiendaOpenSwit);
        up= findViewById(R.id.actualizarbtn);

        //permisos arrays
        locationPermissions= new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //progress
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        //firebase
        firebaseAuth= FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //toma la foto
                showImagePickDialog();
            }
        });

        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //detectar localizacion
                if (!checkLocationPermission()){
                    //detecta
                    detectionLocation();
                }else{
                    //no detecta
                    requestLocationPermission();
                }
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actualizado el perfil
            }
        });
    }

    private void showImagePickDialog() {
        //opciones para tomar la foto
        String[] options= {"Camara","Galeria"};
        //dialogo
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Tomar Foto:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            //camara
                            if(checkCameraPermission()){
                                pickFromCamera();
                            }else{
                                requestCameraPermission();
                            }

                        }else{
                            //galeria
                            if(checkStoragePermission()){
                                pickFromGallery();
                            }
                        }
                    }
                })
                .show();
    }

    private void pickFromGallery() {
    }

    private boolean checkStoragePermission() {
    }

    private void requestCameraPermission() {
    }

    private void pickFromCamera() {
    }

    private boolean checkCameraPermission() {
        return false;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private void detectionLocation() {
        Toast.makeText(this, "Por favor espere...", Toast.LENGTH_SHORT).show();
        locationManager= (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,0,0, this);
    }

    private boolean checkLocationPermission() {
        boolean result= ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }
    private void findAddres() {
        //estado,ciudad, direccion
        Geocoder geocoder;
        List<Address> addresses;
        geocoder= new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitud,longitud, 1);
            String address= addresses.get(0).getAddressLine(0);
            String ciudadd= addresses.get(0).getLocality();
            String estadod= addresses.get(0).getAdminArea();

            //mandar la direccion
            ciudad.setText(ciudadd);
            estado.setText(estadod);
            direccion.setText(address);


        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        latitud= location.getLatitude();
        longitud= location.getLongitude();
        findAddres();
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Localizacion deshabilitada", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUEST_CODE:{
                if (grantResults.length >0){
                    boolean locationAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted){
                        //permiso de seguimiento
                        detectionLocation();
                    }else
                    {
                        //permiso denegado
                        Toast.makeText(this, "El permiso de localizacion es necesario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}