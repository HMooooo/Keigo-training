package com.example.keigo_training;

import android.app.Application;

public class MyGlobals extends Application {
    String speechText;
    int level;//難易度
    int number;//問題番号
    String result;
    String kaitourei;//解答例
    String kaitou;
    boolean boo;//ログイン判定
    boolean r_queBo;
    int seikaisu;//正解数
}
