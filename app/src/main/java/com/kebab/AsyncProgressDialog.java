package com.kebab;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import com.kebab.Llama.Logging;

public abstract class AsyncProgressDialog<TParams, TProgress, TResult> extends AsyncTask<TParams, TProgress, TResult> {
    boolean _Cancellable;
    Context _Context;
    ProgressDialog _Dialog;
    boolean _Indeterminable;
    boolean _IsCancelled;
    String _Message;
    String _Title;

    public abstract TResult DoWorkInBackground(TParams[] tParamsArr);

    public abstract void MarkWorkAsCancelled();

    public abstract void OnAsyncCompletedSuccessfully(TResult tResult);

    public AsyncProgressDialog(Context c, String title, String message, boolean indeterminable, boolean cancellable) {
        this._Context = c;
        this._Title = title;
        this._Message = message;
        this._Indeterminable = indeterminable;
        this._Cancellable = cancellable;
    }

    /* Access modifiers changed, original: protected */
    public void onCancelled() {
        super.onCancelled();
    }

    /* Access modifiers changed, original: protected */
    public void onPostExecute(TResult result) {
        super.onPostExecute(result);
        if (!this._IsCancelled) {
            try {
                this._Dialog.dismiss();
            } catch (Exception ex) {
                Logging.Report(ex, this._Context);
            }
            OnAsyncCompletedSuccessfully(result);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onPreExecute() {
        this._Dialog = ProgressDialog.show(this._Context, this._Title, this._Message, this._Indeterminable, this._Cancellable, new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                AsyncProgressDialog.this._IsCancelled = true;
                AsyncProgressDialog.this.MarkWorkAsCancelled();
            }
        });
    }

    /* Access modifiers changed, original: protected|varargs */
    public void onProgressUpdate(TProgress... values) {
        super.onProgressUpdate(values);
    }

    /* Access modifiers changed, original: protected|varargs */
    public TResult doInBackground(TParams... params) {
        return DoWorkInBackground(params);
    }
}
