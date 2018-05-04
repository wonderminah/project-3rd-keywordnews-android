package com.example.project.keywordnews;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by SCIT on 2018-03-05.
 */

public class Activity_Splash extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(3000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
        String loginId = mPref.getString("loginId", null);
        Log.d("route log", "Activity_Splash > onCreate " + loginId);

        //로그인 아이디가 저장되어있지 않을 시 > 로그인 액티비티로 이동 // TODO : 푸시 테스트 때문에 로그인 계속 해야하도록 만들어 놓음.
        if (loginId == null) {
            Log.d("route log", "Activity_Splash > onCreate > loginId 없음");
            startActivity(new Intent(this, Activity_Login.class));
            this.finish();
        }

        //로그인 아이디가 저장되어 있을 시 > 메인 액티비티로 바로 이동
        else {
            Log.d("route log", "Activity_Splash > onCreate > loginId 있음");
            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }

        //출처: http://yongtech.tistory.com/100
    }
}
