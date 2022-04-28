package com.sehan.mathz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class View_Profile_Screen extends AppCompatActivity {

    private AppBarLayout appBarLayout;
    private NestedScrollView nestedScrollView;
    private ImageView profile;
    private TextView fullName;
    private TextView email;
    private TextView fName;
    private TextView phone;
    private TextView Email;
    private TextView age;
    private ProgressBar progressBar;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__profile__screen);

        appBarLayout = findViewById(R.id.appBarLayout);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        profile = findViewById(R.id.imageView);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        fName = findViewById(R.id.fName);
        phone=findViewById(R.id.phone);
        Email = findViewById(R.id.Email);
        age = findViewById(R.id.age);
        progressBar = findViewById(R.id.progressBar);


        appBarLayout.setVisibility(View.GONE);
        nestedScrollView.setVisibility(View.GONE);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (!(user==null)){
            email.setText(user.getEmail());
            Email.setText(user.getEmail());
            uid = user.getUid();
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot =task.getResult();
                    String name = (String) snapshot.get("name");
                    fName.setText(name);
                    phone.setText((String)snapshot.get("phone"));
                    age.setText((String)snapshot.get("age"));
                    String img = (String) snapshot.get("imageUri");
                    //pro.setImageURI(Uri.parse(img));
                    if (img==null){
                        profile.setImageResource(R.drawable.profile1);
                    }else {
                        Picasso.get().load(img).into(profile);
                    }
                    fullName.setText(name);
                    progressBar.setVisibility(View.GONE);
                    appBarLayout.setVisibility(View.VISIBLE);
                    nestedScrollView.setVisibility(View.VISIBLE);

                }
            }
        });

    }
    public void goEdit(View v){
        Intent i = new Intent(getApplicationContext(),Edite_Profile_Screen.class);
        startActivity(i);
    }
}