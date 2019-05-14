package com.kebab.Llama.EventConditions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceActivity;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Helpers;
import com.kebab.Llama.AppListPreference;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.EventMeta;
import com.kebab.Llama.EventMeta.ConditionStaticInitter2;
import com.kebab.Llama.EventTrigger;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.Llama.SimplePackageInfo;
import com.kebab.Llama.StateChange;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.ResultRegisterableActivity;
import java.io.IOException;

public class ActiveAppCondition extends EventCondition<ActiveAppCondition> {
    public static String MY_ID;
    public static int[] MY_TRIGGERS;
    public static int MY_TRIGGER_OFF;
    public static int MY_TRIGGER_ON;
    String _ActivityName;
    String _FriendlyName;
    boolean _IsActive;
    String _PackageName;

    static {
        EventMeta.InitCondition(EventFragment.ACTIVE_APP_CONDITION, new ConditionStaticInitter2() {
            public void UpdateStatics(String id, int[] triggers, int triggerOn, int triggerOff) {
                ActiveAppCondition.MY_ID = id;
                ActiveAppCondition.MY_TRIGGERS = triggers;
                ActiveAppCondition.MY_TRIGGER_ON = triggerOn;
                ActiveAppCondition.MY_TRIGGER_OFF = triggerOff;
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

    public ActiveAppCondition(boolean isActive, String packageName, String activityName, String friendlyName) {
        this._IsActive = isActive;
        this._PackageName = packageName;
        this._FriendlyName = friendlyName;
        this._ActivityName = activityName;
    }

    public int TestCondition(StateChange state, Context context, Ref<EventTrigger> ref) {
        if (this._IsActive) {
            if (state.TriggerType == MY_TRIGGER_ON) {
                if (state.PackageName.equalsIgnoreCase(this._PackageName)) {
                    return 2;
                }
            } else if (state.PackageName.equalsIgnoreCase(this._PackageName)) {
                return 1;
            }
        } else if (state.TriggerType == MY_TRIGGER_OFF) {
            if (state.PackageNameExiting != null && state.PackageNameExiting.equalsIgnoreCase(this._PackageName)) {
                return 2;
            }
            if (!state.PackageName.equalsIgnoreCase(this._PackageName)) {
                return 1;
            }
        } else if (!state.PackageName.equalsIgnoreCase(this._PackageName)) {
            return 1;
        }
        return 0;
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        if (this._IsActive) {
            sb.append(String.format(context.getString(R.string.hrWhenTheCurrentAppIs1), new Object[]{this._FriendlyName}));
            return;
        }
        sb.append(String.format(context.getString(R.string.hrWhenTheCurrentAppIsNot1), new Object[]{this._FriendlyName}));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 4;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._IsActive ? "1" : "0").append("|");
        sb.append(LlamaStorage.SimpleEscape(this._PackageName)).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._ActivityName)).append("|");
        sb.append(LlamaStorage.SimpleEscape(this._FriendlyName));
    }

    public static ActiveAppCondition CreateFrom(String[] parts, int currentPart) {
        return new ActiveAppCondition(parts[currentPart + 1].equals("1"), LlamaStorage.SimpleUnescape(parts[currentPart + 2]), LlamaStorage.SimpleUnescape(parts[currentPart + 3]), LlamaStorage.SimpleUnescape(parts[currentPart + 4]));
    }

    public PreferenceEx<ActiveAppCondition> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<ActiveAppCondition>((ResultRegisterableActivity) context, context.getString(R.string.hrConditionActiveApp), this) {
            String _DialogSelectedFriendlyName;
            String _DialogSelectedPackageName;
            ActiveAppCondition _DialogSelectedValue;

            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, ActiveAppCondition value) {
                if (value == null) {
                    return "";
                }
                SpannableStringBuilder sb = new SpannableStringBuilder();
                try {
                    value.AppendConditionSimple(context, sb);
                } catch (IOException e) {
                }
                Helpers.CapitaliseFirstLetter(sb);
                return sb.toString();
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(final ResultRegisterableActivity host, ActiveAppCondition existingValue, final GotResultHandler<ActiveAppCondition> gotResultHandler) {
                if (this._DialogSelectedValue == null) {
                    this._DialogSelectedValue = existingValue;
                }
                String[] yesNo = preferenceActivity.getResources().getStringArray(R.array.activeAppStatuses);
                View view = View.inflate(host.GetActivity(), R.layout.active_app_condition, null);
                final Button appButton = (Button) view.findViewById(R.id.appButton);
                final Spinner statusPicker = (Spinner) view.findViewById(R.id.spinner);
                if (this._DialogSelectedValue != null && this._DialogSelectedValue._PackageName.length() > 0) {
                    this._DialogSelectedPackageName = existingValue._PackageName;
                    this._DialogSelectedFriendlyName = existingValue._FriendlyName;
                    appButton.setText(existingValue._FriendlyName.length() == 0 ? existingValue._PackageName : existingValue._FriendlyName);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter(preferenceActivity, 17367048, yesNo);
                adapter.setDropDownViewResource(17367049);
                statusPicker.setAdapter(adapter);
                statusPicker.setSelection(existingValue._IsActive ? 0 : 1);
                appButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View paramView) {
                        AnonymousClass2.this.ShowList(host, new SimplePackageInfo(AnonymousClass2.this._DialogSelectedFriendlyName, AnonymousClass2.this._DialogSelectedPackageName), new GotResultHandler<SimplePackageInfo>() {
                            public void HandleResult(SimplePackageInfo result) {
                                AnonymousClass2.this._DialogSelectedFriendlyName = result.getFriendlyName();
                                AnonymousClass2.this._DialogSelectedPackageName = result.getPackageName();
                                appButton.setText(AnonymousClass2.this._DialogSelectedFriendlyName.length() == 0 ? AnonymousClass2.this._DialogSelectedPackageName : AnonymousClass2.this._DialogSelectedFriendlyName);
                            }
                        });
                    }
                });
                AlertDialog dialog = new Builder(host.GetActivity()).setPositiveButton(R.string.hrOk, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gotResultHandler.HandleResult(new ActiveAppCondition(statusPicker.getSelectedItemPosition() == 0, AnonymousClass2.this._DialogSelectedPackageName, "", AnonymousClass2.this._DialogSelectedFriendlyName));
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.hrCancel, null).setView(view).create();
                dialog.setOwnerActivity(host.GetActivity());
                dialog.show();
            }

            /* Access modifiers changed, original: 0000 */
            public void ShowList(ResultRegisterableActivity context, SimplePackageInfo existingValue, GotResultHandler<SimplePackageInfo> onItemChosen) {
                new AppListPreference<SimplePackageInfo>(context, context.GetActivity().getString(R.string.hrConditionAppNotification), existingValue, true, context.GetActivity().getString(R.string.hrGettingApplicationNames)) {
                    /* Access modifiers changed, original: protected */
                    public SimplePackageInfo ConvertListItemToResult(SimplePackageInfo listItem) {
                        return listItem;
                    }

                    /* Access modifiers changed, original: protected */
                    public String GetHumanReadableValue(Context context, SimplePackageInfo value) {
                        return value.getFriendlyName();
                    }

                    /* Access modifiers changed, original: protected */
                    public boolean IsSelectedItemEqualToListItem(SimplePackageInfo existingSelectedValue, SimplePackageInfo listItem) {
                        if (existingSelectedValue.getPackageName() == null) {
                            return false;
                        }
                        return existingSelectedValue.getPackageName().equals(listItem.getPackageName());
                    }
                }.ShowDialog(onItemChosen);
            }
        };
    }

    public String GetIsValidError(Context context) {
        return null;
    }
}
