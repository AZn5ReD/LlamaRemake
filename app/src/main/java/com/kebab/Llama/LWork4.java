package com.kebab.Llama;

public abstract class LWork4<TResult1, TResult2, TResult3, TResult4> extends LWorkBase {
    TResult1 result1;
    TResult2 result2;
    TResult3 result3;
    TResult4 result4;

    public abstract void InUiThread(TResult1 tResult1, TResult2 tResult2, TResult3 tResult3, TResult4 tResult4);

    public abstract TResult1 InWorkerThread1();

    public abstract TResult2 InWorkerThread2();

    public abstract TResult3 InWorkerThread3();

    public abstract TResult4 InWorkerThread4();

    public final void RunInWorkerThread() {
        this.result1 = InWorkerThread1();
        this.result2 = InWorkerThread2();
        this.result3 = InWorkerThread3();
        this.result4 = InWorkerThread4();
    }

    public final void RunInUiThread() {
        InUiThread(this.result1, this.result2, this.result3, this.result4);
    }
}
