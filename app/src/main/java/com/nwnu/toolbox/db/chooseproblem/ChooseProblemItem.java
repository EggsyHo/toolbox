package com.nwnu.toolbox.db.chooseproblem;

import org.litepal.crud.DataSupport;

public class ChooseProblemItem extends DataSupport {
    private int id;
    private String title;
    private int parentId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
}
