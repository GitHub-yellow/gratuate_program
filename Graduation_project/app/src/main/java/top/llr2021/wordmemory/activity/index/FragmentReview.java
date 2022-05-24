package top.llr2021.wordmemory.activity.index;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import top.llr2021.wordmemory.R;
//import top.llr2021.wordmemory.activity.OCRActivity;
//import top.llr2021.wordmemory.activity.load.LoadGameActivity;
//import top.llr2021.wordmemory.activity.review.MatchActivity;
//import top.llr2021.wordmemory.activity.review.SpeedActivity;
import top.llr2021.wordmemory.activity.MatchActivity;
import top.llr2021.wordmemory.activity.SpeedActivity;
import top.llr2021.wordmemory.config.ConfigData;
import top.llr2021.wordmemory.database.Interpretation;
import top.llr2021.wordmemory.database.User;
import top.llr2021.wordmemory.database.Word;
//import top.llr2021.wordmemory.entity.ItemMatch;
//import top.llr2021.wordmemory.listener.CallBackListener;
//import top.llr2021.wordmemory.object.JsonBaidu;
//import top.llr2021.wordmemory.object.JsonBaiduWords;
//import top.llr2021.wordmemory.util.BaiduHelper;
//import top.llr2021.wordmemory.util.Base64Util;
import top.llr2021.wordmemory.entity.ItemMatch;
import top.llr2021.wordmemory.util.FileUtil;
import top.llr2021.wordmemory.util.MyApplication;
import top.llr2021.wordmemory.util.NumberController;
import top.llr2021.wordmemory.util.WordController;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;

public class FragmentReview extends Fragment implements View.OnClickListener {

    private RelativeLayout layoutPhoto, layoutGame;

    private LinearLayout layoutSpeed, layoutMatch;

    private CircleImageView imgHead;

    private static final String TAG = "FragmentReview";

    private ProgressDialog progressDialog;

    private final int WRONG = 2;
    private final int LOAD_DONE = 3;
    private final int LOAD_SPEED = 4;


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case WRONG:
                    progressDialog.dismiss();
                    Toast.makeText(MyApplication.getContext(), "发生了小错误，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case LOAD_DONE:
                    progressDialog.dismiss();
                    Intent intent2 = new Intent();
                    intent2.setClass(MyApplication.getContext(), MatchActivity.class);
                    startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    break;
                case LOAD_SPEED:
                    progressDialog.dismiss();
                    Intent intent3 = new Intent();
                    intent3.setClass(MyApplication.getContext(), SpeedActivity.class);
                    startActivity(intent3, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        // 设置头像
        List<User> userList = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(User.class);
        Glide.with(MyApplication.getContext()).load(userList.get(0).getUserProfile()).into(imgHead);

    }

    private void init() {
        layoutSpeed = getActivity().findViewById(R.id.layout_re_speed);
        layoutSpeed.setOnClickListener(this);
        layoutMatch = getActivity().findViewById(R.id.layout_re_match);
        layoutMatch.setOnClickListener(this);
        imgHead = getActivity().findViewById(R.id.img_review_head);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (v.getId()) {
            case R.id.layout_re_speed:
                showProgressDialog("数据准备中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        prepareData(ConfigData.getSpeedNum());
                        Message message = new Message();
                        message.what = LOAD_SPEED;
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            case R.id.layout_re_match:
                showProgressDialog("数据准备中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        loadMatchData();
                        Message message = new Message();
                        message.what = LOAD_DONE;
                        handler.sendMessage(message);
                    }
                }).start();
                break;
        }

    }

    private void showProgressDialog(String content) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("请稍后");
        progressDialog.setMessage(content);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void loadMatchData() {
        MatchActivity.allMatches.clear();
        if (!MatchActivity.wordList.isEmpty())
            MatchActivity.wordList.clear();
        if (!MatchActivity.matchList.isEmpty())
            MatchActivity.matchList.clear();
        List<Word> words = LitePal.select("wordId", "word").find(Word.class);
        int[] randomId = NumberController.getRandomNumberList(0, words.size() - 1, ConfigData.getMatchNum());
        for (int i = 0; i < randomId.length; ++i) {
            MatchActivity.matchList.add(new ItemMatch(randomId[i], words.get(randomId[i]).getWord(), false, false));
            MatchActivity.allMatches.add(new ItemMatch(words.get(randomId[i]).getWordId(), words.get(randomId[i]).getWord(), false, false));
            Log.d(TAG, "单词：" + words.get(randomId[i]).getWord());
            List<Interpretation> interpretations = LitePal.where("wordId = ?", words.get(randomId[i]).getWordId() + "").find(Interpretation.class);
            Log.d(TAG, "size: " + interpretations.size());
            StringBuilder stringBuilder = new StringBuilder();
            for (int ii = 0; ii < interpretations.size(); ++ii) {
                if (ii != (interpretations.size() - 1))
                    stringBuilder.append(interpretations.get(ii).getWordType() + ". " + interpretations.get(ii).getCHSMeaning() + "\n");
                else
                    stringBuilder.append(interpretations.get(ii).getWordType() + ". " + interpretations.get(ii).getCHSMeaning());
            }
            Log.d(TAG, "意思: " + stringBuilder.toString());
            MatchActivity.matchList.add(new ItemMatch(randomId[i], stringBuilder.toString(), false, false));
        }
        Collections.shuffle(MatchActivity.matchList);

    }

    private void prepareData(int num) {
        if (!SpeedActivity.wordList.isEmpty())
            SpeedActivity.wordList.clear();
        // 准备单词数据
        List<Word> words = LitePal.select("wordId", "word").find(Word.class);
        // 随机匹配单词ID
        int[] randomId = NumberController.getRandomNumberList(0, words.size() - 1, num);
        for (int i = 0; i < num; ++i) {
            // 添加数据
            SpeedActivity.wordList.add(words.get(randomId[i]));
        }
    }

}
