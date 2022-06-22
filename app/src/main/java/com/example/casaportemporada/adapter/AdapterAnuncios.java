package com.example.casaportemporada.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.casaportemporada.R;
import com.example.casaportemporada.model.Anuncio;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.util.List;

public class AdapterAnuncios extends RecyclerView.Adapter<AdapterAnuncios.MyViewHolder> {

    private List<Anuncio> anuncioList;
    private OnClick onClick;

    public AdapterAnuncios(List<Anuncio> anuncioList, OnClick onClick) {
        this.anuncioList = anuncioList;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio, parent, false);
        return new MyViewHolder(view);
    }

    public interface OnClick {
        public void OnClickListener(Anuncio anuncio);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Anuncio anuncio = anuncioList.get(position);
        Picasso.get().load(anuncio.getUrlImg()).into(holder.img_anuncio);
        holder.text_tituto.setText(anuncio.getTitulo());
        holder.text_descricao.setText(anuncio.getDescricao());
        holder.text_data.setText(LocalDateTime.now().toString());

        holder.itemView.setOnClickListener(view ->{
            onClick.OnClickListener(anuncio);
        });
    }

    @Override
    public int getItemCount() {
        return anuncioList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView img_anuncio;
        TextView text_tituto, text_descricao, text_data;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_anuncio = itemView.findViewById(R.id.img_item_anuncio);
            text_tituto = itemView.findViewById(R.id.text_title);
            text_descricao = itemView.findViewById(R.id.text_descricao);
            text_data = itemView.findViewById(R.id.text_data_hora);
        }
    }
}
