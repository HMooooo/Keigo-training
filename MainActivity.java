package com.example.keigo_training;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    MyGlobals globals;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        globals = (MyGlobals) this.getApplication();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user = mAuth.getUid();

        if (currentUser != null) {
            ImageButton login = (ImageButton) findViewById(R.id.login);
            login.setImageResource(R.drawable.out);

            db.collection("users")
                    .whereEqualTo("userID", user)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                String getUser = "";
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    getUser = document.get("userID").toString();
                                }

                                if (getUser.equals(user)) {
                                    Log.d(TAG, "データを作成しませんでした");
                                    //ユーザー名を変更
                                } else {
                                    //個人データを新規作成
                                    Map<String, Object> muser = new HashMap<>();
                                    muser.put("userID", user);
                                    muser.put("userName", "名無し");
                                    muser.put("score", 0);
                                    muser.put("record", 0);
                                    db.collection("users").document(user).set(muser);
                                    Log.d(TAG, "データを作成しました");
                                }
                            }
                        }
                    });
        }

//        ていねい語ボタン
        findViewById(R.id.easy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globals.level = 1;
                Intent intent = new Intent(MainActivity.this, select.class);
                startActivity(intent);
            }
        });

//        尊敬語ボタン
        findViewById(R.id.normal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globals.level = 2;
                Intent intent = new Intent(MainActivity.this, select.class);
                startActivity(intent);
            }
        });

//        謙譲語ボタン
        findViewById(R.id.hard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                globals.level = 3;
                Intent intent = new Intent(MainActivity.this, select.class);
                startActivity(intent);
            }
        });

//        ランダムボタン
        findViewById(R.id.random).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("user")
                        .whereEqualTo("userID", user)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d(TAG, document.getId() + " => " + document.getData());
                                            String score = document.get("score").toString();
                                            globals.seikaisu = Integer.parseInt(score);
                                        }
                                    } catch (RuntimeException e) {
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                            }
                        });

                if (currentUser == null) {
                    DialogFragment dialogFragment = new LoginDialog();
                    dialogFragment.show(getSupportFragmentManager(), "LoginDialog");
                } else {
                    globals.r_queBo = true;
                    Intent intent = new Intent(MainActivity.this, r_question.class);
                    startActivity(intent);
                }
            }
        });

//        ランキングボタン
        findViewById(R.id.ranking).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ranking.class);
                startActivity(intent);
            }
        });

//        ログインボタン
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_logout();
            }
        });
    }

    public void Yes() {
        Intent intent = new Intent(MainActivity.this, r_question.class);
        startActivity(intent);
    }

    public void Return() {
    }

    public void login_logout() {
        mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getUid();

        if (user == null) {
            //ログイン中じゃなかったらログイン画面へ
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        } else {
            //ログイン中だったらログアウト
            mAuth.getInstance().signOut();
            ImageButton login = (ImageButton) findViewById(R.id.login);
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}