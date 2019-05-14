package com.kebab.Llama;

public abstract class LWork1<TResult> extends LWorkBase {
    TResult result;

    public abstract void InUiThread(TResult tResult);

    public abstract TResult InWorkerThread();

    public final void RunInWorkerThread() {
        this.result = InWorkerThread();
    }

    public final void RunInUiThread() {
        InUiThread(this.result);
    }
}
