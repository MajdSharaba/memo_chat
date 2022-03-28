package com.yawar.memo.observe;

import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Observable;

public class FireBaseTokenObserve extends Observable {
    public PhoneAuthProvider.ForceResendingToken forceResendingToken = null;

    public PhoneAuthProvider.ForceResendingToken getForceResendingToken() {
        return forceResendingToken;
    }

    public void setForceResendingToken(PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        this.forceResendingToken = forceResendingToken;
        setChanged();
        notifyObservers();
    }
}
