package com.example.wordleapp.view;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordleapp.R;
import com.example.wordleapp.adapter.HistoryAdapter;
import com.example.wordleapp.viewmodel.WordleViewModel;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private WordleViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.historyRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewModel = new ViewModelProvider(this).get(WordleViewModel.class);

        adapter = new HistoryAdapter(viewModel.getHistoryList());
        recyclerView.setAdapter(adapter);
    }
}
