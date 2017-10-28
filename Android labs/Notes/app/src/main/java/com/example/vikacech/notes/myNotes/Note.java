package com.example.vikacech.notes.myNotes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Note implements Parcelable {
    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
    private String name;
    private String content;
    private boolean checked;
    private Date date;

    public Note(String name, String context, boolean checked, Date date) {
        this.name = name;
        this.content = context;
        this.checked = checked;
        this.date = date;
    }

    protected Note(Parcel in) {
        name = in.readString();
        content = in.readString();
        checked = in.readByte() != 0;
        date = new Date(in.readLong());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(content);
        parcel.writeByte((byte) (checked ? 1 : 0));
        parcel.writeLong(date.getTime());
    }
}