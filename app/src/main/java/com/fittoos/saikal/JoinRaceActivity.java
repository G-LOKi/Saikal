package com.fittoos.saikal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static com.fittoos.saikal.Keys.IS_REGISTERED;
import static com.fittoos.saikal.Keys.PLAYER1;
import static com.fittoos.saikal.Keys.PLAYER2;
import static com.fittoos.saikal.Keys.PLAYERS_LIST;
import static com.fittoos.saikal.Keys.RACE_KEY;
import static com.fittoos.saikal.Keys.USER_DETAILS;
import static com.fittoos.saikal.Keys.USER_NAME;
import static com.fittoos.saikal.Keys.USER_TYPE_MEMBER;

import com.google.firebase.database.FirebaseDatabase;

public class JoinRaceActivity extends AppCompatActivity {

    private Button mButtonEnterRace;
    private EditText mEditText;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabaseReferenceKeyToRoom;

    //Firebase vars
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_race);

        //****************** link database elements *********************************
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("rooms");
        mDatabaseReferenceKeyToRoom = FirebaseDatabase.getInstance().getReference().child("keytoroom");

        //****************** link ui elements *********************************
        mButtonEnterRace = findViewById(R.id.button_enter_race);
        mEditText = findViewById(R.id.input_key);

        //****************** onClick Listeners of buttons **********************
        mButtonEnterRace.setOnClickListener(view -> {

            String raceKey = mEditText.getText().toString().trim();


            if(raceKey.isEmpty()){
                Toast.makeText(this, "Ask for the Race Key from your fried or create a new race", Toast.LENGTH_SHORT).show();
            } else {

                mDatabaseReferenceKeyToRoom.child(raceKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String raceFirebaseKey = Objects.requireNonNull(snapshot.getValue()).toString();
                        mDatabaseReference.child(raceFirebaseKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                                boolean isRegistered = sharedPreferences.getBoolean(IS_REGISTERED, false);
                                String name = sharedPreferences.getString(USER_NAME, "");

                                if(!isRegistered || name.equals("")){
                                    Toast.makeText(JoinRaceActivity.this, "Your name doesn't exist. register again", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(JoinRaceActivity.this, UserRegistrationActivity.class));
                                    finish();
                                }else{

                                    HashMap<String,String> hp = new HashMap<>();

                                    hp.put("name",name);
                                    hp.put("type",USER_TYPE_MEMBER);

                                    mDatabaseReference.child(raceFirebaseKey).child(PLAYERS_LIST).child(PLAYER2).setValue(hp);

                                    String player1Name = Objects.requireNonNull(snapshot.child(PLAYERS_LIST).child(PLAYER1).child("name").getValue()).toString();
                                    int playerNumber = 2;
                                    String player2Name = name;

                                    if(player1Name.equals("")){
                                        Toast.makeText(JoinRaceActivity.this, "Player 1 name isn't present, create new race", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(view.getContext(), HomeActivity.class);
                                        startActivity(intent);
                                    }

                                    Intent intent = new Intent(view.getContext(), MainRaceActivity.class);
                                    intent.putExtra(PLAYER1,player1Name);
                                    intent.putExtra(PLAYER2,player2Name);
                                    intent.putExtra(RACE_KEY,raceKey);
                                    startActivity(intent);

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(JoinRaceActivity.this, "Key doesn't exist, try again or create your a new race", Toast.LENGTH_SHORT).show();
                    }
                });


            }





        });



    }
}