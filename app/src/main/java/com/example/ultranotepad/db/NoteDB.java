package com.example.ultranotepad.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.ultranotepad.Model.Note;

@Database(entities = Note.class,version = 1)
public abstract class NoteDB extends RoomDatabase {
    public abstract NotesDao notesDao();

    public static final String DATABASE_NAME = "noteDb";
    private static NoteDB instance;

    public static NoteDB getInstance(Context context){
        if (instance == null)
            instance = Room.databaseBuilder(context , NoteDB.class,DATABASE_NAME).allowMainThreadQueries().build();
        return instance;
    }
}