package com.example.ultranotepad.CallBack;

import com.example.ultranotepad.Model.Note;

public interface NoteEventListener {

    void onNoteClick(Note note);

    void onNoteLongClick(Note note);

}
