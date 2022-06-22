package com.example.casaportemporada.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.casaportemporada.R;
import com.example.casaportemporada.activity.authentication.LoginActivity;
import com.example.casaportemporada.adapter.AdapterAnuncios;
import com.example.casaportemporada.helper.FirebaseHelper;
import com.example.casaportemporada.model.Anuncio;
import com.example.casaportemporada.model.Filtro;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterAnuncios.OnClick {

    private RecyclerView rv_all_anuncios;
    private TextView text_info;
    private ProgressBar progressBar;

    private List<Anuncio> anuncioList = new ArrayList<>();
    private AdapterAnuncios adapterAnuncios;
    private ImageButton ib_menu;

    private Filtro filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filter = (Filtro) getIntent().getSerializableExtra("filtro");

        startComponents();
        configRv();
        recuperaAnuncios();
        configClicks();

    }

    private void recuperaAnuncios() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("all_anuncios");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);
                        anuncioList.add(anuncio);
                    }
                    text_info.setText("");

                } else {
                    text_info.setText("Nenhum anúncio encontrado.");
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

    private void recuperaAnunciosFiltrado() {
        DatabaseReference reference = FirebaseHelper.getDatabaseReference()
                .child("all_anuncios");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    anuncioList.clear();

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Anuncio anuncio = snap.getValue(Anuncio.class);

                        assert anuncio != null;
                        int quarto = Integer.parseInt(anuncio.getQuarto());
                        int banheiro = Integer.parseInt(anuncio.getBanheiro());
                        int garagem = Integer.parseInt(anuncio.getGaragem());

                        if (quarto >= filter.getQtdQuarto() &&
                                banheiro >= filter.getQtdBanheiro() &&
                                garagem >= filter.getQtdGaragem()) {
                            anuncioList.add(anuncio);
                        }

                    }

                }

                if (anuncioList.size() == 0) {
                    text_info.setText("Nenhum anúncio encontrado.");
                } else {
                    text_info.setText("");
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

    private void configRv() {
        rv_all_anuncios.setLayoutManager(new LinearLayoutManager(this));
        rv_all_anuncios.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(anuncioList, this);
        rv_all_anuncios.setAdapter(adapterAnuncios);
    }

    private void configClicks() {
        ib_menu.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, ib_menu);
            popupMenu.getMenuInflater().inflate(R.menu.menu_home, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(menuItem -> {

                if (menuItem.getItemId() == R.id.menu_filtrar) {
                    Intent intent = new Intent(this, FiltrarAnunciosActivity.class);
                    intent.putExtra("filter",filter);
                    returnActivity.launch(intent);

                } else if (menuItem.getItemId() == R.id.menu_meus_anuncios) {

                    if (FirebaseHelper.getAutenticado()) {
                        startActivity(new Intent(this, MeusAnunciosActivity.class));
                    } else {
                        showDialogLogin();
                    }
                } else {
                    if (FirebaseHelper.getAutenticado()) {
                        startActivity(new Intent(this, MinhaContaActivity.class));
                    } else {
                        showDialogLogin();
                    }
                }

                return true;
            });

            popupMenu.show();
        });
    }

    private void showDialogLogin() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Autenticação");
        builder.setCancelable(false);
        builder.setNegativeButton("Não", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Sim", (dialog, which) -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startComponents() {
        ib_menu = findViewById(R.id.ib_menu);
        rv_all_anuncios = findViewById(R.id.rv_all_anuncios);
        text_info = findViewById(R.id.text_load_info);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void OnClickListener(Anuncio anuncio) {
        Intent intent = new Intent(this, DetalheAnuncioActivity.class);
        intent.putExtra("anuncio", anuncio);
        startActivity(intent);
    }

    ActivityResultLauncher<Intent> returnActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    filter = (Filtro) result.getData().getSerializableExtra("filter");
                    if (filter.getQtdQuarto() > 0 || filter.getQtdBanheiro() > 0 || filter.getQtdGaragem() > 0) {
                        recuperaAnunciosFiltrado();
                    }
                }
            });
}