package com.example.expressmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class EditPerfilUsuario extends AppCompatActivity {

    private ImageButton back,gps;
    private ImageView profileIv;
    private EditText name,phone,estado,ciudad,direccion;
    private Button actualizar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil_usuario);

        back= findViewById(R.id.back);
        gps= findViewById(R.id.gps);
        profileIv= findViewById(R.id.profileIv);
        name= findViewById(R.id.nameEt);
        phone= findViewById(R.id.phoneEt);
        estado= findViewById(R.id.EstadoEt);
        ciudad= findViewById(R.id.ciudadEt);
        direccion= findViewById(R.id.addresEt);
        actualizar= findViewById(R.id.actualizarbtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onbackPressed();
            }
        });
        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //detectar localizacion
            }
        });

        actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actualizado el perfil
            }
        });
    }

    private void onbackPressed() {

    }
}