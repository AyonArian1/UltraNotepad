package com.example.ultranotepad.CallBack;

import android.os.Build;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ultranotepad.R;

public abstract class MainActionCallback implements ActionMode.Callback{

    private ActionMode action;
    private MenuItem countItem;

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        this.action = actionMode;
        actionMode.getMenuInflater().inflate(R.menu.main_action_mode, menu);
        this.countItem = menu.findItem(R.id.action_check_count);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {

    }

    public void setCountItem(String checkedCount) {
        if (countItem != null)
            this.countItem.setTitle(checkedCount);
    }
}
