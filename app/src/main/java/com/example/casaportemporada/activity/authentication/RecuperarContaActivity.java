package com.example.casaportemporada.activity.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.helper.FirebaseHelper;

import java.util.Objects;

public class RecuperarContaActivity extends AppCompatActivity {

    private TextView text_recuperar_conta;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_conta);

        configCliques();
        iniciaComponentes();
    }


    public void validaDados(View view){
        String email = text_recuperar_conta.getText().toString();

        if(!email.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);
            recuperarSenha(email);

        }else{
            text_recuperar_conta.requestFocus();
            text_recuperar_conta.setError("Digite um email");
            progressBar.setVisibility(View.GONE);
        }

    }

    private void recuperarSenha(String email){
        FirebaseHelper.getAuth().sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(this, "Email enviado com sucesso!", Toast.LENGTH_SHORT).show();
                    }else{
                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.GONE);
                });
    }

    private void configCliques(){
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void iniciaComponentes(){
        TextView text_titulo = findViewById(R.id.text_titulo_toolbar);
        text_recuperar_conta = findViewById(R.id.edit_recuperar_conta);
        progressBar = findViewById(R.id.progressBar);
        text_titulo.setText("Recuperar Conta");
    }
}