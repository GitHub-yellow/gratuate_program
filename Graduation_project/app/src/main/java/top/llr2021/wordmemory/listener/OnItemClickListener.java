package top.llr2021.wordmemory.listener;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import top.llr2021.wordmemory.entity.ItemWordMeanChoice;

public interface OnItemClickListener {

    void onItemClick(RecyclerView parent, View view, int position, ItemWordMeanChoice itemWordMeanChoice);

}
