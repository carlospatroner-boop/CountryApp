package com.example.google;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.example.google.WebServices.Asynchtask;
import com.example.google.WebServices.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity implements Asynchtask {

    GridView lstPaises;
    ArrayList<Pais> listaPaises = new ArrayList<>();
    ArrayList<Pais> listaFiltrada = new ArrayList<>();
    AdaptadorPais adaptador;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        lstPaises = findViewById(R.id.lstPaises);
        searchView = findViewById(R.id.searchView);

        String url = "https://restcountries.com/v3.1/all?fields=name,cca2";
        Map<String, String> datos = new HashMap<>();
        WebService ws = new WebService(url, datos, this, this);
        ws.execute("GET");

        // Configurar el buscador
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrar(newText);
                return true;
            }
        });
    }

    private void filtrar(String texto) {
        listaFiltrada.clear();
        for (Pais p : listaPaises) {
            if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(p);
            }
        }
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void processFinish(String result) throws JSONException {
        listaPaises.clear();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String nombre = obj.getJSONObject("name").getString("common");
                String codigo = obj.getString("cca2");
                listaPaises.add(new Pais(nombre, codigo));
            }

            listaFiltrada.addAll(listaPaises);
            adaptador = new AdaptadorPais(this, listaFiltrada);
            lstPaises.setAdapter(adaptador);

            lstPaises.setOnItemClickListener((parent, view, position, id) -> {
                Pais paisSeleccionado = listaFiltrada.get(position);
                Intent intent = new Intent(MainActivity3.this, MainActivity2.class);
                intent.putExtra("codigoIso", paisSeleccionado.getCodigoIso().toUpperCase());
                startActivity(intent);
            });

        } catch (JSONException e) {
            Log.e("ERROR_API", "Error: " + result);
        }
    }
}