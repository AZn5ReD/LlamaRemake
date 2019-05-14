package com.kebab;

import android.content.Context;
import android.view.View;

public abstract class DialogHandler<TDialogResult> implements DialogHandlerInterface<TDialogResult> {
    public abstract void DialogHasFinished(View view);

    public abstract TDialogResult GetResultFromView();

    public abstract TDialogResult fillValuesFromString(String str);

    public abstract String getHumanReadableValue(TDialogResult tDialogResult);

    public abstract View getView(TDialogResult tDialogResult, Context context, DialogPreference<?, TDialogResult> dialogPreference);

    public abstract String serialiseToString(TDialogResult tDialogResult);

    public boolean RequiresScrollView() {
        return true;
    }

    public boolean HideButtons() {
        return false;
    }

    public void PrepareDataForDialog(Runnable runnable) {
        runnable.run();
    }
}
