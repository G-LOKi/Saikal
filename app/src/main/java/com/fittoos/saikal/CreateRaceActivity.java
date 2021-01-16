package com.fittoos.saikal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.fittoos.saikal.Keys.GAME_DATA_STR;
import static com.fittoos.saikal.Keys.PLAYER1_STR;
import static com.fittoos.saikal.Keys.PLAYER2_STR;
import static com.fittoos.saikal.Keys.PLAYERS_LIST_STR;
import static com.fittoos.saikal.Keys.ROOMS_STR;

public class CreateRaceActivity extends AppCompatActivity {

    private LinearLayout mLL_ProgressWaitForPlayer;
    private Button mButtonDistanceSubmit;
    private EditText mET_InputDistance;

    //Firebase vars
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_race);

        //****************** link ui elements *********************************
        mLL_ProgressWaitForPlayer = findViewById(R.id.ll_progress_wait_for_player);
        mButtonDistanceSubmit = findViewById(R.id.button_distance);
        mET_InputDistance = findViewById(R.id.input_distance);

        //****************** Firebase event listeners **************************
        ValueEventListener player2Listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                {
                    mLL_ProgressWaitForPlayer.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), MainRaceActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        //****************** onClick Listeners of buttons **********************
        mButtonDistanceSubmit.setOnClickListener(view -> {
            mLL_ProgressWaitForPlayer.setVisibility(View.VISIBLE);

            String roomID = getIntent().getStringExtra("roomID");
            DatabaseReference gameDataRef = mDatabase.getReference(GAME_DATA_STR).child(roomID);
            gameDataRef.child("distance").setValue(Integer.parseInt(mET_InputDistance.getText().toString()));

            DatabaseReference roomRef = mDatabase.getReference(ROOMS_STR).child(roomID).child(PLAYERS_LIST_STR).child(PLAYER2_STR);
            roomRef.addValueEventListener(player2Listener);
        });
    }
}