package com.kebab;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.kebab.ClickablePreferenceEx.GotResultHandler;

public abstract class DelayedRadioListPreference<TValue, TListItem> extends DelayedListPreference<TValue, TListItem> implements PreferenceEx<TValue> {
    public abstract TValue ConvertListItemToResult(TListItem tListItem);

    public DelayedRadioListPreference(ResultRegisterableActivity activity, String title, TValue currentValue, boolean runAsynchronously, String asyncMessage) {
        super(activity, title, currentValue, runAsynchronously, asyncMessage);
    }

    /* Access modifiers changed, original: protected */
    public void FillDialogBuilder(TValue existingSelectedItem, Builder dialog, final GotResultHandler<TValue> gotResultHandler) {
        dialog.setSingleChoiceItems(this._ListItemStrings, FindSelectedItemInList(existingSelectedItem, this._ListItems), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gotResultHandler.HandleResult(DelayedRadioListPreference.this.ConvertListItemToResult(DelayedRadioListPreference.this._ListItems.get(which)));
                dialog.dismiss();
            }
        });
    }
}
