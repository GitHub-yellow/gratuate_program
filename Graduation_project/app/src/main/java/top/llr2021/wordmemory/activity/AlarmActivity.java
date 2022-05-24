package top.llr2021.wordmemory.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import top.llr2021.wordmemory.R;
import top.llr2021.wordmemory.broadcast.AlarmReceiver;
import top.llr2021.wordmemory.config.ConfigData;
import top.llr2021.wordmemory.util.MyApplication;

import java.util.Calendar;

public class AlarmActivity extends BaseActivity {

    public static final String INTENT_ALARM = "intent_alarm_learn";

    private TimePicker timePicker;

    private Switch aSwitch;

    private static final String TAG = "AlarmActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        init();

        if (ConfigData.getIsAlarm()) {
            aSwitch.setChecked(true);
            timePicker.setEnabled(true);
            int hour = Integer.parseInt(ConfigData.getAlarmTime().split("-")[0]);
            int minute = Integer.parseInt(ConfigData.getAlarmTime().split("-")[1]);
            timePicker.setHour(hour);
            timePicker.setMinute(minute);
        } else {
            aSwitch.setChecked(false);
            timePicker.setEnabled(false);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timePicker.setEnabled(true);
                    ConfigData.setIsAlarm(true);
                } else {
                    timePicker.setEnabled(false);
                    stopAlarm();
                    ConfigData.setIsAlarm(false);
                }
            }
        });


        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                if (aSwitch.isChecked()) {
                    startAlarm(hourOfDay, minute, false, true);
                }
            }
        });

    }

    private void init() {
        aSwitch = findViewById(R.id.switch_alarm_learn);
        timePicker = findViewById(R.id.timePicker_alarm);
    }

    public static void startAlarm(int hour, int minute, boolean isRepeat, boolean isTip) {

        Intent intent = new Intent(MyApplication.getContext(), AlarmReceiver.class);
        intent.setAction(INTENT_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // 精确触发
        AlarmManager.AlarmClockInfo alarmClockInfo;
        if (!isRepeat) {
            ConfigData.setAlarmTime(hour + "-" + minute);
            if (System.currentTimeMillis() < calendar.getTimeInMillis()) {
                alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                if (isTip)
                    Toast.makeText(MyApplication.getContext(), "已设置" + hour + "时" + minute + "分进行学习提醒", Toast.LENGTH_SHORT).show();
            } else {
                alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000, pendingIntent);
                if (isTip)
                    Toast.makeText(MyApplication.getContext(), "已设置明日" + hour + "时" + minute + "分进行学习提醒", Toast.LENGTH_SHORT).show();
            }

        } else {
            alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000, pendingIntent);
        }
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
    }

    public static void stopAlarm() {
        Intent intent = new Intent(MyApplication.getContext(), AlarmReceiver.class);
        intent.setAction(INTENT_ALARM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) MyApplication.getContext().getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(MyApplication.getContext(), "已取消学习提醒", Toast.LENGTH_SHORT).show();
    }

}
