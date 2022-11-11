package com.example.expressmarket.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expressmarket.Constants;
import com.example.expressmarket.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Addproducto extends AppCompatActivity {

    private ImageButton btnback;
    private EditText tituloEt, descripcionEt;
    private TextView categoriaEt, cantidadEt, precioEt, descuentoPrecioEt, descuentoNotaEt;
    private ImageView productoImg;
    private SwitchCompat descuentoSwitch;
    private Button btnAddproducto;

    private  static final int CAMERA_REQUEST_CODE = 200;
    private  static final int STORAGE_REQUEST_CODE = 300;

    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    private static final int IMAGE_PICK_CAMERA_CODE = 500;

    private  String[] cameraPermissions;
    private ProgressDialog progressDialog;
    private  String[] storagePermissions;

    private FirebaseAuth firebaseAuth;

    private Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproducto);

        btnback = findViewById(R.id.btnback);
        tituloEt = findViewById(R.id.tituloEt);
        productoImg = findViewById(R.id.productoImg);
        descripcionEt = findViewById(R.id.descripcionEt);
        categoriaEt = findViewById(R.id.categoriaEt);
        cantidadEt = findViewById(R.id.cantidadEt);
        precioEt = findViewById(R.id.precioEt);
        descuentoSwitch = findViewById(R.id.descuentoSwitch);
        descuentoPrecioEt = findViewById(R.id.descuentoPrecioEt);
        descuentoNotaEt = findViewById(R.id.descuentoNotaEt);
        btnAddproducto = findViewById(R.id.btnAddproducto);
        progressDialog = new ProgressDialog(this);
        descuentoPrecioEt.setVisibility(View.GONE);
        descuentoNotaEt.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog.setTitle("Espere...");
        progressDialog.setCanceledOnTouchOutside(false);
        cameraPermissions =  new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions =  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        descuentoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    descuentoPrecioEt.setVisibility(View.VISIBLE);
                    descuentoNotaEt.setVisibility(View.VISIBLE);

                }else{
                    descuentoPrecioEt.setVisibility(View.GONE);
                    descuentoNotaEt.setVisibility(View.GONE);

                }

            }
        });
        productoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarImagenDialog();

            }
        });
        categoriaEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriaDialog();
            }
        });

        btnAddproducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private String tituloProducto, descripcionProducto, categoriaProducto, cantidadProducto, precioOriginal, precioDescuento, notaDescuento;
    private boolean descuentoDisponible=false;
    private void inputData() {
            tituloProducto = tituloEt.getText().toString().trim();
            descripcionProducto = descripcionEt.getText().toString().trim();
            categoriaProducto = categoriaEt.getText().toString().trim();
            cantidadProducto= cantidadEt.getText().toString().trim();
            precioOriginal = precioEt.getText().toString().trim();
            descuentoDisponible = descuentoSwitch.isChecked();

            if (TextUtils.isEmpty(tituloProducto)){
                Toast.makeText(this,"Titulo Requerido", Toast.LENGTH_SHORT).show();
                return ;
            }
            if (TextUtils.isEmpty(categoriaProducto)){
                Toast.makeText(this,"Categoria Requerida", Toast.LENGTH_SHORT).show();
                return ;
            }
            if (TextUtils.isEmpty(precioOriginal)){
                Toast.makeText(this,"Precio Requerido", Toast.LENGTH_SHORT).show();
                return ;
            }
            if (descuentoDisponible){
                precioDescuento = descuentoPrecioEt.getText().toString().trim();
                notaDescuento = descuentoNotaEt.getText().toString().trim();
                if (TextUtils.isEmpty(precioDescuento)){
                    Toast.makeText(this, "Precio con descuento Requerido", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else {
                precioDescuento = "0";
                notaDescuento = "";
            }
            addproducto();


    }

    private void addproducto() {
        progressDialog.setMessage("Añadiendo producto...");
        progressDialog.show();

        final String timestamp = ""+System.currentTimeMillis();

        if (image_uri == null){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("productId", ""+timestamp);
            hashMap.put("tituloProducto", ""+tituloProducto);
            hashMap.put("descripcionProducto", ""+descripcionProducto);
            hashMap.put("categoriaProducto", ""+categoriaProducto);
            hashMap.put("cantidadProducto", ""+cantidadProducto);
            hashMap.put("imagenProducto", "");
            hashMap.put("precioOriginal", ""+precioOriginal);
            hashMap.put("precioDescuento", ""+precioDescuento);
            hashMap.put("notaDescuento", ""+notaDescuento);
            hashMap.put("descuentoDisponible", ""+descuentoDisponible);
            hashMap.put("timestamp", ""+timestamp);
            hashMap.put("uid", ""+firebaseAuth.getUid());

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            clearData();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Addproducto.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            
                        }
                    });

        }else{
            String filePathAndName= "product_images/"+""+timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadImageUri = uriTask.getResult();
                            if (uriTask.isSuccessful()){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("productId", ""+timestamp);
                                hashMap.put("tituloProducto", ""+tituloProducto);
                                hashMap.put("descripcionProducto", ""+descripcionProducto);
                                hashMap.put("categoriaProducto", ""+categoriaProducto);
                                hashMap.put("cantidadProducto", ""+cantidadProducto);
                                hashMap.put("imagenProducto", ""+downloadImageUri);
                                hashMap.put("precioOriginal", ""+precioOriginal);
                                hashMap.put("precioDescuento", ""+precioDescuento);
                                hashMap.put("notaDescuento", ""+notaDescuento);
                                hashMap.put("descuentoDisponible", ""+descuentoDisponible);
                                hashMap.put("timestamp", ""+timestamp);
                                hashMap.put("uid", ""+firebaseAuth.getUid());

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                reference.child(firebaseAuth.getUid()).child("Products").child(timestamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                clearData();

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(Addproducto.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Addproducto.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void clearData() {

        tituloEt.setText("");
        descripcionEt.setText("");
        categoriaEt.setText("");
        cantidadEt.setText("");
        precioEt.setText("");
        descuentoPrecioEt.setText("");
        descuentoNotaEt.setText("");
        productoImg.setImageResource(R.drawable.ic_carritocompra_azul);
        image_uri = null;
    }


    private void categoriaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Categoria de Productos")
                .setItems(Constants.productosCategorias, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String categoria = Constants.productosCategorias[which];
                        categoriaEt.setText(categoria);
                    }
                })
                .show();
    }

    private void mostrarImagenDialog() {

        String[] opciones={"Camara","Galeria"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tomar Imagen")
                .setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        if (which==0){
                            if (revisarPermisoCamara()){
                                tomarCamara();
                            }else {
                                solicitarPermisoCamara();
                            }

                        }else {
                            if (revisarPermisoAlmacenamiento()){
                                tomarGaleria();
                            }else {
                                solicitarPermisoAlmacenamiento();
                            }

                        }
                    }
                })
        .show();

    }

    private void tomarGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void tomarCamara(){
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.Images.Media.TITLE, "temp_Image_Title");
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "temp_Image_Description");

        image_uri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean revisarPermisoAlmacenamiento(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void solicitarPermisoAlmacenamiento(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);

    }

    private boolean revisarPermisoCamara(){
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)==
                (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)==
                (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }
    private void solicitarPermisoCamara(){
        ActivityCompat.requestPermissions(this,cameraPermissions, CAMERA_REQUEST_CODE);

    }
    //

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case  CAMERA_REQUEST_CODE: {
                if (grantResults.length>0){
                    boolean camaraAceptada = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean almacenamientoAceptado = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camaraAceptada && almacenamientoAceptado){
                        tomarCamara();

                    }else{
                        Toast.makeText(this, "Los permisos de Camara y Almacenamiento son necesarios", Toast.LENGTH_SHORT).show();

                    }
                }
            }
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length>0){

                    boolean almacenamientoAceptado = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (almacenamientoAceptado){
                        tomarGaleria();
                    }else {
                        Toast.makeText(this, "Almacenamiento es necesario", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                image_uri = data.getData();
                productoImg.setImageURI(image_uri);
            }else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                productoImg.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
