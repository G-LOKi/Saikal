package com.fittoos.saikal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import static com.fittoos.saikal.Keys.IS_REGISTERED;
import static com.fittoos.saikal.Keys.USER_DETAILS;
import static com.fittoos.saikal.Keys.USER_NAME;

public class UserRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);

        TextInputEditText usernameInput = findViewById(R.id.username_input);
        Button submitButton = findViewById(R.id.next_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(usernameInput.getText().toString().trim().isEmpty()){
                    Toast.makeText(UserRegistrationActivity.this, "You don't have a name? You may enter your favourite fruit then.", Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences sharedPreferences = v.getContext().getSharedPreferences(USER_DETAILS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(USER_NAME, usernameInput.getText().toString().trim());
                    editor.putBoolean(IS_REGISTERED, true);
                    editor.commit();
                    startActivity(new Intent(UserRegistrationActivity.this,HomeActivity.class));
                    finish();
                }

            }
        });

    }
}