package com.sehan.mathz;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_Screen extends AppCompatActivity {

    private static final int RC_SIGN_IN_GOOGLE = 1;
    private static final String EMAIL_PASSWORD_LOGIN = "EMAIL_PASSWORD_LOGIN";
    private static final String UID = "UID";

    private Button loginBtn;
    private EditText email;
    private EditText password;
    private ImageView continuGoogle;
    private GoogleSignInClient mGoogleSignInClient;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__screen);
         loginBtn =findViewById(R.id.btnLogin);
         email = findViewById(R.id.etEmail);
         password = findViewById(R.id.etPassword);
         continuGoogle = findViewById(R.id.continuGoogle);

         loginBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (isValidate()) {
                     String userEmail = email.getText().toString();
                     String userPassword = password.getText().toString();
                     doLogin(userEmail, userPassword);
                 }else {
                     Toast.makeText(getApplicationContext(),"Something went wrong",Toast.LENGTH_SHORT).show();
                 }
             }
         });

         continuGoogle.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 continuWithGoogle();
             }
         });
    }

    private boolean isValidate() {
        String Email = email.getText().toString().trim();
        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()){
            return false;
        }if (TextUtils.isEmpty(email.getText().toString())){
            return false;
        }if (TextUtils.isEmpty(password.getText().toString())){
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN_GOOGLE){
            Task<GoogleSignInAccount> signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data);
            hadleSigInResult(signedInAccountFromIntent);
        }
    }

    private void hadleSigInResult(Task<GoogleSignInAccount> signedInAccountFromIntent) {
        GoogleSignInAccount result = signedInAccountFromIntent.getResult();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(result.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                uid = currentUser.getUid();
                FirebaseFirestore.getInstance().collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot result1 = task.getResult();
                            if(result1.exists()){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(getApplicationContext(),Registration_Screen.class);
                                intent.putExtra(EMAIL_PASSWORD_LOGIN,false);
                                intent.putExtra(UID,uid);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Something went try wrong!, Try again.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void continuWithGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //startActivityForResult(signInIntent,RC_SIGN_IN_GOOGLE);
        someActivityResultLauncher.launch(signInIntent);
    }


    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> signedInAccountFromIntent = GoogleSignIn.getSignedInAccountFromIntent(data);
                        hadleSigInResult(signedInAccountFromIntent);
                    }
                }
            }
    );




    private void doLogin(String userEmail, String userPassword) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Something went try wrong!, Try again.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void registrarClick(View view){
        Intent intent = new Intent(getApplicationContext(),Registration_Screen.class);
        intent.putExtra("EMAIL_PASSWORD_LOGIN",true);
        startActivity(intent);
    }


}