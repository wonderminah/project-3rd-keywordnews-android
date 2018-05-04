package com.example.project.keywordnews;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project.keywordnews.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class Activity_Join extends Activity {

    EditText etId, etPw, etPwc, etName, etPhnum;
    Button joinBt;

    URL url;
    HttpURLConnection con;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_join);

        //변수 받기
        etId = findViewById(R.id.etId);
        etPw = findViewById(R.id.etPw);
        etPwc = findViewById(R.id.etPwc);
        etName = findViewById(R.id.etName);
        etPhnum = findViewById(R.id.etPhnum);
        joinBt = findViewById(R.id.joinBt);

        //Main Thread에서 네트워크 접속 가능하도록 설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    //JOIN 버튼 클릭 시 (joinBt에 onClick으로 연결상태)
    public void doJoin(View view) {

        //입력한 값 받아오기
        String ppid = etId.getText().toString();
        String pppw = etPw.getText().toString();
        String pppwc = etPwc.getText().toString();
        String ppname = etName.getText().toString();
        String ppphnum = etPhnum.getText().toString();

        //유효성 검사
        if (ppid.equals("")) {
            Toast.makeText(this, "아이디를 입력하세요.", Toast.LENGTH_SHORT);
            return;
        }
        if (pppw.equals("")) {
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT);
            return;
        }
        if (pppwc.equals("")) {
            Toast.makeText(this, "비밀번호를 한 번 더 입력하세요.", Toast.LENGTH_SHORT);
            return;
        }
        if (pppw.equals(pppwc) == false) {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT);
            return;
        }
        if (ppname.equals("")) {
            Toast.makeText(this, "이름을 입력하세요.", Toast.LENGTH_SHORT);
            return;
        }
        if (ppphnum.equals("")) {
            Toast.makeText(this, "전화번호를 입력하세요.", Toast.LENGTH_SHORT);
            return;
        }

        //사용자가 입력한 데이터 (서버로 보낼 데이터)를 Map에 저장
        HashMap<String, String> params = new HashMap<>();
        params.put("ppid", ppid);
        params.put("pppw", pppw);
        params.put("ppname", ppname);
        params.put("ppphnum", ppphnum);

        //요청시 보낼 쿼리스트림으로 변환
        String param = makeParams(params);
        Log.d("params", "" + params);

        try {
            //서버의 IP주소, PORT번호, Context root, Request Mapping경로
            url = new URL("http://10.10.15.96:8888/keywordnews/insertPeople");
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
                con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;cahrset=UTF-8");
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
                    Toast.makeText(this, page, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) { //에러 발생
            Toast.makeText(this, "ERROR MESSAGE: " + e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("debug", "error message: " + e.toString());
        } finally {
            if(con != null) {
                con.disconnect();
            }
        }
    }

    //서버로 보낼 데이터를 쿼리 스트링으로 변환 ("?이름=값&이름2=값2" 형식)
    public String makeParams(HashMap<String,String> params){
        StringBuffer sbParam = new StringBuffer();
        String key = "";
        String value = "";
        boolean isAnd = false;

        for(Map.Entry<String,String> elem : params.entrySet()){
            key = elem.getKey();
            value = elem.getValue();

            if(isAnd){
                sbParam.append("&");
            }

            sbParam.append(key).append("=").append(value);

            if(!isAnd){
                if(params.size() >= 2){
                    isAnd = true;
                }
            }
        }

        return sbParam.toString();
    }
}
