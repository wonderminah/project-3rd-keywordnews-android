package com.example.project.keywordnews;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by SCIT on 2018-03-05.
 */

public class Activity_Keyword extends Activity{

    ListView listView;
    Button addBt;
    ArrayAdapter<String> listViewAdapter;
    String page = "";
    URL url;
    HttpURLConnection con;
    SharedPreferences mPref;
    String loginId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("route log", "Activity_Keyword > onCreate");
        super.onCreate(savedInstanceState);

        //키워드 레이아웃 세팅
        setContentView(R.layout.layout_keyword);

        //액션바 설정
        getActionBar().setTitle("Add or Delete Keyword");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Main Thread에서 네트워크 접속 가능하도록 설정
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //SharedPreference로부터 loginId를 가져온다.
        mPref = PreferenceManager.getDefaultSharedPreferences(this);
        loginId = mPref.getString("loginId", null);
        Log.d("route log", "loginId: " + loginId);

        //가져온 loginId를 params에 담는다.
        HashMap<String, String> params = new HashMap<>();
        params.put("ppid", loginId);

        //요청시 보낼 쿼리스트림으로 변환
        StringBuffer sbParam = new StringBuffer();
        String key = "";
        String value = "";
        boolean isAnd = false;

        for(Map.Entry<String,String> elem : params.entrySet()){
            key = elem.getKey();
            value = elem.getValue();

            if (isAnd) {
                sbParam.append("&");
            }

            sbParam.append(key).append("=").append(value);

            if (!isAnd) {
                if(params.size() >= 2){
                    isAnd = true;
                }
            }
        }
        String param = sbParam.toString();

        //DB에서 loginId의 키워드를 가져온다.
        try {
            url = new URL("http://10.10.10.163:8888/keywordnews/selectKeyword"); // TODO : IP설정
        } catch (MalformedURLException e) {
            Toast.makeText(this,"잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
        }

        try{
            con = (HttpURLConnection) url.openConnection();

            if(con != null) {
                con.setConnectTimeout(10000);	 //연결제한시간. 0은 무한대기.
                con.setUseCaches(false);		 //캐쉬 사용여부
                con.setRequestMethod("POST");    //URL 요청에 대한 메소드 설정 : POST.
                con.setRequestProperty("Accept-Charset", "UTF-8"); // Accept-Charset 설정.
                con.setRequestProperty("Context_Type", "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream os = con.getOutputStream();
                os.write(param.getBytes("UTF-8"));
                os.flush();
                os.close();

                if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
                    String line;

                    while ((line = reader.readLine()) != null){
                        page += line; //page는 DB에서 keyword를 배열 형태로로 받아오게 된다.
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.d("error", "Activity_keyword > error: " + e.toString());
        } finally {
            if(con != null){
                con.disconnect();
            }
        }

        JSONArray jarray = null;
        JSONObject item = null;

        final List<String> keywordList = new ArrayList<>();
        try {
            Log.d("route log", "page: " + page);
            jarray = new JSONArray(page);
            for (int i = 0; i < jarray.length(); i++) {
                item = jarray.getJSONObject(i);
                keywordList.add(item.getString("keyword"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("route log", "Activity_Keyword > jsonParse: " + e.toString());
        }

        //리스트뷰에 뿌린다.
        ListView listView = (ListView) findViewById(R.id.listView);
        listViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                keywordList
        );
        listView.setAdapter(listViewAdapter);

        KeywordLongClickHandler kh = new KeywordLongClickHandler();
        listView.setOnItemLongClickListener(kh);
    }

    //액션버튼을 액션바에 추가.
    //액티비티가 만들어질 때 미리 자동 호출되어 화면에 메뉴 기능을 추가한다. (교재 410p 참조)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("route log", "Activity_Keyword > onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.addkeyword, menu);
        return true;
    }

    AlertDialog dialog; //안에 들어가는 다이얼로그를 멤버변수로. onStop()때 끄기 위해.
    //액션바 ADD 버튼 클릭 시 키워드 추가
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.addKeyword:  //정상 작동함.
                Log.d("route log", "Activity_Keyword > onOptionsItemSelected > case R.id.addKeyword");

                dialog = new AlertDialog.Builder(Activity_Keyword.this).create();
                dialog.setCancelable(true);
                final LinearLayout layout = (LinearLayout) View.inflate(Activity_Keyword.this, R.layout.dialog_insertkeyword, null);
                dialog.setView(layout);

                Button addBt = layout.findViewById(R.id.addBt);
                addBt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText keyword = layout.findViewById(R.id.keyword);
                        Toast.makeText(Activity_Keyword.this, keyword.getText(), Toast.LENGTH_SHORT).show();
                        Log.d("route log", "Activity_Keyword > onOptionsItemSelected > onClick > keyword.getText(): " + keyword.getText().toString());

                        String keywordStr = keyword.getText().toString();
                        if (keyword.getText().toString() == "") { // TODO : 아무것도 입력하지 않고 추가버튼 눌렀을 때 리턴해줘야 하는데, 안되는 상태.
                            Toast.makeText(Activity_Keyword.this, "Write Keyword to add", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("ppid", loginId);
                            params.put("keyword", keywordStr);

                            String param = makeParams(params);
                            String page = "http://10.10.10.163:8888/keywordnews/insertKeyword"; // TODO : IP설정
                            String[] stringArray = {param, page};

                            new MyTask().execute(stringArray);
                        }
                    }
                });
                dialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
            }
        return true;
    }

