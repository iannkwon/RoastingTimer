package test.com.loastingtimer;


import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.Locale;
import java.util.Timer;

public class TimerActivity extends AppCompatActivity {
    TextView text_timer;
    TextView text_list;
    TextView text_tem;
    TextView text_tem2;
    TextView text_humi;
    TextView text_humi2;

    EditText et_tem;
    EditText et_humi;
    EditText et_origin;

    Button btn_tStart;
    Button btn_1st;
    Button btn_2st;
    Button btn_check;
    Button btn_save;
    Button btn_cancel;

    final static int init = 0;
    final static int run = 1;
    final static int pause = 2;

    int status = init;
    int count = 1;
    long baseTime;
    long pauseTime;
    String pop1 = "1st POP!";
    String pop2 = "2st POP!";

    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        text_timer = (TextView) findViewById(R.id.text_timer);
        text_list = (TextView) findViewById(R.id.text_list);
        text_tem = (TextView) findViewById(R.id.text_tem);
        text_tem2 = (TextView) findViewById(R.id.text_tem2);
        text_humi = (TextView) findViewById(R.id.text_humi);
        text_humi2 = (TextView) findViewById(R.id.text_humi2);

        et_tem = (EditText)findViewById(R.id.et_tem);
        et_humi = (EditText)findViewById(R.id.et_humi);
        et_origin = (EditText)findViewById(R.id.et_origin);

        btn_tStart = (Button)findViewById(R.id.btn_tStart);
        btn_1st = (Button)findViewById(R.id.btn_1st);
        btn_2st = (Button)findViewById(R.id.btn_2st);
        btn_check = (Button)findViewById(R.id.btn_check);
        btn_save = (Button)findViewById(R.id.btn_save);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);

        text_list.setMovementMethod(new ScrollingMovementMethod());

    }//end create


    Handler handler = new Handler(){
      public void handleMessage(Message msg){
          text_timer.setText(getTimeOut());

          handler.sendEmptyMessage(0);
      }
    };

    String getTimeOut(){
        long now = SystemClock.elapsedRealtime();
        long outTime = now - baseTime;
        String easy_outTime = String.format("%02d:%02d:%02d", outTime/1000 /60, (outTime/1000)%60, (outTime%1000)/10);
        return easy_outTime;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }//end destroy


    public void onClick(View v){



        switch (v.getId()){
            case R.id.btn_tStart:
                switch (status){
                    case init:
                        baseTime = SystemClock.elapsedRealtime();
                        handler.sendEmptyMessage(0);
                        btn_tStart.setText("정지");
                        btn_check.setEnabled(true);
                        status = run;
                        break;
                    case run:
                        handler.removeMessages(0);
                        pauseTime = SystemClock.elapsedRealtime();
                        btn_tStart.setText("시작");
                        btn_check.setText("리셋");
                        status = pause;
                        break;
                    case pause:
                        long now = SystemClock.elapsedRealtime();
                        handler.sendEmptyMessage(0);
                        baseTime += (now - pauseTime);
                        btn_tStart.setText("정지");
                        btn_check.setText("기록");
                        status = run;
                        break;
                }
                break;
            case R.id.btn_check:
                switch (status){
                    case run:
                        String str = text_list.getText().toString();
                        str +=  String.format("%d. %s\n",count,getTimeOut());
                        text_list.setText(str);
                        count++;
                        break;
                    case pause:
                        //handler stop
                        handler.removeMessages(0);

                        btn_tStart.setText("시작");
                        btn_check.setText("기록");
                        text_timer.setText("00:00:00");

                        status = init;
                        count = 1;
                        text_list.setText("");
                        btn_check.setEnabled(false);
                        break;
                }
                break;
            case R.id.btn_1st:
                switch (status){
                    case run:
                        String str = text_list.getText().toString();
                        str +=  String.format("%d. %s %s \n",count,getTimeOut(),pop1);
                        text_list.setText(str);
                        count++;
                        break;
                    case pause:

                }
                break;
            case R.id.btn_2st:
                switch (status){
                    case run:
                        String str = text_list.getText().toString();
                        str +=  String.format("%d. %s %s \n",count,getTimeOut(),pop2);
                        text_list.setText(str);
                        count++;
                        break;
                    case pause:
                }
                break;
            case R.id.btn_save:

                if (text_list.length()!=0) {
                    final SQLiteDatabase mDatabase = openOrCreateDatabase("timer.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

                    //테이블 삭제
//                mDatabase.execSQL("DROP TABLE IF EXISTS roasting;");
//                Log.i("delete successed","ok");


                    AlertDialog.Builder ab = new AlertDialog.Builder(context);

                    //제목셋팅
                    ab.setTitle("타이머 저장");
                    //AlertDialog 셋팅
                    ab.setMessage("타이머를 저장 하시겠습니까?")
                            .setCancelable(false)
                            .setPositiveButton("저장",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //프로그래 닫힘

                                            long rNow = System.currentTimeMillis();
                                            //현재 시간을 date변수에 저장한다.
                                            Date date = new Date(rNow);
                                            //시간을 나타낼 포맷을 정한다 (yyyy/MM/dd 같은 형태로 변형 가능)
                                            java.text.SimpleDateFormat sdfNow = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                            //nowDate 변수에 값을 저장한다
                                            String rdate = sdfNow.format(date);
                                            String memo = " ";

                                            ContentValues values = new ContentValues();
                                            values.put("origin", String.valueOf(et_origin.getText()));
                                            values.put("rdate", String.valueOf(rdate));
                                            values.put("temp", String.valueOf(et_tem.getText()));
                                            values.put("humi", String.valueOf(et_humi.getText()));
                                            values.put("record", String.valueOf(text_list.getText()));
                                            values.put("end", String.valueOf(text_timer.getText()));
                                            values.put("memo",String.valueOf(memo));
                                            long rowNum = mDatabase.insert("roasting", null, values);
                                            Log.i("testing", "insert rowNum>>>" + rowNum);
                                            Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(),"저장 완료.",Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    })
                            .setNegativeButton("취소",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                        }
                                    });
                    //다이얼로그 생성
                    AlertDialog ad = ab.create();
                    //다이얼로그 보여주기
                    ad.show();


                    break;
                } else {
                    Toast.makeText(getApplicationContext(),"저장할 값이 없습니다.",Toast.LENGTH_LONG).show();
                    break;
                }
            case R.id.btn_cancel:

                AlertDialog.Builder ab = new AlertDialog.Builder(context);

                //제목셋팅
                ab.setTitle("타이머 종료");
                //AlertDialog 셋팅
                ab.setMessage("타이머를 종료 하시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("종료",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //프로그래 닫힘
                                        TimerActivity.this.finish();
                                    }
                                })
                        .setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                //다이얼로그 생성
                AlertDialog ad = ab.create();
                //다이얼로그 보여주기
                ad.show();


//                Intent intent = new Intent(TimerActivity.this, MainActivity.class);
//                startActivity(intent);
                break;




        } //end swich


    } //end onClick

}//end class

