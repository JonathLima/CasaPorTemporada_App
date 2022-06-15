package com.example.casaportemporada.activity.authentication;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.activity.MainActivity;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_login_email;
    private EditText edit_login_senha;
    private GoogleSignInClient client;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getAuthGoogle();
        configClique();
        iniciaComponentes();

    }

    private void configClique() {
        findViewById(R.id.edit_text_login_criar_conta).setOnClickListener(view -> {
            startActivity(new Intent(this, CriarContaActivity.class));
        });

        findViewById(R.id.edit_text_login_esqueceu_senha).setOnClickListener(view -> {
            startActivity(new Intent(this, RecuperarContaActivity.class));
        });
    }

    public void validaDados(View view) {
        String email = edit_login_email.getText().toString();
        String senha = edit_login_senha.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {

                progressBar.setVisibility(View.VISIBLE);

                logar(email, senha);

            } else {
                edit_login_senha.requestFocus();
                edit_login_senha.setError("Digite a senha");
            }

        } else {
            edit_login_email.requestFocus();
            edit_login_email.setError("Digite o email");
        }
    }

    private void logar(String email, String senha) {
        FirebaseHelper.getAuth().signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        finish();
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        String error = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void getAuthGoogle() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("602451511552-qbq74538b2hf7kbj7o4n1rr22bn7c7hq.apps.googleusercontent.com")
                .requestEmail()
                .build();

        client = GoogleSignIn.getClient(this, options);
    }

    private void getCredentialGooglesignIn(AuthCredential credential){
        FirebaseHelper.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(tasks -> {
                    if (tasks.isSuccessful()) {
                        Intent main = new Intent(getApplicationContext(), MainActivity.class);
                        finish();
                        startActivity(main);

                    } else {
                        String error = Objects.requireNonNull(tasks.getException()).getMessage();
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void loginWithGoogle(View view) {
        Intent i = client.getSignInIntent();
        openActivity.launch(i);
    }

    private void iniciaComponentes() {
        edit_login_email = findViewById(R.id.edit_text_login_email);
        edit_login_senha = findViewById(R.id.edit_text_login_senha);
        progressBar = findViewById(R.id.progressBar);
    }

    ActivityResultLauncher<Intent> openActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        getCredentialGooglesignIn(credential);

                    } catch (ApiException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

}