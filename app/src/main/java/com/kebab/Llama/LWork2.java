package com.kebab.Llama;

public abstract class LWork2<TResult1, TResult2> extends LWorkBase {
    TResult1 result1;
    TResult2 result2;

    public abstract void InUiThread(TResult1 tResult1, TResult2 tResult2);

    public abstract TResult1 InWorkerThread1();

    public abstract TResult2 InWorkerThread2();

    public final void RunInWorkerThread() {
        this.result1 = InWorkerThread1();
        this.result2 = InWorkerThread2();
    }

    public final void RunInUiThread() {
        InUiThread(this.result1, this.result2);
    }
}
