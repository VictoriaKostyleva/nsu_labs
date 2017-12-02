package com.example.vikacech.notes;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Note> notes;
    private OnItemClickListener onItemClickListener;

    public RecyclerAdapter(ArrayList<Note> dataset) {//TODO
        notes = dataset;
    }

    public RecyclerAdapter(ArrayList<Note> dataset, OnItemClickListener onItemClickListener) {
        notes = dataset;
        this.onItemClickListener = onItemClickListener;
    }

    // Создает новые views (вызывается layout manager-ом)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        // тут можно программно менять атрибуты лэйаута (size, margins, paddings и др.)

        return new ViewHolder(v);
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.mTextView.setText(notes.get(position).getName());
//        holder.mCheckBox.setChecked(notes.get(position).isChecked());
        holder.bind(notes.get(position), onItemClickListener);
    }

    public void updateNotes(ArrayList<Note> newNotes) {//may delete
        final NoteListCallback diffCallback = new NoteListCallback(this.getNotes(), newNotes);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        notes.clear();
        notes.addAll(newNotes);
        diffResult.dispatchUpdatesTo(this);
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void deleteNote(int id) {//TODO
        notes.remove(id);
    }

    public void addNote(Note e) {
        notes.add(e);
    }


    // Возвращает размер данных (вызывается layout manager-ом)
    @Override
    public int getItemCount() {
        return notes.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Note item);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox mCheckBox;
        private TextView mTextView;
        private View view;

        public ViewHolder(View v) {
            super(v);
            view = v;
            mTextView = (TextView) v.findViewById(R.id.info_text);
//            mCheckBox = (CheckBox) v.findViewById(R.id.check_box);
        }

        public void bind(final Note note, final OnItemClickListener listener) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemClick(note);
                    return true;//
                }
            });
        }
    }

}
