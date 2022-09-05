package com.yawar.memo.call;

import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;

public class SimpleSdpObserver implements SdpObserver {

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        System.out.println("onCreateSuccess");
    }

    @Override
    public void onSetSuccess() {
        System.out.println("onSetSuccess");

    }

    @Override
    public void onCreateFailure(String s) {
        System.out.println("onCreateFailure"+s);

    }

    @Override
    public void onSetFailure(String s) {
        System.out.println("onSetFailure"+s);

    }

}
