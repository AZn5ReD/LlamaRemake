package com.kebab;

import android.content.Context;
import android.view.View;

public interface DialogHandlerInterface<TDialogResult> {
    void DialogHasFinished(View view);

    TDialogResult GetResultFromView();

    boolean HideButtons();

    void PrepareDataForDialog(Runnable runnable);

    boolean RequiresScrollView();

    TDialogResult fillValuesFromString(String str);

    String getHumanReadableValue(TDialogResult tDialogResult);

    View getView(TDialogResult tDialogResult, Context context, DialogPreference<?, TDialogResult> dialogPreference);

    String serialiseToString(TDialogResult tDialogResult);
}
