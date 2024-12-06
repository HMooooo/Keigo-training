package com.example.keigo_training;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ranking extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);
        mAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();
        String user = mAuth.getUid();
        TextView tv_record = (TextView) findViewById(R.id.record);

        ArrayList<String> data = new ArrayList<>();

//        ユーザー名取り出し
        db.collection("users")
                .whereEqualTo("userID", user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            String sname = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                sname = document.get("userName").toString();
                                EditText ename = findViewById(R.id.user);
                                ename.setText(sname);

                                String record = document.get("record").toString();
                                int irecord = Integer.parseInt(record);
                                tv_record.setText("あなたの記録：" + irecord);
                            }
                        }
                    }
                });

//        名前変更
        findViewById(R.id.confirm).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (user != null) {
                            EditText ename = findViewById(R.id.user);
                            String sname = ename.getText().toString();
                            Map<String, Object> muser = new HashMap<>();
                            muser.put("userName", sname);
                            db.collection("users").document(user).update(muser);
                            Toast.makeText(ranking.this, "変更しました",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ranking.this, "ログインしていません",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

//        リストビュー結びつけ
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        final ListView lvl = (ListView) findViewById(R.id.rank);
        lvl.setAdapter(adapter);

        db.collection("users")
                .orderBy("record", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = document.get("userName").toString();
                                String record = document.get("record").toString();
                                adapter.add(name + ":" + record);
                            }
                            lvl.setAdapter(adapter);
                        }
                    }
                });

//        戻るボタン
        findViewById(R.id.return_rank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ranking.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
