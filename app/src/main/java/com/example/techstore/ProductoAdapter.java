package com.example.techstore;

import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    List<producto> lista;

    public ProductoAdapter(List<producto> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProducto;
        TextView txtNombre, txtPrecio;

        public ViewHolder(View itemView) {
            super(itemView);

            imgProducto = itemView.findViewById(R.id.imgProducto);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        producto p = lista.get(position);

        String nombre = (p.nombre != null) ? p.nombre : "Sin nombre";
        String precioStr = (p.precio != null) ? p.precio.trim() : "0";

        // ✅ CORRECTO: usar imagenUrl
        String imagen = (p.imagen != null) ? p.imagen : "";

        holder.txtNombre.setText(nombre);
        holder.txtPrecio.setText("$ " + precioStr);

        Glide.with(holder.itemView.getContext())
                .load(imagen)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgProducto);

        // 🔥 CLICK → AGREGAR / SUMAR CARRITO
        holder.itemView.setOnClickListener(v -> {

            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // 🔥 LIMPIAR PRECIO
            String limpio = precioStr.replaceAll("[^0-9]", "");

            int tempPrecio = 0;
            try {
                tempPrecio = Integer.parseInt(limpio);
            } catch (Exception e) {
                tempPrecio = 0;
            }

            final int precioFinal = tempPrecio; // 🔥 SOLUCIÓN ERROR LAMBDA

            if (precioFinal <= 0) {
                Toast.makeText(v.getContext(),
                        "Error: precio inválido",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            db.collection("carrito")
                    .whereEqualTo("nombre", nombre)
                    .get()
                    .addOnSuccessListener(query -> {

                        if (!query.isEmpty()) {

                            // 🔁 YA EXISTE → SUMAR CANTIDAD
                            for (QueryDocumentSnapshot doc : query) {

                                Long cantidadActual = doc.getLong("cantidad");

                                int nuevaCantidad = (cantidadActual != null)
                                        ? cantidadActual.intValue() + 1
                                        : 1;

                                doc.getReference().update("cantidad", nuevaCantidad);

                                Toast.makeText(v.getContext(),
                                        "Cantidad actualizada (" + nuevaCantidad + ")",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {

                            // 🆕 NUEVO PRODUCTO
                            HashMap<String, Object> carrito = new HashMap<>();

                            carrito.put("nombre", nombre);
                            carrito.put("precio", String.valueOf(precioFinal)); // 🔥 STRING (sin errores)
                            carrito.put("imagenUrl", imagen);
                            carrito.put("cantidad", 1);

                            db.collection("carrito")
                                    .add(carrito)
                                    .addOnSuccessListener(doc ->
                                            Toast.makeText(v.getContext(),
                                                    "Agregado al carrito ✅",
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