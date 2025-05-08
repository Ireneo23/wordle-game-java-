package com.example.wordleapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wordleapp.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<String[]> historyList;
    private final OnDeleteClickListener deleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public HistoryAdapter(List<String[]> historyList, OnDeleteClickListener deleteClickListener) {
        this.historyList = historyList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        String[] historyItem = historyList.get(position);
        holder.status.setText(historyItem[0]);
        holder.date.setText(historyItem[1]);
        holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateHistoryList(List<String[]> newHistoryList) {
        this.historyList = newHistoryList;
        notifyDataSetChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView status, date;
        ImageButton deleteButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            status = itemView.findViewById(R.id.historyStatus);
            date = itemView.findViewById(R.id.historyDate);
            deleteButton = itemView.findViewById(R.id.deleteHistory);
        }
    }
}
