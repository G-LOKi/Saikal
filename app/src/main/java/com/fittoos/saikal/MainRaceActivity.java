package com.fittoos.saikal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        String roomID = getIntent().getStringExtra("roomID");

        //Text UI
        if(getIntent().getStringExtra("userType").equals(USER_TYPE_OWNER))
        {
            mTV_nameSelf.setText(getIntent().getStringExtra(PLAYER1));
        }
        else
        {
            mTV_nameSelf.setText(getIntent().getStringExtra(PLAYER2));
        }

        DatabaseReference roomRef = mDatabase.getReference(ROOMS_STR);
        DatabaseReference player1DistRef = roomRef.child(roomID).child(PLAYERS_LIST_STR).child(PLAYER1_STR).child(DISTANCE_COVERED_STR);
        DatabaseReference player2DistRef = roomRef.child(roomID).child(PLAYERS_LIST_STR).child(PLAYER2_STR).child(DISTANCE_COVERED_STR);

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
    }
}