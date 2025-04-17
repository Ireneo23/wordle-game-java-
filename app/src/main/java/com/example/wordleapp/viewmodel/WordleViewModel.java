package com.example.wordleapp.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.wordleapp.model.GameHistoryModel;
import com.example.wordleapp.utils.ApiClient;

import java.util.ArrayList;
import java.util.List;

public class WordleViewModel extends ViewModel {
    private MutableLiveData<String> targetWord = new MutableLiveData<>();
    private List<GameHistoryModel> historyList = new ArrayList<>();

    public LiveData<String> getTargetWord() { return targetWord; }

    public void fetchNewWord(Context context) {
        ApiClient.getRandomWord(context, response -> {
            String word = response.replace("[", "").replace("]", "").replace("\"", "");
            targetWord.setValue(word.toUpperCase());
        }, error -> Log.e("API", "Error: " + error.toString()));
    }

    public void addHistory(GameHistoryModel model) {
        historyList.add(model);
    }

    public List<GameHistoryModel> getHistoryList() {
        return historyList;
    }

}
