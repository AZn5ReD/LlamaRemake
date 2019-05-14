package com.kebab.Llama.EventConditions;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.preference.PreferenceActivity;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.HelpersC;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter1;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.NfcFriendlyName;
import com.kebab.Llama.R;
import com.kebab.Llama.StateChange;
import com.kebab.Nfc.LlamaNfcWriterActivity;
import com.kebab.Nfc.NfcHelperBelow9;
import com.kebab.Nfc.NfcWatcher;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import java.io.IOException;
import java.util.ArrayList;

public class NfcDetectedCondition extends EventCondition<NfcDetectedCondition> {
    public static String MY_ID;
    public static int MY_TRIGGER;
    public static int[] MY_TRIGGERS;
    boolean _AlwaysTrue = false;
    String _NfcHexId;

    static {
        EventMeta.InitCondition(EventFragment.NFC_DETECTED_CONDITION, new ConditionStaticInitter1() {
            public void UpdateStatics(String id, int[] triggers, int trigger) {
                NfcDetectedCondition.MY_ID = id;
                NfcDetectedCondition.MY_TRIGGERS = triggers;
                NfcDetectedCondition.MY_TRIGGER = trigger;
            }
        });
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return MY_ID;
    }

    public int[] getEventTriggers() {
        return MY_TRIGGERS;
    }

    public NfcDetectedCondition(String nfcHexId, boolean alwaysTrue) {
        this._NfcHexId = nfcHexId;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (state.TriggerType == MY_TRIGGER && HelpersC.StringEquals(state.TriggerNfcId, this._NfcHexId)) {
            return 2;
        }
        return 0;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        String name = null;
        if (Instances.Service != null) {
            name = Instances.Service.GetNfcName(this._NfcHexId, true);
        }
        sb.append(String.format(context.getString(R.string.hrWhenNfcTag1IsDectected), new Object[]{name}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._NfcHexId)).append("|");
        sb.append(0);
    }

    public static NfcDetectedCondition CreateFrom(String[] parts, int currentPart) {
        return new NfcDetectedCondition(LlamaStorage.SimpleUnescape(parts[currentPart + 1]), parts[currentPart + 2] == "1");
    }

