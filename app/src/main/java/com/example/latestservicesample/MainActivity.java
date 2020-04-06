package com.example.latestservicesample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //インテントオブジェクトを取得
        Intent intent=getIntent();
        //通知タップからの引き継ぎデータを取得
        boolean fromNotification=intent.getBooleanExtra("fromNotification",false);
        //通知のタップからの引き継ぎデータ(fromNotification)が存在する場合、つまり通知のタップからならば..
        if(fromNotification){
            //再生ボタンをタップ不可に、停止ボタンをタップ可に変更
            Button btPlay=findViewById(R.id.btPlay);
            Button btStop=findViewById(R.id.btStop);
            btPlay.setEnabled(false);
            btStop.setEnabled(true);
        }
    }
    public void onPlayButtonClick(View view){
        //インテントオブジェクトを生成
        Intent intent=new Intent(MainActivity.this,SoundManageService.class);//4-1
        //サービスを起動
        startService(intent);//4-2
        //再生ボタンをタップ不可に、停止ボタンをタップ可能に変更
        Button btPlay =findViewById(R.id.btPlay);
        Button btStop =findViewById(R.id.btStop);
        btPlay.setEnabled(false);
        btStop.setEnabled(true);
    }

    public void onStopButtonClick(View view){
        //インテントオブジェクトを生成
        Intent intent=new Intent(MainActivity.this,SoundManageService.class);
        //サービスを停止
        stopService(intent);
        //再生ボタンをタップ可能に、停止ボタンをタップ不可に変更
        Button btPlay=findViewById(R.id.btPlay);
        Button btStop=findViewById(R.id.btStop);
        btPlay.setEnabled(true);
        btStop.setEnabled(false);
    }


}
