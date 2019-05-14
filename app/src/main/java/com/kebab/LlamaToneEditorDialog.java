package com.kebab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Llama.Constants;
import com.kebab.Llama.Content.LlamaToneContentProvider;
import com.kebab.Llama.EventActions.RingtonePicker;
import com.kebab.Llama.EventActions.RingtonePicker.RingtonePickerResultCallback;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.TextEntryDialog.ButtonHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class LlamaToneEditorDialog {

    static class ToneListItem {
        String ActualToneName;
        String LlamaToneName;
        String Uri;

        public static class NameComparer implements Comparator<ToneListItem> {
            public int compare(ToneListItem x, ToneListItem y) {
                return String.CASE_INSENSITIVE_ORDER.compare(x.LlamaToneName, y.LlamaToneName);
            }
        }

        public ToneListItem(String llamaToneName, String actualToneName, String uri) {
            this.LlamaToneName = llamaToneName;
            this.ActualToneName = actualToneName;
            this.Uri = uri;
        }
    }

    public static void Show(ResultRegisterableActivity activity, List<Tuple<String, String>> tones, RunnableArg<List<Tuple<String, String>>> onSuccess) {
        final Activity context = activity.GetActivity();
        final ArrayList<ToneListItem> editingTones = new ArrayList();
        HashSet<String> allNames = LlamaService.GetAllLlamaToneNames(context);
        HashMap<String, String> existingNames = new HashMap();
        for (Tuple<String, String> e : tones) {
            existingNames.put(e.Item1, e.Item2);
            allNames.add(e.Item1);
        }
        Iterator i$ = allNames.iterator();
        while (i$.hasNext()) {
            String name = (String) i$.next();
            String toneUri = (String) existingNames.get(name);
            if (Constants.SilentRingtone.equals(toneUri)) {
                editingTones.add(new ToneListItem(name, context.getString(R.string.hrSilent), toneUri));
            } else if (toneUri == null) {
                editingTones.add(new ToneListItem(name, null, null));
            } else {
                Ringtone tone = RingtoneManager.getRingtone(context, Uri.parse(toneUri));
                if (tone == null) {
                    editingTones.add(new ToneListItem(name, context.getString(R.string.hrUnknown), toneUri));
                } else {
                    editingTones.add(new ToneListItem(name, tone.getTitle(context), toneUri));
                }
            }
        }
        Collections.sort(editingTones, new NameComparer());
        ListView list = new ListView(context);
        final BaseAdapter adapter = new BaseAdapter() {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v;
                String text1;
                String text2;
                if (convertView == null) {
                    v = View.inflate(context, 17367053, null);
                } else {
                    v = convertView;
                }
                if (position == 0) {
                    text1 = context.getString(R.string.hrCreateNewLlamaTone);
                    text2 = context.getString(R.string.hrTapToCreateANewLlamaTone);
                } else {
                    ToneListItem map = (ToneListItem) getItem(position);
                    text1 = map.LlamaToneName;
                    if (map.Uri == null || map.Uri.length() == 0) {
                        text2 = "-" + context.getString(R.string.hrDontChange) + "-";
                    } else if (Constants.SilentRingtone.equals(map.Uri)) {
                        text2 = context.getString(R.string.hrSilent);
                    } else {
                        text2 = map.ActualToneName;
                    }
                }
                ((TextView) v.findViewById(16908308)).setText(text1);
                ((TextView) v.findViewById(16908309)).setText(text2);
                return v;
            }

            public int getCount() {
                return editingTones.size() + 1;
            }

            public Object getItem(int position) {
                if (position == 0) {
                    return null;
                }
                return editingTones.get(position - 1);
            }

            public long getItemId(int position) {
                return (long) position;
            }
        };
        list.setAdapter(adapter);
        final ResultRegisterableActivity resultRegisterableActivity = activity;
        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View itemView, int index, long id) {
                if (index == 0) {
                    TextEntryDialog.Show(context, context.getString(R.string.hrPleaseEnterNameForTheLlamaTone), new ButtonHandler() {
                        public void Do(String result) {
                            if (result != null && result.length() > 0) {
                                result = result.replace("/", "").replace("&", "");
                                Iterator i$ = editingTones.iterator();
                                while (i$.hasNext()) {
                                    if (((ToneListItem) i$.next()).LlamaToneName.equals(result)) {
                                        Helpers.ShowSimpleDialogMessage(context, context.getString(R.string.hrALlamaToneNamed1AlreadyExists, new Object[]{result}));
                                        return;
                                    }
                                }
                                ToneListItem selectedItem = new ToneListItem(result, null, null);
                                editingTones.add(selectedItem);
                                LlamaToneEditorDialog.ShowRingtonePicker(resultRegisterableActivity, adapter, selectedItem);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    return;
                }
                LlamaToneEditorDialog.ShowRingtonePicker(resultRegisterableActivity, adapter, (ToneListItem) editingTones.get(index - 1));
            }
        });
        final Ref<AlertDialog> dialogCaptured = new Ref();
        final RunnableArg<List<Tuple<String, String>>> runnableArg = onSuccess;
        AlertDialog dialog = new Builder(context).setTitle(R.string.hrLlamaTones).setPositiveButton(R.string.hrSave, new OnClickListener() {
            public void onClick(DialogInterface dialog, int paramInt) {
                runnableArg.Run(LlamaToneEditorDialog.ConvertToneListToArrayList(editingTones));
            }
        }).setNeutralButton(R.string.hrCopyTo, new OnClickListener() {
            public void onClick(DialogInterface dialog, int paramInt) {
                LlamaToneEditorDialog.ShowCopyToDialog(context, editingTones, (AlertDialog) dialogCaptured.Value);
            }
        }).setNegativeButton(R.string.hrCancel, null).setView(list).create();
        dialogCaptured.Value = dialog;
        dialog.show();
    }

    private static void ShowRingtonePicker(final ResultRegisterableActivity activity, final BaseAdapter adapter, final ToneListItem selectedItem) {
        RingtonePicker.Show(R.string.hrWhichTypeOfToneIsLlamaTone, activity, null, activity.GetActivity().getString(R.string.hrClearCancelLlamaTone), true, new RingtonePickerResultCallback() {
            public void run(Uri uri, String title, boolean isSilent) {
                if (isSilent) {
                    String str;
                    selectedItem.ActualToneName = title;
                    ToneListItem toneListItem = selectedItem;
                    if (isSilent) {
                        str = Constants.SilentRingtone;
                    } else {
                        str = uri.toString();
                    }
                    toneListItem.Uri = str;
                } else if (uri == null) {
                    selectedItem.ActualToneName = activity.GetActivity().getString(R.string.hrDontChange);
                    selectedItem.Uri = null;
                } else if (uri.toString().startsWith(LlamaToneContentProvider.CONTENT_URI.toString())) {
                    Helpers.ShowSimpleDialogMessage(activity.GetActivity(), activity.GetActivity().getString(R.string.hrCantSelectLlamaToneAsLlamaTone));
                } else {
                    selectedItem.ActualToneName = title;
                    selectedItem.Uri = uri.toString();
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    protected static ArrayList<Tuple<String, String>> ConvertToneListToArrayList(ArrayList<ToneListItem> editingTones) {
        ArrayList<Tuple<String, String>> result = new ArrayList();
        Iterator i$ = editingTones.iterator();
        while (i$.hasNext()) {
            ToneListItem item = (ToneListItem) i$.next();
            if (item.Uri != null && item.Uri.length() > 0) {
                result.add(new Tuple(item.LlamaToneName, item.Uri));
            }
        }
        return result;
    }

    protected static void ShowCopyToDialog(Activity context, final ArrayList<ToneListItem> tones, final AlertDialog otherDialog) {
        if (Instances.HasServiceOrRestart(context)) {
            final String[] profileNames = Instances.Service.GetProfileNames();
            Arrays.sort(profileNames, String.CASE_INSENSITIVE_ORDER);
            final boolean[] selectedIndexes = new boolean[profileNames.length];
            new Builder(context).setTitle(R.string.hrCopyLlamaTonesToProfile).setMultiChoiceItems(profileNames, null, new OnMultiChoiceClickListener() {
                public void onClick(DialogInterface arg0, int index, boolean isChecked) {
                    selectedIndexes[index] = isChecked;
                }
            }).setPositiveButton(R.string.hrCopyToSelected, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<Tuple<String, String>> llamaTones = LlamaToneEditorDialog.ConvertToneListToArrayList(tones);
                    HashSet<String> selectedProfiles = new HashSet();
                    for (int i = 0; i < selectedIndexes.length; i++) {
                        if (selectedIndexes[i]) {
                            selectedProfiles.add(profileNames[i]);
                        }
                    }
                    Instances.Service.CopyLlamaTonesToProfiles(llamaTones, selectedProfiles);
                    otherDialog.show();
                }
            }).setNegativeButton(R.string.hrCancel, new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    otherDialog.show();
                }
            }).show();
        }
    }
}
