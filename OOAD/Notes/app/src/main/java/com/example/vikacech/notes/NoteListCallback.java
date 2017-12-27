package com.example.vikacech.notes;

import android.support.v7.util.DiffUtil;


import com.example.vikacech.notes.myNotes.Note;

import java.util.List;

public class NoteListCallback extends DiffUtil.Callback {
    private final List<Note> oldList;
    private final List<Note> newList;

    public NoteListCallback(List<Note> oldList, List<Note> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}