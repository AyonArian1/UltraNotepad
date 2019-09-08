package com.example.ultranotepad;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.example.ultranotepad.Adapter.NoteAdapter;
import com.example.ultranotepad.CallBack.MainActionCallback;
import com.example.ultranotepad.CallBack.NoteEventListener;
import com.example.ultranotepad.Model.Note;
import com.example.ultranotepad.db.NoteDB;
import com.example.ultranotepad.db.NotesDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.ActionMode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.ultranotepad.EditNoteActivity.NOTE_EXTRA_KEY;

public class MainActivity extends AppCompatActivity implements NoteEventListener {

    private RecyclerView recyclerView;
    private ArrayList<Note> notes;
    private NoteAdapter adapter;
    private NotesDao dao;
    private MainActionCallback actionCallback;
    private int checkCount = 0;
    private FloatingActionButton fab;
    private static final int NUM_COLOUMN = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_notes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }

        //Staggered grid view layout
        /*StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLOUMN, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);*/



        //Reversed Linear Layout the recycler view
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);


        fab = findViewById(R.id.fab);
        //Add new note on clicking floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddNewNote();
            }
        });

        //Database instance set
        dao = NoteDB.getInstance(this).notesDao();
        loadNotes();
    }

    private void loadNotes() {
        this.notes = new ArrayList<>();
        List<Note> list = dao.getNotes();
        this.notes.addAll(list);

        this.adapter = new NoteAdapter(this.notes, this);
        this.adapter.setListener(this);
        adapter.notifyDataSetChanged();
        this.recyclerView.setAdapter(adapter);
        //swipeToDeleteHelper.attachToRecyclerView(recyclerView);
        showEmptyText();
    }

    private void showEmptyText() {
        TextView emptyTextview = (TextView) findViewById(R.id.empty_notes_txtView);
        if (notes.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyTextview.setVisibility(View.VISIBLE);
        } else {
            emptyTextview.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    //Add a new note by go to EditActivity
    private void onAddNewNote() {
        startActivity(new Intent(MainActivity.this, EditNoteActivity.class));
        finish();
    }


    //On click to edit the note
    @Override
    public void onNoteClick(Note note) {
        Intent edit = new Intent(MainActivity.this, EditNoteActivity.class);
        edit.putExtra(NOTE_EXTRA_KEY, note.getId());
        startActivity(edit);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //On long click to select Multiple notes and delete notes
    @Override
    public void onNoteLongClick(Note note) {
        note.setChecked(true);
        checkCount = 1;
        adapter.setMultiCheckMode(true);

        adapter.setListener(new NoteEventListener() {
            @Override
            public void onNoteClick(Note note) {
                note.setChecked(!note.isChecked());
                //to set how much notes are selected
                if (note.isChecked()) {
                    checkCount++;
                } else {
                    checkCount--;
                }
                //For share one note
                if (checkCount > 1) {
                    actionCallback.changeShareItemVisible(false);
                } else {
                    actionCallback.changeShareItemVisible(true);
                }
                //If there are no notes selected action mode is going to finish
                if (checkCount == 0) {
                    actionCallback.getAction().finish();
                }

                actionCallback.getAction().setTitle(checkCount + " selected");

                //actionCallback.setCountItem(checkCount + "/" + notes.size());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNoteLongClick(Note note) {

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
                getAction().finish();
                return false;
            }
        };
        startActionMode(actionCallback);
        fab.hide();
        actionCallback.getAction().setTitle(checkCount + " selected");

        //actionCallback.setCountItem(checkCount + "/" + notes.size());
    }

    //share note
    private void onShareNotes() {
        Note note = adapter.getCheckedNotes().get(0);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String noteTextForShare = note.getNoteText() + "\nBy: " + getString(R.string.app_name);
        shareIntent.putExtra(Intent.EXTRA_TEXT, noteTextForShare);
        startActivity(shareIntent);
    }

    //Multiple Delete notes
    private void onDeleteMultiNotes() {
        List<Note> checkednote = adapter.getCheckedNotes();

        if (checkednote.size() != 0) {
            for (Note note : checkednote) {
                dao.deleteNote(note);
            }
            Toast.makeText(this, checkCount + " notes deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No notes Selected", Toast.LENGTH_SHORT).show();
        }

    }

    //All checkbox is unchceked the option menu gone
    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        adapter.setMultiCheckMode(false);
        adapter.setListener(this);
        fab.show();
        loadNotes();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }


    /*
    //Swipe to delete notes
    private ItemTouchHelper swipeToDeleteHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            return 0;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            if (notes != null) {
                //get position of note
                Note swipedNote = notes.get(viewHolder.getAdapterPosition());
                //if any note is swiped
                if (swipedNote != null) {
                    swipeToDelete(swipedNote, viewHolder);
                }
            }
        }
    });

    private void swipeToDelete(final Note swipedNote, final RecyclerView.ViewHolder viewHolder) {
        new AlertDialog.Builder(this)
                .setMessage("Want to delete this note?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dao.deleteNote(swipedNote);
                        notes.remove(swipedNote);
                        adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        recyclerView.getAdapter().notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                })
                .setCancelable(false)
                .create().show();
    }*/
}
