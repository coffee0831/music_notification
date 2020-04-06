package com.example.latestservicesample;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.io.IOException;

public class SoundManageService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //メディアプレーヤーフィールド
    private MediaPlayer _player; //a
    @Override
    public void onCreate(){//b
        //フィールドのメディアプレーヤーオブジェクトを生成
        _player = new MediaPlayer();
        //通知チャンネルのID文字列を用意。
        String id ="soundmanagerservice_notification_channel";//1-1
        //通知チャンネル名をstring.xmlから取得
        String name=getString(R.string.notification_channel_name);//1-2
        //通知チャンネルの重要度を標準に設定。
        int importance=NotificationManager.IMPORTANCE_DEFAULT;
        //通知チャンネルを生成→NotificationChanelをnewする
        NotificationChannel cahnnel =new NotificationChannel(id,name,importance);
        //NotificationManagerオブジェクトを取得→NotificationChanelオブジェクトを有効にする為に生成
        //アクティビティやサービスの親クラスContextクラスのメソッド「getSystemService」を使用、戻り値オブジェクト
        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);//2
        //通知チャンネル「cahnnel」を登録→「cahnnel」をNotificationオブジェクトとして渡す
        manager.createNotificationChannel(cahnnel);
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){//3-1
        //音声ファイルのURI文字列を作成
        String MediaFileUriStr="android.resource://"+getPackageName()+"/"+R.raw.karuizawa_birds;
        //音声ファイルのURIをもとにURIオブジェクトを生成
        Uri MediaFileUri =Uri.parse(MediaFileUriStr);
        try {//3-2
            //メディアプレーヤーに音声ファイルを指定。
            _player.setDataSource(SoundManageService.this,MediaFileUri);
            //非同期でのメディア再生準備が完了した際のリスナを設定
            _player.setOnPreparedListener(new PlayerPreparedListener());
            //メディア再生が終了した際のリスナを設定
            _player.setOnCompletionListener(new PlayerCompletionListener());
            //非同期でメディア再生を準備
            _player.prepareAsync();
        }catch (IOException e){
            e.printStackTrace();
        }
        //定数を返す
        return START_NOT_STICKY;//3-3
    }

    @Override
    public void onDestroy(){
        //プレーヤーが再生中なら
        if(_player.isPlaying()){
            //プレーヤーを停止。
            _player.stop();
        }
        //プレイヤーを解放
        _player.release();
        //プレイヤー用フィールドをnullに。
        _player=null;
    }

    //メディア再生準備が完了時のリスナクラス
    private class PlayerPreparedListener implements MediaPlayer.OnPreparedListener{
        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            //Notificationを作成するBuilderクラス生成
            NotificationCompat.Builder builder=new NotificationCompat.Builder(SoundManageService.this,"soundmanagerservice_notification_channel");
            //通知エリアに表示されるアイコンを設定。
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);
            //通知ドロワーでの表示タイトルを設定。
            builder.setContentTitle(getString(R.string.msg_notification_text_start));
            //起動先Activityクラスを指定したIntentオブジェクトを生成。
            Intent intent=new Intent(SoundManageService.this,MainActivity.class);
            //移動先アクティビティに引き継ぎデータを格納
            //通常のアクティビティ起動なのか、通知をタップしたことによる起動なのか
            intent.putExtra("fromNotification",true);
            //通知ドロワーからアクティビティの起動をするために→PendingIntentオブジェクトを取得し利用する
            PendingIntent stopServiceIntent=PendingIntent.getActivity(SoundManageService.this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT);//1
            //PendingIntentオブジェクトをビルダーに設定
            builder.setContentIntent(stopServiceIntent);//2
            //タップされた通知メッセージを自動的に削除するように設定。
            builder.setAutoCancel(true);
            //BuilderからNotificationオブジェクトを生成
            Notification notification =builder.build();
            //NotificationManagerオブジェクトを取得
            NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            //通知
            manager.notify(1,notification);

        }
    }
    //メディア再生が終了した時のリスナクラス
    private class PlayerCompletionListener implements MediaPlayer.OnCompletionListener{
        @Override
        public void onCompletion(MediaPlayer mp){
            //Notification(通知)を出すために作成するBuilderクラスを生成
            //NotificationCompat.BuilderクラスのネストクラスBuilderクラスをnew
            NotificationCompat.Builder builder=new NotificationCompat.Builder(SoundManageService.this,"soundmanagerservice_notification_channel");
            //通知エリアに表示されるアイコンを設定
            //android SDKにもともと用意されているアイコンを使用
            builder.setSmallIcon(android.R.drawable.ic_dialog_info);//2-1
            //通話ドロワーでの表示タイトルを設定
            //string.xmlに記述した文字列を取得し引き数に渡す
            builder.setContentTitle(getString(R.string.msg_notification_text_finish));//2-2
            //BuilderからNotificationオブジェクトを生成→戻り値は生成されたNotificationオブジェクト
            Notification notification =builder.build();
            //NotificationManagerオブジェクトを取得
            NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            //通知→Notificationオブジェクトを表示する→notify()メソッド
            manager.notify(0,notification);
            stopSelf();
        }
    }

}
