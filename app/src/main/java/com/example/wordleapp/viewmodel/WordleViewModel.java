package com.example.wordleapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class WordleViewModel extends ViewModel {
    private final MutableLiveData<List<String[]>> historyListLiveData = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String[]>> getHistoryListLiveData() {
        return historyListLiveData;
    }

    public List<String[]> getHistoryList() {
        return historyListLiveData.getValue();
    }

    public void addHistoryItem(String status, String datetime) {
        List<String[]> historyList = new ArrayList<>(historyListLiveData.getValue());
        historyList.add(new String[]{status, datetime});
        historyListLiveData.setValue(historyList);
    }

    public void deleteHistoryItem(int position) {
        List<String[]> historyList = new ArrayList<>(historyListLiveData.getValue());
        if (position >= 0 && position < historyList.size()) {
            historyList.remove(position);
            historyListLiveData.setValue(historyList);
        }
    }
}
