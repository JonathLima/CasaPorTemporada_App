package com.example.casaportemporada.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Filter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.casaportemporada.R;
import com.example.casaportemporada.model.Filtro;

public class FiltrarAnunciosActivity extends AppCompatActivity {

    private TextView text_filter_quartos;
    private TextView text_filter_banheiros;
    private TextView text_filter_garagens;

    private SeekBar sb_filter_quartos;
    private SeekBar sb_filter_banheiros;
    private SeekBar sb_filter_garagens;

    private int qtd_quarto;
    private int qtd_banheiro;
    private int qtd_garagem;

    private Button btn_limpar;
    private Button btn_filtrar;

    private Filtro filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtrar_anuncios);

        startComponents();

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            filter = (Filtro) bundle.getSerializable("filter");

            if (filter != null) {
                configFilters();
            }

        }

        configClicks();
        configSb();
    }


    public void filter(View view) {
        if (filter == null) filter = new Filtro();

        if (qtd_quarto > 0) filter.setQtdQuarto(qtd_quarto);
        if (qtd_banheiro > 0) filter.setQtdBanheiro(qtd_banheiro);
        if (qtd_garagem > 0) filter.setQtdGaragem(qtd_garagem);

        if (qtd_quarto > 0 || qtd_banheiro > 0 || qtd_garagem > 0) {
            Intent intent = new Intent();
            intent.putExtra("filter", filter);
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    public void clearFilter(View view) {
        qtd_quarto = 0;
        qtd_banheiro = 0;
        qtd_garagem = 0;

        sb_filter_quartos.setProgress(0);
        sb_filter_banheiros.setProgress(0);
        sb_filter_garagens.setProgress(0);
        finish();

    }

    private void configFilters() {
        sb_filter_quartos.setProgress(filter.getQtdQuarto());
        sb_filter_banheiros.setProgress(filter.getQtdBanheiro());
        sb_filter_garagens.setProgress(filter.getQtdGaragem());

        text_filter_quartos.setText(filter.getQtdQuarto() + " quartos ou mais");
        text_filter_banheiros.setText(filter.getQtdBanheiro() + " banheiros ou mais");
        text_filter_garagens.setText(filter.getQtdGaragem() + " garagens ou mais");

        qtd_quarto = filter.getQtdQuarto();
        qtd_banheiro = filter.getQtdBanheiro();
        qtd_garagem = filter.getQtdGaragem();
    }

    private void configSb() {
        sb_filter_quartos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_filter_quartos.setText(progress + " quartos ou mais");
                qtd_quarto = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_filter_banheiros.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_filter_banheiros.setText(progress + " banheiros ou mais");
                qtd_banheiro = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_filter_garagens.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_filter_garagens.setText(progress + " garagens ou mais");
                qtd_garagem = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void configClicks() {
        findViewById(R.id.ib_voltar).setOnClickListener(task -> {
            startActivity(new Intent(this, MainActivity.class));
        });
    }

    private void startComponents() {
        TextView text_toobar_title = findViewById(R.id.text_titulo_toolbar);
        text_toobar_title.setText("Filtrar Anúncios");

        text_filter_quartos = findViewById(R.id.text_filter_quartos);
        text_filter_banheiros = findViewById(R.id.text_filter_banheiros);
        text_filter_garagens = findViewById(R.id.text_filter_garagens);

        sb_filter_quartos = findViewById(R.id.sb_filter_quartos);
        sb_filter_banheiros = findViewById(R.id.sb_filter_banheiros);
        sb_filter_garagens = findViewById(R.id.sb_filter_garagens);
        btn_limpar = findViewById(R.id.btn_limpar);
        btn_filtrar = findViewById(R.id.btn_filtrar);
    }


}