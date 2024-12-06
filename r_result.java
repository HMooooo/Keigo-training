package com.example.keigo_training;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

public class r_result extends AppCompatActivity {
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    MyGlobals myglobals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r_result);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user = mAuth.getUid();
        myglobals = (MyGlobals) this.getApplication();

//        正解数カウント
        TextView ans = findViewById(R.id.answere_R);
        ans.setText(myglobals.kaitou);
        TextView pass = findViewById(R.id.pass_R);
        pass.setText("連続" + valueOf(myglobals.seikaisu) + "問正解");
        TextView rec = findViewById(R.id.record);
        if (currentUser != null) {
            db.collection("users")
                    .whereEqualTo("userID", user)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String record = document.get("record").toString();
                                    rec.setText("過去の記録：" + record);
                                }
                            }
                        }
                    });
        } else {
            rec.setText("");
        }

//        戻る
        findViewById(R.id.skip_R).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    Map<String, Object> muser = new HashMap<>();
                    muser.put("score", 0);
                    db.collection("users").document(user).update(muser);
                }else{
                    myglobals.seikaisu=0;
                }

                Intent intent = new Intent(r_result.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        アップロードボタン
        findViewById(R.id.uplord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Toast.makeText(r_result.this, "ログインしていません", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> muser = new HashMap<>();
                    muser.put("record", myglobals.seikaisu);
                    db.collection("users").document(user).update(muser);
                    Toast.makeText(r_result.this, "アップロードしました", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}