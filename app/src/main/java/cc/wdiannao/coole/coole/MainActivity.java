package cc.wdiannao.coole.coole;

import android.Manifest;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    //数据内部共享区的
    private SharedPreferences sp;

    private Button button_b_v;
    private Button btn_about;

    private ListView listView;
    int weizhi = 0;
    boolean select_strat_end = true;
    //定义一个列表集合
    List<Map<String,Object>> listItems;
    Map<String, Object> map;
    //定义一个simpleAdapter,供列表项使用
    SimpleAdapter simpleAdapter;

    private int mHour;
    private int mMinute;
    static final int TIME_DIALOG_ID = 0;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // TODO Auto-generated method stub
            mHour = hourOfDay;
            mMinute = minute;

            if (select_strat_end) {

                map = new HashMap<String, Object>();
                map.put("start", pad(mHour) + " : " + pad(mMinute));
                listItems.add(weizhi, map);

                SharedPreferences.Editor edit = sp.edit();
                edit.putString("" + weizhi, pad(mHour) + " : " + pad(mMinute));
                edit.putInt("listLen", listItems.size() - 1);
                edit.commit();

                simpleAdapter.notifyDataSetChanged();
            }else {
                SharedPreferences.Editor edit = sp.edit();
                String listItem = sp.getString("" + weizhi, "");
                if (listItem.indexOf("p") > 0) {
                    listItem = listItem.substring(0,listItem.indexOf("p"));
                    edit.putString("" + weizhi,  listItem + "p" + pad(mHour) + " : " + pad(mMinute));
                }else {
                    edit.putString("" + weizhi,  listItem + "p" + pad(mHour) + " : " + pad(mMinute));
                }

                edit.commit();
                map = listItems.get(weizhi);
                map.put("end", pad(mHour) + " : " + pad(mMinute));
                simpleAdapter.notifyDataSetChanged();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化
        init();


        listView = findViewById(R.id.listview_time_quantum);

        listItems = new ArrayList<Map<String, Object>>();

        sp = this.getSharedPreferences("config", 0);
        int listLen = sp.getInt("listLen", 0);
        if (listLen > 0) {
            for (int i = 0; i < listLen; i ++) {
                String listItem = sp.getString("" + i, "");

                Log.d("fgdg",listItem);

                if (listItem.indexOf("p") > 0) {
                    String[] timeStr = listItem.split("p");
                    //System.out.println(timeStr[0] + timeStr[1]);
                    map = new HashMap<String, Object>();
                    map.put("start", timeStr[0]);
                    map.put("end", timeStr[1]);
                    listItems.add(map);
                }
            }
        }

        map=new HashMap<String, Object>();
        map.put("start", "+");
        map.put("end", "+");
        listItems.add(map);

        simpleAdapter = new SimpleAdapter(this, listItems, R.layout.listview_mode, new String[]{"start","end"}, new int[]{R.id.start,R.id.end});
        listView.setAdapter(simpleAdapter);

        //listView长按事件
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                Log.d(position + "", id + "");
                //定义AlertDialog.Builder对象，当长按列表项的时候弹出确认删除对话框
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("确定删除?");
                builder.setTitle("提示");

                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(listItems.remove(position)!=null){
                            System.out.println("success");
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putInt("listLen", listItems.size() - 2);
                            edit.commit();
                        }else {
                            System.out.println("failed");
                        }
                        simpleAdapter.notifyDataSetChanged();
                        Toast.makeText(getBaseContext(), "删除列表项", Toast.LENGTH_SHORT).show();
                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long len) {
                weizhi = position;
                Log.d(position + "", len + "");
                if (position == listItems.size() - 1) {
                    select_strat_end = true;
                    showDialog(TIME_DIALOG_ID);

                }else {
                    Toast.makeText(MainActivity.this, "no", Toast.LENGTH_LONG).show();
                    select_strat_end = false;
                    showDialog(TIME_DIALOG_ID);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    private static String pad(int i){
        if (i >= 10)
            return String.valueOf(i);
        else
            return "0" + String.valueOf(i);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
                        false);
        }
        return null;
    }


    //new

    //初始方法
    private void init(){
        //权限的初始化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            permission();
        }

        //获取app初始设置
        Utils.getAppSetting(MainActivity.this);

        //注册按钮监听
        button_b_v = findViewById(R.id.button_b_v);
        button_b_v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingPreferenceActivity.class);
                startActivity(intent);
            }
        });
        btn_about = findViewById(R.id.btn_about);
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });

        //开启服务
        startService();

    }

    //启动服务
    private void startService() {

        Intent intent = new Intent(this,CooleService.class);
        startService(intent);

    }

    //权限申请
    private void permission(){
        requestRunPermisssion(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_PHONE_STATE}, new PermissionListener() {
            @Override
            public void onGranted() {
                //表示所有权限都授权了
                Toast.makeText(MainActivity.this, "所有权限都授权了，可以搞事情了", Toast.LENGTH_SHORT).show();
                //我们可以执行打电话的逻辑

            }

            @Override
            public void onDenied(List<String> deniedPermission) {
                for(String permission : deniedPermission){
                    Toast.makeText(MainActivity.this, "被拒绝的权限：" + permission, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
