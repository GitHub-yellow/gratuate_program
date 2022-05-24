package top.llr2021.wordmemory.activity.index;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import top.llr2021.wordmemory.R;
//import top.llr2021.wordmemory.activity.AboutActivity;
//import top.llr2021.wordmemory.activity.AlarmActivity;
//import top.llr2021.wordmemory.activity.CalendarActivity;
//import top.llr2021.wordmemory.activity.ChartActivity;
//import top.llr2021.wordmemory.activity.LearnInNotifyActivity;
import top.llr2021.wordmemory.activity.AboutActivity;
import top.llr2021.wordmemory.activity.AlarmActivity;
import top.llr2021.wordmemory.activity.ChartActivity;
import top.llr2021.wordmemory.activity.LearnInNotifyActivity;
import top.llr2021.wordmemory.activity.ListActivity;
import top.llr2021.wordmemory.activity.CalendarActivity;
import top.llr2021.wordmemory.activity.LoginActivity;
import top.llr2021.wordmemory.activity.MainActivity;
import top.llr2021.wordmemory.activity.PlanActivity;
//import top.llr2021.wordmemory.activity.SynchronyActivity;
import top.llr2021.wordmemory.activity.SynchronyActivity;
import top.llr2021.wordmemory.activity.WelcomeActivity;
import top.llr2021.wordmemory.config.ConfigData;
import top.llr2021.wordmemory.database.MyDate;
import top.llr2021.wordmemory.database.User;
import top.llr2021.wordmemory.util.MyApplication;
import top.llr2021.wordmemory.util.TimeController;

import org.litepal.LitePal;

import java.util.Date;
import java.util.List;

public class FragmentMe extends Fragment implements View.OnClickListener {

    private LinearLayout layoutCalendar, layoutWordList, layoutData, layoutPlan, layoutMoney;

    private RelativeLayout layoutAlarm, layoutNotify, layoutAbout, layoutSyno;

    private TextView textDays, textWordNum, textMoney;

    private TextView textName;

    private CircleImageView imgHead;

    private  TextView textLogout;


    private static final String TAG = "FragmentMe";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        Log.d(TAG, "onActivityCreated: ");

        boolean isNight = ConfigData.getIsNight();

        // 设置头像和用户名信息
        List<User> userList = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(User.class);
        textName.setText(userList.get(0).getUserName());
        Glide.with(MyApplication.getContext()).load(userList.get(0).getUserProfile()).into(imgHead);


        layoutMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] date = TimeController.getStringDate(TimeController.getCurrentDateStamp()).split("-");
                final int currentMoney = Integer.parseInt(textMoney.getText().toString().trim());
                if (currentMoney >= 100) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("提示")
                            .setMessage("确定要花费100铜板进行日历补卡吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                                            new DatePickerDialog.OnDateSetListener() {
                                                @Override
                                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                                    List<MyDate> myDateList = LitePal.where("year = ? and month = ? and date = ?", year + "", (month + 1) + "", dayOfMonth + "").find(MyDate.class);
                                                    if (myDateList.isEmpty()) {
                                                        if (year == Integer.parseInt(date[0]) && month == Integer.parseInt(date[1]) - 1 && dayOfMonth == Integer.parseInt(date[2])) {
                                                            Toast.makeText(MyApplication.getContext(), "不可对今日进行补打卡", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            MyDate myDate = new MyDate();
                                                            myDate.setDate(dayOfMonth);
                                                            myDate.setUserId(ConfigData.getQQNumLogged());
                                                            myDate.setYear(year);
                                                            myDate.setMonth(month + 1);
                                                            myDate.save();
                                                            User user = new User();
                                                            if (currentMoney - 100 > 0)
                                                                user.setUserMoney(currentMoney - 100);
                                                            else
                                                                user.setToDefault("userMoney");
                                                            user.updateAll("userId = ?", ConfigData.getQQNumLogged() + "");
                                                            updateData();
                                                            Toast.makeText(MyApplication.getContext(), "补卡成功！", Toast.LENGTH_SHORT).show();
                                                        }
                                                    } else {
                                                        Toast.makeText(MyApplication.getContext(), "已在该日进行打卡，不可重复", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            },
                                            Integer.parseInt(date[0]),
                                            Integer.parseInt(date[1]) - 1,
                                            Integer.parseInt(date[2]));
                                    DatePicker datePicker = datePickerDialog.getDatePicker();
                                    datePicker.setMaxDate(new Date().getTime());
                                    datePickerDialog.show();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                } else {
                    Toast.makeText(MyApplication.getContext(), "抱歉，你的铜板个数还不足要求。必须满足100个铜板才可以补打卡哦", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void init() {

        imgHead = getActivity().findViewById(R.id.img_me_head);
        layoutCalendar = getActivity().findViewById(R.id.layout_me_calendar);
        layoutCalendar.setOnClickListener(this);
        layoutWordList = getActivity().findViewById(R.id.layout_me_word_list);
        layoutWordList.setOnClickListener(this);
        textDays = getActivity().findViewById(R.id.text_me_days);
        textWordNum = getActivity().findViewById(R.id.text_me_words);
        textMoney = getActivity().findViewById(R.id.text_me_money);
        layoutData = getActivity().findViewById(R.id.layout_me_analyse);
        layoutData.setOnClickListener(this);
        layoutPlan = getActivity().findViewById(R.id.layout_me_plan);
        layoutPlan.setOnClickListener(this);
        layoutAlarm = getActivity().findViewById(R.id.layout_me_alarm);
        layoutAlarm.setOnClickListener(this);
        layoutNotify = getActivity().findViewById(R.id.layout_me_notify);
        layoutNotify.setOnClickListener(this);
        layoutMoney = getActivity().findViewById(R.id.layout_me_money);
        layoutAbout = getActivity().findViewById(R.id.layout_me_about);
        layoutAbout.setOnClickListener(this);
        layoutSyno = getActivity().findViewById(R.id.layout_me_syno);
        layoutSyno.setOnClickListener(this);
        textName = getActivity().findViewById(R.id.text_me_name);
        textLogout = getActivity().findViewById(R.id.text_logout);
        textLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final AlertDialog dialog = builder.create();
                View dialogView = View.inflate(getContext(), R.layout.dialog_logout, null);
                dialog.setView(dialogView);
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.drawable.drawable_round_edge);
                dialog.findViewById(R.id.text_logout_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigData.setIsLogged(false);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        Log.i("testtt","跳转后执行");
                    }
                });
                dialog.findViewById(R.id.text_logout_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.layout_me_calendar:
                intent.setClass(getActivity(), CalendarActivity.class);
                break;
            case R.id.layout_me_word_list:
                intent.setClass(getActivity(), ListActivity.class);
                break;
            case R.id.layout_me_analyse:
                intent.setClass(getActivity(), ChartActivity.class);
                break;
            case R.id.layout_me_plan:
                intent.setClass(getActivity(), PlanActivity.class);
                break;
            case R.id.layout_me_alarm:
                intent.setClass(getActivity(), AlarmActivity.class);
                break;
            case R.id.layout_me_notify:
                intent.setClass(getActivity(), LearnInNotifyActivity.class);
                break;
            case R.id.layout_me_about:
                intent.setClass(getActivity(), AboutActivity.class);
                break;
            case R.id.layout_me_syno:
                intent.setClass(getActivity(), SynchronyActivity.class);
                break;
        }
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateData();
    }

    private void updateData() {
        // 设置天数
        List<MyDate> myDateList = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(MyDate.class);
        textDays.setText(myDateList.size() + "");
        // 设置单词数与金币数
        List<User> userList = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(User.class);
        textWordNum.setText(userList.get(0).getUserWordNumber() + "");
        textMoney.setText(userList.get(0).getUserMoney() + "");
    }

}
