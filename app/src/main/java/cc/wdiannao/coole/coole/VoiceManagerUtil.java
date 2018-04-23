package cc.wdiannao.coole.coole;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by yaque on 2018/3/23.
 */

public class VoiceManagerUtil {
    private int maxVoice[] = new int[5];//0系统音量 1是铃声音量 2是音乐音量 3是提示声音量 4电话音量
    private int currentVoice[] = new int[5];
    private AudioManager audioManager;
    private static VoiceManagerUtil mInstance;
    private int flag = 0;

    private VoiceManagerUtil(Context context){
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public synchronized static VoiceManagerUtil getInstance(Context context){
        if(mInstance == null){
            mInstance = new VoiceManagerUtil(context);
        }
        return mInstance;
    }

    public int[] getCurrentVoice() {
        currentVoice[0] = getCurrentSystemVoice();
        currentVoice[1] = getCurrentRingVoice();
        currentVoice[2] = getCurrentMusicVoice();
        currentVoice[3] = getCurrentAlarmVoice();
        currentVoice[4] = getCurrentCallVoice();

        return maxVoice;
    }

    public int[] getMaxVoice() {
        maxVoice[0] = getMaxSystemVoice();
        maxVoice[1] = getMaxRingVoice();
        maxVoice[2] = getMaxMusicVoice();
        maxVoice[3] = getMaxAlarmVoice();
        maxVoice[4] = getMaxCallVoice();

        return maxVoice;
    }

    public void setAllVoice(int currentVoice[]) {
        setSystemVoice(currentVoice[0]);
        setRingVoice(currentVoice[1]);
        setMusicVoice(currentVoice[2]);
        setAlarmVoice(currentVoice[3]);
        setCallVoice(currentVoice[4]);
    }


    public void setSystemVoice(int voice) {
        //系统音量
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, voice, flag);
    }

    public void setRingVoice(int voice) {
        //铃声音量

        audioManager.setStreamVolume(AudioManager.STREAM_RING, voice, flag);
    }

    public void setMusicVoice(int voice) {
        //音乐音量

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, voice, flag);
    }

    public void setAlarmVoice(int voice) {
        //提示音量
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, voice, flag);
    }

    public void setCallVoice(int voice) {
        //电话音量

        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, voice, flag);
    }

    public int getMaxSystemVoice() {
        int maxV;
        //系统音量
        maxV = audioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM );
        return maxV;
    }

    public int getMaxRingVoice() {
        int maxV;
        //铃声音量
        maxV = audioManager.getStreamMaxVolume( AudioManager.STREAM_RING );
        return maxV;
    }

    public int getMaxMusicVoice() {
        int maxV;
        //音乐音量
        maxV = audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
        return maxV;
    }

    public int getMaxAlarmVoice() {
        int maxV;
        //提示音量
        maxV = audioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM );
        return maxV;
    }

    public int getMaxCallVoice() {
        int maxV;
        //电话音量
        maxV = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
        return maxV;
    }

    public int getCurrentSystemVoice() {
        int maxV;
        //系统音量
        maxV = audioManager.getStreamVolume( AudioManager.STREAM_SYSTEM );
        return maxV;
    }

    public int getCurrentRingVoice() {
        int maxV;
        //铃声音量
        maxV = audioManager.getStreamVolume(AudioManager.STREAM_RING );
        return maxV;
    }

    public int getCurrentMusicVoice() {
        int maxV;
        //音乐音量
        maxV = audioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
        return maxV;
    }

    public int getCurrentAlarmVoice() {
        int maxV;
        //提示音量
        maxV = audioManager.getStreamVolume( AudioManager.STREAM_ALARM );
        return maxV;
    }

    public int getCurrentCallVoice() {
        int maxV;
        //电话音量
        maxV = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
        return maxV;
    }
}
