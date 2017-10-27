package com.example.vikacech.notes.myNotes;

import java.util.Date;

public class Note {
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

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getName() {

        return name;
    }

    public String getContent() {
        return content;
    }

    public boolean isChecked() {
        return checked;
    }
}