package com.ysy.mindmap.utils;

import java.util.BitSet;
import java.util.Observable;

public class SuspendableObservable extends Observable {

    private boolean isNeedToNotify;
    private boolean isSuspended;
    private BitSet updatedFields;

    @Override
    public void notifyObservers() {
        if (!isSuspended) {
            super.notifyObservers(getUpdatedFields());
            getUpdatedFields().clear();
            isNeedToNotify = false;
        } else {
            isNeedToNotify = true;
        }
    }

    public void notifyObservers(int changedData) {
        getUpdatedFields().set(changedData);
        notifyObservers();
    }

    public void setChangeAndNotifyObservers(int changeBit) {
        setChanged();
        notifyObservers(changeBit);
    }

    public void suspendBinding() {
        isSuspended = true;
    }

    public void resumeBinding() {
        isSuspended = false;
        if (isNeedToNotify) {
            notifyObservers();
        }
    }

    protected BitSet getUpdatedFields() {
        if (updatedFields == null) {
            updatedFields = new BitSet();
        }
        return updatedFields;
    }
}
