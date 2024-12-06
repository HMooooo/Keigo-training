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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class question extends AppCompatActivity {

    //  リクエストコードを指定
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    HttpRequestAsync req;
    MyGlobals myGlobals;

    //Cloud Firestoreの準備
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    static boolean bo;

    String test;
    String covertedTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question);
        myGlobals = (MyGlobals) this.getApplication();
        bo = true;

//        問題文を挿入
        db.collection("mondai")
                .whereEqualTo("level", myGlobals.level)
                .whereEqualTo("number", myGlobals.number)
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
                                    TextView tVtn = (TextView) findViewById(R.id.toiname);
                                    TextView tVt = (TextView) findViewById(R.id.toi);
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
        String et1 = "";

        //                問題を取り出し
        db.collection("mondai")
                .whereEqualTo("level", myGlobals.level)
                .whereEqualTo("number", myGlobals.number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String kai = document.get("kaitourei").toString();
                                String kaitou = document.get("kaitou").toString();
                                myGlobals.kaitourei = kai;
                                myGlobals.kaitou = kaitou;
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

//        結果ボタン  合否判定
        findViewById(R.id.result).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                解答を取り出し
                TextView tv1 = (TextView) findViewById(R.id.input);
                String et1St = tv1.getText().toString();

//                Toastの処理
                String stA = "";
                if (myGlobals.kaitourei.equals(et1St)) {
                    stA = "合格";
                } else {
                    stA = "不合格";
                }
//                ↑の結果をグローバルで結果画面に送る
                myGlobals.result = stA;

                Intent intent = new Intent(question.this, result.class);
                startActivity(intent);
                finish();
            }
        });

//       話すボタン 音声認識のインテントへリクエスト
        findViewById(R.id.speak).setOnClickListener(
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
                            Toast.makeText(question.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //  音声認識インテントからのレスポンス後（結果コードとデータを受け取る）
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
        myGlobals.boo = true;
        req = new HttpRequestAsync(this);
    }

    //  showToastメソッド　(受け取ったルビ振り文字列を表示)
    public void showToast(String result) {
        TextView input = findViewById(R.id.input);
        if (!result.equals("えぬゆーえるえる")) {
            input.setText(result);
        } else {
        }
    }

}