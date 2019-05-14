package com.kebab.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Llama.Instances.HelloableListActivity;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class PeoplePickerActivity extends HelloableListActivity {
    static final String EXTRA_EXISTING_SELECTION = "EXISTING_SELECTION";
    static final String EXTRA_SELECTION = "SELECTION";
    OnPersonCheckedChangeListener PersonCheckListener = new OnPersonCheckedChangeListener(this);
    Button _CancelButton;
    private ArrayAdapter<Contact> _ContactAdapter = null;
    private ArrayList<Contact> _ContactsTemp;
    private ArrayList<Contact> _ContactsUi = new ArrayList();
    Button _OkButton;
    private ProgressDialog _ProgressDialog = null;
    private HashSet<String> _SelectedItems = new HashSet();
    volatile boolean _ThreadCancelled;

    class OnPersonCheckedChangeListener implements OnCheckedChangeListener {
        PeoplePickerActivity _Owner;

        public OnPersonCheckedChangeListener(PeoplePickerActivity owner) {
            this._Owner = owner;
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Contact contact = (Contact) buttonView.getTag();
            if (isChecked) {
                Logging.ReportSensitive("PeoplePicker", "Checked [" + contact.ContactName + "/" + contact.LookupKey + "]", buttonView.getContext());
                PeoplePickerActivity.this._SelectedItems.add(contact.LookupKey);
            } else {
                Logging.ReportSensitive("PeoplePicker", "Unchecked [" + contact.ContactName + "/" + contact.LookupKey + "]", buttonView.getContext());
                PeoplePickerActivity.this._SelectedItems.remove(contact.LookupKey);
            }
            this._Owner.updateTitle();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.peoplepicker);
        this._OkButton = (Button) findViewById(R.id.okButton);
        this._CancelButton = (Button) findViewById(R.id.cancelButton);
        if (savedInstanceState != null) {
            this._SelectedItems.addAll(savedInstanceState.getStringArrayList(EXTRA_SELECTION));
        } else {
            this._SelectedItems.addAll(getIntent().getExtras().getStringArrayList(EXTRA_EXISTING_SELECTION));
        }
        updateTitle();
        this._OkButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putStringArrayListExtra(PeoplePickerActivity.EXTRA_SELECTION, new ArrayList(PeoplePickerActivity.this._SelectedItems));
                PeoplePickerActivity.this.setResult(-1, resultIntent);
                PeoplePickerActivity.this.finish();
            }
        });
        this._CancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PeoplePickerActivity.this.setResult(0);
                PeoplePickerActivity.this.finish();
            }
        });
        this._ContactAdapter = new ArrayAdapter<Contact>(this, R.layout.checkboxrow, this._ContactsUi) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                if (convertView == null) {
                    view = View.inflate(getContext(), R.layout.checkboxrow, null);
                } else {
                    view = convertView;
                }
                Contact contact = (Contact) PeoplePickerActivity.this._ContactsUi.get(position);
                CheckBox nameCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
                nameCheckBox.setOnCheckedChangeListener(null);
                nameCheckBox.setText("");
                Contact tag = (Contact) view.getTag();
                if (tag != null) {
                    Contact tagC = tag;
                    Logging.ReportSensitive("PeoplePicker", "Reusing view for [" + tagC.ContactName + "/" + tagC.LookupKey + "] > [" + position + "@" + contact.ContactName + "/" + contact.LookupKey + "]", getContext());
                }
                view.setTag(contact);
                nameCheckBox.setChecked(PeoplePickerActivity.this._SelectedItems.contains(contact.LookupKey));
                nameCheckBox.setText(contact.ContactName);
                nameCheckBox.setOnCheckedChangeListener(PeoplePickerActivity.this.PersonCheckListener);
                return view;
            }
        };
        setListAdapter(this._ContactAdapter);
        this._ProgressDialog = ProgressDialog.show(this, "Please wait...", "Retrieving contacts ...", true, true);
        this._ProgressDialog.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                PeoplePickerActivity.this._ThreadCancelled = true;
                PeoplePickerActivity.this.cancelDialog();
            }
        });
        new Thread(null, new Runnable() {
            public void run() {
                PeoplePickerActivity.this._ThreadCancelled = false;
                PeoplePickerActivity.this.getContacts();
            }
        }, "ContactReadBackground").start();
    }

    private void updateTitle() {
        setTitle(String.format(getString(R.string.hrNoisyContactPicker1Selected), new Object[]{Integer.valueOf(this._SelectedItems.size())}));
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        super.onPause();
    }

    /* Access modifiers changed, original: protected */
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(EXTRA_SELECTION, new ArrayList(this._SelectedItems));
    }

    private void ReportBackToUiThread(final ArrayList<Contact> contactsTemp, final boolean cancelled, final boolean exception) {
        runOnUiThread(new Runnable() {
            public void run() {
                if (PeoplePickerActivity.this._ProgressDialog.isShowing()) {
                    PeoplePickerActivity.this._ProgressDialog.dismiss();
                }
                if (exception) {
                    new Builder(PeoplePickerActivity.this).setTitle(R.string.hrErrorReadingContacts).setMessage(R.string.hrThereWasAnErrorReadingYourContacts).setNegativeButton(R.string.hrClose, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            PeoplePickerActivity.this.cancelDialog();
                        }
                    }).show();
                } else if (!cancelled) {
                    PeoplePickerActivity.this._ContactsUi.addAll(contactsTemp);
                    PeoplePickerActivity.this._ContactAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public void cancelDialog() {
        setResult(0);
        finish();
    }

    private void getContacts() {
        String[] projection = new String[]{"_id", "display_name", "lookup"};
        this._ContactsTemp = new ArrayList();
        boolean cancelled = false;
        boolean exception = false;
        int nullNames = 0;
        Cursor cursor = null;
        try {
            cursor = managedQuery(Contacts.CONTENT_URI, projection, null, null, null);
            while (!cancelled && cursor.moveToNext()) {
                long contactId = cursor.getLong(0);
                String contactName = cursor.getString(1);
                String lookupKey = cursor.getString(2);
                if (contactName == null || lookupKey == null) {
                    nullNames++;
                } else {
                    this._ContactsTemp.add(new Contact(contactName, contactId, lookupKey));
                }
                cancelled = this._ThreadCancelled;
            }
            Collections.sort(this._ContactsTemp, Contact.CASE_INSENSITIVE_NAME_COMPARER);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception ex) {
            Logging.Report(ex, (Context) this);
            exception = true;
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        if (nullNames > 0) {
            Logging.Report("Found " + nullNames + " contacts with nullnames", (Context) this);
        }
        ReportBackToUiThread(this._ContactsTemp, cancelled, exception);
    }

    public static void StartPeopleIds(Activity context, int requestCode, ArrayList<String> existingSelection) {
        Intent intent = new Intent(context, PeoplePickerActivity.class);
        intent.putStringArrayListExtra(EXTRA_EXISTING_SELECTION, existingSelection);
        context.startActivityForResult(intent, requestCode);
    }

    public static ArrayList<String> ResultForPeopleIds(int resultCode, Intent resultIntent) {
        if (resultCode == -1) {
            return resultIntent.getStringArrayListExtra(EXTRA_SELECTION);
        }
        return null;
    }
}
