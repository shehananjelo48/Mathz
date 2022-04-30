package com.sehan.mathz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Registration_Screen extends AppCompatActivity {

    private static final String UID = "UID";
    private static final String EMAIL_PASSWORD_LOGIN = "EMAIL_PASSWORD_LOGIN";

    private EditText name,Age,PhoneNumber,Email,Password;
    private Button Register;
    String uid_Google;
    boolean isEmailPasswordLoginEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent =getIntent();
        isEmailPasswordLoginEnabled = intent.getBooleanExtra(EMAIL_PASSWORD_LOGIN, true);
        uid_Google = intent.getStringExtra(UID);

        setContentView(R.layout.activity_registration__screen);
        name = findViewById(R.id.etName);
        Age = findViewById(R.id.etAge);
        PhoneNumber = findViewById(R.id.etPhoneNumber);
        Email = findViewById(R.id.etEmail);
        Password = findViewById(R.id.etPassword);
        Register = findViewById(R.id.btnRegister);

        if (!isEmailPasswordLoginEnabled){
            Email.setVisibility(View.GONE);
            Password.setVisibility(View.GONE);
        }

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation()) {
                    if (isEmailPasswordLoginEnabled) {
                        createUserAccount(Email.getText().toString(), Password.getText().toString());
                    } else {
                        saveUserIntoFirebase(uid_Google);
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean Validation() {
        String Email1 = Email.getText().toString();
        if (!isEmailPasswordLoginEnabled) {
            if (!Patterns.EMAIL_ADDRESS.matcher(Email1).matches()) {
                return false;
            }
            if (TextUtils.isEmpty(Password.getText().toString())) {
                return false;
            }
            if (!(Password.getText().length() == 6)) {
                return false;
            }
            if (TextUtils.isEmpty(Email.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(Password.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(name.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(Age.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(PhoneNumber.getText().toString())) {
                return false;
            }
            if (!(PhoneNumber.getText().length() == 10)) {
                return false;
            }
        } else {
            if (TextUtils.isEmpty(name.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(name.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(Age.getText().toString())) {
                return false;
            }
            if (TextUtils.isEmpty(PhoneNumber.getText().toString())) {
                return false;
            }
            if (!(PhoneNumber.getText().length() == 10)) {
                return false;
            }

        }
        return true;
    }

    private void createUserAccount(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    String uid = task.getResult().getUser().getUid();
                    saveUserIntoFirebase(uid);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserIntoFirebase(String uid) {
        String name = this.name.getText().toString();
        String age = this.Age.getText().toString();
        String phoneNumber = this.PhoneNumber.getText().toString();
        User user = new User(name,age,phoneNumber,null);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(Registration_Screen.this, "User successfully saved to firestore!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registration_Screen.this, "User save failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginClick(View view){
        Intent intent = new Intent(getApplicationContext(),Login_Screen.class);
        intent.putExtra(EMAIL_PASSWORD_LOGIN,true);
        startActivity(intent);
    }
}