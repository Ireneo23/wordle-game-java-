package com.example.wordleapp;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.wordleapp.databinding.ActivityMainBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        viewModel.fetchWordOfTheDay();

        viewModel.getWordOfTheDay().observe(this, word -> {
            // Update UI with the word of the day
            // For example, you can set it to a TextView
            binding.titleProject.setText(word);
        });

        // Example of API call
        WordRepository repository = new WordRepository();
        repository.fetchWordOfTheDay(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String word = response.body().string();
                    // Update UI with the word of the day
                    runOnUiThread(() -> binding.titleProject.setText(word));
                }
            }
        });
    }
}