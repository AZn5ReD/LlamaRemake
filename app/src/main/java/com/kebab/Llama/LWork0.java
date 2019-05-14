package com.kebab.Llama;

public abstract class LWork0 extends LWorkBase {
    public abstract void InUiThread();

    public abstract void InWorkerThread();

    public final void RunInWorkerThread() {
        InWorkerThread();
    }

    public final void RunInUiThread() {
        InUiThread();
    }
}
