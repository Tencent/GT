package com.tencent.wstt.gt;

// Declare any non-default types here with import statements

interface IRemoteClient {
    boolean isAlive();

    void onDisconnected();
}
