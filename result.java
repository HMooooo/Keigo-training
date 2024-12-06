package com.example.keigo_training;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static android.content.ContentValues.TAG;

public class result extends AppCompatActivity {

    //    SoundPol設定
    private SoundPool soundPool;
    private int soundYes, soundNo;

    @SuppressLint("WrongViewCast")
    MyGlobals myGlobals;
    int lastNumber;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);

//        SoundPool利用
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
//                ストリーム数に応じて
                .setMaxStreams(2)
                .build();

//        正解音声
        soundYes = soundPool.load(this, R.raw.yes, 1);

//        不正解音声
        soundNo = soundPool.load(this, R.raw.no, 1);

//        画像準備
        ImageView iv = (ImageView) findViewById(R.id.image);

        //Cloud Firestoreの準備
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        myGlobals = (MyGlobals) this.getApplication();
        Button bt = findViewById(R.id.next);
        TextView ans = findViewById(R.id.answere);
        ans.setText(myGlobals.kaitou);

        db.collection("mondai")
                .whereEqualTo("level", myGlobals.level)
                .orderBy("number", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String lastnum = document.get("number").toString();
                                    lastNumber = Integer.parseInt(lastnum);

//                                    合格、不合格判別
                                    Boolean bl = true;
                                    if (myGlobals.result == "合格") {
                                        bl = true;
//                                        最重問題化判別
                                        if (myGlobals.number != lastNumber) {
                                            String text_return = "次へ";
                                            bt.setText(text_return);
                                        } else {
                                            String text_return = "戻る";
                                            bt.setText(text_return);
                                        }
                                    } else {
                                        String text_return = "戻る";
                                        bt.setText(text_return);
                                        bl = false;
                                    }

//                                    画像変更

                                    int streamID = 0;

                                    if (bl == true) {
                                        iv.setImageResource(R.drawable.o);

                                        do {
                                            streamID = soundPool.play(soundYes, 1.0f, 1.0f, 0, 0, 1);
                                        } while (streamID == 0);

                                    } else {
                                        iv.setImageResource(R.drawable.n);

                                        do {
                                            streamID = soundPool.play(soundNo, 1.0f, 1.0f, 1, 0, 1);
                                        } while (streamID == 0);
                                    }

                                }
                            } catch (RuntimeException e) {
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        問題へボタン
        findViewById(R.id.skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(result.this, select.class);
                startActivity(intent);
                finish();
            }
        });

//        次へボタン、戻る切り替え
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                合否判定続き（グローバルからstAの値を持ってくる）
                if (myGlobals.result.equals("合格")) {
                    if (myGlobals.number == lastNumber) {
                        Intent intent = new Intent(result.this, select.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 変数プラス１
                        myGlobals.number += 1;
                        Intent intent = new Intent(result.this, question.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(result.this, question.class);
                    startActivity(intent);
                    finish();
                }
                Log.e("lastNumber", String.valueOf(lastNumber));
                Log.e("number", String.valueOf(myGlobals.number));
            }
        });

    }
}