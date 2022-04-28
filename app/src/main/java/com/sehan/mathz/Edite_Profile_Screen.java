package com.sehan.mathz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Edite_Profile_Screen extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private ProgressBar progressBar;
    private ImageView pro;
    private TextInputEditText age;
    private TextInputEditText phoneNumber;
    private TextInputEditText fName;

    private String uid;
    private Uri mImageuri;
    private boolean imageUpload;
    StorageReference mStoragerefernce;
    private String downloadImageURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edite__profile__screen);

        age = findViewById(R.id.age);
        phoneNumber = findViewById(R.id.pNumber);
        progressBar = findViewById(R.id.progressBar);
        pro= findViewById(R.id.imageView);
        fName = findViewById(R.id.fName);
        progressBar.setVisibility(View.VISIBLE);
        imageUpload = false;

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (!(user==null)){
            uid = user.getUid();
        }
        mStoragerefernce= FirebaseStorage.getInstance().getReference().child("User");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot =task.getResult();
                    fName.setText((String) snapshot.get("name"));
                    age.setText((String) snapshot.get("age"));
                    phoneNumber.setText((String) snapshot.get("phone"));
                    String img = (String) snapshot.get("imageUri");
                    //pro.setImageURI(Uri.parse(img));
                    if (img==null){
                        pro.setImageResource(R.drawable.profile1);
                    }else {
                        Picasso.get().load(img).into(pro);
                    }
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });
    }
    public void change(View v){
        progressBar.setVisibility(View.VISIBLE);
        String firstname = this.fName.getText().toString();

        if (imageUpload) {

            final StorageReference filerefernce = mStoragerefernce.child(mImageuri.getLastPathSegment() + firstname + ".jpg");
            final UploadTask uploadTask = filerefernce.putFile(mImageuri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message = e.toString();
                    Toast.makeText(getApplicationContext(), "Error" + message, Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            downloadImageURL = filerefernce.getDownloadUrl().toString();
                            return filerefernce.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageURL = task.getResult().toString();
                                saveDataWithImage();
                            }
                        }
                    });
                }
            });
        }else {
            saveData();
        }



    }

    private void saveDataWithImage() {
        String firstname = this.fName.getText().toString();
        String age = this.age.getText().toString();
        String phonenumber = this.phoneNumber.getText().toString();
        User user= new User(firstname,age,phonenumber,downloadImageURL);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "User successfully saved to firestore!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "User save failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void saveData() {
        String firstname = this.fName.getText().toString();
        String age = this.age.getText().toString();
        String phonenumber = this.phoneNumber.getText().toString();
        User user= new User(firstname,age,phonenumber,null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "User successfully saved to firestore!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
                progressBar.setVisibility(View.GONE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "User save failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST ){
            if (requestCode!=RESULT_OK && data != null) {
                mImageuri = data.getData();
                pro.setImageURI(mImageuri);
                imageUpload = true;
                //Picasso.get().load(mImageuri).into(mImageView);
            }
        }
    }
}