package com.example.keigo_training;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class select extends AppCompatActivity {

    //Cloud Firestoreの準備
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyGlobals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        ArrayList<String> data = new ArrayList<>();
        globals = (MyGlobals) this.getApplication();
        TextView tvLevel = findViewById(R.id.level);

        //        リストビュー結びつけ
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextSize(35);
                view.setHeight(100);
                return view;
            }
        };

        final ListView lvl = (ListView) findViewById(R.id.list);

//        リストビューに挿入
        db.collection("mondai")//collectionが"mondai"のところ
                .whereEqualTo("level", globals.level)
                .orderBy("number", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {//タスクがあったら
                            for (QueryDocumentSnapshot document : task.getResult()) {//そのタスクを持ってくる
                                Log.d(TAG, document.getId() + " => " + document.getData());//Log出す
                                String mondai = "";
                                String number;
                                number = document.get("number").toString();
                                mondai = document.get("mondai").toString();//sに"mondaibun"をStringにして入れる
                                adapter.add(number + " , " + mondai);
                                Log.e("hottaWrite", number);

                                String level = document.get("level").toString();
                                if (level.equals("1")) {
                                    tvLevel.setText("１　丁寧語");
                                } else if (level.equals("2")) {
                                    tvLevel.setText("２　尊敬語");
                                } else {
                                    tvLevel.setText("３　謙譲語");
                                }

                            }
                            lvl.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        リストビューを押す
        lvl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                db.collection("mondai")//collectionが"mondai"のところ
                        .whereEqualTo("level", globals.level)
                        .whereEqualTo("number", position + 1)//クエリ："bangou"がiのドキュメントを指定
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        String num;
                                        num = document.get("number").toString();//aに"number"をStringにしていれる
                                        globals.number = Integer.parseInt(num);//Global変数にaをintに変換していれる
                                        Log.e("hotta0", String.valueOf(globals.number));
                                    }
                                } else {
                                    Log.w(TAG, "Error getting documents.", task.getException());
                                }
                                Intent intent = new Intent(select.this, question.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });

//        開始画面に戻る(戻るボタン)
        findViewById(R.id.return_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(select.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
