package com.example.techstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.ViewHolder> {

    private final List<Usuario> lista;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UsuarioAdapter(List<Usuario> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtEmail;
        TextView txtRol;
        Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtEmail = itemView.findViewById(R.id.txtEmail);
            txtRol = itemView.findViewById(R.id.txtRol);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Usuario u = lista.get(position);

        holder.txtNombre.setText(u.getNombre());
        holder.txtEmail.setText(u.getEmail());
        holder.txtRol.setText(u.getRol());

        holder.btnEliminar.setOnClickListener(v ->
                db.collection("usuarios")
                        .whereEqualTo("email", u.getEmail())
                        .get()
                        .addOnSuccessListener(query -> {

                            for (QueryDocumentSnapshot doc : query) {
                                doc.getReference().delete();
                            }

                            Toast.makeText(
                                    v.getContext(),
                                    v.getContext().getString(
                                            R.string.usuario_eliminado
                                    ),
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(
                                        v.getContext(),
                                        v.getContext().getString(
                                                R.string.error_generico,
                                                e.getMessage()
                                        ),
                                        Toast.LENGTH_LONG
                                ).show()
                        )
        );
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}