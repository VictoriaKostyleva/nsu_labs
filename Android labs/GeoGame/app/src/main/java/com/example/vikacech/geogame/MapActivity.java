package com.example.vikacech.geogame;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private final int SWAP_NUM = 2;
    Button btnAccept;
    TextView taskText;
    FloatingActionButton fab;
    private GoogleMap googleMap;
    private Marker marker;
    private ArrayList<String> taskArray = new ArrayList<String>();
    private ArrayList<String> nameArray = new ArrayList<String>();
    private ArrayList<String> badWords = new ArrayList<String>();
    private int i;
    private int k;
    private String ListPreference;

    static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MapActivity.class);
        return intent;
    }

    public static void swap(String a, String b) {
        String c;
        c = a;
        a = b;
        b = c;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPrefs();
    }

    private void getPrefs() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        ListPreference = prefs.getString("listPref", "MAP_TYPE_NORMAL");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        taskArray.add("Show me Russia, please");
        taskArray.add("Well done! Show me Australia");
        taskArray.add("Good boy! Now Germany");
        taskArray.add("Great! Liechtenstein?");//Liechtenstein
        taskArray.add("Cool! Now Egypt");
        taskArray.add("You are a good boy ;)");

        nameArray.add("Россия");
        nameArray.add("Австралия");
        nameArray.add("Германия");
        nameArray.add("Лихтенштейн");
        nameArray.add("Египет");

        int min = 1;//not 0 because in the first task there is no "great" or "well done"
        int max = nameArray.size() - 1;

        int a;//TODO вынести в отдельную функцию
        int b;
        Random random = new Random();
        a = random.nextInt(max) + min;
        b = random.nextInt(max) + min;

        for (i = 0; i < SWAP_NUM; i++) {
            swap(taskArray.get(a), taskArray.get(b));
            swap(nameArray.get(a), nameArray.get(b));

            a = random.nextInt(max) + min;
            b = random.nextInt(max) + min;
            i++;
        }
        i = 0;

        badWords.add("Try again");
        badWords.add("No, you are not right");
        badWords.add("Really? No");
        badWords.add("Are you crazy?");
        badWords.add("Silly you");
        badWords.add("You are an idiot");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnAccept = (Button) findViewById(R.id.button_accept);
        btnAccept.getBackground().setColorFilter(Color.parseColor("#303F9F"), PorterDuff.Mode.MULTIPLY);//TODO
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (marker != null) {
                    LatLng coordinates = marker.getPosition();
                    double lat = coordinates.latitude;
                    double lng = coordinates.longitude;

                    Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());//TODO
                    List<Address> addresses = null;
                    try {
                        addresses = gcd.getFromLocation(lat, lng, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses.size() > 0) {
                        String countryName = addresses.get(0).getCountryName();

                        if (countryName.equals(nameArray.get(i))) {
                            Toast toast = Toast.makeText(getApplicationContext(), "Correct :)", Toast.LENGTH_SHORT);
                            toast.show();
                            i++;
                            if (i == taskArray.size() - 1) {
                                btnAccept.setVisibility(View.GONE);
                            }
                            taskText.setText(taskArray.get(i));
                            k = 0;
                        } else {
                            String badSay = badWords.get(k);
                            Toast toast = Toast.makeText(getApplicationContext(), badSay, Toast.LENGTH_SHORT);
                            toast.show();
                            if (k >= badWords.size() - 1) {
                                k = badWords.size() - 1;
                            } else {
                                k++;
                            }

                        }
                    }
                    marker.remove();


                }

            }
        });

        taskText = (TextView) findViewById(R.id.task);
        taskText.setText(taskArray.get(i));
        i = 0;
        k = 0;

        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.floatingActionButton:
                        AlertDialog.Builder dialogSettings = new AlertDialog.Builder(MapActivity.this);
                        dialogSettings.setCancelable(true)
                                .setTitle("Settings")
                                .setNegativeButton("You can change a map style",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {


                                                AlertDialog.Builder dialogDelete2 = new AlertDialog.Builder(MapActivity.this);
                                                dialogDelete2.setCancelable(true)
                                                        .setTitle("Changing a map style, select theme");

                                                dialogDelete2.setItems(new CharSequence[]
                                                                {"Silver", "Night", "Aubergine", "Dark"},
                                                        new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                boolean success;
                                                                // The 'which' argument contains the index position
                                                                // of the selected item
                                                                switch (which) {
//                                                        case 0:
//                                                            success = googleMap.setMapStyle(new MapStyleOptions(getResources()
//                                                                    .getString(R.string.style_standard)));
//                                                            if (!success) {
//                                                            }
                                                                    case 0:
                                                                        success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                                                                                .getString(R.string.style_silver)));
                                                                        if (!success) {
                                                                        }
                                                                        break;
                                                                    case 1:
                                                                        success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                                                                                .getString(R.string.style_night)));
                                                                        if (!success) {
                                                                        }
                                                                        break;
                                                                    case 2:
                                                                        success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                                                                                .getString(R.string.style_aubergine)));
                                                                        if (!success) {
                                                                        }
                                                                        break;
                                                                    case 3:
                                                                        success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                                                                                .getString(R.string.style_dark)));
                                                                        if (!success) {
                                                                        }
                                                                        break;
                                                                }
                                                            }
                                                        });
                                                dialogDelete2.create().show();


                                            }
                                        });
                        AlertDialog alert = dialogSettings.create();
                        alert.show();


                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

        if(ListPreference.equals("MAP_TYPE_NORMAL")) {//TODO change to case-break
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else {
            fab.setVisibility(View.GONE);
            if(ListPreference.equals("MAP_TYPE_HYBRID"))
            googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            else {
                if(ListPreference.equals("MAP_TYPE_SATELLITE"))
                    googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                else {
                    if(ListPreference.equals("MAP_TYPE_TERRAIN"))
                        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                }
            }
        }

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(final LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                }
                marker = googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });


    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
        Integer clickCount = (Integer) marker.getTag();

        // Check if a click count was set, then display the click count.
        if (clickCount != null) {
            clickCount = clickCount + 1;
            marker.setTag(clickCount);
            Toast.makeText(this,
                    marker.getTitle() +
                            " has been clicked " + clickCount + " times.",
                    Toast.LENGTH_SHORT).show();
        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }

}




