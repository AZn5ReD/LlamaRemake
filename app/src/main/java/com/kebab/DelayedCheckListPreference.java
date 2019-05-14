package com.kebab;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Llama.R;
import java.util.HashSet;

public abstract class DelayedCheckListPreference<TValue, TListItem> extends DelayedListPreference<TValue, TListItem> implements PreferenceEx<TValue> {
    boolean[] _Checks;

    public abstract TValue ConvertCheckedListItemToResult(HashSet<Integer> hashSet);

    public DelayedCheckListPreference(ResultRegisterableActivity activity, String title, TValue currentValue, boolean runAsynchronously, String asyncMessage) {
        super(activity, title, currentValue, runAsynchronously, asyncMessage);
    }

    /* Access modifiers changed, original: protected */
    public void FillDialogBuilder(TValue existingSelectedItem, Builder dialog, final GotResultHandler<TValue> gotResultHandler) {
        this._Checks = new boolean[this._ListItems.size()];
        final HashSet<Integer> checkedIndexes = new HashSet();
        FindSelectedItemsInList(existingSelectedItem, this._Checks, checkedIndexes);
        dialog.setPositiveButton(R.string.hrOkeyDoke, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                gotResultHandler.HandleResult(DelayedCheckListPreference.this.ConvertCheckedListItemToResult(checkedIndexes));
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.hrCancel, null);
        dialog.setMultiChoiceItems(this._ListItemStrings, this._Checks, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface arg0, int position, boolean isChecked) {
                if (isChecked) {
                    checkedIndexes.add(Integer.valueOf(position));
                } else {
                    checkedIndexes.remove(Integer.valueOf(position));
                }
            }
        });
    }

    private void FindSelectedItemsInList(TValue existingSelectedItem, boolean[] checkArray, HashSet<Integer> checkedIndexes) {
        for (int i = 0; i < checkArray.length; i++) {
            boolean indexIsChecked = IsSelectedItemEqualToListItem(existingSelectedItem, this._ListItems.get(i));
            checkArray[i] = indexIsChecked;
            if (indexIsChecked) {
                checkedIndexes.add(Integer.valueOf(i));
            }
        }
    }
}
