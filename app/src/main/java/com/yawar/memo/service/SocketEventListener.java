package com.yawar.memo.service;

import io.socket.emitter.Emitter;

class SocketEventListener extends Emitter implements Emitter.Listener {
    private final String mEvent;
    private final Listener mListener;

    public SocketEventListener(String mEvent, Listener mListener) {
        this.mEvent = mEvent;
        this.mListener = mListener;
    }

    @Override
    public void call(Object... objects) {
        if (this.mListener != null) {
            this.mListener.onEventCall(mEvent, objects);
        }
    }

    public interface Listener {
        void onEventCall(String event, Object... objects);
    }

}

