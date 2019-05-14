package com.kebab;

import android.app.AlertDialog.Builder;
import android.content.Context;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Llama.R;
import java.util.List;

public abstract class DelayedListPreference<TValue, TListItem> extends ClickablePreferenceEx<TValue> implements PreferenceEx<TValue> {
    String _AsyncMessage;
    int _ListItemResourceId;
    protected CharSequence[] _ListItemStrings;
    protected List<TListItem> _ListItems;
    boolean _RunAsynchronously;

    public abstract CharSequence ConvertListItemToString(TListItem tListItem);

    public abstract void FillDialogBuilder(TValue tValue, Builder builder, GotResultHandler<TValue> gotResultHandler);

    public abstract String GetHumanReadableValue(Context context, TValue tValue);

    public abstract List<TListItem> GetListItems();

    public abstract boolean IsSelectedItemEqualToListItem(TValue tValue, TListItem tListItem);

    public DelayedListPreference(ResultRegisterableActivity activity, String title, TValue currentValue, boolean runAsynchronously, String asyncMessage) {
        super(activity, title, currentValue);
        this._AsyncMessage = asyncMessage;
        this._RunAsynchronously = runAsynchronously;
    }

    /* Access modifiers changed, original: protected */
    public void PrepareForFindSelectedItem(TValue tValue) {
    }

    /* Access modifiers changed, original: protected */
    public int FindSelectedItemInList(TValue existingSelectedValue, List<TListItem> listItems) {
        PrepareForFindSelectedItem(existingSelectedValue);
        for (int i = 0; i < listItems.size(); i++) {
            if (IsSelectedItemEqualToListItem(existingSelectedValue, listItems.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /* Access modifiers changed, original: protected */
    public CharSequence[] ConvertListItemsToStrings(List<TListItem> listItems) {
        CharSequence[] names = new CharSequence[listItems.size()];
        for (int i = 0; i < names.length; i++) {
            names[i] = ConvertListItemToString(listItems.get(i));
        }
        return names;
    }

    private void ShowListDialog(TValue existingSelectedItem, GotResultHandler<TValue> gotResultHandler) {
        Builder dialog = new AlertDialogEx.Builder(this._Host.GetActivity());
        FillDialogBuilder(existingSelectedItem, dialog, gotResultHandler);
        dialog.show();
    }

    public void ShowDialog(GotResultHandler<TValue> gotResultHandler) {
        OnPreferenceClicked(this._Host, _GetCurrentValue(), gotResultHandler);
    }

    /* Access modifiers changed, original: protected */
    public void OnPreferenceClicked(ResultRegisterableActivity host, TValue existingValue, GotResultHandler<TValue> gotResultHandler) {
        Context context = host.GetActivity();
        if (this._RunAsynchronously) {
            final TValue tValue = existingValue;
            final GotResultHandler<TValue> gotResultHandler2 = gotResultHandler;
            new AsyncProgressDialog<Object, Object, List<TListItem>>(context, context.getString(R.string.hrPleaseWait), this._AsyncMessage, true, true) {
                /* Access modifiers changed, original: protected */
                public List<TListItem> DoWorkInBackground(Object[] params) {
                    if (DelayedListPreference.this._ListItems != null) {
                        return DelayedListPreference.this._ListItems;
                    }
                    DelayedListPreference.this._ListItems = DelayedListPreference.this.GetListItems();
                    DelayedListPreference.this._ListItemStrings = DelayedListPreference.this.ConvertListItemsToStrings(DelayedListPreference.this._ListItems);
                    return DelayedListPreference.this._ListItems;
                }

                /* Access modifiers changed, original: protected */
                public void MarkWorkAsCancelled() {
                }

                /* Access modifiers changed, original: protected */
                public void OnAsyncCompletedSuccessfully(List<TListItem> data) {
                    DelayedListPreference.this._ListItems = data;
                    DelayedListPreference.this.ShowListDialog(tValue, gotResultHandler2);
                }
            }.execute(new Object[]{null, null});
            return;
        }
        this._ListItems = GetListItems();
        this._ListItemStrings = ConvertListItemsToStrings(this._ListItems);
        ShowListDialog(existingValue, gotResultHandler);
    }
}
