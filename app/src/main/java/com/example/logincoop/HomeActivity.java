package com.example.logincoop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    EditText et_animal, et_course, et_color;
    TextView tv_email, tv_uid, tv_firstName, tv_lastName;
    Button btn_ViewData, btn_Logout;

    // Firebase Store
    private FirebaseFirestore store;

    // Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Google Auth
    GoogleSignInAccount googleAccount;
    GoogleSignInOptions gso;
    String currentUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Firebase
        store = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Google Auth
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        currentUID = googleAccount.getId();

        if(googleAccount == null){
            Toast.makeText(HomeActivity.this, "Google Account is null", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(HomeActivity.this, "ID = " + currentUID, Toast.LENGTH_LONG).show();
        }


        // Get Views
        et_animal = findViewById(R.id.et_FavoriteAnimal);
        et_course = findViewById(R.id.et_FavoriteCourse);
        et_color = findViewById(R.id.et_FavoriteColor);
//
        tv_email = findViewById(R.id.tv_UserEmail);
        tv_uid = findViewById(R.id.tv_UserID);
        tv_firstName = findViewById(R.id.tv_UserFirst);
        tv_lastName = findViewById(R.id.tv_UserLast);

        btn_ViewData = findViewById(R.id.btn_ViewData);
        btn_Logout = findViewById(R.id.btn_Logout);

        // Set Google Profile
        tv_email.setText(googleAccount.getEmail());
        tv_firstName.setText(googleAccount.getGivenName());
        tv_lastName.setText(googleAccount.getFamilyName());
        tv_uid.setText(googleAccount.getId());

        // Set event listeners
        /**
         * Stores the data to Firestore
         */
        btn_ViewData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, UserDataActivity.class);

                Map<String, Object> user_data = new HashMap<>();
                user_data.put("Animal", et_animal.getText().toString());
                user_data.put("Color", et_color.getText().toString());
                user_data.put("Course", et_course.getText().toString());

                store.collection("user_data")
                        .document(currentUID)
                        .set(user_data)
                        .addOnSuccessListener(new OnSuccessListener(){
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(HomeActivity.this, "Data loaded successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener(){
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(HomeActivity.this, "Error occurred when loading data", Toast.LENGTH_SHORT).show();
                            }
                        });

                startActivity(intent);
            }
        });

        /**
         * Signs the user out with firebase and google
         */
        btn_Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                GoogleSignIn.getClient(HomeActivity.this, gso).signOut();

                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}