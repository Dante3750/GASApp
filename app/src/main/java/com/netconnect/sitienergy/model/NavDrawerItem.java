package com.netconnect.sitienergy.model;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private int menuIcon;


    public NavDrawerItem() {

    }

//    public NavDrawerItem(boolean showNotify, String title, int menuIcon) {
//        this.showNotify = showNotify;
//        this.title = title;
//        this.menuIcon = menuIcon;
//    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }

}
