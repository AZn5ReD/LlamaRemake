package com.kebab;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.preference.ListPreference;
import android.util.AttributeSet;
import com.kebab.PreferenceEx.Helper;
import java.util.ArrayList;
import java.util.List;

public class ListPreferenceMultiselect<TValue> extends ListPreference implements PreferenceEx<TValue> {
    private static final String SEPARATOR = "OV=I=XseparatorX=I=VO";
    CharSequence _ExistingSummary;
    OnGetValueEx<TValue> _OnGetValueEx;
    OnPreferenceClick _OnPreferenceClick;
    private boolean[] mClickedDialogEntryIndices;

    public ListPreferenceMultiselect(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (getEntries() != null) {
            this.mClickedDialogEntryIndices = new boolean[getEntries().length];
        }
    }

    public void setEntries(CharSequence[] entries) {
        super.setEntries(entries);
        this.mClickedDialogEntryIndices = new boolean[entries.length];
    }

    public ListPreferenceMultiselect(Context context) {
        this(context, null);
    }

    /* Access modifiers changed, original: protected */
    public void onPrepareDialogBuilder(Builder builder) {
        CharSequence[] entries = getEntries();
        CharSequence[] entryValues = getEntryValues();
        if (entries == null || entryValues == null || entries.length != entryValues.length) {
            throw new IllegalStateException("ListPreference requires an entries array and an entryValues array which are both the same length");
        }
        restoreCheckedEntries();
        builder.setMultiChoiceItems(entries, this.mClickedDialogEntryIndices, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean val) {
                ListPreferenceMultiselect.this.mClickedDialogEntryIndices[which] = val;
            }
        });
    }

    public static String[] parseStoredValue(CharSequence val) {
        if (val == null || "".equals(val)) {
            return null;
        }
        return ((String) val).split(SEPARATOR);
    }

    private void restoreCheckedEntries() {
        CharSequence[] entryValues = getEntryValues();
        String[] vals = parseStoredValue(getValue());
        if (vals != null) {
            for (String trim : vals) {
                String val = trim.trim();
                for (int i = 0; i < entryValues.length; i++) {
                    if (entryValues[i].equals(val)) {
                        this.mClickedDialogEntryIndices[i] = true;
                        break;
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogClosed(boolean positiveResult) {
        String result = getValueForCheckedItemsArray();
        if (positiveResult && callChangeListener(result)) {
            setValue(result);
        }
        onChanged();
    }

    /* Access modifiers changed, original: 0000 */
    public String getValueForCheckedItemsArray() {
        CharSequence[] entryValues = getEntryValues();
        if (entryValues == null || entryValues.length == 0) {
            return "";
        }
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < entryValues.length; i++) {
            if (this.mClickedDialogEntryIndices[i]) {
                value.append(entryValues[i]).append(SEPARATOR);
            }
        }
        if (value.length() > 0) {
            value.setLength(value.length() - SEPARATOR.length());
        }
        return value.toString();
    }

    public String[] getValues() {
        String[] result = parseStoredValue(getValue());
        if (result == null) {
            return new String[0];
        }
        return result;
    }

    public List<Integer> getSelectedValueIndexes() {
        ArrayList<Integer> list = new ArrayList();
        for (int i = 0; i < this.mClickedDialogEntryIndices.length; i++) {
            if (this.mClickedDialogEntryIndices[i]) {
                list.add(Integer.valueOf(i));
            }
        }
        return list;
    }

    public void setValues(Iterable<String> values) {
        for (int i = 0; i < this.mClickedDialogEntryIndices.length; i++) {
            this.mClickedDialogEntryIndices[i] = false;
        }
        for (String value : values) {
            Integer index = IterableHelpers.FindIndex(getEntryValues(), value);
            if (index != null) {
                this.mClickedDialogEntryIndices[index.intValue()] = true;
            }
        }
        setValue(getValueForCheckedItemsArray());
        onChanged();
    }

    public void onClick() {
        if (this._OnPreferenceClick == null || this._OnPreferenceClick.CanShowDialog(this)) {
            super.onClick();
        }
    }

    public void setOnPreferenceClick(OnPreferenceClick onPreferenceClick) {
        this._OnPreferenceClick = onPreferenceClick;
    }

    public TValue GetValueEx() {
        return this._OnGetValueEx.GetValue(this);
    }

    public void SetOnGetValueExCallback(OnGetValueEx<TValue> onGetValueEx) {
        this._OnGetValueEx = onGetValueEx;
    }

    public void setSummary(CharSequence value) {
        this._ExistingSummary = value;
        Helper.UpdateValueAndSummary(this);
    }

    public void setActualSummary(CharSequence value) {
        super.setSummary(value);
    }

    public CharSequence getOriginalSummary() {
        return this._ExistingSummary;
    }

    public void onChanged() {
        Helper.UpdateValueAndSummary(this);
    }

    public void onAttachedToActivity() {
        super.onAttachedToActivity();
        this._ExistingSummary = super.getSummary();
        Helper.UpdateValueAndSummary(this);
    }

    public CharSequence getHumanReadableValue() {
        if (this.mClickedDialogEntryIndices == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        boolean needComma = false;
        CharSequence[] entries = getEntries();
        for (int i = 0; i < this.mClickedDialogEntryIndices.length; i++) {
            if (this.mClickedDialogEntryIndices[i]) {
                if (needComma) {
                    sb.append(", ");
                }
                sb.append(entries[i]);
                needComma = true;
            }
        }
        return sb.toString();
    }
}
