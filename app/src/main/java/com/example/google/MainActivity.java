package com.example.google;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaquevedo);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mapa = googleMap;

        LatLng facultadCiencias = new LatLng(-1.0125015742155783, -79.46970314387);

        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(facultadCiencias, 16);
        mapa.moveCamera(camUpd1);

        mapa.addMarker(new MarkerOptions()
                .position(facultadCiencias)
                .title("Facultad de Ciencias de la computacion")
                .snippet("Universidad")
        );

        PolylineOptions lineas = new PolylineOptions()
                .add(new LatLng(-1.011192860347783, -79.47183818212743))
                .add(new LatLng(-1.0131666581099048, -79.47183818212743))
                .add(new LatLng(-1.0136064715779394, -79.4671496810295))
                .add(new LatLng(-1.0113966764766527, -79.46701020616845))
                .add(new LatLng(-1.011192860347783, -79.47183818212743));

        lineas.width(8);
        lineas.color(Color.RED);

        mapa.addPolyline(lineas);
    }

}