package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ArrayHelpers;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.Llama.ThirdPartyIntents;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import com.kebab.RunnableArg;
import com.kebab.RunnableWithResult;
import com.kebab.Tuple;
import com.kebab.Tuple3;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class AndroidIntentAction extends EventAction<AndroidIntentAction> {
    String _Base64Intent;
    String _FriendlyName;
    int _StartType;

    static class IntentExtraItem {
        protected String dataAsString;
        protected int dataTypeIndex;
        protected String key;

        IntentExtraItem() {
        }

        public static IntentExtraItem Create(String[] dataTypeNames) {
            int dataTypeIndex = ArrayHelpers.FindIndex(dataTypeNames, "string").intValue();
            IntentExtraItem result = new IntentExtraItem();
            result.dataTypeIndex = dataTypeIndex;
            result.dataAsString = "";
            result.key = "";
            return result;
        }

        public String addToIntent(Intent intent, String[] dataTypeNames, Context context) {
            String dataTypeName = dataTypeNames[this.dataTypeIndex];
            boolean errored = false;
            if (dataTypeName.equals("byte")) {
                try {
                    intent.putExtra(this.key, Byte.valueOf(this.dataAsString).byteValue());
                } catch (NumberFormatException e) {
                    errored = true;
                }
            } else if (dataTypeName.equals("short")) {
                try {
                    intent.putExtra(this.key, Short.valueOf(this.dataAsString).shortValue());
                } catch (NumberFormatException e2) {
                    errored = true;
                }
            } else if (dataTypeName.equals("int")) {
                try {
                    intent.putExtra(this.key, Integer.valueOf(this.dataAsString).intValue());
                } catch (NumberFormatException e3) {
                    errored = true;
                }
            } else if (dataTypeName.equals("long")) {
                try {
                    intent.putExtra(this.key, Long.valueOf(this.dataAsString).longValue());
                } catch (NumberFormatException e4) {
                    errored = true;
                }
            } else if (dataTypeName.equals("double")) {
                try {
                    intent.putExtra(this.key, Double.valueOf(this.dataAsString));
                } catch (NumberFormatException e5) {
                    errored = true;
                }
            } else if (dataTypeName.equals("float")) {
                try {
                    intent.putExtra(this.key, Float.valueOf(this.dataAsString).floatValue());
                } catch (NumberFormatException e6) {
                    errored = true;
                }
            } else if (dataTypeName.equals("boolean")) {
                try {
                    intent.putExtra(this.key, Boolean.valueOf(this.dataAsString).booleanValue());
                } catch (NumberFormatException e7) {
                    errored = true;
                }
            } else if (dataTypeName.equals("string")) {
                try {
                    intent.putExtra(this.key, this.dataAsString);
                } catch (NumberFormatException e8) {
                    errored = true;
                }
            } else {
                errored = true;
            }
            if (!errored) {
                return null;
            }
            return context.getString(R.string.hrCouldNotConvertExtra3, new Object[]{this.key, this.dataAsString, dataTypeName});
        }

        public static IntentExtraItem Create(String[] dataTypeNames, String key, Object value) {
            String typeName;
            String valueAsString;
            if (value instanceof Byte) {
                typeName = "byte";
                valueAsString = value + "";
            } else if (value instanceof Short) {
                typeName = "short";
                valueAsString = value + "";
            } else if (value instanceof Integer) {
                typeName = "int";
                valueAsString = value + "";
            } else if (value instanceof Boolean) {
                typeName = "boolean";
                valueAsString = value + "";
            } else if (value instanceof Long) {
                typeName = "long";
                valueAsString = value + "";
            } else if (value instanceof Float) {
                typeName = "float";
                valueAsString = value + "";
            } else if (value instanceof Double) {
                typeName = "double";
                valueAsString = value + "";
            } else if (!(value instanceof String)) {
                return null;
            } else {
                typeName = "string";
                valueAsString = value + "";
            }
            int dataTypeIndex = ArrayHelpers.FindIndex(dataTypeNames, typeName).intValue();
            IntentExtraItem result = new IntentExtraItem();
            result.key = key;
            result.dataTypeIndex = dataTypeIndex;
            result.dataAsString = valueAsString;
            return result;
        }
    }

    public AndroidIntentAction(String friendlyName, String packageOrBase64Intent, int startType) {
        this._FriendlyName = friendlyName;
        this._Base64Intent = packageOrBase64Intent;
        this._StartType = startType;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        Intent intent;
        try {
            intent = (Intent) Helpers.GetParcelableFromString(this._Base64Intent, Intent.class);
        } catch (Exception ex) {
            intent = null;
            Logging.Report(ex, (Context) service);
        }
        if (intent == null) {
            service.HandleFriendlyError(String.format(service.getString(R.string.hrFailedToDecodeIntentInEvent1), new Object[]{event.Name}), false);
            return;
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            for (String key : extras.keySet()) {
                String valueObject = (String) extras.get(key);
                if (valueObject instanceof String) {
                    String value = valueObject;
                    if (value != null && value.contains("##")) {
                        intent.putExtra(key, service.ExpandVariables(value));
                    }
                }
            }
        }
        service.startShortcut(intent, this._FriendlyName, this._StartType);
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 3;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.ANDROID_INTENT_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyName));
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(this._Base64Intent));
        sb.append("|");
        sb.append(this._StartType);
    }

    public static AndroidIntentAction CreateFrom(String[] parts, int currentPart) {
        return new AndroidIntentAction(parts[currentPart + 1], parts[currentPart + 2], Integer.parseInt(parts[currentPart + 3]));
    }

    public PreferenceEx<AndroidIntentAction> CreatePreference(PreferenceActivity context) {
        PackageManager pm = context.getPackageManager();
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<AndroidIntentAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionAndroidIntent), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, AndroidIntentAction value) {
                return value._FriendlyName;
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, AndroidIntentAction existingValue, GotResultHandler<AndroidIntentAction> gotResultHandler) {
                View view = View.inflate(host.GetActivity(), R.layout.android_intent_editor, null);
                Intent existingIntent = (Intent) Helpers.GetParcelableFromString(existingValue._Base64Intent, Intent.class);
                final TextView packageNameText = (TextView) view.findViewById(R.id.package_name);
                final TextView classNameText = (TextView) view.findViewById(R.id.class_name);
                final TextView actionText = (TextView) view.findViewById(R.id.action);
                final TextView categoryText = (TextView) view.findViewById(R.id.category);
                final TextView dataText = (TextView) view.findViewById(R.id.data);
                final TextView dataTypeText = (TextView) view.findViewById(R.id.data_type);
                final Spinner choosePreset = (Spinner) view.findViewById(R.id.choose_preset);
                final String[] dataTypeNames = host.GetActivity().getResources().getStringArray(R.array.intentEditorExtraTypesValues);
                final Spinner intentType = (Spinner) view.findViewById(R.id.intent_type);
                ListView extrasList = (ListView) view.findViewById(R.id.list);
                Button extrasAdd = (Button) view.findViewById(R.id.add);
                final ArrayList<IntentExtraItem> extras = new ArrayList();
                Ref<ArrayAdapter<IntentExtraItem>> extrasListAdapterRef = new Ref();
                final ResultRegisterableActivity resultRegisterableActivity = host;
                final ArrayList<IntentExtraItem> arrayList = extras;
                final ArrayAdapter<IntentExtraItem> extrasListAdapter = new ArrayAdapter<IntentExtraItem>(host.GetActivity(), 0, extras) {
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = LayoutInflater.from(resultRegisterableActivity.GetActivity()).inflate(R.layout.android_intent_editor_extra, null);
                        }
                        IntentExtraItem item = (IntentExtraItem) arrayList.get(position);
                        Spinner spinner = (Spinner) convertView.findViewById(R.id.spinner);
                        EditText editTextName = (EditText) convertView.findViewById(R.id.textName);
                        EditText editTextValue = (EditText) convertView.findViewById(R.id.textValue);
                        spinner.setOnItemSelectedListener(null);
                        editTextName.removeTextChangedListener((TextWatcher) editTextName.getTag());
                        editTextValue.removeTextChangedListener((TextWatcher) editTextValue.getTag());
                        spinner.setOnItemSelectedListener(null);
                        spinner.setSelection(item.dataTypeIndex);
                        editTextName.setText(item.key);
                        editTextValue.setText(item.dataAsString);
                        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> adapterView, View arg1, int index, long arg3) {
                                ((IntentExtraItem) arrayList.get(position)).dataTypeIndex = index;
                            }

                            public void onNothingSelected(AdapterView<?> adapterView) {
                            }
                        });
                        TextWatcher watcherName = new TextWatcher() {
                            public void afterTextChanged(Editable s) {
                                ((IntentExtraItem) arrayList.get(position)).key = s.toString();
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        };
                        TextWatcher watcherValue = new TextWatcher() {
                            public void afterTextChanged(Editable s) {
                                ((IntentExtraItem) arrayList.get(position)).dataAsString = s.toString();
                            }

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                            }
                        };
                        editTextName.addTextChangedListener(watcherName);
                        editTextName.setTag(watcherName);
                        editTextValue.addTextChangedListener(watcherValue);
                        editTextValue.setTag(watcherValue);
                        return convertView;
                    }
                };
                final Ref<Integer> ref = new Ref();
                final ListView listView = extrasList;
                Runnable anonymousClass2 = new Runnable() {
                    public void run() {
                        if (ref.Value == null) {
                            int height = listView.getHeight();
                            if (height > 0) {
                                ref.Value = Integer.valueOf(height);
                            }
                        }
                        if (ref.Value != null) {
                            LayoutParams layoutParams = listView.getLayoutParams();
                            layoutParams.height = ((Integer) ref.Value).intValue() * listView.getAdapter().getCount();
                            listView.setLayoutParams(layoutParams);
                        }
                    }
                };
                extrasListAdapterRef.Value = extrasListAdapter;
                extrasList.setAdapter(extrasListAdapter);
                final Runnable runnable = anonymousClass2;
                extrasListAdapter.registerDataSetObserver(new DataSetObserver() {
                    public void onChanged() {
                        runnable.run();
                    }
                });
                final String[] strArr = dataTypeNames;
                extrasAdd.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        extras.add(IntentExtraItem.Create(strArr));
                        extrasListAdapter.notifyDataSetChanged();
                    }
                });
                Tuple3<Integer, Integer, Intent>[] allIntents = ThirdPartyIntents.GetAll();
                final String[] names = ThirdPartyIntents.GetNames(host.GetActivity(), allIntents, 1);
                names[0] = host.GetActivity().getString(R.string.hrCustomAndroidIntent);
                new Handler().postDelayed(anonymousClass2, 250);
                final ArrayList<IntentExtraItem> arrayList2 = extras;
                final ArrayAdapter<IntentExtraItem> arrayAdapter = extrasListAdapter;
                final RunnableArg<Tuple<Intent, Integer>> updateFromIntent = new RunnableArg<Tuple<Intent, Integer>>() {
                    public void Run(Tuple<Intent, Integer> v) {
                        if (v != null && v.Item1 != null) {
                            CharSequence charSequence;
                            Intent value = v.Item1;
                            ComponentName cn = value.getComponent();
                            if (cn == null) {
                                packageNameText.setText(value.getPackage());
                            } else {
                                packageNameText.setText(cn.getPackageName());
                                classNameText.setText(cn.getClassName());
                            }
                            actionText.setText(value.getAction());
                            categoryText.setText(value.getCategories() == null ? "" : IterableHelpers.ConcatenateString(value.getCategories(), "|"));
                            TextView textView = dataText;
                            if (value.getData() == null) {
                                charSequence = "";
                            } else {
                                charSequence = value.getData().toString();
                            }
                            textView.setText(charSequence);
                            dataTypeText.setText(value.getType());
                            intentType.setSelection(((Integer) v.Item2).intValue());
                            arrayList2.clear();
                            if (value.getExtras() != null) {
                                for (String key : value.getExtras().keySet()) {
                                    IntentExtraItem item = IntentExtraItem.Create(dataTypeNames, key, value.getExtras().get(key));
                                    if (item != null) {
                                        arrayList2.add(item);
                                    }
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                };
                ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter(host.GetActivity(), 17367048, names);
                arrayAdapter2.setDropDownViewResource(17367049);
                choosePreset.setAdapter(arrayAdapter2);
                Integer index = IterableHelpers.FindIndex((CharSequence[]) names, existingValue._FriendlyName);
                if (index != null) {
                    choosePreset.setSelection(index.intValue());
                }
                final Tuple3<Integer, Integer, Intent>[] tuple3Arr = allIntents;
                choosePreset.setOnItemSelectedListener(new OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View arg1, int index, long arg3) {
                        if (index != 0) {
                            updateFromIntent.Run(Tuple.Create(tuple3Arr[index - 1].Item3, tuple3Arr[index - 1].Item2));
                        }
                    }

                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
                updateFromIntent.Run(Tuple.Create(existingIntent, Integer.valueOf(existingValue._StartType)));
                final TextView textView = packageNameText;
                final TextView textView2 = classNameText;
                final TextView textView3 = actionText;
                final TextView textView4 = dataTypeText;
                final TextView textView5 = dataText;
                final TextView textView6 = categoryText;
                final ResultRegisterableActivity resultRegisterableActivity2 = host;
                final ArrayList<IntentExtraItem> arrayList3 = extras;
                final String[] strArr2 = dataTypeNames;
                final Spinner spinner = intentType;
                final GotResultHandler<AndroidIntentAction> gotResultHandler2 = gotResultHandler;
                RunnableWithResult<String> storer = new RunnableWithResult<String>() {
                    public String Run() {
                        String packageName = textView.getText().toString();
                        String className = textView2.getText().toString();
                        String action = textView3.getText().toString();
                        String dataType = textView4.getText().toString();
                        String dataString = textView5.getText().toString();
                        String category = textView6.getText().toString();
                        Uri dataUri = null;
                        try {
                            if (dataString.length() > 0) {
                                dataUri = Uri.parse(dataString);
                            }
                        } catch (Exception ex) {
                            Logging.Report(ex, resultRegisterableActivity2.GetActivity());
                        }
                        Intent intent = new Intent();
                        if (action.length() > 0) {
                            intent.setAction(action);
                        }
                        for (String s : category.replace(' ', '|').replace(',', '|').split("\\|", -1)) {
                            if (s.length() > 0) {
                                intent.addCategory(s);
                            }
                        }
                        StringBuilder errors = new StringBuilder();
                        Iterator i$ = arrayList3.iterator();
                        while (i$.hasNext()) {
                            IntentExtraItem extra = (IntentExtraItem) i$.next();
                            if (extra.key.length() > 0) {
                                String error = extra.addToIntent(intent, strArr2, resultRegisterableActivity2.GetActivity());
                                if (error != null) {
                                    errors.append(error).append("\n");
                                }
                            }
                        }
                        if (errors.length() > 0) {
                            return errors.toString();
                        }
                        String friendlyName;
                        if (dataType.length() > 0) {
                            intent.setType(dataType);
                        }
                        if (dataUri != null) {
                            intent.setData(dataUri);
                        }
                        intent.setFlags(0);
                        if (className.length() != 0) {
                            intent.setClassName(packageName, className);
                        } else if (packageName.length() > 0) {
                            intent.setPackage(packageName);
                        }
                        String buff = Helpers.GetParcelAsString(intent);
                        if (!Helpers.VerifyIntentsMatch(preferenceActivity, intent, (Intent) Helpers.GetParcelableFromString(buff, Intent.class))) {
                            Toast.makeText(preferenceActivity, "Failed to store intent. Please email the Llama developer.", 1);
                        }
                        if (choosePreset.getSelectedItemPosition() > 0) {
                            friendlyName = names[choosePreset.getSelectedItemPosition()];
                        } else if (packageName.length() <= 0) {
                            friendlyName = action;
                        } else if (action.length() > 0) {
                            friendlyName = action + " " + packageName;
                        } else {
                            friendlyName = packageName;
                        }
                        if (friendlyName.length() == 0) {
                            friendlyName = resultRegisterableActivity2.GetActivity().getString(R.string.hrUnknown);
                        }
                        gotResultHandler2.HandleResult(new AndroidIntentAction(friendlyName, buff, spinner.getSelectedItemPosition()));
                        return null;
                    }
                };
                AlertDialog dialog = new Builder(host.GetActivity()).setTitle(R.string.hrActionAndroidIntent).setView(view).setPositiveButton(R.string.hrOk, null).setNegativeButton(R.string.hrCancel, null).setCancelable(false).create();
                dialog.getWindow().setSoftInputMode(32);
                dialog.getWindow().setLayout(-1, -2);
                dialog.setOwnerActivity(host.GetActivity());
                dialog.setCancelable(false);
                dialog.show();
                final RunnableWithResult<String> runnableWithResult = storer;
                final ResultRegisterableActivity resultRegisterableActivity3 = host;
                final AlertDialog alertDialog = dialog;
                dialog.getButton(-1).setOnClickListener(new OnClickListener() {
                    public void onClick(View arg0) {
                        String errors = (String) runnableWithResult.Run();
                        if (errors != null) {
                            Helpers.ShowSimpleDialogMessage(resultRegisterableActivity3.GetActivity(), errors);
                        } else {
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(String.format(context.getString(R.string.hrRunIntent1), new Object[]{this._FriendlyName}));
    }

    public String GetIsValidError(Context context) {
        if (this._Base64Intent == null || this._Base64Intent.length() == 0) {
            return context.getString(R.string.hrCreateAnIntent);
        }
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }
}
