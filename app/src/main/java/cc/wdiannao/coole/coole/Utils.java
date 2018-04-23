package cc.wdiannao.coole.coole;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by yaque on 2018/3/23.
 */

public class Utils {

    public static boolean isInTimeControl =false;

    //APP设置读取
    public static int openStatu = 0;
    public static int closeStatu = 0;
    public static int time_quantum = 0;
    public static boolean [] autoVoiceChange = {false,false,true,false,false,false};//系统、铃声、媒体、提示、通话、是否启动改自动调节功能

    //权限注册对于5.0以后版本需要权限申请
    public static void getDoNotDisturb(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !notificationManager.isNotificationPolicyAccessGranted()) {
            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            context.startActivity(intent);
        }
    }

    //get App setting
    public static void getAppSetting(Context context) {
        //set the default values we defined in the XML
        PreferenceManager.setDefaultValues(context, R.xml.base_setting_preference, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        //get the values of the settings options
        autoVoiceChange[0] = preferences.getBoolean("system_voice", false);
        autoVoiceChange[1] = preferences.getBoolean("ring_voice", false);
        autoVoiceChange[2] = preferences.getBoolean("music_voice", false);
        autoVoiceChange[3] = preferences.getBoolean("alarm_voice", false);
        autoVoiceChange[4] = preferences.getBoolean("call_voice", false);
        autoVoiceChange[5] = preferences.getBoolean("is_start_auto_change", false);

        boolean open_silent = preferences.getBoolean("open_silent", false);
        boolean open_vibrate = preferences.getBoolean("open_vibrate", false);
        boolean open_normal = preferences.getBoolean("open_normal", false);

        boolean close_silent = preferences.getBoolean("close_silent", false);
        boolean close_vibrate = preferences.getBoolean("close_vibrate", false);
        boolean close_normal = preferences.getBoolean("close_normal", false);

        boolean time_quantum_silent = preferences.getBoolean("time_silent", false);
        boolean time_quantum_vibrate = preferences.getBoolean("time_vibrate", false);
        boolean time_quantum_normal = preferences.getBoolean("time_normal", false);

        String string = preferences.getString("custom_storage", "");

        if (open_silent) {
            openStatu = 0;
        }else if (open_vibrate) {
            openStatu = 1;
        }else if (open_normal) {
            openStatu = 2;
        }
        if (close_silent) {
            closeStatu = 0;
        }else if (close_vibrate) {
            closeStatu = 1;
        }else if (close_normal) {
            closeStatu = 2;
        }
        if (time_quantum_normal) {
            time_quantum = 2;
        }else if (time_quantum_vibrate) {
            time_quantum = 1;
        }else if (time_quantum_silent) {
            time_quantum = 0;
        }

        //Toast.makeText(CooleService.this, string, Toast.LENGTH_LONG).show();
    }
}
