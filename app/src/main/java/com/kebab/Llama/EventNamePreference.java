package com.kebab.Llama;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import java.util.ArrayList;
import java.util.Collections;

public class EventNamePreference extends Preference {
    public PreferenceActivity Activity;
    public boolean ShowGroupName = true;
    private String _EventGroup = "";
    private String _EventName = "";
    ArrayList<String> _ExistingGroups;

    public EventNamePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EventNamePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EventNamePreference(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference arg0) {
                View view = View.inflate(EventNamePreference.this.getContext(), R.layout.event_name_and_group, null);
                final EditText eventName = (EditText) view.findViewById(R.id.text1);
                final AutoCompleteTextView eventGroup = (AutoCompleteTextView) view.findViewById(R.id.text2);
                Button groupButton = (Button) view.findViewById(R.id.group_button);
                TextView groupLabel = (TextView) view.findViewById(R.id.label);
                eventName.setText(EventNamePreference.this._EventName);
                eventGroup.setText(EventNamePreference.this._EventGroup);
                if (EventNamePreference.this.ShowGroupName) {
                    if (EventNamePreference.this._ExistingGroups == null) {
                        EventNamePreference.this.LoadGroups();
                    }
                    if (EventNamePreference.this._ExistingGroups != null) {
                        eventGroup.setAdapter(new ArrayAdapter(EventNamePreference.this.getContext(), R.layout.simple_dropdown_listline, EventNamePreference.this._ExistingGroups.toArray(new String[0])));
                        AutoCompleteHelper.InitAutoCompleteButton(eventGroup, groupButton, EventNamePreference.this._ExistingGroups);
                    }
                } else {
                    eventGroup.setVisibility(8);
                    groupButton.setVisibility(8);
                    groupLabel.setVisibility(8);
                }
                Dialog dialog = new Builder(EventNamePreference.this.Activity).setView(view).setPositiveButton(R.string.hrOk, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EventNamePreference.this._EventName = eventName.getText().toString();
                        EventNamePreference.this._EventGroup = eventGroup.getText().toString();
                        EventNamePreference.this.updateSummary();
                    }
                }).setNegativeButton(R.string.hrCancel, null).create();
                dialog.getWindow().setSoftInputMode(37);
                dialog.show();
                return true;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void LoadGroups() {
        if (Instances.HasServiceOrRestart(getContext())) {
            this._ExistingGroups = Instances.Service.GetAllGroupNames();
            Collections.sort(this._ExistingGroups, String.CASE_INSENSITIVE_ORDER);
        }
    }

    public String getEventName() {
        return this._EventName;
    }

    public String getGroupName() {
        return this._EventGroup;
    }

    public void setEventName(String name) {
        this._EventName = name;
        updateSummary();
    }

    public void setEventGroup(String groupName) {
        this._EventGroup = groupName;
        updateSummary();
    }

    private void updateSummary() {
        setSummary(this._EventName + (this._EventGroup.length() > 0 ? " (" + this._EventGroup + ")" : ""));
    }
}
