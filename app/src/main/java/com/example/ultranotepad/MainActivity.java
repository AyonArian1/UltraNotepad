package com.example.ultranotepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.ultranotepad.Adapter.NoteAdapter;
import com.example.ultranotepad.CallBack.MainActionCallback;
import com.example.ultranotepad.CallBack.NoteEventListener;
import com.example.ultranotepad.Model.Note;
import com.example.ultranotepad.db.NoteDB;
import com.example.ultranotepad.db.NotesDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.NetworkOnMainThreadException;
import android.preference.DialogPreference;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.ultranotepad.EditNoteActivity.NOTE_EXTRA_KEY;

public class MainActivity extends AppCompatActivity implements NoteEventListener {

    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NoteAdapter adapter;
    private NotesDao dao;
    private MainActionCallback actionCallback;
    private int checkCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewNote();
            }
        });

        dao = NoteDB.getInstance(this).notesDao();
    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();
        this.notes.addAll(list);
        this.adapter = new NoteAdapter(this.notes, this);
        this.adapter.setListener(this);
        this.recyclerView.setAdapter(adapter);
    }

    private void onAddNewNote() {
        startActivity(new Intent(MainActivity.this, EditNoteActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    @Override
    public void onNoteClick(Note note) {
        Intent edit = new Intent(MainActivity.this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_KEY, note.getId());
        startActivity(edit);
    }

    @Override
    public void onNoteLongClick(Note note) {
        note.setChecked(true);
        checkCount = 1;
        adapter.setMultiCheckMode(true);

        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked(!note.isChecked());
                if (note.isChecked())
                    checkCount++;
                else
                    checkCount--;

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note){

            }
        });

        actionCallback = new MainActionCallback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_delete) {
                    onDeleteMultiNotes();
                } else if (menuItem.getItemId() == R.id.action_share) {
                    onShareNotes();
                }
                return true;
            }
        };
        actionCallback.setCount(checkCount + "/" + notes.size());
        startActionMode(actionCallback);

    }

    private void onShareNotes() {
    }

    private void onDeleteMultiNotes() {
        List<Note> checkednote = adapter.getCheckedNotes();

        if (checkednote.size() != 0) {
            for (Note note : checkednote) {
                dao.deleteNote(note);
            }
            loadNotes();
        } else {
            Toast.makeText(this, "No notes Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);

        adapter.setMultiCheckMode(false);
        adapter.setListener(this);
    }
}
