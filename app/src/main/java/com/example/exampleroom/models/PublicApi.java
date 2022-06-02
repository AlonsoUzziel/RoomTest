package com.example.exampleroom.models;

import java.util.ArrayList;

public class PublicApi {
    private int count;
    private ArrayList<Entry> entries;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Entry> getEntries() {
        return entries;
    }

    public void setEntries(ArrayList<Entry> entries) {
        this.entries = entries;
    }
}
