package com.example.wordleapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<String> wordOfTheDay = new MutableLiveData<>();

    public LiveData<String> getWordOfTheDay() {
        return wordOfTheDay;
    }

    public void fetchWordOfTheDay() {
        // Here you would call the repository to fetch the word of the day
        // For now, let's just set a dummy value
        wordOfTheDay.setValue("WORDS");
    }
}