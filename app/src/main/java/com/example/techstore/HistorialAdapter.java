package com.example.techstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter
        extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    private final List<Orden> lista;

    public HistorialAdapter(List<Orden> lista) {
        this.lista = lista;
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTotal;
        TextView txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTotal = itemView.findViewById(R.id.txtTotalItem);
            txtEstado = itemView.findViewById(R.id.txtEstadoItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orden, parent, false);

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Orden orden = lista.get(position);

        String total = (orden.getTotal() != null && !orden.getTotal().isEmpty())
                ? orden.getTotal()
                : "0";

        String estado = (orden.getEstado() != null && !orden.getEstado().isEmpty())
                ? orden.getEstado()
                : holder.itemView.getContext().getString(R.string.estado_desconocido);

        holder.txtTotal.setText(
                holder.itemView.getContext().getString(R.string.total_formato, total)
        );

        holder.txtEstado.setText(
                holder.itemView.getContext().getString(R.string.estado_formato, estado)
        );
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}