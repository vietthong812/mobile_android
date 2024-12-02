package tdtu.EStudy_App.adapters;

import tdtu.EStudy_App.models.Word;

public interface OnWordMarkedListener {
    void onWordMarked(Word word, boolean isMarked);
}