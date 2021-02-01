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
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static com.fittoos.saikal.Keys.DISTANCE_COVERED_STR;
import static com.fittoos.saikal.Keys.GAME_DATA_STR;
import static com.fittoos.saikal.Keys.NAME_STR;
import static com.fittoos.saikal.Keys.OWNER;
import static com.fittoos.saikal.Keys.PLAYER1;
import static com.fittoos.saikal.Keys.PLAYER1_STR;
import static com.fittoos.saikal.Keys.PLAYER2;
import static com.fittoos.saikal.Keys.PLAYER2_STR;
import static com.fittoos.saikal.Keys.PLAYERS_LIST_STR;
import static com.fittoos.saikal.Keys.RACE_KEY;
import static com.fittoos.saikal.Keys.ROOMS_STR;
import static com.fittoos.saikal.Keys.TYPE_STR;

public class CreateRaceActivity extends AppCompatActivity {

    private LinearLayout mLL_ProgressWaitForPlayer;
    private Button mButtonDistanceSubmit;
    private EditText mET_InputDistance;
    private TextView mTV_RoomKey;

    //Firebase vars
    private final FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private String roomID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_race);

        //****************** link ui elements *********************************
        mLL_ProgressWaitForPlayer = findViewById(R.id.ll_progress_wait_for_player);
        mButtonDistanceSubmit = findViewById(R.id.button_distance);
        mET_InputDistance = findViewById(R.id.input_distance);
        mTV_RoomKey = findViewById(R.id.room_key);

        //****************** Firebase event listeners **************************
        ValueEventListener player2Listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                {
                    mLL_ProgressWaitForPlayer.setVisibility(View.GONE);
                    Intent intent = new Intent(getApplicationContext(), MainRaceActivity.class);
                    intent.putExtra(RACE_KEY, roomID);
                    intent.putExtra(PLAYER1, getIntent().getStringArrayExtra("playerName"));
                    intent.putExtra(PLAYER2, snapshot.child(NAME_STR).getValue(String.class));
                    intent.putExtra("userType", OWNER);
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

            DatabaseReference keyToRoomRef = mDatabase.getReference(Keys.KEY_TO_ROOM_STR);
            DatabaseReference roomRef = mDatabase.getReference(ROOMS_STR);
            roomID =  roomRef.push().getKey();
            String path = roomID + "/" + Keys.PLAYERS_LIST_STR + "/" + Keys.PLAYER1_STR;

            Map<String, Object> player1Details = new HashMap<>();
            player1Details.put(NAME_STR, getIntent().getStringArrayExtra("playerName"));
            player1Details.put(TYPE_STR, OWNER);
            player1Details.put(DISTANCE_COVERED_STR, 0);
            roomRef.child(path).setValue(player1Details);

            //String easy = RandomString.digits + "ACEFGHJKLMNPQRUVWXY";
            //RandomString rs = new RandomString(5, new SecureRandom(), easy);
            //RandomString gen = new RandomString(5, ThreadLocalRandom.current());
            RandomString rs = new RandomString();
            String roomKey = rs.nextString();
            mTV_RoomKey.setText(roomKey);

            keyToRoomRef.child(roomKey).setValue(roomID);

            assert roomID != null;
            roomRef.child(roomID).child("distance").setValue(Integer.parseInt(mET_InputDistance.getText().toString()));

            DatabaseReference player2Ref = roomRef.child(roomID).child(PLAYERS_LIST_STR).child(PLAYER2_STR);
            player2Ref.addValueEventListener(player2Listener);
        });
    }
}