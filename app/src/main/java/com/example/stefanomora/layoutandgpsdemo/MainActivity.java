package com.example.stefanomora.layoutandgpsdemo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * GPS
 * La classe principale implemente LocationListener per poter ricevere le notifiche
 * dal GPS. L'implementazione dell'interfaccia prevede la presenza delle funzioni:
 * <li>- public void onProviderEnabled(String provider)</li>
 * - public void onLocationChanged(Location location)
 * - public void onStatusChanged(String provider, int status, Bundle extras)
 * - public void onProviderDisabled(String provider)
 * Ricordarsi di aggiungere il permesso android.permission.ACCESS_FINE_LOCATION
 *
 *
 * Gestione delle nuove richieste di accesso - dalla 6.0
 * Richiesta a runtime dell'accesso alle risorse definite come critiche (es. GPS).
 * Deve essere controllato in aggiunta all'inserimento delle <uses-permission> nel manifet
 *
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager locationManager;
    private boolean bGpsEnabled;

    RelativeLayout rlBackOverlay;
    RelativeLayout rlTopOverlay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Overlay
        rlBackOverlay = findViewById(R.id.idBackOverlay);
        rlTopOverlay = findViewById(R.id.idTopOverlay);

        DemoMapOverlay overlay = new DemoMapOverlay(getApplicationContext());
        /*
        GeoPoint pt = new GeoPoint(
                (int) (43.292002 * 1E6),
                (int) (-73.292982 * 1E6));
        overlay.addPoint(pt);
        GeoPoint pt2 = new GeoPoint(
                (int) (41.3849292 * 1E6),
                (int) (-71.93982 * 1E6));
        overlay.addPoint(pt2); */
        rlBackOverlay.getOverlay().add(overlay);
        rlBackOverlay.invalidate();


        // GPS
        // Note: dal 6.0 è necessario richiedere l'accesso a runtime con la seguente chiamata
        // La risposta alla richiesta è gestita in onRequestPermissionsResult()
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        /********** get Gps location service LocationManager object ***********/
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            // getting GPS status
            bGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Parameters :
            //   First(provider)    :  the name of the provider with which to register
            //   Second(minTime)    :  the minimum time interval for notifications,
            //                         in milliseconds. This field is only used as a hint
            //                         to conserve power, and actual time between location
            //                         updates may be greater or lesser than this value.
            //   Third(minDistance) :  the minimum distance interval for notifications, in meters
            //   Fourth(listener)   :  a {#link LocationListener} whose onLocationChanged(Location)
            //                         method will be called for each location update
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    3000,       // 3 sec
                    10,
                    this);
        }
        catch (SecurityException ex){
            bGpsEnabled = false;
            UpdateStatus(ex.getMessage());
        }

        /********* After registration onLocationChanged method  ********/
        /********* called periodically after each 3 sec ***********/
        UpdateStatus();
    }


    /**
     * Funzione coinvolta nella gestione delle richieste di accesso alle risorse critiche
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("GPS", "Granted!");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("GPS", "NOT Granted!");
                    bGpsEnabled = false;
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Cambia il layout

            return true;
        }
        if ( id == R.id.idShow ) {
            rlTopOverlay.setVisibility(View.VISIBLE);
            return true;
        }
        if ( id == R.id.idHide ) {
            rlTopOverlay.setVisibility(View.INVISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onProviderEnabled(String provider) {
        /******** Called when User on Gps  *********/
        Toast.makeText(getBaseContext(), "Gps turned on ", Toast.LENGTH_LONG).show();
        bGpsEnabled = true;
        UpdateStatus();
    }

    public void onProviderDisabled(String provider) {
        /******** Called when User off Gps *********/
        Toast.makeText(getBaseContext(), "Gps turned off ", Toast.LENGTH_LONG).show();
        bGpsEnabled = false;
        UpdateStatus();

        // Apre il pannello Android per abilitare il GPS
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        String str = "Latitude: "+location.getLatitude()+" Longitude: "+location.getLongitude();
        Toast.makeText(getBaseContext(), str, Toast.LENGTH_LONG).show();
        UpdateStatus(str);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d ( "GPS", "onStatusChanged: " + status);
    }


    void UpdateStatus() {
        TextView tv = (TextView) findViewById(R.id.tvMsg);
        tv.setText( bGpsEnabled ? "GPS ON" : "GPS OFF");
    }


    void UpdateStatus(String msg) {
        TextView tv = (TextView) findViewById(R.id.tvCoordinate);
        tv.setText(msg);
    }
}
