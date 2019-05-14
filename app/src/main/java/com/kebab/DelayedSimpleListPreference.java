package com.kebab;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.kebab.ClickablePreferenceEx.GotResultHandler;

public abstract class DelayedSimpleListPreference<TValue, TListItem> extends DelayedListPreference<TValue, TListItem> implements PreferenceEx<TValue> {
    public abstract TValue ConvertListItemToResult(TListItem tListItem);

    public DelayedSimpleListPreference(ResultRegisterableActivity activity, String title, TValue currentValue, boolean runAsynchronously, String asyncMessage) {
        super(activity, title, currentValue, runAsynchronously, asyncMessage);
    }

    /* Access modifiers changed, original: protected */
    public void FillDialogBuilder(TValue tValue, Builder dialog, final GotResultHandler<TValue> gotResultHandler) {
        dialog.setItems(this._ListItemStrings, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gotResultHandler.HandleResult(DelayedSimpleListPreference.this.ConvertListItemToResult(DelayedSimpleListPreference.this._ListItems.get(which)));
                dialog.dismiss();
            }
        });
    }
}
