package com.sehan.mathz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {


    private static final String LEVEL = "LEVEL";
    private ImageView Logout;
    private ImageView Easy;
    private ImageView Medium;
    private ImageView Hard;
    private ImageView Profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logout = findViewById(R.id.logout);
        Easy = findViewById(R.id.easyImg);
        Medium = findViewById(R.id.mediumImg);
        Hard = findViewById(R.id.hardImg);
        Profile = findViewById(R.id.profileImg);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            String uid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot result = task.getResult();
                        String  imageUri = (String) result.get("imageUri") ;

                        if (imageUri == null){
                            Profile.setImageResource(R.drawable.profile1);
                        } else {
                            Picasso.get().load(imageUri).into(Profile);
                        }
                    }
                }
            });
        }


        Hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Question_Screen.class);
                intent.putExtra(LEVEL,"H");
                startActivity(intent);
//                finish();
            }
        });
        Medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Question_Screen.class);
                intent.putExtra(LEVEL,"M");
                startActivity(intent);
//                finish();
            }
        });
        Easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Question_Screen.class);
                intent.putExtra(LEVEL,"E");
                startActivity(intent);
//                finish();
            }
        });
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient client = GoogleSignIn.getClient(getApplicationContext(), gso);
        client.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(),"Logout",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),Login_Screen.class);
                startActivity(intent);
                finish();
            }
        });
    }
    public void goProfile(View v){
        Intent i = new Intent(getApplicationContext(),View_Profile_Screen.class);
        startActivity(i);
    }
}