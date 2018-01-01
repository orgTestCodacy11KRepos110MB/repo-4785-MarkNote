package me.shouheng.notepal.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import me.shouheng.notepal.R;
import me.shouheng.notepal.databinding.ActivityListBaseWithDrawerBinding;
import me.shouheng.notepal.util.FragmentHelper;


/**
 * Created by wangshouheng on 2017/10/10.*/
public abstract class BaseListActivity extends CommonActivity<ActivityListBaseWithDrawerBinding> {

    protected abstract CharSequence getActionbarTitle();

    protected abstract Fragment getNotesFragment();

    protected abstract Fragment getSnaggingsFragment();

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_list_base_with_drawer;
    }

    @Override
    protected void doCreateView(Bundle savedInstanceState) {
        configToolbar();

        configDrawer();
    }

    private void configToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (!isDarkTheme()) toolbar.setPopupTheme(R.style.AppTheme_PopupOverlay);
        actionBar.setTitle(getActionbarTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
    }

    private void configDrawer() {
        getBinding().drawerLayout.openDrawer(GravityCompat.START);
        View header = getBinding().navView.inflateHeaderView(R.layout.archive_header);
        Toolbar drawerToolbar = header.findViewById(R.id.toolbar);
        drawerToolbar.setTitle(R.string.items);
        drawerToolbar.setBackgroundColor(primaryColor());

        getBinding().navView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_back) {
                BaseListActivity.super.onBackPressed();
                return true;
            }
            getBinding().drawerLayout.closeDrawers();
            execute(menuItem.getItemId());
            return true;
        });
        getBinding().navView.getMenu().findItem(R.id.nav_notes).setChecked(true);

        execute(R.id.nav_notes);
    }

    private void execute(@IdRes final int id) {
        new Handler().postDelayed(() -> {
            switch (id) {
                case R.id.nav_notes:
                    FragmentHelper.replace(this, getNotesFragment(), R.id.fragment_container);
                    break;
                case R.id.nav_minds:
                    FragmentHelper.replace(this, getSnaggingsFragment(), R.id.fragment_container);
                    break;
            }
        }, 350);
    }

    public void setDrawerLayoutLocked(boolean lock){
        getBinding().drawerLayout.setDrawerLockMode(lock ?
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED :
                DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    protected Fragment getCurrentFragment(){
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Fragment fragment = getCurrentFragment();
                if (!fragment.onOptionsItemSelected(item)) {
                    getBinding().drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getBinding().drawerLayout.isDrawerOpen(GravityCompat.START)){
            getBinding().drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}