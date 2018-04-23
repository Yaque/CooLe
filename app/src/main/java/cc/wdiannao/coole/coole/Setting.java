package cc.wdiannao.coole.coole;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by yaque on 2018/3/28.
 */

public class Setting {
    private boolean [] autoVoiceChange = {false,false,true,false,false,false};
    //系统、铃声、媒体、提示、通话、是否启动改自动调节功能
    private boolean [] openScreenVoiceChange = {true,false,false};
    private boolean [] closeScreenVoiceChange = {false,true,false};
    //静音、震动、铃声

    private boolean [] timeVoiceChange = {true,false,false};


    public boolean[] getAutoVoiceChange() {
        return autoVoiceChange;
    }

    public boolean[] getCloseScreenVoiceChange() {
        return closeScreenVoiceChange;
    }

    public boolean[] getOpenScreenVoiceChange() {
        return openScreenVoiceChange;
    }



    public boolean[] getTimeVoiceChange() {
        return timeVoiceChange;
    }

    public void setAutoVoiceChange(boolean[] autoVoiceChange) {
        this.autoVoiceChange = autoVoiceChange;
    }

    public void setCloseScreenVoiceChange(boolean[] closeScreenVoiceChange) {
        this.closeScreenVoiceChange = closeScreenVoiceChange;
    }

    public void setOpenScreenVoiceChange(boolean[] openScreenVoiceChange) {
        this.openScreenVoiceChange = openScreenVoiceChange;
    }

    public void setTimeVoiceChange(boolean[] timeVoiceChange) {
        this.timeVoiceChange = timeVoiceChange;
    }

    //get setting
    private void getSetting(Context context) {
        //set the default values we defined in the XML
        PreferenceManager.setDefaultValues(context, R.xml.base_setting_preference, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);


        //get the values of the settings options
        //自动音量设置
        autoVoiceChange[0] = preferences.getBoolean("system_voice", false);
        autoVoiceChange[1] = preferences.getBoolean("ring_voice", false);
        autoVoiceChange[2] = preferences.getBoolean("music_voice", false);
        autoVoiceChange[3] = preferences.getBoolean("alarm_voice", false);
        autoVoiceChange[4] = preferences.getBoolean("call_voice", false);
        autoVoiceChange[5] = preferences.getBoolean("is_start_auto_change", false);

        //开屏调节
        openScreenVoiceChange[0] = preferences.getBoolean("open_silent", false);
        openScreenVoiceChange[1] = preferences.getBoolean("open_vibrate", false);
        openScreenVoiceChange[2] = preferences.getBoolean("open_normal", false);

        //关屏调节
        closeScreenVoiceChange[0] = preferences.getBoolean("close_silent", false);
        closeScreenVoiceChange[0] = preferences.getBoolean("close_vibrate", false);
        closeScreenVoiceChange[0] = preferences.getBoolean("close_normal", false);

        String string = preferences.getString("custom_storage", "");

//        if (open_silent) {
//            openStatu = 0;
//        }else if (open_vibrate) {
//            openStatu = 1;
//        }else if (open_normal) {
//            openStatu = 2;
//        }
//        if (close_silent) {
//            closeStatu = 0;
//        }else if (close_vibrate) {
//            closeStatu = 1;
//        }else if (close_normal) {
//            closeStatu = 2;
//        }

        //Toast.makeText(CooleService.this, string, Toast.LENGTH_LONG).show();
    }
}
