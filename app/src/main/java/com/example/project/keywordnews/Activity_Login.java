package com.example.project.keywordnews;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by SCIT on 2018-03-05.
 */

public class Activity_Login extends Activity {

    EditText etId, etPw;
    Button loginBt, joinBt;
    String ppid, pppw;

    URL url;
    HttpURLConnection con;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        //변수 받기
        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
        loginBt = findViewById(R.id.loginBt);
        joinBt = findViewById(R.id.joinBt);

        //Main Thread에서 네트워크 접속 가능하도록 설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    //LOGIN 버튼 클릭시 로그인 실행 (loginBt에 onClick으로 연결상태)
    public void doLogin(View view) {

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String loginId = mPref.getString("loginId", null);
        Log.d("route log", "Activity_Login > doLogin > loginId: " + loginId);

        String message = "";

        //아이디, 비밀번호 가져오기
        ppid = etId.getText().toString();
        pppw = etPw.getText().toString();

        //유효성 검사
        if (ppid == null) {
            Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT); return;
        }
        if (pppw == null) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT); return;
        }

        //Get updated InstanceID token. >> 토큰값은 정상적으로 들어옴. 다만 로그인을 한 번 하고나면 이 액티비티는 지나치지 않는다는 것을 명심.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("route log", "Activity_Login > onCreate > Refreshed token: " + refreshedToken);

        //사용자가 입력한 데이터 (서버로 보낼 데이터)를 Map에 저장
        HashMap<String, String> params = new HashMap<>();
        params.put("ppid", ppid);
        params.put("pppw", pppw);
        params.put("pptoken", refreshedToken);

        //ServerTask에 보낼 파라메터(params, requestUrl) 정의
//        HashMap<String, String> tokenMap = new HashMap<>();
//        tokenMap.put("refreshedToken", refreshedToken);
//        String requestUrl = "http://10.10.10.163:8888/keywordnews/pushFCMNotification"; // TODO : IP설정
//        Object[] objects = {tokenMap, requestUrl};
//        new ServerTask().execute(objects);

        //요청시 보낼 쿼리스트림으로 변환
        String param = makeParams(params);

        try {
            //서버의 IP주소, PORT번호, Context root, Request Mapping경로
            url = new URL("http://10.10.10.163:8888/keywordnews/loginPeople"); // TODO : IP설정
        } catch (MalformedURLException e) {
            Toast.makeText(this,"잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
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

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

                    String line;
                    String page = "";

                    while ((line = reader.readLine()) != null) {
                        page += line;
                    }
                    message = page;

                    //로그인 실패시
                    if (message.equals("Login Failed")) {
                        Toast.makeText(this, page, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //로그인 성공시 >> 아이디를 저장하고 메인 액티비티로 이동
                    else {
                        SharedPreferences.Editor editor = mPref.edit();
                        editor.putString("loginId", message);
                        editor.commit();
                        Toast.makeText(this, "Welcome, " + message + "!", Toast.LENGTH_SHORT).show();
                        Log.d("route log", "Activity_Login - loginId after save: " + loginId);
                        startActivity(new Intent(this, MainActivity.class));
                    }
                }
            }
        } catch (Exception e) { //에러 발생
            Toast.makeText(this, "에러메시지: " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("route log", "error message: " + e.toString());
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
    }


    //서버로 보낼 데이터를 쿼리 스트링으로 변환 ("?이름=값&이름2=값2" 형식)
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

    //JOIN 버튼 클릭 시 회원가입 화면으로 연결(onClick으로 연결상태)
    public void goToJoinPage(View view) {
        startActivity(new Intent(this, Activity_Join.class));
    }

    BroadcastReceiver mHandleMessageReceiver;
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        try{
            if(mHandleMessageReceiver != null)
                unregisterReceiver(mHandleMessageReceiver);
        }
        catch(Exception e)
        {

        }
        super.onDestroy();

    }
}
