package com.example.keigo_training;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//かな返還API
class HttpRequestAsync extends AsyncTask<Void, Void, String> {

    question question_;
    r_question r_question;
    URL url;
    MyGlobals myGlobals;
    String line;
    String readStr = "";
    BufferedReader reader;
    public String json3;
    String speak;

    HttpRequestAsync(question question) {
        question_ = question;
    }

    HttpRequestAsync(r_question question) {
        r_question = question;
    }

    //非同期処理(doInBackground)の前処理　(exesute(）呼出し後）
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //  非同期処理
    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection connection = null;
        //接続先URL
        String URL = "https://labs.goo.ne.jp/api/hiragana";

        try {
            url = new URL(URL);
            // 接続先URLへのコネクションを開く．まだ接続されていない
            connection = (HttpURLConnection) url.openConnection();
            // HTTPメソッドをPOSTに指定
            connection.setRequestMethod("POST");
            //HTTPリダイレクトを自動的に従わない
            connection.setInstanceFollowRedirects(false);
            //レスポンスボディの受信を許可する（=入力可能）
            connection.setDoInput(true);
            // リクエストボディの送信を許可する(=出力可能）
            connection.setDoOutput(true);

            //リクエストボディ
            String body = "{\"app_id\":\"[your APIkey]\", " +
                    "\"request_id\":\"record003\", " +
                    "\"sentence\":\" " + speak + " \", " +
                    "\"output_type\":\"hiragana\"}";

            //header
            //データタイプをJSONに指定
            connection.setRequestProperty("Content-type", "application/json");

            //body
            byte[] outputInBytes = body.getBytes(StandardCharsets.UTF_8);
            // リクエストボディの書き込み
            OutputStream os = connection.getOutputStream();
            os.write(outputInBytes);
            os.close();

            //接続・通信開始
            connection.connect();

            //レスポンスコードを取得
            int status = connection.getResponseCode();
            //レスポンスコードが"200"の時
            if (status == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(in));

                while (null != (line = reader.readLine())) {
                    readStr += line;
                }

                Log.d("tag_test", "read string:" + readStr);
                in.close();

                String[] json = readStr.split(",");
                String[] json2 = json[0].split(":");
                json3 = json2[1].substring(2, json2[1].length() - 1);

//                key
                Log.d("JSON", "read string" + json3);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json3;
    }

    //非同期処理の後   ainActivityのメソッド（toast表示）呼び出し
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this.speak = result;

        if (result == null) {
        } else {
            if (question.bo == true) {
                question_.showToast(result);
            } else {
                r_question.showToast(result);
            }
        }
    }

    public void excute(ArrayList<String> result) {
        this.speak = String.valueOf(result);
    }

}


