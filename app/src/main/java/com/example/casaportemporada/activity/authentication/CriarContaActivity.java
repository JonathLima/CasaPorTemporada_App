package com.example.casaportemporada.activity.authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.User;

public class CriarContaActivity extends AppCompatActivity {

    private EditText editTextNome;
    private EditText editTextEmail;
    private String lastCharChanged = "";
    private EditText editTextTelefone;
    private EditText editTextSenha;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta);
        configCliques();
        iniciaComponentes();

        editTextTelefone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Integer phoneLength = editTextTelefone.getText().toString().length();

                if (editTextTelefone.toString().equals("")) {
                    editTextTelefone.append("(");
                }

                if (phoneLength > 1) {
                    lastCharChanged = editTextTelefone.getText().toString().substring(phoneLength - 1);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer phoneLength = editTextTelefone.getText().toString().length();


                if (phoneLength == 2) {
                    if (!lastCharChanged.equals(" ")) {

                        editTextTelefone.append(" ");
                    } else {
                        editTextTelefone.getText().delete(phoneLength - 1, phoneLength);
                    }

                } else if (phoneLength == 8) {
                    if (!lastCharChanged.equals("-")) {

                        editTextTelefone.append("-");
                    } else {
                        editTextTelefone.getText().delete(phoneLength - 1, phoneLength);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void validaDados(View view) {
        String nome = editTextNome.getText().toString();
        String email = editTextEmail.getText().toString();
        String telefone = editTextTelefone.getText().toString();
        String senha = editTextSenha.getText().toString();


        if (!nome.isEmpty()) {
            if (!email.isEmpty()) {

                if (!telefone.isEmpty()) {

                    if (!senha.isEmpty()) {
                        progressBar.setVisibility(View.VISIBLE);
                        User user = new User();
                        user.setNome(nome);
                        user.setEmail(email);
                        user.setTelefone(telefone);
                        user.setSenha(senha);

                        cadastrarUser(user);

                    } else {
                        editTextSenha.requestFocus();
                        editTextSenha.setError("Informe uma senha");
                        progressBar.setVisibility(View.GONE);
                    }

                } else {
                    editTextTelefone.requestFocus();
                    editTextTelefone.setError("Informe seu telefone");
                }

            } else {
                editTextEmail.requestFocus();
                editTextEmail.setError("Informe seu email");
            }

        } else {
            editTextNome.requestFocus();
            editTextNome.setError("Informe seu nome");
        }

    }

    private void cadastrarUser(User user) {
        FirebaseHelper.getAuth().createUserWithEmailAndPassword(
                user.getEmail(), user.getSenha()
        ).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                String idUser = task.getResult().getUser().getUid();
                user.setId(idUser);
                user.saveUser();

                finish();
                startActivity(new Intent(this, LoginActivity.class));
            } else {
                String error = task.getException().getMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void configCliques() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> finish());
    }

    private void iniciaComponentes() {

        editTextNome = findViewById(R.id.text_criar_conta_nome);
        editTextEmail = findViewById(R.id.text_criar_conta_email);
        editTextTelefone = findViewById(R.id.text_criar_conta_telefone);
        editTextSenha = findViewById(R.id.text_criar_conta_senha);
        progressBar = findViewById(R.id.progressBar);

        TextView textTituloToolbar = findViewById(R.id.text_titulo_toolbar);
        textTituloToolbar.setText("Crie sua Conta");
    }
}