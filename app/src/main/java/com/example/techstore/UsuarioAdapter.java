package com.example.techstore;

import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    List<Usuario> lista;

    public UsuarioAdapter(List<Usuario> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre, txtEmail, txtRol;
        Button btnEliminar;

        public ViewHolder(View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRol = itemView.findViewById(R.id.txtRol);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Usuario u = lista.get(position);

        holder.txtNombre.setText(u.nombre);
        holder.txtEmail.setText(u.email);
        holder.txtRol.setText(u.rol);

        holder.btnEliminar.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("usuarios")
                    .whereEqualTo("email", u.email)
                    .get()
                    .addOnSuccessListener(query -> {
                        for (var doc : query) {
                            doc.getReference().delete();
                        }
                        Toast.makeText(v.getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}