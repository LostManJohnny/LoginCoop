package com.example.logincoop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

public class UserDataActivity extends AppCompatActivity {

    // Views
    TextView tv_Animal, tv_Course, tv_Color;

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
        setContentView(R.layout.activity_user_data);

        mAuth = FirebaseAuth.getInstance();
        store = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();
        googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();

        if(googleAccount == null){
            Intent intent = new Intent(UserDataActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        currentUID = googleAccount.getId();

        // Get Views
        tv_Animal = findViewById(R.id.tv_Animal);
        tv_Course = findViewById(R.id.tv_Course);
        tv_Color = findViewById(R.id.tv_Color);

        // Get Store Data
        store.collection("user_data")
                .document(currentUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                tv_Animal.setText((String) document.get("Animal"));
                                tv_Course.setText((String) document.get("Course"));
                                tv_Color.setText((String) document.get("Color"));
                            }
                            else{
                                Toast.makeText(UserDataActivity.this, "Error : Document does not exist", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(UserDataActivity.this, "Error : Call to Firestore unsuccessful", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}