    public PreferenceEx<NfcDetectedCondition> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<NfcDetectedCondition>((ResultRegisterableActivity) context, context.getString(R.string.hrConditionNfcTagDetected), this) {
            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(ResultRegisterableActivity host, NfcDetectedCondition existingValue, final GotResultHandler<NfcDetectedCondition> gotResultHandler) {
                if (NfcHelperBelow9.isSupported(host.GetActivity())) {
                    final Ref<Dialog> dialog = new Ref();
                    final Ref<NfcWatcher> watcher = new Ref();
                    watcher.Value = new NfcWatcher() {
                        public void notifyNfcPresent(final String nfcHexId, String nfcFriendlyName) {
                            if (nfcFriendlyName == null) {
                                TextEntryDialog.Show(preferenceActivity, preferenceActivity.getString(R.string.hrLlamaHasFoundATagPleaseNameIt), new ButtonHandler() {
                                    public void Do(String result) {
                                        Instances.Service.AddNfcTag(nfcHexId, result);
                                        AnonymousClass1.this.ShowUseTagConfirmation(nfcHexId, result);
                                    }
                                });
                            } else {
                                ShowUseTagConfirmation(nfcHexId, nfcFriendlyName);
                            }
                            if (dialog.Value != null) {
                                ((Dialog) dialog.Value).dismiss();
                                Instances.Service.UnregisterNfcWatcher((NfcWatcher) watcher.Value);
                            }
                        }

                        private void ShowUseTagConfirmation(final String nfcHexId, String nfcFriendlyName) {
                            new Builder(preferenceActivity).setTitle(preferenceActivity.getString(R.string.hrConditionNfcTagDetected)).setMessage(preferenceActivity.getString(R.string.hrDoYouWantToUseTheTag1InThisCondition, new Object[]{nfcFriendlyName})).setPositiveButton(R.string.hrYes, new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    gotResultHandler.HandleResult(new NfcDetectedCondition(nfcHexId, false));
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.hrNo, null).show();
                        }
                    };
                    Instances.Service.RegisterNfcWatcher((NfcWatcher) watcher.Value);
                    final Runnable showNfcWriterDialog = new Runnable() {
                        public void run() {
                            ((ResultRegisterableActivity) preferenceActivity).RegisterActivityResult(new Intent(preferenceActivity, LlamaNfcWriterActivity.class), new ResultCallback() {
                                public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                                    String nfcHexId = data == null ? null : data.getStringExtra(LlamaNfcWriterActivity.EXTRA_TAG_HEX_ID);
                                    if (nfcHexId != null) {
                                        ((NfcWatcher) watcher.Value).notifyNfcPresent(nfcHexId, Instances.Service.GetNfcName(nfcHexId, false));
                                    }
                                }
                            }, null);
                        }
                    };
                    final ArrayList<NfcFriendlyName> nfcTags = Instances.Service.GetAllNfcTags(true);
                    if (nfcTags.size() == 0) {
                        dialog.Value = new Builder(preferenceActivity).setMessage(R.string.hrLlamaDoesNotKnowAboutAnyNfcTagsTapOneOnYourPhone).setPositiveButton("Format tag for Llama", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showNfcWriterDialog.run();
                            }
                        }).setNegativeButton(R.string.hrCancelTagReading, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Instances.Service.UnregisterNfcWatcher((NfcWatcher) watcher.Value);
                            }
                        }).setOnCancelListener(new OnCancelListener() {
                            public void onCancel(DialogInterface dialog) {
                                Instances.Service.UnregisterNfcWatcher((NfcWatcher) watcher.Value);
                            }
                        }).create();
                    } else {
                        CharSequence[] items = new CharSequence[nfcTags.size()];
                        for (int i = 0; i < items.length; i++) {
                            items[i] = ((NfcFriendlyName) nfcTags.get(i)).Name;
                        }
                        dialog.Value = new Builder(preferenceActivity).setItems(items, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Instances.Service.UnregisterNfcWatcher((NfcWatcher) watcher.Value);
                                gotResultHandler.HandleResult(new NfcDetectedCondition(((NfcFriendlyName) nfcTags.get(which)).HexString, false));
                            }
                        }).setPositiveButton("Format new tag for Llama", new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                showNfcWriterDialog.run();
                            }
                        }).create();
                        if (NfcHelperBelow9.isEnabled(preferenceActivity)) {
                            Helpers.ShowTip(preferenceActivity, (int) R.string.hrYouCanAlsoUseAnotherTagByTappingItOnYourPhone);
                        } else {
                            Helpers.ShowTip(preferenceActivity, (int) R.string.hrNfcIsNotCurrentlyTurnedOnTurnItOnToAddTags);
                        }
                    }
                    ((Dialog) dialog.Value).setOnCancelListener(new OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            Instances.Service.UnregisterNfcWatcher((NfcWatcher) watcher.Value);
                        }
                    });
                    ((Dialog) dialog.Value).show();
                    return;
                }
                Helpers.ShowSimpleDialogMessage(host.GetActivity(), host.GetActivity().getString(R.string.hrYourPhoneDoesNotSupportNfc));
            }

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, NfcDetectedCondition value) {
                if (value._NfcHexId == null || value._NfcHexId.length() == 0) {
                    return "";
                }
                return context.getString(R.string.hrWhenNfcTag1IsDectected, new Object[]{Instances.Service.GetNfcName(value._NfcHexId, true)});
            }
        };
    }

    public String GetIsValidError(Context context) {
        if (this._NfcHexId == null || this._NfcHexId.length() == 0) {
            return context.getString(R.string.hrPleaseChooseAnNfcTag);
        }
        return null;
    }
}
