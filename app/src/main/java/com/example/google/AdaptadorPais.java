package com.example.google;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class AdaptadorPais extends ArrayAdapter<Pais> {
    public AdaptadorPais(Context context, ArrayList<Pais> paises) {
        super(context, 0, paises);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Pais pais = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pais, parent, false);
        }

        TextView txtNombre = convertView.findViewById(R.id.txtNombre);
        ImageView imgBandera = convertView.findViewById(R.id.imgBandera);

        txtNombre.setText(pais.getNombre());

        // Aseguramos que el código ISO esté en mayúsculas para la URL
        String urlBandera = "http://www.geognos.com/api/en/countries/flag/" + pais.getCodigoIso().toUpperCase() + ".png";
        Glide.with(getContext()).load(urlBandera).into(imgBandera);

        return convertView;
    }
}