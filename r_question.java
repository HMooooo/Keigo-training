package com.example.keigo_training;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class r_question extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    MyGlobals myGlobals;
    HttpRequestAsync req;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    String test;
    String covertedTest;
    int lastNumber;
    int r_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_question);
        myGlobals = (MyGlobals) this.getApplication();
        question.bo = false;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user = mAuth.getUid();

        if (currentUser != null && myGlobals.r_queBo == true) {
            db.collection("users")
                    .whereEqualTo("userID", user)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String score = document.get("score").toString();
                                    myGlobals.seikaisu = Integer.parseInt(score);
                                    myGlobals.r_queBo = false;
                                }
                            }
                        }
                    });
        } else {
        }

        db.collection("mondai")
                .orderBy("randomNumber", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String lastnum = document.get("randomNumber").toString();
                                    lastNumber = Integer.parseInt(lastnum);
                                    Random r = new Random();
                                    r_number = r.nextInt(lastNumber + 1);
//                                    r_level=(int)(Math.random())*lastNumber;
                                    mondai();
                                }
                            } catch (RuntimeException e) {
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        音声ボタン
        findViewById(R.id.speak_R).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //音声認識のIntentインスタンス
                        Intent intent
                                = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        //自由形式の音声認識に基づく言語モデルを指定
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        //（オプション）デフォルトの言語で音声認識を行う
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                                Locale.getDefault());
                        //ユーザーに発話を求めるテキスト「音声を入力してください」を指定
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力してください");
                        try {
                            //音声認識の開始
                            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
                        } catch (Exception e) {
                            Toast.makeText(r_question.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

//        中断ボタン
        findViewById(R.id.interrup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    Map<String, Object> muser = new HashMap<>();
                    muser.put("score", myGlobals.seikaisu);
                    db.collection("users").document(user).update(muser);
                }
                DialogFragment dialogFragment = new InterrpDialog();
                dialogFragment.show(getSupportFragmentManager(), "interrp_dialog");
            }
        });

//        合否判定
        findViewById(R.id.result_R).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //問題を取り出す
                TextView tv1 = (TextView) findViewById(R.id.input_R);
                String et1St = tv1.getText().toString();

//                合否判定
                String stA_R = "";
                if (myGlobals.kaitourei.equals(et1St)) {
//                    次の問題へ
                    stA_R = "合格";
                    myGlobals.seikaisu += 1;
                    Intent intent = new Intent(r_question.this, r_question.class);
                    startActivity(intent);
                } else {
//                    ランダム結果へ
                    stA_R = "不合格";
                    Intent intent = new Intent(r_question.this, r_result.class);
                    startActivity(intent);
                }

            }
        });
    }

    //    ランダムで問題標示
    void mondai() {
        TextView tVtn = (TextView) findViewById(R.id.toiname_R);
        TextView tVt = (TextView) findViewById(R.id.toi_R);
        db.collection("mondai")
                .whereEqualTo("randomNumber", r_number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            try {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    String mondaibun = document.get("mondaibun").toString();
                                    String mondai = document.get("mondai").toString();
                                    String number = document.get("number").toString();
                                    myGlobals.kaitourei = document.get("kaitourei").toString();
                                    myGlobals.kaitou = document.get("kaitou").toString();
                                    tVtn.setText(number + " , " + mondai);
                                    tVt.setText(mondaibun);
                                }
                            } catch (RuntimeException e) {
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //onActivityResultをオーバーライド
        super.onActivityResult(requestCode, resultCode, data);
        //リクエストコードが一致しているか
        ArrayList<String> result = null;
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            //結果コードがRESULT＿OKかつデータがある場合
            if (resultCode == RESULT_OK && data != null) {
                //受取データをArrayListで取得
                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                /*goto comment out 1221*/
                //                myGlobals.speechText = Objects.requireNonNull(result).get(0);
                test = String.valueOf(result);
                covertedTest = test.substring(1, test.length() - 1);
            }
        }

//      HttpRequestAsyncクラスをインスタンス化
        setNewAsync();
//      ルビ振りAPIの処理要求
        req.onPostExecute(covertedTest);
        req.execute();
    }

    //  setNewAsyncメソッド　
    private void setNewAsync() {
        myGlobals.boo = false;
        req = new HttpRequestAsync(this);
    }

    //      受け取ったルビ振り文字列を表示
    //    nullを削除
    public void showToast(String result) {
        TextView input = findViewById(R.id.input_R);
        if (!result.equals("えぬゆーえるえる")) {
            input.setText(result);
        } else {
        }
    }

    //    ダイアログの処理
    public void inteY() {
        Toast.makeText(r_question.this, "中断しました", Toast.LENGTH_SHORT).show();
        //        画面遷移(中断した後の画面)
        Intent intent = new Intent(r_question.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void inteN() {
    }
}
