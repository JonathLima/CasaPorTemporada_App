package com.example.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.casaportemporada.R;
import com.example.casaportemporada.adapter.AdapterAnuncios;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Anuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity {

    private List<Anuncio> anuncioList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView text_load_info;
    private RecyclerView rv_anuncios;
    private AdapterAnuncios adapterAnuncios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);


        startComponents();
        configRv();
        configClicks();
        retriveAnuncios();

    }

    private void configRv(){
        rv_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList);
        rv_anuncios.setAdapter(adapterAnuncios);
    }

    private void retriveAnuncios(){
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdFirebase());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    anuncioList.clear();
                    for(DataSnapshot snap : snapshot.getChildren()){
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        anuncioList.add(anuncio);
                    }
                    text_load_info.setText("");
                }else{
                    text_load_info.setText("Nenhum anúncio encontrado.");
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
                Collections.reverse(anuncioList);
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configClicks(){
        findViewById(R.id.ib_voltar).setOnClickListener(click ->{
            startActivity(new Intent(this, MainActivity.class));
        });

        findViewById(R.id.ib_toolbar_add).setOnClickListener(click -> {
            startActivity(new Intent(this, FormAnuncioActivity.class));
        });

    }

    private void startComponents(){
        TextView title = findViewById(R.id.text_titulo_toolbar);
        rv_anuncios = findViewById(R.id.rv_anuncios);
        progressBar = findViewById(R.id.progressBar);
        text_load_info = findViewById(R.id.text_load_info);

        title.setText("Meus Anúncios");

    }
}