package com.example.wordleapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordleapp.R;
import com.example.wordleapp.model.GameHistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<GameHistoryModel> historyList;

    public HistoryAdapter(List<GameHistoryModel> historyList) {
        this.historyList = historyList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, status, attempts;
        ImageButton deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.historyDate);
            status = itemView.findViewById(R.id.historyStatus);
            deleteBtn = itemView.findViewById(R.id.deleteHistory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GameHistoryModel model = historyList.get(position);
        holder.date.setText(model.getDateTime());
        holder.status.setText(model.getStatus());
        holder.attempts.setText("Attempts: " + model.getAttempts());

        holder.deleteBtn.setOnClickListener(v -> {
            historyList.remove(position);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() { return historyList.size(); }
}
