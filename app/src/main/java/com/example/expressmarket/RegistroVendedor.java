package com.example.expressmarket;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
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

    private double latitud=0.0, longitud=0.0;

    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

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

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere un momento");
        progressDialog.setCanceledOnTouchOutside(false);

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
                inputData();
            }
        });

    }

    private String nombred, nameTiendad, phoned, gastoEnvd, ciudadd, estadod, direcciond, emaild, passd, cpassd;
    private void inputData() {
        //imput data
        nombred= name.getText().toString().trim();
        nameTiendad= shopname.getText().toString().trim();
        phoned= phone.getText().toString().toString();
        gastoEnvd= envio.toString().toString().trim();
        ciudadd= ciudad.getText().toString().trim();
        estadod= estado.getText().toString().trim();
        direcciond= direccion.getText().toString().trim();
        emaild= email.getText().toString().trim();
        passd= pass.getText().toString().trim();
        cpassd= cpass.getText().toString().trim();
        //validacion
        if (TextUtils.isEmpty(nombred)){
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nameTiendad)){
            Toast.makeText(this, "Ingrese el nombre de la tienda", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(phoned)){
            Toast.makeText(this, "Ingrese su telefono", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(gastoEnvd)){
            Toast.makeText(this, "Ingrese su gasto de envio", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(direcciond)){
            Toast.makeText(this, "Ingrese su direccion", Toast.LENGTH_SHORT).show();
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emaild).matches()){
            Toast.makeText(this, "Email invalido", Toast.LENGTH_SHORT).show();
        }
        if (passd.length()<6){
            Toast.makeText(this, "La contrasena debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
        }
        if (!pass.equals(cpassd)){
            Toast.makeText(this, "Las contrasenas no coinciden", Toast.LENGTH_SHORT).show();
        }
        if(latitud==0.0 || longitud==0.0){
            Toast.makeText(this, "Por favor presiona el GPS para detectar su ubicacion", Toast.LENGTH_SHORT).show();
        }

        createAccount();


    }

    private void createAccount() {
        progressDialog.setMessage("Creando cuenta...");
                progressDialog.show();

        //creacion de cuenta
        firebaseAuth.createUserWithEmailAndPassword(emaild,passd)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //creacion de cuenta
                        saverFirebaseData();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Cuenta no creada
                        progressDialog.dismiss();
                        Toast.makeText(RegistroVendedor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saverFirebaseData() {
        progressDialog.setMessage("Informacion de cuenta");

        String timestamp= ""+System.currentTimeMillis();
        if (image_uri==null){
            //guardar informacion sin imagen

            //configurar los datos a guardar
            HashMap<String, Object> hashMap= new HashMap<>();
            hashMap.put("uid",""+firebaseAuth.getUid());
            hashMap.put("email",""+emaild);
            hashMap.put("nombre",""+nameTiendad);
            hashMap.put("phone",""+phoned);
            hashMap.put("envio",""+gastoEnvd);
            hashMap.put("estado",""+estadod);
            hashMap.put("ciudad",""+ciudadd);
            hashMap.put("direccion",""+direcciond);
            hashMap.put("latitud",""+latitud);
            hashMap.put("longitud",""+longitud);
            hashMap.put("timestamp",""+timestamp);
            hashMap.put("tipo",""+"Vendedor");
            hashMap.put("online","true");
            hashMap.put("shopOpen","true");
            hashMap.put("imagen","");

            //save
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //base actualizada
                            progressDialog.dismiss();
                            startActivity(new Intent(RegistroVendedor.this, MainVendedor.class));
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error al  actualizar base
                            progressDialog.dismiss();
                            startActivity(new Intent(RegistroVendedor.this, MainVendedor.class));
                            finish();

                        }
                    });
        }else{
            //guarda informacion con imagen

            //nombre y ruta de la imagen
            String filePathAndName= "profiel_images/"+ ""+firebaseAuth.getUid();
            //subir imagen
            StorageReference storageReference= FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             //obtener url para subir la imagen
                            Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();

                            if (uriTask.isSuccessful()){
                                //configurar los datos a guardar
                                HashMap<String, Object> hashMap= new HashMap<>();
                                hashMap.put("uid",""+firebaseAuth.getUid());
                                hashMap.put("email",""+emaild);
                                hashMap.put("nombre",""+nameTiendad);
                                hashMap.put("phone",""+phoned);
                                hashMap.put("envio",""+gastoEnvd);
                                hashMap.put("estado",""+estadod);
                                hashMap.put("ciudad",""+ciudadd);
                                hashMap.put("direccion",""+direcciond);
                                hashMap.put("latitud",""+latitud);
                                hashMap.put("longitud",""+longitud);
                                hashMap.put("timestamp",""+timestamp);
                                hashMap.put("tipo",""+"Vendedor");
                                hashMap.put("online","true");
                                hashMap.put("shopOpen","true");
                                hashMap.put("imagen","" +downloadImageUri); //url para subir la imagen

                                //save
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //base actualizada
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistroVendedor.this, MainVendedor.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //error al  actualizar base
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistroVendedor.this, MainVendedor.class));
                                                finish();

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(RegistroVendedor.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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

//revisar
    private void detecLocation(){
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
///Revisar
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
    public void onProviderEnabled(String provider) {

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
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted= grantResults[0] ==PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permiso aceptados
                        pickFromGallery();
                    }else{
                        //permisos denegados
                        Toast.makeText(this, "Los permisos de galeria son necesarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode== RESULT_OK){
            if (requestCode== IMAGE_PICK_GALLERY_CODE){
                //toma la imagen
                image_uri= data.getData();
                //manda la imagen
                perfil.setImageURI(image_uri);
            }else if (requestCode== IMAGE_PICK_CAMERA_CODE){
                perfil.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }
}