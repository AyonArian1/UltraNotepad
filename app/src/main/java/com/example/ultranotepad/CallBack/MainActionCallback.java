package com.example.ultranotepad.CallBack;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ultranotepad.R;

public abstract class MainActionCallback implements ActionMode.Callback {

    private ActionMode action;
    //private MenuItem countItem;
    private MenuItem shareItem;

    //Inflate menu more action mode
    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        this.action = actionMode;
        actionMode.getMenuInflater().inflate(R.menu.main_action_mode, menu);
        this.shareItem = menu.findItem(R.id.action_share);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    /*public void setCountItem(String checkedCount) {
        if (countItem != null)
            this.countItem.setTitle(checkedCount);
    }*/
    //One note will be share at one time
    public void changeShareItemVisible(boolean b) {
        shareItem.setVisible(b);
    }

    //getter for actionMode
    public ActionMode getAction() {
        return action;
    }
}
