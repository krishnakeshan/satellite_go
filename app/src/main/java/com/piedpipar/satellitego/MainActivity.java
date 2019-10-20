package com.piedpipar.satellitego;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    //Properties
    private FusedLocationProviderClient locationProviderClient;
    private Location userLocation;

    private Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.n2yo.com/rest/v1/satellite/").build();
    private SatellitesAPI satellitesAPI = retrofit.create(SatellitesAPI.class);
    private ArrayList<SatelliteData> satData = new ArrayList<>();

    private ArFragment arFragment;
    private TextView userLocationTextView;
    private ScrollView satInfoScrollView;
    private TextView satIdTextView;
    private TextView satNameTextView;
    private TextView satPositionTextView;
    private AppCompatButton viewPassesButton;
    private AppCompatButton closeDialogButton;

    private ModelRenderable planetRenderable;
    private ModelRenderable auraSatRenderable;

    private Satellite satNodeToView;
    private AnchorNode baseAnchorNode;
    private Node planetNode = new Node();

    //Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get views
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_ar_fragment);
        userLocationTextView = findViewById(R.id.activity_main_user_location_text_view);
        satInfoScrollView = findViewById(R.id.activity_main_sat_info_scroll_view);
        satIdTextView = findViewById(R.id.activity_main_sat_norad_id_text_view);
        satNameTextView = findViewById(R.id.activity_main_sat_name_text_view);
        satPositionTextView = findViewById(R.id.activity_main_sat_position_text_view);
        viewPassesButton = findViewById(R.id.activity_main_view_passes_button);
        closeDialogButton = findViewById(R.id.activity_main_close_button);

        //get location
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            //got location
            userLocation = location;
            userLocationTextView.setText("Your Location: " + userLocation.getLatitude() + "," + userLocation.getLongitude());

            //get satellite data
            getSatelliteData();
        }).addOnFailureListener(e -> {
            //error getting location
            Log.d("DebugK", "Error Getting Location " + e.getMessage());
        });

        //build model renderables
        ModelRenderable.builder().setSource(this, Uri.parse("Earth.sfb")).build().thenAccept(renderable -> planetRenderable = renderable);
        ModelRenderable.builder().setSource(this, R.raw.satellite).build().thenAccept(renderable -> auraSatRenderable = renderable);

        //set AR Plane Tap
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            //create a base node that's attached to the scene, at the base of the plane
            baseAnchorNode = new AnchorNode(hitResult.createAnchor());
            baseAnchorNode.setParent(arFragment.getArSceneView().getScene());

            //show planet
            planetNode.setParent(baseAnchorNode);
            planetNode.setRenderable(planetRenderable);
            planetNode.setWorldScale(new Vector3(2.f, 2.0f, 2.0f));
            planetNode.setLocalPosition(new Vector3(0.0f, 0.3f, 0.0f));
        });

        //set current satellite views
        setCurrentSatelliteViews();

        //set view passes button
        viewPassesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (satNodeToView != null) {
                    Intent viewPassesIntent = new Intent(MainActivity.this, PassesActivity.class);
                    if (satNodeToView.satData.visualPassesJSON != null) {
                        viewPassesIntent.putExtra("satName", satNodeToView.satData.satName);
                        viewPassesIntent.putExtra("passesJSON", satNodeToView.satData.visualPassesJSON.toString());
                    }
                    startActivity(viewPassesIntent);
                }
            }
        });

        //set click listener on close button
        closeDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                satInfoScrollView.setVisibility(View.GONE);
            }
        });
    }

    //method to get satellite data
    void getSatelliteData() {
        //for each satellite, request data
        for (int satId : SatellitesAPI.satelliteIds) {
            Call<ResponseBody> call = satellitesAPI.getSatelliteData(satId, userLocation.getLatitude(), userLocation.getLongitude(), userLocation.getAltitude(), 300);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    //got satellite position and trajectory, create objects and assign
                    try {
                        SatelliteData satelliteData = new SatelliteData(new JSONObject(response.body().string()));
                        satData.add(satelliteData);

                        //if fetching data is over for every satellite, start laying out the board
                        if (satData.size() == SatellitesAPI.satelliteIds.length) {
                            showSatellites();

                            fetchVisualPasses();

                            //fetchRadioPasses();
                        }
                    } catch (Exception e) {
                        Log.d("DebugK", "Error getting satellite data " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    //couldn't get satellite data
                    Log.d("DebugK", "Error getting satellite data " + t.getMessage());
                }
            });
        }
    }

    //method to show satellites on world
    void showSatellites() {
        //create sat nodes
        for (SatelliteData satelliteData : satData) {
            Satellite satNode = new Satellite(satelliteData);
            satNode.setParent(planetNode);
            satNode.setRenderable(auraSatRenderable);
            satNode.setCurrentPosition(0);

            //attach tap listener
            satNode.setOnTapListener(new Node.OnTapListener() {
                @Override
                public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                    satNodeToView = satNode;
                    setCurrentSatelliteViews();

                    //show dialog
                    satInfoScrollView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    //method to get visual passes data for a satellite
    void fetchVisualPasses() {
        for (int satId : SatellitesAPI.satelliteIds) {
            Call<ResponseBody> call = satellitesAPI.getSatelliteVisualPasses(satId, userLocation.getLatitude(), userLocation.getLongitude(), userLocation.getAltitude(), 10, 1);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        //get response object
                        JSONObject responseObject = new JSONObject(response.body().string());

                        Log.d("DebugK", responseObject.getJSONObject("info").getInt("satid") + " - " + responseObject.toString());

                        //continue if any passes were found
                        if (responseObject.getJSONObject("info").getInt("passescount") > 0) {
                            //find the corresponding SatelliteData object for this
                            int satId = responseObject.getJSONObject("info").getInt("satid");
                            for (SatelliteData satelliteData : satData) {
                                if (satId == satelliteData.id) {
                                    //found corresponding object, set the pass data
                                    satelliteData.setVisualPasses(responseObject.getJSONArray("passes"));
                                }
                            }

                            //send a notification
                            NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, "com.piedpipar.satellitego")
                                    .setContentTitle("New Satellite Passing")
                                    .setContentText("A satellite you may be interested in will pass above you.")
                                    .setSmallIcon(R.drawable.ic_launcher_foreground);

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                NotificationChannel notificationChannel = new NotificationChannel("com.piedpipar.satellitego", "main", NotificationManager.IMPORTANCE_DEFAULT);
                                notificationChannel.setDescription("some description");
                                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(notificationChannel);
                            }

                            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                            notificationManagerCompat.notify(123, builder.build());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
    }

    //method to setup current satellite views
    void setCurrentSatelliteViews() {
        if (satNodeToView != null) {
            satIdTextView.setText("" + satNodeToView.satData.id);
            satNameTextView.setText(satNodeToView.satData.satName);
            satPositionTextView.setText(satNodeToView.satData.positions.get(satNodeToView.currentPosition).lat + ", " + satNodeToView.satData.positions.get(satNodeToView.currentPosition).lon);
        }
    }
}
