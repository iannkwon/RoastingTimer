package test.com.loastingtimer;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {

    ListView list_select;
    SQLiteDatabase mDatabase;
    Button btn_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        final ArrayList<String> list = new ArrayList<>();

        mDatabase = openOrCreateDatabase("timer.db",0,null);

        //select * from roasting
        Cursor c = mDatabase.query("roasting",
                null,null,null,null,null,"num desc");
        c.moveToFirst(); //커서 포인터 위치를 첫번째 레코드 위치로
        while (!c.isAfterLast()){ //현재 커서포인터위치에서 다음레코드가 있는지 확인 , 마지막 레코드이면 true recode
            String data = c.getString(c.getColumnIndex("rdate"))+"\n"+
                    "원산지 : "+c.getString(c.getColumnIndex("origin"))+"\n"+
                    "온도 : "+c.getString(c.getColumnIndex("temp"))+"\n"+
                    "습도 : "+c.getString(c.getColumnIndex("humi"))+"\n"+
                    "기록"+"\n"+c.getString(c.getColumnIndex("record"))+"\n"+
                    "배출 : "+c.getString(c.getColumnIndex("end"))+"\n"+
                    "메모"+"\n"+c.getString(c.getColumnIndex("memo"));
            list.add(data);
            c.moveToNext();//커서포인터위치를 다음 레코드 위치로
        }

        list_select = (ListView) findViewById(R.id.list_select);
        list_select.setAdapter(new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1,list
        ));

        list_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("SelectActivity","row>>>"+parent.getItemAtPosition(position));
                Intent intent = new Intent(getApplicationContext(),
                        UpdateActivity.class);
                intent.putExtra("info", parent.getItemAtPosition(position).toString());  //부모뷰의 데이터 요구

                startActivity(intent);


            }
        });
//        btn_home = (Button)findViewById(R.id.btn_home);
//        btn_home.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });




    }
}
