
private void resetGame() {
    // Reset game state
    currentAttempt = 0;
    currentLetterIndex = 0;
    currentGuess.setLength(0);
    txtWinLose.setVisibility(View.GONE);
    nextGameButton.setVisibility(View.GONE);

    // Clear grid and keyboard
    for (List<TextView> row : guessTextViews) {
        for (TextView textView : row) {
            textView.setText("");
            textView.setBackgroundColor(Color.rgb(99, 99, 99));
            textView.setTextColor(Color.WHITE);
            textView.setOnClickListener(v -> handleCellClick(textView));
        }
    }
    for (Button btn : keyboardButtons) {
        btn.setBackgroundColor(Color.parseColor("#636363"));
        btn.setOnClickListener(view -> handleKeyPress(btn.getText().toString().charAt(0)));
        // 添加圆角背景
        btn.setBackgroundResource(R.drawable.button_rounded);
    }

    // Fetch a new word
    fetchRandomWord();
}
