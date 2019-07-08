package com.ssb.provider0708;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText nameEdit,phoneEdit;
    Button upsert;

    //읽어온 데이터를 저장할 ArrayList
    ArrayList<Map<String,Object>> list;

    //공유 데이터의 Uri를 저장할 변수
    Uri uri;

    //수정 모드인지 저장할 변수
    boolean isUpdate;
    //선택한 셀의 id를 저장할 변수
    String _id;

    //공유 데이터를 읽어서 ListView에 출력해주는 메소드
    private  void setAdapter(){
        //데이터를 저장할 인스턴스를 생성
        list = new ArrayList<>();
        //데이터 가졍오기
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        //데이터를 읽어서 저장하기
        while (cursor.moveToNext()){
            HashMap<String,Object> map = new HashMap<>();
            map.put("id",cursor.getString(0));
            map.put("name",cursor.getString(1));
            map.put("phone",cursor.getString(2));
            list.add(map);
        }
        //데이터 출력
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,list,android.R.layout.simple_list_item_2,
                new String[]{"name","phone"},new int[]{android.R.id.text1,android.R.id.text2});
        listView.setAdapter(adapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //뷰 찾아오기
        listView = (ListView)findViewById(R.id.listview);
        nameEdit =(EditText)findViewById(R.id.nameedit);
        phoneEdit=(EditText)findViewById(R.id.phoneedit);
        upsert=(Button)findViewById(R.id.upsert);

        //콘텐트 프로바이더의 uri 생성
        uri = Uri.parse("content://com.example.part.Provider");
        //데이터를 읽어와서 출력하는 메소드 호출
        setAdapter();

        //ListView의 Item을 길게 누르면 호출되는 이벤트 핸들러
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               //선택한 데이터 가져오기
               Map<String,Object>map=list.get(i);
               //공유 데이터 영역에서 삭제
                //uri 영역에서 _id가 선택한 데이터의 id인 것을 삭제
                getContentResolver().delete(uri,"_id=?",new String[]{map.get("id").toString()});
                //데이터를 다시 출력 하도록
                setAdapter();
                return false;
            }
        });

        //ListView의 셀을 클릭했을 때 이벤트 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //선택한 데이터 찾아오기
                Map<String,Object>map = list.get(i);
                //EditText 출력하기
                nameEdit.setText(map.get("name").toString());
                phoneEdit.setText(map.get("phone").toString());
                _id=map.get("id").toString();
                //수정모드 변경
                isUpdate =true;
            }
        });

        upsert.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String name = nameEdit.getText().toString();
                String phone = phoneEdit.getText().toString();
                if(isUpdate==true){
                    ContentValues values = new ContentValues();
                    values.put("name",name);
                    values.put("phone",phone);
                    getContentResolver().update(uri,values,"_id=?",new String[]{_id});
                    isUpdate =false;
                    setAdapter();
                    nameEdit.setText("");
                    phoneEdit.setText("");
                }else {
                    ContentValues values = new ContentValues();
                    values.put("name",name);
                    values.put("phone",phone);
                    getContentResolver().insert(uri,values);
                    setAdapter();
                    nameEdit.setText("");
                    phoneEdit.setText("");
                }
            }
        });

    }

}
