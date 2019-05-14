package com.kebab.Activities;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import com.kebab.Llama.R;
import java.util.ArrayList;
import java.util.HashSet;

public class ContactAdapter extends ArrayAdapter<Contact> {
    private ArrayList<Contact> _Items;
    private HashSet<Long> _SelectedItems;

    public ContactAdapter(Context context, int textViewResourceId, ArrayList<Contact> items, HashSet<Long> selectedContacts) {
        super(context, textViewResourceId, items);
        this._Items = items;
        this._SelectedItems = selectedContacts;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.checkboxrow, null);
        } else {
            view = convertView;
        }
        final Contact contact = (Contact) this._Items.get(position);
        if (contact != null) {
            final CheckBox nameCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
            nameCheckBox.setChecked(this._SelectedItems.contains(Long.valueOf(contact.Id)));
            if (nameCheckBox != null) {
                nameCheckBox.setText(contact.ContactName);
            }
            final Runnable sortItOut = new Runnable() {
                public void run() {
                    nameCheckBox.setChecked(!nameCheckBox.isChecked());
                    if (nameCheckBox.isChecked()) {
                        Log.i("BDFVADS", "Checked " + contact.ContactName);
                        ContactAdapter.this._SelectedItems.add(Long.valueOf(contact.Id));
                        return;
                    }
                    Log.i("BDFVADS", "Unchecked " + contact.ContactName);
                    ContactAdapter.this._SelectedItems.remove(Long.valueOf(contact.Id));
                }
            };
            view.setOnClickListener(new OnClickListener() {
                public void onClick(View arg0) {
                    sortItOut.run();
                }
            });
            view.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 1) {
                        sortItOut.run();
                    }
                    return true;
                }
            });
        }
        return view;
    }

    public void updateData(ArrayList<Contact> contacts) {
        this._Items.addAll(contacts);
        notifyDataSetChanged();
    }
}
