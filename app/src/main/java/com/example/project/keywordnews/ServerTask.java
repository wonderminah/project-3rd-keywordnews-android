package com.example.project.keywordnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SCIT on 2018-03-09.
 */

public class ServerTask extends AsyncTask{

    URL url;
    HttpURLConnection con;
    String message;

    @Override
    protected Object doInBackground(Object[] objects) {
        Log.d("route log", "ServerTask > doInBackground()");

        HashMap<String, String> params = (HashMap<String, String>) objects[0];

        //서버로 보낼 데이터를 쿼리 스트링으로 변환 ("?이름=값&이름2=값2" 형식)
        StringBuffer sbParam = new StringBuffer();
        String key = "";
        String value = "";
        boolean isAnd = false;
        for(Map.Entry<String,String> elem : params.entrySet()) {
            key = elem.getKey();
            value = elem.getValue();
            if (isAnd) {
                sbParam.append("&");
            }
            sbParam.append(key).append("=").append(value);
            if (!isAnd) {
                if(params.size() >= 2) {
                    isAnd = true;
                }
            }
        }
        String param = sbParam.toString();
        String requestUrl = (String) objects[1];
        Log.d("route log", "param: " + param + " requestUrl: " + requestUrl);

        try {
            url = new URL(requestUrl); //서버의 IP주소, PORT번호, Context root, Request Mapping경로
        }
        catch (MalformedURLException e) {
            Log.e("route log", "ServerTask > doInBackground > try1 > error message: " + e.toString());
        }

        try {
            con = (HttpURLConnection) url.openConnection();

            if (con != null) {
                con.setConnectTimeout(0);	    //연결제한시간. 0은 무한대기.
                con.setUseCaches(false);		//캐쉬 사용여부
                con.setRequestMethod("POST");   //URL 요청에 대한 메소드 설정 : POST.
                con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream os = con.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String line;
                    String page = "";
                    while ((line = reader.readLine()) != null) {
                        page += line;
                    }
                    message = page;
                }
            }
        }
        catch (Exception e) { //에러 발생
            Log.e("route log", "ServerTask > doInBackground > try2 > error message: " + e.toString());
        }
        finally {
            if(con != null) {
                con.disconnect();
            }
        }

        return message;
    }


    public String makeParams(HashMap<String,String> params) {
        StringBuffer sbParam = new StringBuffer();
        String key = "";
        String value = "";
        boolean isAnd = false;

        for(Map.Entry<String,String> elem : params.entrySet()) {
            key = elem.getKey();
            value = elem.getValue();
            if(isAnd) {
                sbParam.append("&");
            }
            sbParam.append(key).append("=").append(value);
            if(!isAnd){
                if(params.size() >= 2) {
                    isAnd = true;
                }
            }
        }
        return sbParam.toString();
    }
}