    //AsyncTask를 통해 잠시 키워드를 DB에 저장하러 다녀오기 위해 액티비티가 멈출 때, 다이얼로그를 닫아주어 액티비티 누설을 막는다.
    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
//        출처: http://gogorchg.tistory.com/entry/꼭-다이얼로그를-onDestroy에서-dismiss-시키는-버릇을-만들자 [항상 초심으로]
    }

    public class MyTask extends AsyncTask<String, Void, Void> {
        String message = "";

        @Override
        protected Void doInBackground(String... stringArray) {
            Log.d("route log", "Activity_Keyword > MyTask > doInBackground > param: " + stringArray[0]);
            Log.d("route log", "Activity_Keyword > MyTask > doInBackground > page: " + stringArray[1]);

            try {
                //서버의 IP주소, PORT번호, Context root, Request Mapping경로
                url = new URL(stringArray[1]); // TODO : IP설정
            } catch (MalformedURLException e) {
                Toast.makeText(Activity_Keyword.this,"잘못된 URL입니다.", Toast.LENGTH_SHORT).show();
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
                    os.write(stringArray[0].getBytes("UTF-8"));
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
            } catch (Exception e) { //에러 발생
                Log.d("route log", "error message: " + e.toString());
                e.printStackTrace();
            } finally {
                if(con != null) {
                    con.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("route log", "Activity_Keyword > MyTask > onPostExecute()");
            super.onPostExecute(aVoid);
            Toast.makeText(Activity_Keyword.this, message, Toast.LENGTH_LONG).show(); //키워드 추가 시도결과 알림

//            Intent intent = new Intent(Activity_Keyword.this, Activity_Keyword.class);
//            startActivity(intent); // TODO : 키워드 저장 후 액티비티 새로고침...
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

    //키워드 롱클릭시 키워드 삭제
    public class KeywordLongClickHandler implements ListView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
            Log.d("route log", "Activity_Keyword > KeywordLongClickHandler > loginId: " + loginId); //아이디 진입여부 확인

            //롱클릭한 키워드를 가져온다.
            String keyword = adapterView.getItemAtPosition(position).toString();
            Log.d("route log", "Activity_Keyword > KeywordLongClickHandler > keyword: " + keyword); //키워드 진입여부 확인

            //사용자가 입력한 데이터 (서버로 보낼 데이터)를 Map에 저장
            HashMap<String, String> params = new HashMap<>();
            params.put("ppid", loginId);
            params.put("keyword", keyword);

            //요청시 보낼 쿼리스트림으로 변환
            String param = makeParams(params);
            String page = "http://10.10.10.163:8888/keywordnews/deleteKeyword"; // TODO : IP설정
            String[] stringArray = {param, page};
            new MyTask().execute(stringArray);

            return false;
        }
    }
}
