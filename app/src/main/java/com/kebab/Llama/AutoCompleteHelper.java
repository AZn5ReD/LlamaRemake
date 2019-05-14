package com.kebab.Llama;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import java.util.List;

public class AutoCompleteHelper {
    public static void InitAutoCompleteButton(final AutoCompleteTextView textBox, final Button nameButton, final List<String> values) {
        textBox.setAdapter(new ArrayAdapter(textBox.getContext(), 17367050, values.toArray(new String[0])));
        textBox.setDropDownHeight(-2);
        nameButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AutoCompleteHelper.ShowList(textBox, nameButton, values);
            }
        });
    }

    public static void ShowList(final AutoCompleteTextView textBox, Button nameButton, List<String> values) {
        if (values.size() != 0) {
            final AutoCompleteTextView autoCompleteTextView = textBox;
            final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(textBox.getContext(), 17367057, 17367057, values) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view;
                    if (convertView == null) {
                        view = View.inflate(autoCompleteTextView.getContext(), 17367057, null);
                    } else {
                        view = convertView;
                    }
                    ((TextView) view.findViewById(16908308)).setText((CharSequence) getItem(position));
                    return view;
                }
            };
            new Builder(textBox.getContext(), true).setAdapter(listAdapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int index) {
                    textBox.setText((CharSequence) listAdapter.getItem(index));
                    dialog.dismiss();
                    textBox.dismissDropDown();
                }
            }).show();
        }
    }
}
