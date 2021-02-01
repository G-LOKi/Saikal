package com.fittoos.saikal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static com.fittoos.saikal.Keys.DISTANCE_COVERED_STR;
import static com.fittoos.saikal.Keys.DISTANCE_STR;
import static com.fittoos.saikal.Keys.OWNER;
import static com.fittoos.saikal.Keys.PLAYER1;
import static com.fittoos.saikal.Keys.PLAYER1_STR;
import static com.fittoos.saikal.Keys.PLAYER2;
import static com.fittoos.saikal.Keys.PLAYER2_STR;
import static com.fittoos.saikal.Keys.PLAYERS_LIST_STR;
import static com.fittoos.saikal.Keys.ROOMS_STR;
import static com.fittoos.saikal.Keys.USER_TYPE_OWNER;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.fittoos.saikal.Keys.DISTANCE_COVERED;
import static com.fittoos.saikal.Keys.PLAYER1;
import static com.fittoos.saikal.Keys.PLAYER2;
import static com.fittoos.saikal.Keys.PLAYERS_LIST;
import static com.fittoos.saikal.Keys.RACE_KEY;
import static com.fittoos.saikal.Keys.USER_TYPE;
import static com.fittoos.saikal.Keys.USER_TYPE_MEMBER;
import static com.fittoos.saikal.Keys.USER_TYPE_OWNER;

public class MainRaceActivity extends AppCompatActivity {

    private RelativeLayout mRR_positionSelf;
    private RelativeLayout mRR_positionOther;
    private View mV_dummySelf;
    private View mV_dummyOther;
    private TextView mTV_nameSelf;
    private TextView mTV_nameOther;

    private int raceDistance;

    //Firebase vars
    private final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private static final int REQUEST_PERMISSIONS_LOCATION_SETTINGS_REQUEST_CODE = 33;
    private static final int REQUEST_PERMISSIONS_LAST_LOCATION_REQUEST_CODE = 34;
    private static final int REQUEST_PERMISSIONS_CURRENT_LOCATION_REQUEST_CODE = 35;

    private FusedLocationProviderClient mFusedLocationClient;

    LocationRequest locationRequest;
    Location lastLocation = null;
    Location currentLocation = null;
    double lastDistance = 0.0;

    private LocationCallback locationCallback;

    private String currentUserId;
    private  String raceFirebaseKey;

    //Firebase vars
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_race);

        mRR_positionSelf = findViewById(R.id.avatar_position_self);
        mV_dummyOther = findViewById(R.id.dummy_self);
        mTV_nameSelf = findViewById(R.id.name_self);
        mRR_positionSelf = findViewById(R.id.avatar_position_other);
        mV_dummyOther = findViewById(R.id.dummy_other);
        mTV_nameOther = findViewById(R.id.name_other);

        Bundle extras = getIntent().getExtras();

        String playerType = extras.getString(USER_TYPE,null);
        raceFirebaseKey = extras.getString(RACE_KEY,null);

        if(playerType==null){
            startActivity(new Intent(MainRaceActivity.this,HomeActivity.class));
            Toast.makeText(this, "User not present, Join or Create again", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(raceFirebaseKey == null){
            startActivity(new Intent(MainRaceActivity.this,HomeActivity.class));
            Toast.makeText(this, "Race not present, Join or Create again", Toast.LENGTH_SHORT).show();
            finish();
        }

        if(playerType==USER_TYPE_MEMBER){
            currentUserId = PLAYER2;
        }else if(playerType==USER_TYPE_OWNER){
            currentUserId = PLAYER1;
        }else {
            startActivity(new Intent(MainRaceActivity.this,HomeActivity.class));
            Toast.makeText(this, "User not present, Join or Create again", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Text UI
        if(getIntent().getStringExtra("userType").equals(USER_TYPE_OWNER))
        {
            mTV_nameSelf.setText(getIntent().getStringExtra(PLAYER1));
            mTV_nameOther.setText(getIntent().getStringExtra(PLAYER2));
        }
        else
        {
            mTV_nameSelf.setText(getIntent().getStringExtra(PLAYER2));
            mTV_nameOther.setText(getIntent().getStringExtra(PLAYER1));
        }





        DatabaseReference roomRef = mDatabase.getReference(ROOMS_STR);
        DatabaseReference player1DistRef = roomRef.child(raceFirebaseKey).child(PLAYERS_LIST_STR).child(PLAYER1_STR).child(DISTANCE_COVERED_STR);
        DatabaseReference player2DistRef = roomRef.child(raceFirebaseKey).child(PLAYERS_LIST_STR).child(PLAYER2_STR).child(DISTANCE_COVERED_STR);

        //****************** Firebase event listeners **************************
        roomRef.child(DISTANCE_STR).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                raceDistance = Integer.parseInt(Objects.requireNonNull(snapshot.getValue(String.class)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ValueEventListener player1DistList = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int distCovered = Integer.parseInt(Objects.requireNonNull(snapshot.getValue(String.class)));
                float ratio = distCovered*1f/raceDistance;
                mV_dummyOther.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1-ratio));
                mRR_positionOther.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,ratio));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ValueEventListener player2DistList = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int distCovered = Integer.parseInt(Objects.requireNonNull(snapshot.getValue(String.class)));
                float ratio = distCovered*1f/raceDistance;
                mV_dummyOther.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,1-ratio));
                mRR_positionOther.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, ratio));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        if(getIntent().getStringExtra("userType").equals(USER_TYPE_OWNER))
            player2DistRef.addValueEventListener(player2DistList);
        else
            player1DistRef.addValueEventListener(player1DistList);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("rooms").child("raceFirebaseKey").child(PLAYERS_LIST).child(currentUserId);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(7500); //use a value fo about 10 to 15s for a real app
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //Google services client for location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);


                double newDistanceCovered = 0.0;

                currentLocation = locationResult.getLastLocation();

                if(lastLocation!=null){

                    newDistanceCovered = distance(lastLocation,currentLocation);

                }

                if(newDistanceCovered>.020) {
                    lastDistance = lastDistance + newDistanceCovered;
                    lastLocation = currentLocation;

                    mDatabaseReference.child(DISTANCE_COVERED).setValue(lastDistance);

                    float ratio = (float) lastDistance * 1f / raceDistance;
                    mV_dummySelf.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1 - ratio));
                    mRR_positionSelf.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, ratio));
                }
            }

        };
    }



    public static double distance(Location loc1, Location loc2) {

        double lat1 = loc1.getLatitude();
        double lat2 = loc2.getLatitude();

        double lon1 = loc1.getLongitude();
        double lon2 = loc2.getLongitude();

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // in Kilometers


        return distance;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_REQUEST_FINE_LOCATION);

//            checkForLocationSettings();
        } else {


            mFusedLocationClient.getLastLocation().addOnSuccessListener(MainRaceActivity.this, location -> {
                if (location != null) {
                    lastLocation = location;
                }

                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            });
        }


    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(MainRaceActivity.this, location -> {
                        if (location != null) {
                            lastLocation = location;
                        }

                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

                        }
                    });
                } else {

                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSION_REQUEST_FINE_LOCATION);

                    Toast.makeText(getApplicationContext(), "This app requires location permissions to be granted", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}