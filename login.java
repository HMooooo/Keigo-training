package com.example.keigo_training;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    boolean bo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        EditText mail = findViewById(R.id.editText_mail);
        EditText pass = findViewById(R.id.editText_password);
        Switch sw = (Switch) findViewById(R.id.switch_sign);
        sw.setOnCheckedChangeListener(this);

//        確定ボタン
        findViewById(R.id.button_sign).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String smail = mail.getText().toString();
                        String spass = pass.getText().toString();
                        if (!smail.equals("") && !spass.equals("")) {
                            if (bo == true) {
                                //新規登録
                                createAccount(smail, spass);
                            } else {
                                //サインイン
                                signIn(smail, spass);
                            }
                        } else {
                            Toast.makeText(login.this, "入力されていません",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

//        戻るボタン
        findViewById(R.id.button_return).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(login.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }

    @Override
//    ログイン、新規切り替え
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        TextView log = findViewById(R.id.login);
        if (b == true) {
            bo = true;
            log.setText("新規登録");
        } else {
            bo = false;
            log.setText("ログイン");
        }
    }

    //新規登録メソッド
    private void createAccount(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            if (password.length() < 6) {
                                Toast.makeText(login.this, "パスワードは6文字以上",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(login.this, "作成失敗",
                                        Toast.LENGTH_SHORT).show();
                            }
                            updateUI(null);
                        }
                    }
                });
        // [END create_user_with_email]
    }

    //サインインのメソッド
    private void signIn(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            ;
                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(login.this, "ログイン失敗",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void reload() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    private void updateUI(FirebaseUser user) {
        mAuth = FirebaseAuth.getInstance();
        if (user != null) {
            Intent intent = new Intent(login.this, MainActivity.class);
            startActivity(intent);
        }
    }

}