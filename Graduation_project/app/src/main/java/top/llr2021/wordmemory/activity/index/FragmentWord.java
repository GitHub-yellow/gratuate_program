package top.llr2021.wordmemory.activity.index;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import top.llr2021.wordmemory.R;
import top.llr2021.wordmemory.activity.BaseActivity;
import top.llr2021.wordmemory.activity.MainActivity;
import top.llr2021.wordmemory.activity.SearchActivity;
import top.llr2021.wordmemory.activity.WordDetailActivity;
import top.llr2021.wordmemory.activity.WordFolderActivity;
import top.llr2021.wordmemory.activity.load.LoadWordActivity;
import top.llr2021.wordmemory.config.ConfigData;
import top.llr2021.wordmemory.config.ConstantData;
import top.llr2021.wordmemory.database.Interpretation;
import top.llr2021.wordmemory.database.MyDate;
import top.llr2021.wordmemory.database.UserConfig;
import top.llr2021.wordmemory.database.Word;
import top.llr2021.wordmemory.util.NumberController;
import top.llr2021.wordmemory.util.WordController;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class FragmentWord extends Fragment implements View.OnClickListener {

    private CardView cardStart, cardSearch;
    private ImageView imgRefresh, imgSearch,wordBook;
    private View  tranSearchView;
    private TextView textStart;
    private RelativeLayout layoutFiles;

    private TextView textWord, textMean,wordName;

    private TextView progressBarPer,wordNum,learnDaily,reviewDaily;

    private ProgressBar progressBar;

    private static final String TAG = "FragmentWord";

    private int currentBookId;

    private boolean isOnClick = true;

    private int currentRandomId;

    public static int prepareData = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        Log.d(TAG, "onActivityCreated: ");

//zhu       if (MainActivity.needRefresh) {
//            prepareData = 0;
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    BaseActivity.prepareDailyData();
//                }
//            }).start();
//        }

    }

    // 初始化控件
    private void init() {
        imgRefresh = getActivity().findViewById(R.id.img_refresh);
        imgRefresh.setOnClickListener(this);
        cardStart = getActivity().findViewById(R.id.card_index_start);
        cardStart.setOnClickListener(this);
        textMean = getActivity().findViewById(R.id.text_main_show_word_mean);
        textMean.setOnClickListener(this);
        textWord = getActivity().findViewById(R.id.text_main_show_word);
        textStart = getActivity().findViewById(R.id.text_main_start);
        textStart.setOnClickListener(this);
        layoutFiles = getActivity().findViewById(R.id.layout_main_words);
        layoutFiles.setOnClickListener(this);
        cardSearch = getActivity().findViewById(R.id.card_main_search);
        cardSearch.setOnClickListener(this);

        imgSearch = getActivity().findViewById(R.id.img_review_search);
        tranSearchView = getActivity().findViewById(R.id.view_search_tran);

        wordBook = getActivity().findViewById(R.id.img_word_book);
        wordName = getActivity().findViewById(R.id.text_word_name);
        progressBarPer = getActivity().findViewById(R.id.text_progressBarPer_Show);
        wordNum = getActivity().findViewById(R.id.text_wordNum_show);
        progressBar = (ProgressBar)getActivity().findViewById(R.id.progressBar);
        learnDaily = getActivity().findViewById(R.id.text_learn_daily);
        reviewDaily = getActivity().findViewById(R.id.text_review_daily);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_refresh:
                // 旋转动画
                RotateAnimation animation = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(700);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setRandomWord();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imgRefresh.startAnimation(animation);
                break;
            case R.id.text_main_start:
                if (isOnClick) {
                    Intent mIntent = new Intent(getActivity(), LoadWordActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    isOnClick = false;
                }
                break;
            case R.id.text_main_show_word_mean:
                WordDetailActivity.wordId = currentRandomId;
                Intent intent = new Intent(getActivity(), WordDetailActivity.class);
                intent.putExtra(WordDetailActivity.TYPE_NAME, WordDetailActivity.TYPE_GENERAL);
                startActivity(intent);
                break;
            case R.id.card_main_search:
                Intent intent2 = new Intent(getActivity(), SearchActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat activityOptionsCompat2 = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        tranSearchView, "imgSearch");
                startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
            case R.id.layout_main_words:
                Intent intent3 = new Intent(getActivity(), WordFolderActivity.class);
                startActivity(intent3, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void setRandomWord() {
        ++prepareData;
        Log.d(TAG, "setRandomWord: " + ConstantData.wordTotalNumberById(currentBookId));
        int randomId = NumberController.getRandomNumber(1, ConstantData.wordTotalNumberById(currentBookId));
        Log.d(TAG, "当前ID" + randomId);
        currentRandomId = randomId;
        Log.d(TAG, "要传入的ID" + currentRandomId);
        Log.d(TAG, randomId + "");
        Word word = LitePal.where("wordId = ?", randomId + "").select("wordId", "word").find(Word.class).get(0);
        Log.d(TAG, word.getWord());
        List<Interpretation> interpretations = LitePal.where("wordId = ?", word.getWordId() + "").find(Interpretation.class);
        textWord.setText(word.getWord());
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < interpretations.size(); ++i) {
            stringBuilder.append(interpretations.get(i).getWordType() + ". " + interpretations.get(i).getCHSMeaning());
            if (i != interpretations.size() - 1)
                stringBuilder.append("\n");
        }
        textMean.setText(stringBuilder.toString());
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Calendar calendar = Calendar.getInstance();
        List<Word> words = LitePal.where("deepMasterTimes <> ?", 3 + "").select("wordId").find(Word.class);
        List<MyDate> myDates = LitePal.where("year = ? and month = ? and date = ? and userId = ?",
                calendar.get(Calendar.YEAR) + "",
                (calendar.get(Calendar.MONTH) + 1) + "",
                calendar.get(Calendar.DATE) + "",
                ConfigData.getQQNumLogged() + "").find(MyDate.class);
        if (!words.isEmpty()) {
            if (myDates.isEmpty()) {
                // 未完成计划
                cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorLightBlue));
                textStart.setTextColor(getActivity().getColor(R.color.colorFontInBlue));
                textStart.setText("开始背单词");
                isOnClick = true;
            } else {
                // 完成计划
                if ((myDates.get(0).getWordLearnNumber() + myDates.get(0).getWordReviewNumber()) > 0) {
                    cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorBgWhite));
                    textStart.setTextColor(getActivity().getColor(R.color.colorFontInWhite));
                    textStart.setText("已完成今日任务");
                    cardStart.setClickable(false);
                    isOnClick = false;
                } else {
                    // 未完成计划
                    cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorLightBlue));
                    textStart.setTextColor(getActivity().getColor(R.color.colorFontInBlue));
                    textStart.setText("开始背单词");
                    isOnClick = true;
                }
            }
        } else {
            cardStart.setCardBackgroundColor(getActivity().getColor(R.color.colorBgWhite));
            textStart.setTextColor(getActivity().getColor(R.color.colorFontInWhite));
            textStart.setText("恭喜！已背完此书");
            cardStart.setClickable(false);
            isOnClick = false;
        }
        // 设置界面数据
        List<UserConfig> userConfigs = LitePal.where("userId = ?", ConfigData.getQQNumLogged() + "").find(UserConfig.class);
        currentBookId = userConfigs.get(0).getCurrentBookId();

        if (prepareData == 0)
            // 设置随机数据
            setRandomWord();

        int bookId = userConfigs.get(0).getCurrentBookId();
        Glide.with(this).load(ConstantData.bookPicById(bookId)).into(wordBook);
        wordName.setText(ConstantData.bookNameById(bookId));
        List<Word> isLearned = LitePal.where("isLearned = ?", "1").select("wordId,word").find(Word.class);
        int isLearnedNum = isLearned.size();
        final int wordSum = ConstantData.wordTotalNumberById(bookId);
        String isLearnedNumPer = new BigDecimal((float)(100*isLearnedNum)/wordSum).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        progressBarPer.setText("已完成"+isLearnedNumPer+"%");
        wordNum.setText(isLearnedNum+"/"+wordSum+"词");

        progressBar.setProgress((100*isLearnedNum)/wordSum);

        WordController.generateDailyLearnWords(userConfigs.get(0).getLastStartTime());
        WordController.generateDailyReviewWords();
        learnDaily.setText(""+ WordController.needLearnWords.size());
        reviewDaily.setText(""+ (WordController.needReviewWords.size() + WordController.justLearnedWords.size()));


    }
}
