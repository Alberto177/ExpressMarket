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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditPerfilVendedor extends AppCompatActivity implements LocationListener {

    //componentes
    private ImageButton back, gps;
    private ImageView perfil;
    private EditText name, nameTienda, phone, gasto, estado, ciudad, direccion;
    private SwitchCompat shopOpen;
    private Button up;

    //constates de permisos
    private static final int LOCATION_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 300;
    //imagen
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;
    //Arrays
    private String[] locationPermissions;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //imagen uri
    private Uri image_uri;

    private double latitud = 0.0;
    private double longitud = 0.0;

    //mensajes
    private ProgressDialog progressDialog;
    //firebase
    private FirebaseAuth firebaseAuth;

    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil_vendedor);

        back = findViewById(R.id.back);
        gps = findViewById(R.id.gps);
        perfil = findViewById(R.id.perfilIv);
        name = findViewById(R.id.nameEt);
        nameTienda = findViewById(R.id.nameTiendEt);
        phone = findViewById(R.id.phoneEt);
        gasto = findViewById(R.id.gastoEnvioEt);
        estado = findViewById(R.id.EstadoEt);
        ciudad = findViewById(R.id.ciudadEt);
        direccion = findViewById(R.id.addresEt);
        shopOpen = findViewById(R.id.tiendaOpenSwit);
        up = findViewById(R.id.actualizarbtn);

        //permisos arrays
        locationPermissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //progress
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor espere");
        progressDialog.setCanceledOnTouchOutside(false);

        //firebase
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();


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
                if (checkLocationPermission()) {
                    //detecta
                    detectionLocation();
                } else {
                    //no detecta
                    requestLocationPermission();
                }
            }
        });

        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actualizado el perfil
                inputData();
            }
        });
    }

    private String named, shopd, phoned, gastod, estadod, ciudadd, direcciond;
    private boolean open;

    private void inputData() {
        named = name.getText().toString().trim();
        shopd = nameTienda.getText().toString().trim();
        phoned = phone.getText().toString().trim();
        gastod = gasto.getText().toString().trim();
        estadod = estado.getText().toString().trim();
        ciudadd = ciudad.getText().toString().trim();
        direcciond = direccion.getText().toString().trim();
        open = shopOpen.isChecked(); //falso o verdadero

        updatePerfil();

    }

    private void updatePerfil() {
        progressDialog.setMessage("Actualizando Perfil");
        progressDialog.show();

        if (image_uri == null) {
            //actualizar sin imagen
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("nombre", "" + named);
            hashMap.put("nombreT", "" + shopd);
            hashMap.put("phone", "" + phoned);
            hashMap.put("gasto", "" + gastod);
            hashMap.put("estado", "" + estadod);
            hashMap.put("ciudad", "" + ciudadd);
            hashMap.put("direccion", "" + direcciond);
            hashMap.put("latitud", "" + latitud);
            hashMap.put("longitud", "" + longitud);
            hashMap.put("shopOpen", "" + open);

            //actualiza a db
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //actualizado
                            progressDialog.dismiss();
                            Toast.makeText(EditPerfilVendedor.this, "Perfil Actualizado..", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //no actualizado
                            progressDialog.dismiss();
                            Toast.makeText(EditPerfilVendedor.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        } else {
            //actualizar con imagen

            //subir imagen
            String filePathAndName = "profile_images/" + "" + firebaseAuth.getUid();
            //tomar
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            Uri downloadImgeUri = uriTask.getResult();

                            if (uriTask.isSuccessful()) {
                                //imagen recibida
                                //actualizar sin imagen
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("nombre", "" + named);
                                hashMap.put("nombreT", "" + shopd);
                                hashMap.put("phone", "" + phoned);
                                hashMap.put("gasto", "" + gastod);
                                hashMap.put("estado", "" + estadod);
                                hashMap.put("ciudad", "" + ciudadd);
                                hashMap.put("direccion", "" + direcciond);
                                hashMap.put("latitud", "" + latitud);
                                hashMap.put("longitud", "" + longitud);
                                hashMap.put("shopOpen", "" + open);
                                hashMap.put("imagen", "" + downloadImgeUri);

                                //actualiza a db
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
                                ref.child(firebaseAuth.getUid()).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //actualizado
                                                progressDialog.dismiss();
                                                Toast.makeText(EditPerfilVendedor.this, "Perfil Actualizado..", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //no actualizado
                                                progressDialog.dismiss();
                                                Toast.makeText(EditPerfilVendedor.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditPerfilVendedor.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });

        }

    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        } else {
            loadMyInfo();
        }
    }

    private void loadMyInfo() {
        //manda la informacion del usuario
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild("uid").equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String tipo = "" + ds.child("tipo").getValue();
                            String direct = "" + ds.child("direccion").getValue();
                            String ciudadt = "" + ds.child("ciudad").getValue();
                            String estadot = "" + ds.child("estado").getValue();
                            String enviot = "" + ds.child("envio").getValue();
                            String emailt = "" + ds.child("email").getValue();
                            latitud = Double.parseDouble("" + ds.child("latitud").getValue());
                            longitud = Double.parseDouble("" + ds.child("longitud").getValue());
                            String namet = "" + ds.child("nombre").getValue();
                            String onlinet = "" + ds.child("online").getValue();
                            String phonet = "" + ds.child("phone").getValue();
                            String imagent = "" + ds.child("imagen").getValue();
                            String timet = "" + ds.child("timestamp").getValue();
                            String namett = "" + ds.child("nombreT").getValue();
                            String tiendaopentt = "" + ds.child("shopOpen").getValue();
                            String uit = "" + ds.child("uid").getValue();

                            name.setText(namet);
                            phone.setText(phonet);
                            estado.setText(estadot);
                            ciudad.setText(ciudadt);
                            direccion.setText(direct);
                            nameTienda.setText(namett);
                            gasto.setText(enviot);

                            if (tiendaopentt.equals("true")) {
                                shopOpen.setChecked(true);
                            } else {
                                shopOpen.setChecked(false);
                            }

                            try {
                                Picasso.get().load(imagent).placeholder(R.drawable.ic_store_gray).into(perfil);
                            } catch (Exception e) {
                                perfil.setImageResource(R.drawable.ic_person_gray);
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showImagePickDialog() {
        //opciones para tomar la foto
        String[] options = {"Camara", "Galeria"};
        //dialogo
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tomar Foto:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            //camara
                            if (checkCameraPermission()) {
                                //abre la camara
                                pickFromCamera();
                            } else {
                                //no abre la camara
                                requestCameraPermission();
                            }

                        } else {
                            //galeria
                            if (checkStoragePermission()) {
                                //abre la galeria
                                pickFromGallery();
                            } else {
                                //no abre la galeria
                                requestStoragePermission();
                            }
                        }
                    }
                })
                .show();
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private boolean checkCameraPermission() {
        Boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) ==
                (PackageManager.PERMISSION_GRANTED);

        Boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private boolean checkLocationPermission() {
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                (PackageManager.PERMISSION_GRANTED);

        return result;
    }

    private void pickFromGallery() {
        //intent toma la foto de la galeria
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera() {
        //intent toma la foto de camara
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Image Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image Description");

        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private void detectionLocation() {
        Toast.makeText(this, "Por favor espere...", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 0, 0, this);
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
                        //permiso
                        detectionLocation();
                    }else
                    {
                        //permiso denegado
                        Toast.makeText(this, "El permiso de localizacion es necesario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean cameraAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted= grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted){
                        //permiso
                        pickFromCamera();
                    }else{
                        //permiso denegado
                        Toast.makeText(this, "Los permisos de camara son necesarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){
                    boolean storageAccepted= grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permiso
                        pickFromGallery();
                    }else{
                        //permiso denegado
                        Toast.makeText(this, "Los permisos de galeria son necesarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if (resultCode == IMAGE_PICK_GALLERY_CODE){
                //toma la foto de galeria
                image_uri = data.getData();
                //envia la foto
                perfil.setImageURI(image_uri);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                perfil.setImageURI(image_uri);
            }

        }




        super.onActivityResult(requestCode, resultCode, data);
    }
}