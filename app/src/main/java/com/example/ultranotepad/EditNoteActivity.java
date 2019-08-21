package com.example.ultranotepad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.ultranotepad.CallBack.NoteEventListener;
import com.example.ultranotepad.Model.Note;
import com.example.ultranotepad.db.NoteDB;
import com.example.ultranotepad.db.NotesDao;

import java.util.Date;

public class EditNoteActivity extends AppCompatActivity {

    private EditText noteEditText;
    private NotesDao dao;
    private Note temp;
    public static final String NOTE_EXTRA_KEY = "note_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        noteEditText = (EditText) findViewById(R.id.input_note);
        dao = NoteDB.getInstance(this).notesDao();

        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_KEY, 0);
            temp = dao.getNoteById(id);
            noteEditText.setText(temp.getNoteText());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_new_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_note_save) {
            onSaveNote();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSaveNote() {
        String text = noteEditText.getText().toString().trim();

        if (!text.isEmpty()) {
            long date = new Date().getTime();
            if (temp==null){
                temp = new Note(text,date);
                dao.insertNote(temp);
            }
            else {
                temp.setNoteText(text);
                temp.setNoteDate(date);
                dao.updateNote(temp);
            }
            finish();
        }
    }
}
