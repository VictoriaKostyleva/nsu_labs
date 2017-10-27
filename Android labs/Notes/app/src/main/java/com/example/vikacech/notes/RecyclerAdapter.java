package com.example.vikacech.notes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.vikacech.notes.myNotes.Note;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

//    private String[] mDataset;

    private ArrayList<Note> notes;
    private OnItemClickListener onItemClickListener;

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

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Заменяет контент отдельного view (вызывается layout manager-ом)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mTextView.setText(notes.get(position).getName());
        holder.mCheckBox.setChecked(notes.get(position).isChecked());

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

        public CheckBox mCheckBox;
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.info_text);
            mCheckBox = (CheckBox) v.findViewById(R.id.check_box);
        }
    }

}
