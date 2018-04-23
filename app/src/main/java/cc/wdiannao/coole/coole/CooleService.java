package cc.wdiannao.coole.coole;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yaque on 2018/3/14.
 */

public class CooleService extends Service {
    private static final String TAG = "MyService";
    private boolean ipone = false;
    private boolean isScreenOn = true;

    //new
    //调节音量的相关变量
    VoiceManagerUtil voiceManagerUtil = null;
    int oldEnd = 0;
    private boolean isRecording = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "--------->onBind: ");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "--------->onCreate: ");
    }

    @Override
    public void onStart(final Intent intent, final int startId) {
        super.onStart(intent, startId);
        Log.e(TAG, "--------->onStart: ");
        Utils.getDoNotDisturb(getBaseContext());

        clickScreen();
        getIponeStaut();

        //初始话音量调节
        initVoiceRecord();

        //初始化时间监听
        initTimeMonitor();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        Log.e(TAG, "--------->onStartCommand: ");

//        设置状态栏显示
        useForeground();

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "--------->onDestroy: ");
        super.onDestroy();
    }


    //设置服务前台运行
    public void useForeground() {
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
    /* Method 01
     * this method must SET SMALLICON!
     * otherwise it can't do what we want in Android 4.4 KitKat,
     * it can only show the application info page which contains the 'Force Close' button.*/
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(CooleService.this)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setTicker("ticker")
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("也许当前正在录音")
                .setContentIntent(pendingIntent);
        Notification notification = mNotifyBuilder.build();

    /* Method 02
    Notification notification = new Notification(R.drawable.ic_launcher, tickerText,
            System.currentTimeMillis());
    notification.setLatestEventInfo(PlayService.this, getText(R.string.app_name),
            currSong, pendingIntent);
    */

        startForeground(10, notification);
    }

    //检测屏幕状态，在开锁状态下设置系统静音，在屏幕关闭状态下设置震动货铃声，目前默认铃声
    private void clickScreen() {
        final IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.d(TAG, "onReceive");
                String action = intent.getAction();
                Utils.getAppSetting(CooleService.this);

                if (!Utils.isInTimeControl) {//判断当前时间控制是否有效

                    if (Intent.ACTION_SCREEN_ON.equals(action)) {
                        Log.d(TAG, "screen on");
                        isScreenOn = true;
                        if (!ipone) {
                            setModeVoice(Utils.openStatu);
                        } else {
                            setModeVoice(Utils.closeStatu);
                        }

                    } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        Log.d(TAG, "screen off");
                        isScreenOn = false;
                        setModeVoice(Utils.closeStatu);
                    } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
                        Log.d(TAG, "screen unlock");
                        setModeVoice(Utils.openStatu);
                    } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
                        Log.i(TAG, " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
                    }
                }
            }
        };
        Log.d(TAG, "registerReceiver");
        registerReceiver(mBatInfoReceiver, filter);
    }

    //设置三种模式通过数字：0静音,1震动,2铃声的控制。
    private void setModeVoice(int mode) {
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
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
        showModeVoice();
    }

    //获取手机来电状态
    private void getIponeStaut() {
        //获取系统的TelephonyManager对象
        TelephonyManager tManager=(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个通话状态监听器
        PhoneStateListener pListener=new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String number) {
                // TODO Auto-generated method stub
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE://无任何状态
                        //setModeVoice(openStatu);
                        Log.d("l","l");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://接听来电
                        ipone = false;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://来电
                        Log.d("号码", number + "");
                        ipone = true;
                        break;
                    default:
                        break;
                }
            }
        };
        //为tManager添加监听器
        tManager.listen(pListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    private void showModeVoice() {
        String attude = "";
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
            attude = "铃声";
        }else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {
            attude = "静音";
        }else if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE){
            attude = "震动";
        }
        Log.d("状态", attude);
    }

    private void initTimeMonitor(){
        //开启时间监听
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                TimeMonitor.getTimeSetting(CooleService.this);
            }
        };
        timer.schedule(timerTask, 1000, 1000);
    }

    private void initVoiceRecord() {
        //调节音量的初始
        voiceManagerUtil = VoiceManagerUtil.getInstance(this);
        //录音的线程
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (Utils.autoVoiceChange[5]){
                    startRecord();
                }

            }
        };
        timer.schedule(timerTask,1000,2000);//延时1s，每隔500毫秒执行一次run方法
    }

    //开始录音
    private void startRecord() {
        Log.i(TAG,"开始录音");
        //16K采集率
        int frequency = 16000;
        //格式
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        //16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

        try {
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            short[] buffer = new short[bufferSize];
            audioRecord.startRecording();
            Log.i(TAG, "开始录音");
            isRecording = true;
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                useMode(bufferReadResult, buffer);


                oldEnd = getVoiceFromMic(bufferReadResult, buffer,
                        VoiceManagerUtil.getInstance(getBaseContext()).getMaxMusicVoice());
            }
            audioRecord.stop();
        } catch (Throwable t) {
            Log.e(TAG, "录音失败");
        }
    }

    private int getVoiceFromMic(int bufferReadResult, short[] buffer, int max) {
        long sum = 0;
        long avg = 0;
        int end = 0;
        for (int i = 0; i < bufferReadResult; i++) {
            sum = Math.abs(buffer[i])  + sum;
        }
        avg = sum / bufferReadResult;
        end = (int) (avg / 1000.0 * max);
        Log.d(avg + "",end + "");
        return end;
    }

    private void useMode(int bufferReadResult, short[] buffer) {
        int record = 0;
        int power;
        for (int i = 0; i < Utils.autoVoiceChange.length - 1; i++) {
            if (Utils.autoVoiceChange[i]) {
                record = i;
            }
        }
        switch (record){
            case 0:
                power = getVoiceFromMic(bufferReadResult, buffer,
                        VoiceManagerUtil.getInstance(getBaseContext()).getMaxSystemVoice());
                if (power != 0 && ((Utils.openStatu != 0 && isScreenOn && !Utils.isInTimeControl)
                        || (Utils.closeStatu != 0 && !isScreenOn  && !Utils.isInTimeControl)) || Utils.time_quantum !=0) {
                    voiceManagerUtil.setSystemVoice(power);
                    isRecording = false;

                }else {
                    if ((Utils.openStatu != 0 && isScreenOn  && !Utils.isInTimeControl)
                            || (Utils.closeStatu != 0 && !isScreenOn  && !Utils.isInTimeControl)  || Utils.time_quantum !=0) {
                        voiceManagerUtil.setSystemVoice((int) (VoiceManagerUtil.getInstance(getBaseContext()).getMaxSystemVoice() * 0.5));
                    }
                    isRecording = false;
                }

                break;
            case 1:
                power = getVoiceFromMic(bufferReadResult, buffer,
                        VoiceManagerUtil.getInstance(getBaseContext()).getMaxRingVoice());
                if (power != 0 && ((Utils.openStatu != 0 && isScreenOn  && !Utils.isInTimeControl)
                        || (Utils.closeStatu != 0 && !isScreenOn  && !Utils.isInTimeControl)  || Utils.time_quantum !=0)) {
                    voiceManagerUtil.setRingVoice(power);
                    isRecording = false;
                }else {
                    if ((Utils.openStatu != 0 && isScreenOn  && !Utils.isInTimeControl)
                            || (Utils.closeStatu != 0 && !isScreenOn  && !Utils.isInTimeControl)  || Utils.time_quantum !=0) {
                        voiceManagerUtil.setRingVoice((int) (VoiceManagerUtil.getInstance(getBaseContext()).getMaxRingVoice() * 0.5));
                    }
                    isRecording = false;
                }

                break;
            case 2:
                power = getVoiceFromMic(bufferReadResult, buffer,
                        VoiceManagerUtil.getInstance(getBaseContext()).getMaxMusicVoice());
                if (power != 0 && ((Utils.openStatu != 0 && isScreenOn  && !Utils.isInTimeControl)
                        || (Utils.closeStatu != 0 && !isScreenOn  && !Utils.isInTimeControl)  || Utils.time_quantum !=0)) {
                    voiceManagerUtil.setMusicVoice(power);
                    isRecording = false;

                }else {
                    if ((Utils.openStatu != 0 && isScreenOn  && !Utils.isInTimeControl)
                            || (Utils.closeStatu != 0 && !isScreenOn  && !Utils.isInTimeControl)  || Utils.time_quantum !=0) {
                        voiceManagerUtil.setMusicVoice((int) (VoiceManagerUtil.getInstance(getBaseContext()).getMaxMusicVoice() * 0.5));
                    }
                    isRecording = false;
                }

                break;
            case 3:
                power = getVoiceFromMic(bufferReadResult, buffer,
                        VoiceManagerUtil.getInstance(getBaseContext()).getMaxAlarmVoice());
                if (power != 0) {
                    voiceManagerUtil.setAlarmVoice(power);
                    isRecording = false;

                }else {
                    voiceManagerUtil.setAlarmVoice((int) (VoiceManagerUtil.getInstance(getBaseContext()).getMaxAlarmVoice() * 0.5));
                    isRecording = false;
                }

                break;
            case 4:
                power = getVoiceFromMic(bufferReadResult, buffer,
                        VoiceManagerUtil.getInstance(getBaseContext()).getMaxCallVoice());
                if (power != 0) {
                    voiceManagerUtil.setCallVoice(power);
                    isRecording = false;

                }else {
                    voiceManagerUtil.setCallVoice((int)(VoiceManagerUtil.getInstance(getBaseContext()).getMaxCallVoice() * 0.5));
                    isRecording = false;
                }

                break;
        }
    }
}