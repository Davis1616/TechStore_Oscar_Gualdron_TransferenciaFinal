package com.example.techstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarritoAdapter
        extends RecyclerView.Adapter<CarritoAdapter.ViewHolder> {

    private final List<Carrito> lista;

    public CarritoAdapter(List<Carrito> lista) {
        this.lista = lista;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgProducto;

        TextView txtNombre;
        TextView txtPrecio;
        TextView txtCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto =
                    itemView.findViewById(R.id.imgProducto);

            txtNombre =
                    itemView.findViewById(R.id.txtNombre);

            txtPrecio =
                    itemView.findViewById(R.id.txtPrecio);

            txtCantidad =
                    itemView.findViewById(R.id.txtCantidad);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View vista = LayoutInflater.from(
                parent.getContext()
        ).inflate(
                R.layout.item_carrito,
                parent,
                false
        );

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Carrito producto = lista.get(position);

        String nombre =
                producto.getNombre() != null
                        ? producto.getNombre()
                        : holder.itemView.getContext().getString(
                        R.string.sin_nombre
                );

        String precio =
                producto.getPrecio() != null
                        ? producto.getPrecio()
                        : "0";

        holder.txtNombre.setText(nombre);

        holder.txtPrecio.setText(
                holder.itemView.getContext().getString(
                        R.string.precio_formato,
                        precio
                )
        );

        holder.txtCantidad.setText(
                holder.itemView.getContext().getString(
                        R.string.cantidad_formato,
                        producto.getCantidad()
                )
        );

        Glide.with(holder.itemView.getContext())
                .load(producto.getImagenUrl())
                .placeholder(
                        android.R.drawable.ic_menu_gallery
                )
                .into(holder.imgProducto);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}