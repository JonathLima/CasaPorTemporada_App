package com.example.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Anuncio;
import com.example.casaportemporada.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetalheAnuncioActivity extends AppCompatActivity {

    private ImageView img_anuncio;
    private TextView text_title;
    private TextView text_description;
    private EditText edit_quarto;
    private EditText edit_banheiro;
    private EditText edit_garagem;

    private Anuncio anuncio;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_anuncio);

        startComponents();
        configClicks();

        anuncio = (Anuncio) getIntent().getSerializableExtra("anuncio");
        recuperarPhone();
        configDados();


    }

    public void ligar(View view){
            if(user != null){
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + user.getTelefone()));
                startActivity(intent);
                
            }else{
                Toast.makeText(this, "Carregando informações, aguarde...", Toast.LENGTH_SHORT).show();
            }
    }

    private void recuperarPhone(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("users")
                .child(anuncio.getUserId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(){
        if(anuncio != null){
            Picasso.get().load(anuncio.getUrlImg()).into(img_anuncio);
            text_title.setText(anuncio.getTitulo());
            text_description.setText(anuncio.getDescricao());
            edit_quarto.setText(anuncio.getQuarto());
            edit_banheiro.setText(anuncio.getBanheiro());
            edit_garagem.setText(anuncio.getGaragem());
        }else{
            Toast.makeText(this, "Não foi possível recuperar os dados.", Toast.LENGTH_SHORT).show();
        }
    }

    private void configClicks() {
        findViewById(R.id.ib_voltar).setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void startComponents() {
        TextView text_title_toolbar = findViewById(R.id.text_titulo_toolbar);
        img_anuncio = findViewById(R.id.img_detail_anuncio);
        text_title = findViewById(R.id.text_title_detail_anuncio);
        text_description = findViewById(R.id.text_description_detail_anuncio);
        edit_quarto = findViewById(R.id.edit_quarto_detail_anuncio);
        edit_banheiro = findViewById(R.id.edit_banheiro_detail_anuncio);
        edit_garagem = findViewById(R.id.edit_garagem_detail_anuncio);
        text_title_toolbar.setText("Detalhes do Anúncio");

    }
}