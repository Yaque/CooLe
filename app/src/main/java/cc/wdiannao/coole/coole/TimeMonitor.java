package cc.wdiannao.coole.coole;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.AUDIO_SERVICE;

/**
 * Created by yaque on 2018/4/22.
 */

public class TimeMonitor {

    private static AudioManager audioManager;


    public static void getTimeSetting(Context context){
        //do something
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);

        Map<String, Object> map;
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        Log.d("ddd", "music  h h h h h h h");
        SharedPreferences sp = context.getSharedPreferences("config", 0);
        int listLen = sp.getInt("listLen", 0);
        if (listLen > 0) {
            for (int i = 0; i < listLen; i++) {
                String listItem = sp.getString("" + i, "");
                if (listItem.indexOf("p") > 0) {
                    String[] timeStr = listItem.split("p");
                    map = new HashMap<String, Object>();
                    map.put("start", timeStr[0]);
                    map.put("end", timeStr[1]);
                    listItems.add(map);
                }
            }
        }
        if (!listItems.isEmpty()){
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            //Log.d(hour + "", minute + "");
            for (int i = 0; i < listItems.size(); i++) {
                map = listItems.get(i);
                String start = (String) map.get("start");
                //Log.d("start       test", start);
                String[] startStrValue = start.split(":");
                int[] startIntValue = {0, 0};
                for (int j = 0; j < 2; j++) {
                    startStrValue[j] = startStrValue[j].replace(" ", "");
                    if (startStrValue[j].charAt(0) == '0') {
                        startIntValue[j] = new Integer(startStrValue[j].charAt(1) + "");
                    }else {
                        startIntValue[j] = new Integer(startStrValue[j]);
                    }
                }

                String end = (String) map.get("end");
                //Log.d("end       test",end);
                String[] endStrValue = end.split(":");
                int[] endIntValue = {0, 0};
                for (int j = 0; j < 2; j++) {
                    endStrValue[j] = endStrValue[j].replace(" ", "");
                    //Log.d("end       test",endStrValue[j]);
                    //Log.d("end       test",endStrValue[j].charAt(1) + "");
                    if (endStrValue[j].charAt(0) == '0') {
                        endIntValue[j] = new Integer(endStrValue[j].charAt(1) + "");
                    }else {
                        endIntValue[j] = new Integer(endStrValue[j]);
                    }
                }
                Log.d("时间是不等人的" + hour + "   " + minute + "前", startIntValue[0] + " " + startIntValue[1] + " " + endIntValue[0]
                        + " " + endIntValue[1]);
                if ((hour == startIntValue[0] && minute > startIntValue[1] && minute < endIntValue[1])
                        || (hour > startIntValue[0] && hour < endIntValue[0])
                        || (hour == endIntValue[0] && minute < endIntValue[1])) {
                    Log.d("oooo    ", "ppppppp");

                    Utils.isInTimeControl = true;
                    if (!isOK()) {
                        setModeVoice(Utils.time_quantum);
                        //Log.d("静音", "jingying");
                    }
                }
            }
        }

    }

    private static void setModeVoice(int mode) {
        switch (mode) {
            case 0:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case 1:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case 2:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
        }
    }

    private static boolean isOK() {
        switch (Utils.time_quantum) {
            case 0:
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    return true;
                }else {
                    return false;
                }
            case 1:
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                    return true;
                }else {
                    return false;
                }
            case 2:
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
                    return true;
                }else {
                    return false;
                }
        }
        return false;
    }
}
