package com.example.ultranotepad.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ultranotepad.CallBack.NoteEventListener;
import com.example.ultranotepad.Model.Note;
import com.example.ultranotepad.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NotesHolder>{

    private ArrayList<Note> notes;
    private Context context;
    private NoteEventListener listener;

    private boolean multiCheckMode = false;

    public NoteAdapter(ArrayList<Note> notes, Context context) {
        this.notes = notes;
        this.context = context;
    }

    @NonNull
    @Override
    public NotesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_item_list_sample,parent,false);
        return new NotesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesHolder holder, int position) {
        final Note note = getNotes(position);
        if (note!= null){
            holder.noteText.setText(note.getNoteText());
            holder.noteDate.setText(dateFromLong(note.getNoteDate()));

            //On click items
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onNoteClick(note);
                }
            });

            //On long click listener
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onNoteLongClick(note);
                    return false;
                }
            });

            if (multiCheckMode){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setChecked(note.isChecked());
            }else {
                holder.checkBox.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private Note getNotes(int position){
        return notes.get(position);
    }

    public List<Note> getCheckedNotes(){
        List<Note> checkNotes = new ArrayList<>();
        for (Note n : this.notes) {
            if (n.isChecked())
                checkNotes.add(n);
        }
        return checkNotes;
    }


    public class NotesHolder extends RecyclerView.ViewHolder {

        TextView noteText, noteDate;
        CheckBox checkBox;

        public NotesHolder(@NonNull View itemView) {
            super(itemView);

            noteText = (TextView) itemView.findViewById(R.id.note_text);
            noteDate = (TextView) itemView.findViewById(R.id.note_date);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

    }
    public static String dateFromLong(long time) {
        DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy 'at' hh:mm aaa", Locale.ENGLISH);
        return format.format(new Date(time));
    }

    public void setListener(NoteEventListener listener) {
        this.listener = listener;
    }

    public void setMultiCheckMode(boolean multiCheckMode) {
        this.multiCheckMode = multiCheckMode;
        notifyDataSetChanged();
    }
}
