package com.example.techstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    List<Orden> lista;

    public HistorialAdapter(List<Orden> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTotal, txtEstado;

        public ViewHolder(View itemView) {
            super(itemView);

            txtTotal = itemView.findViewById(R.id.txtTotalItem);
            txtEstado = itemView.findViewById(R.id.txtEstadoItem);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_orden, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Orden o = lista.get(position);

        //  PROTECCIÓN TOTAL (evita que se cierre la app)
        String total = (o != null && o.total != null && !o.total.isEmpty())
                ? o.total
                : "0";

        String estado = (o != null && o.estado != null && !o.estado.isEmpty())
                ? o.estado
                : "desconocido";

        holder.txtTotal.setText("Total: $" + total);
        holder.txtEstado.setText("Estado: " + estado);
    }

    @Override
    public int getItemCount() {
        return (lista != null) ? lista.size() : 0;
    }
}