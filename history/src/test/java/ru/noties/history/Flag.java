package ru.noties.history;

public class Flag {

    private Boolean value;

    public void mark() {
        this.value = Boolean.TRUE;
    }

    public boolean marked() {
        return value != null && value;
    }
}
