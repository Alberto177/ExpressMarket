package com.example.expressmarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class RegistroVendedor extends AppCompatActivity implements LocationListener {

    private ImageButton back,gps;
    private ImageView perfil;
    private EditText name, shopname, phone, envio, estado, ciudad, direccion, email,
            pass, cpass;
    private Button registro;

    //Constantes
    private static final int LOCATION_REQUEST_CODE=100;
    private static final int CAMERA_REQUEST_CODE=200;
    private static final int STORAGE_REQUEST_CODE=300;
    //constante imagen tomada
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private static final int IMAGE_PICK_CAMERA_CODE=500;


    //permisos array
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //tomar foto
    private Uri image_uri;

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
        cameraPermissions= new String[]{Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions= new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


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
                //tomar imagen
                showImagePickDialog();
            }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registro de usuario
            }
        });

    }

    private void showImagePickDialog() {
        //Opciones para obtener la imagen
        String[] options= {"Cama","Gallery"};
        //dialogo
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Tomar Foto")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      if (which==0) {
                          //click camara
                          if(checkCameraPermission()){
                                //permiso de camara
                              pickFromCamera();
                          }else{
                                //no
                              requestCameraPermission();
                          }

                      }else{
                          //click galleria
                          if(checkStoragePermission()){
                              //permiso
                              pickFromGallery();

                          }else{
                                //no
                              requestStoragePermission();
                          }
                      }
                    }
                })
                .show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private  void pickFromCamera(){
        ContentValues  contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE,"Temp_Image");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"Temp_Image Description");

        image_uri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
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
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
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
            direccion.setTetext(direccion);
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
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted= grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted= grantResults[1] ==PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permiso aceptados
                        pickFromCamera();
                    }else{
                        //permisos denegados
                        Toast.makeText(this, "Los permisos de camara son necesarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }


}