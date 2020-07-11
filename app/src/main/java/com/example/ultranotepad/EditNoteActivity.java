package com.example.ultranotepad;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ultranotepad.CallBack.NoteEventListener;
import com.example.ultranotepad.Model.Note;
import com.example.ultranotepad.db.NoteDB;
import com.example.ultranotepad.db.NotesDao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class EditNoteActivity extends AppCompatActivity {

    private EditText noteEditText;
    private NotesDao dao;
    private Note temp;
    public static final String NOTE_EXTRA_KEY = "note_id";
    SharedPreferences sharedPreferences = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = getSharedPreferences("night",0);
        Boolean booleanValue = sharedPreferences.getBoolean("night_mode",true);
        if (booleanValue){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        setTitle("Edit Note");



        noteEditText = findViewById(R.id.input_note);
        dao = NoteDB.getInstance(this).notesDao();

        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(NOTE_EXTRA_KEY, 0);
            temp = dao.getNoteById(id);
            noteEditText.setText(temp.getNoteText());
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditNoteActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_new_save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== R.id.save_note){
            onSaveNote();
        }
        else if (id == R.id.edit_save_internal){
            String text = noteEditText.getText().toString().trim();

            if (!text.equals("")){
                saveAsTextFile(text,text);
            }
            else {
                Toast.makeText(this, "Write something to save", Toast.LENGTH_SHORT).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void onSaveNote() {
        String text = noteEditText.getText().toString().trim();

        if (!text.isEmpty()) {
            long date = new Date().getTime();
            if (temp == null) {
                temp = new Note(text, date);
                dao.insertNote(temp);
            } else {
                temp.setNoteText(text);
                temp.setNoteDate(date);
                dao.updateNote(temp);
            }
            onBackPressed();
        } else {
            onBackPressed();
        }
    }

    private void saveAsTextFile(String filename, String content) {
        String fileName = filename + ".txt";

        //create file
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), fileName);

        //write file
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(content.getBytes());
            fos.close();
            Toast.makeText(this, "Saved to External Storage", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving", Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            startActivity(new Intent(EditNoteActivity.this,MainActivity.class));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
