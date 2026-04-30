package com.example.techstore;

import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    List<Carrito> lista;

    public CarritoAdapter(List<Carrito> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombre, txtPrecio, txtCantidad;

        public ViewHolder(View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carrito, parent, false);

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Carrito p = lista.get(position);

        if (p == null) return;

        holder.txtNombre.setText(p.nombre != null ? p.nombre : "Sin nombre");

        // 🔥 MOSTRAR PRECIO STRING
        holder.txtPrecio.setText("$ " + (p.precio != null ? p.precio : "0"));

        holder.txtCantidad.setText("x" + p.cantidad);

        Glide.with(holder.itemView.getContext())
                .load(p.imagenUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgProducto);
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}