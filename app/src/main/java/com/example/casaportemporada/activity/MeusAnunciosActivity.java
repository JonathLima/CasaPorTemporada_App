package com.example.casaportemporada.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.casaportemporada.R;
import com.example.casaportemporada.adapter.AdapterAnuncios;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Anuncio;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.tsuryo.swipeablerv.SwipeLeftRightCallback;
import com.tsuryo.swipeablerv.SwipeableRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeusAnunciosActivity extends AppCompatActivity implements AdapterAnuncios.OnClick {

    private List<Anuncio> anuncioList = new ArrayList<>();

    private ProgressBar progressBar;
    private TextView text_load_info;
    private SwipeableRecyclerView rv_anuncios;
    private AdapterAnuncios adapterAnuncios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        startComponents();
        configRv();
        configClicks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        retriveAnuncios();
    }

    private void configRv() {
        rv_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList, this);
        rv_anuncios.setAdapter(adapterAnuncios);

        rv_anuncios.setListener(new SwipeLeftRightCallback.Listener() {
            @Override
            public void onSwipedLeft(int position) {
            }

            @Override
            public void onSwipedRight(int position) {
                showDialogDelete(position);
            }
        });
    }

    private void showDialogDelete(int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deletar anúncio");
        builder.setMessage("Confirma que deseja deletar este anúncio?");
        builder.setPositiveButton("Sim", ((dialog, which) -> {
            anuncioList.get(position).delete();
            adapterAnuncios.notifyItemRemoved(position);
        }));

        builder.setNegativeButton("Não", ((dialog, which) -> {
           dialog.dismiss();
           adapterAnuncios.notifyDataSetChanged();
        }));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void retriveAnuncios() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("anuncios")
                .child(FirebaseHelper.getIdFirebase());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        anuncioList.add(anuncio);
                    }
                    text_load_info.setText("");
                } else {
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

    private void configClicks() {
        findViewById(R.id.ib_voltar).setOnClickListener(click -> {
            startActivity(new Intent(this, MainActivity.class));
        });

        findViewById(R.id.ib_toolbar_add).setOnClickListener(click -> {
            startActivity(new Intent(this, FormAnuncioActivity.class));
        });

    }

    private void startComponents() {
        TextView title = findViewById(R.id.text_titulo_toolbar);
        rv_anuncios = findViewById(R.id.rv_anuncios);
        progressBar = findViewById(R.id.progressBar);
        text_load_info = findViewById(R.id.text_load_info);

        title.setText("Meus Anúncios");

    }

    @Override
    public void OnClickListener(Anuncio anuncio) {
        Intent intent = new Intent(this, FormAnuncioActivity.class);
        intent.putExtra("anuncio", anuncio);
        startActivity(intent);
    }
}