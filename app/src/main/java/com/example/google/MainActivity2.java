package com.example.google;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.example.google.WebServices.Asynchtask;
import com.example.google.WebServices.WebService;

public class MainActivity2 extends AppCompatActivity implements OnMapReadyCallback, Asynchtask {

    private GoogleMap mapa;
    private String codigoIso;
    private TextView lblNombre, lblCapital, lblCodigo, lblTelCode, lblPoblacion, lblMoneda;
    private ImageView imgBandera;
    private RadioGroup rgMapTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        
        inicializarVistas();
        
        if (getIntent().getExtras() != null) {
            codigoIso = getIntent().getStringExtra("codigoIso");
            if(codigoIso != null) codigoIso = codigoIso.toUpperCase().trim();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapaquevedo2);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        configurarSelectorMapa();
    }

    private void inicializarVistas() {
        lblNombre = findViewById(R.id.lblNombrePais);
        lblCapital = findViewById(R.id.lblCapital);
        lblCodigo = findViewById(R.id.lblCodigo);
        lblTelCode = findViewById(R.id.lblTelCode);
        lblPoblacion = findViewById(R.id.lblPoblacion);
        lblMoneda = findViewById(R.id.lblMoneda);
        imgBandera = findViewById(R.id.imgBanderaDetalle);
        rgMapTypes = findViewById(R.id.rgMapTypes);
    }

    private void configurarSelectorMapa() {
        rgMapTypes.setOnCheckedChangeListener((group, checkedId) -> {
            if (mapa == null) return;
            if (checkedId == R.id.rbNormal) mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            else if (checkedId == R.id.rbSatellite) mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            else if (checkedId == R.id.rbTerrain) mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;
        if (codigoIso != null && !codigoIso.isEmpty()) {
            obtenerInfoGeografica();
            obtenerDetallesPais();
        }
    }

    private void obtenerInfoGeografica() {
        String url = "http://www.geognos.com/api/en/countries/info/" + codigoIso + ".json";
        new WebService(url, new HashMap<>(), this, this).execute("GET");
    }

    private void obtenerDetallesPais() {
        String url = "https://restcountries.com/v3.1/alpha/" + codigoIso;
        new WebService(url, new HashMap<>(), this, this).execute("GET");
    }

    @Override
    public void processFinish(String result) throws JSONException {
        try {
            if (result.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(result);
                actualizarInterfazDetalles(jsonArray.getJSONObject(0));
            } else {
                JSONObject json = new JSONObject(result);
                if (!json.isNull("Results")) {
                    dibujarGeometriaMapa(json.getJSONObject("Results"));
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    private void actualizarInterfazDetalles(JSONObject data) throws JSONException {
        String nombre = data.getJSONObject("name").optString("common");
        String capital = data.has("capital") ? data.getJSONArray("capital").getString(0) : "N/A";
        String pop = String.format("%,d", data.optLong("population", 0));
        String area = String.format("%,.0f km²", data.optDouble("area", 0));
        
        String moneda = "N/A";
        if (data.has("currencies")) {
            JSONObject curr = data.getJSONObject("currencies");
            Iterator<String> keys = curr.keys();
            if (keys.hasNext()) {
                String key = keys.next();
                moneda = curr.getJSONObject(key).optString("name") + " (" + key + ")";
            }
        }

        String prefijo = "N/A";
        if (data.has("idd")) {
            JSONObject idd = data.getJSONObject("idd");
            prefijo = idd.optString("root") + (idd.has("suffixes") ? idd.getJSONArray("suffixes").getString(0) : "");
        }

        final String fCap = capital, fPop = pop, fArea = area, fMon = moneda, fPre = prefijo;
        runOnUiThread(() -> {
            lblNombre.setText(nombre);
            lblCapital.setText("Capital: " + fCap);
            lblPoblacion.setText("Pob: " + fPop);
            lblMoneda.setText("Moneda: " + fMon);
            lblTelCode.setText("Prefijo: " + fPre);
            lblCodigo.setText("Superficie: " + fArea);
            
            String flagUrl = "http://www.geognos.com/api/en/countries/flag/" + codigoIso + ".png";
            Glide.with(this).load(flagUrl).into(imgBandera);
        });
    }

    private void dibujarGeometriaMapa(JSONObject results) throws JSONException {
        JSONArray center = results.optJSONArray("GeoPt");
        if (center != null) {
            LatLng pos = new LatLng(center.getDouble(0), center.getDouble(1));
            mapa.clear();
            
            JSONObject rect = results.optJSONObject("GeoRectangle");
            if (rect != null) {
                PolylineOptions opt = new PolylineOptions()
                    .add(new LatLng(rect.getDouble("North"), rect.getDouble("West")))
                    .add(new LatLng(rect.getDouble("North"), rect.getDouble("East")))
                    .add(new LatLng(rect.getDouble("South"), rect.getDouble("East")))
                    .add(new LatLng(rect.getDouble("South"), rect.getDouble("West")))
                    .add(new LatLng(rect.getDouble("North"), rect.getDouble("West")))
                    .color(Color.RED).width(10f).geodesic(true);
                mapa.addPolyline(opt);
            }
            mapa.addMarker(new MarkerOptions().position(pos).title(results.optString("Name")));
            mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 4f));
        }
    }
}