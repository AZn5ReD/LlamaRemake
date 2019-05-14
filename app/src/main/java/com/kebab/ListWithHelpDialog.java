package com.kebab;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Llama.R;

public class ListWithHelpDialog {

    public interface OnListWithHelpClickListener {
        void OnHelpClick(int i);

        void OnItemSelected(int i);
    }

    public static void Show(Activity context, String title, final String[] items, final OnListWithHelpClickListener listener) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService("layout_inflater");
        new Builder(context).setTitle(title).setInverseBackgroundForced(true).setAdapter(new ListAdapter() {
            public boolean areAllItemsEnabled() {
                return true;
            }

            public boolean isEnabled(int position) {
                return true;
            }

            public int getCount() {
                return items.length;
            }

            public Object getItem(int position) {
                return items[position];
            }

            public long getItemId(int position) {
                return (long) position;
            }

            public int getItemViewType(int position) {
                return 666;
            }

            public View getView(final int position, View convertView, ViewGroup parent) {
                View v;
                if (convertView == null) {
                    v = inflater.inflate(R.layout.listitemwithhelp, null);
                } else {
                    v = convertView;
                }
                ((TextView) v.findViewById(16908308)).setText(items[position]);
                v.findViewById(R.id.help).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        listener.OnHelpClick(position);
                    }
                });
                return v;
            }

            public int getViewTypeCount() {
                return 1;
            }

            public boolean hasStableIds() {
                return true;
            }

            public boolean isEmpty() {
                return items.length == 0;
            }

            public void registerDataSetObserver(DataSetObserver observer) {
            }

            public void unregisterDataSetObserver(DataSetObserver observer) {
            }
        }, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.OnItemSelected(which);
            }
        }).show();
    }
}
