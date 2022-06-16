package com.example.casaportemporada.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Anuncio;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.io.IOException;
import java.security.Permission;
import java.util.List;

public class FormAnuncioActivity extends AppCompatActivity {

    private EditText edit_titulo_anuncio;
    private EditText edit_descricao_anuncio;
    private EditText edit_quarto_anuncio;
    private EditText edit_banheiro_anuncio;
    private EditText edit_garagem_anuncio;
    private CheckBox cb_disponivel;
    private ProgressBar progressBar;

    private ImageView img_anuncio;
    private String pathImg;
    private Bitmap image;

    private Anuncio anuncio;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anuncio);

        startComponents();
        configClick();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        openActivity.launch(intent);
    }

    public void verifyPermissionGallery(View view) {
        PermissionListener permissionlistener = new PermissionListener() {

            @Override
            public void onPermissionGranted() {
                openGallery();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(FormAnuncioActivity.this, "Permissão Negada.", Toast.LENGTH_SHORT).show();
            }
        };

        showDialogPermissionGallery(permissionlistener, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private void showDialogPermissionGallery(PermissionListener listener, String[] permissions) {
        TedPermission.create()
                .setPermissionListener(listener)
                .setDeniedTitle("Permissões negadas.")
                .setDeniedMessage("Você negou as permissões para acessar a galeria do dispositivo, deseja permitir ?")
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissions)
                .check();
    }

    private void validateData() {
        String titulo = edit_titulo_anuncio.getText().toString();
        String descricao = edit_descricao_anuncio.getText().toString();
        String quarto = edit_quarto_anuncio.getText().toString();
        String banheiro = edit_banheiro_anuncio.getText().toString();
        String garagem = edit_garagem_anuncio.getText().toString();


        if (!titulo.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!quarto.isEmpty()) {
                    if (!banheiro.isEmpty()) {
                        if (!garagem.isEmpty()) {

                            if(anuncio == null) anuncio = new Anuncio();

                            anuncio.setTitulo(titulo);
                            anuncio.setDescricao(descricao);
                            anuncio.setQuarto(quarto);
                            anuncio.setBanheiro(banheiro);
                            anuncio.setGaragem(garagem);
                            anuncio.setStatus(cb_disponivel.isChecked());

                            if(pathImg != null){
                                progressBar.setVisibility(View.VISIBLE);
                                saveImgAnuncio();
                            }else{
                                Toast.makeText(this, "Selecione uma imagem para o anúncio", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }


                        } else {
                            edit_garagem_anuncio.requestFocus();
                            edit_garagem_anuncio.setError("Informação obrigatória");
                        }
                    } else {
                        edit_banheiro_anuncio.requestFocus();
                        edit_banheiro_anuncio.setError("Informação obrigatória");
                    }
                } else {
                    edit_quarto_anuncio.requestFocus();
                    edit_quarto_anuncio.setError("Informação obrigatória");
                }
            } else {
                edit_descricao_anuncio.requestFocus();
                edit_descricao_anuncio.setError("Informe uma descrição");
            }

        } else {
            edit_titulo_anuncio.requestFocus();
            edit_titulo_anuncio.setError("Informe um título");
        }

    }

    private void saveImgAnuncio(){
        StorageReference storageReference = FirebaseHelper.getStorageReference()
                .child("images")
                .child("anuncios")
                .child(anuncio.getId() + ".jpeg");

        UploadTask uploadTask = storageReference.putFile(Uri.parse(pathImg));
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnCompleteListener(task ->
            {
                String urlImg = task.getResult().toString();
                anuncio.setUrlImg(urlImg);
                anuncio.save();

                finish();
                startActivity(new Intent(this, MainActivity.class));
            });
        }).addOnFailureListener(e ->{
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void configClick() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        });
        findViewById(R.id.ib_toolbar_salvar).setOnClickListener(view -> {
            validateData();
        });
    }

    private void startComponents() {
        TextView text_titulo = findViewById(R.id.text_titulo_toolbar);
        text_titulo.setText("Anúncio");
        edit_titulo_anuncio = findViewById(R.id.edit_titulo_anuncio);
        edit_descricao_anuncio = findViewById(R.id.edit_descricao_anuncio);
        edit_quarto_anuncio = findViewById(R.id.edit_quarto_anuncio);
        edit_banheiro_anuncio = findViewById(R.id.edit_banheiro_anuncio);
        edit_garagem_anuncio = findViewById(R.id.edit_garagem_anuncio);
        progressBar = findViewById(R.id.progressBar);
        cb_disponivel = findViewById(R.id.cb_disponivel);
        img_anuncio = findViewById(R.id.img_anuncio);

    }

    ActivityResultLauncher<Intent> openActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                }
            }
    );

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri localeImgSelected = data.getData();
            pathImg = localeImgSelected.toString();

            if (Build.VERSION.SDK_INT < 28) {
                try {
                    image = MediaStore.Images.Media.getBitmap(getBaseContext().getContentResolver(), localeImgSelected);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(getBaseContext().getContentResolver(), localeImgSelected);
                try {
                    image = ImageDecoder.decodeBitmap(source);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            img_anuncio.setImageBitmap(image);

        }

    }
}