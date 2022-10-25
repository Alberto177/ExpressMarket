package com.example.expressmarket;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class RegistroUsuario extends AppCompatActivity {

    private ImageButton back,gps;
    private ImageView perfil;
    private EditText name, phone, estado,ciudad, direccion, correo, pass, cpass;
    private Button registrar;
    private TextView regVende;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_usuario);

        back= findViewById(R.id.back);
        gps= findViewById(R.id.gps);
        perfil= findViewById(R.id.perfilIv);
        name= findViewById(R.id.nameEt);
        phone= findViewById(R.id.phoneEt);
        estado= findViewById(R.id.EstadoEt);
        ciudad= findViewById(R.id.ciudadEt);
        direccion= findViewById(R.id.addresEt);
        correo= findViewById(R.id.emailEt);
        pass= findViewById(R.id.passwordEt);
        cpass= findViewById(R.id.cpasswordEt);
        registrar= findViewById(R.id.registroBtn);
        regVende=findViewById(R.id.regisVendtTv);

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
            }
        });
        perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //imagen
            }
        });
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //registro de usuario
            }
        });
        regVende.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //abre la ventana del registro de vendedor
                startActivity(new Intent(RegistroUsuario.this, RegistroVendedor.class));
            }
        });
    }
}