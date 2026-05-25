package com.example.techstore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    private final List<Producto> lista;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProductoAdapter(List<Producto> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombre, txtPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);

        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Producto p = lista.get(position);

        final String nombreFinal = (p.getNombre() != null) ? p.getNombre() : "Sin nombre";
        final String precioStrFinal = (p.getPrecio() != null) ? p.getPrecio().trim() : "0";
        final String imagenFinal = (p.getImagen() != null) ? p.getImagen() : "";

        holder.txtNombre.setText(nombreFinal);
        holder.txtPrecio.setText("$ ".concat(precioStrFinal));

        Glide.with(holder.itemView.getContext())
                .load(imagenFinal)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgProducto);

        holder.itemView.setOnClickListener(v -> {

            int precioFinal;

            try {
                precioFinal = Integer.parseInt(precioStrFinal.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                Toast.makeText(v.getContext(),
                        "Precio inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (precioFinal <= 0) {
                Toast.makeText(v.getContext(),
                        "Precio inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("carrito")
                    .whereEqualTo("nombre", nombreFinal)
                    .get()
                    .addOnSuccessListener(query -> {

                        if (!query.isEmpty()) {

                            for (QueryDocumentSnapshot doc : query) {

                                Long cantidadActual = doc.getLong("cantidad");
                                int nuevaCantidad = (cantidadActual != null)
                                        ? cantidadActual.intValue() + 1
                                        : 1;

                                doc.getReference().update("cantidad", nuevaCantidad);

                                Toast.makeText(v.getContext(),
                                        "Cantidad actualizada: " + nuevaCantidad,
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            HashMap<String, Object> carrito = new HashMap<>();

                            carrito.put("nombre", nombreFinal);
                            carrito.put("precio", String.valueOf(precioFinal));
                            carrito.put("imagenUrl", imagenFinal);
                            carrito.put("cantidad", 1);

                            db.collection("carrito")
                                    .add(carrito)
                                    .addOnSuccessListener(doc ->
                                            Toast.makeText(v.getContext(),
                                                    "Agregado al carrito",
                                                    Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(v.getContext(),
                                                    "Error: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show()
                                    );
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(v.getContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show()
                    );
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }
}