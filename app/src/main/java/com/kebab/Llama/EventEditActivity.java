package com.kebab.Llama;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.Helpers;
import com.kebab.ListWithHelpDialog;
import com.kebab.ListWithHelpDialog.OnListWithHelpClickListener;
import com.kebab.Llama.EventActions.EventAction;
import com.kebab.Llama.EventConditions.EventCondition;
import com.kebab.Llama.EventConditions.GroupOfEventConditions;
import com.kebab.Llama.EventConditions.PhoneRebootCondition;
import com.kebab.Llama.Instances.HelloablePreferenceActivity;
import com.kebab.OnPreferenceClick;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.Helper;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import com.kebab.SeekBarPreference;
import com.kebab.Tuple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class EventEditActivity extends HelloablePreferenceActivity implements ResultRegisterableActivity {
    PreferenceGroup _ActionsGroup;
    HashMap<Integer, Tuple<ResultCallback, Object>> _ActivityRequests = new HashMap();
    PreferenceScreen _Advanced;
    CheckBoxPreference _AllowRetrigger;
    boolean _AnonymousMode = false;
    CheckBoxPreference _CancelDelayedEvent;
    boolean _ClickToRemoveMode = false;
    boolean _ConditionEditMode = false;
    boolean _ConditionOrMode = false;
    PreferenceGroup _ConditionsGroup;
    CheckBoxPreference _DelayEvent;
    SeekBarPreference<?> _DelayEventMinutes;
    SeekBarPreference<?> _DelayEventSeconds;
    CheckBoxPreference _DialogConfirmation;
    CheckBoxPreference _EventEnabled;
    EventNamePreference _EventName;
    Event _EventToEdit;
    boolean _IsEventEdit;
    String _OldName;
    HashSet<Runnable> _OnDestoryRunnables = new HashSet();
    SeekBarPreference<?> _QueueEventMinutes;
    SeekBarPreference<?> _QueueEventSeconds;
    boolean _QueuedEventMode = false;
    private Button _RemoveButton;
    OnPreferenceClick _RemoveHandler;
    CheckBoxPreference _RepeatEvent;
    SeekBarPreference<?> _RepeatEventMinutes;
    CheckBoxPreference _RequireConfirmation;
    int requestCode = Constants.REQUEST_CODE_CUSTOM_START_OFFSET;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._EventToEdit = (Event) getIntent().getExtras().get("Event");
        this._AnonymousMode = getIntent().getBooleanExtra(Constants.EXTRA_ANONYMOUS_EVENT, false);
        this._QueuedEventMode = getIntent().getBooleanExtra(Constants.EXTRA_QUEUED_EVENT, false);
        this._ConditionEditMode = getIntent().getBooleanExtra(Constants.EXTRA_CONDITION_EDIT_MODE, false);
        this._ConditionOrMode = getIntent().getBooleanExtra(Constants.EXTRA_CONDITION_OR_MODE, true);
        this._IsEventEdit = getIntent().getBooleanExtra(Constants.EXTRA_IS_EDIT, false);
        boolean allowRetriggerValue = getIntent().getBooleanExtra(Constants.EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME, false);
        if (savedInstanceState != null) {
            Event savedEvent = (Event) savedInstanceState.getParcelable("Event");
            if (savedEvent != null) {
                this._EventToEdit = savedEvent;
            }
            if (savedInstanceState.containsKey(Constants.EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME)) {
                allowRetriggerValue = savedInstanceState.getBoolean(Constants.EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME);
            }
        }
        setContentView(R.layout.editevent);
        addPreferencesFromResource(R.xml.edit_event);
        this._OldName = this._EventToEdit.Name;
        this._EventName = (EventNamePreference) findPreference("eventName");
        this._EventName.Activity = this;
        this._EventEnabled = (CheckBoxPreference) findPreference("eventEnabled");
        this._AllowRetrigger = (CheckBoxPreference) findPreference("allowRetrigger");
        this._Advanced = (PreferenceScreen) findPreference("advanced");
        this._RepeatEvent = (CheckBoxPreference) findPreference("repeatEvent");
        this._RepeatEventMinutes = (SeekBarPreference) findPreference("repeatEventMinutes");
        this._DelayEvent = (CheckBoxPreference) findPreference("delayEvent");
        this._DelayEventMinutes = (SeekBarPreference) findPreference("delayEventMinutes");
        this._DelayEventSeconds = (SeekBarPreference) findPreference("delayEventSeconds");
        this._CancelDelayedEvent = (CheckBoxPreference) findPreference("cancelDelayedEvent");
        this._RequireConfirmation = (CheckBoxPreference) findPreference("requireConfirmation");
        this._DialogConfirmation = (CheckBoxPreference) findPreference("dialogConfirmation");
        this._QueueEventMinutes = (SeekBarPreference) findPreference("queueEventMinutes");
        this._QueueEventSeconds = (SeekBarPreference) findPreference("queueEventSeconds");
        this._RemoveHandler = new OnPreferenceClick() {
            public boolean CanShowDialog(Preference arg0) {
                if (!EventEditActivity.this._ClickToRemoveMode) {
                    return true;
                }
                if (EventEditActivity.this._ActionsGroup != null) {
                    EventEditActivity.this._ActionsGroup.removePreference(arg0);
                }
                if (EventEditActivity.this._ConditionsGroup != null) {
                    EventEditActivity.this._ConditionsGroup.removePreference(arg0);
                }
                EventEditActivity.this._ClickToRemoveMode = false;
                EventEditActivity.this._RemoveButton.setText(R.string.hrRemove);
                return false;
            }
        };
        if (this._AnonymousMode) {
            this._EventName.setTitle(R.string.hrLlamaShortcutName);
            this._EventName.ShowGroupName = false;
            getPreferenceScreen().removePreference(this._EventEnabled);
            getPreferenceScreen().removePreference(this._Advanced);
            getPreferenceScreen().removePreference(this._QueueEventMinutes);
            getPreferenceScreen().removePreference(this._QueueEventSeconds);
        } else {
            this._ConditionsGroup = new PreferenceCategory(this);
            if (this._ConditionEditMode && this._ConditionOrMode) {
                this._ConditionsGroup.setTitle(R.string.hrConditionsMatchAny);
            } else {
                this._ConditionsGroup.setTitle(R.string.hrConditionsMatchAll);
            }
            getPreferenceScreen().addPreference(this._ConditionsGroup);
            CharSequence string = this._IsEventEdit ? getString(R.string.hrLlamaDashEditingEvent) : this._ConditionEditMode ? this._ConditionOrMode ? getString(R.string.hrLlamaDashOrConditions) : getString(R.string.hrLlamaDashAndConditions) : getString(R.string.hrLlamaDashNewEvent);
            setTitle(string);
            if (this._QueuedEventMode) {
                getPreferenceScreen().removePreference(this._EventEnabled);
                getPreferenceScreen().removePreference((PreferenceScreen) findPreference("advanced"));
                this._QueueEventMinutes.setValue(getIntent().getIntExtra(Constants.EXTRA_EVENT_QUEUE_DELAY, 0));
                this._QueueEventSeconds.setValue(getIntent().getIntExtra(Constants.EXTRA_EVENT_QUEUE_DELAY_SECONDS, 0));
            } else {
                getPreferenceScreen().removePreference(this._QueueEventMinutes);
                getPreferenceScreen().removePreference(this._QueueEventSeconds);
            }
        }
        if (this._ConditionEditMode) {
            getPreferenceScreen().removePreference(this._EventEnabled);
            getPreferenceScreen().removePreference(this._EventName);
            getPreferenceScreen().removePreference(this._Advanced);
            ((Button) findViewById(R.id.testButton)).setVisibility(8);
            ((Button) findViewById(R.id.addActionButton)).setVisibility(8);
            if (this._ConditionOrMode) {
                this._AllowRetrigger.setChecked(allowRetriggerValue);
            } else {
                getPreferenceScreen().removePreference(this._AllowRetrigger);
            }
        } else {
            this._ActionsGroup = new PreferenceCategory(this);
            this._ActionsGroup.setTitle(R.string.hrActions);
            getPreferenceScreen().addPreference(this._ActionsGroup);
            getPreferenceScreen().removePreference(this._AllowRetrigger);
        }
        this._EventName.setEventName(this._EventToEdit.Name);
        this._EventName.setEventGroup(this._EventToEdit.GroupName);
        this._EventEnabled.setChecked(this._EventToEdit.Enabled);
        this._RepeatEvent.setChecked(this._EventToEdit.RepeatMinutesInterval > 0);
        this._RepeatEventMinutes.setValue(Math.max(1, this._EventToEdit.RepeatMinutesInterval));
        CheckBoxPreference checkBoxPreference = this._DelayEvent;
        boolean z = this._EventToEdit.DelayMinutes > 0 || this._EventToEdit.DelaySeconds > 0;
        checkBoxPreference.setChecked(z);
        this._DelayEventMinutes.setValue(Math.max(0, this._EventToEdit.DelayMinutes));
        this._DelayEventSeconds.setValue(Math.max(0, this._EventToEdit.DelaySeconds));
        this._CancelDelayedEvent.setChecked(this._EventToEdit.CancelDelayedIfFailed);
        this._RequireConfirmation.setChecked(this._EventToEdit.ConfirmationStatus != 0);
        this._DialogConfirmation.setChecked(this._EventToEdit.ConfirmationDialog);
        Iterator i$ = this._EventToEdit._Conditions.iterator();
        while (i$.hasNext()) {
            addFragmentToGroup((EventCondition) i$.next(), false);
        }
        i$ = this._EventToEdit._Actions.iterator();
        while (i$.hasNext()) {
            addFragmentToGroup((EventAction) i$.next(), false);
        }
        Preference moreInfo = findPreference("moreInfo");
        if (moreInfo != null) {
            moreInfo.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    Helpers.ShowSimpleDialogMessage(EventEditActivity.this, EventEditActivity.this.getString(R.string.hrEventAdvancedDetailedDescription));
                    return true;
                }
            });
        }
        Preference allowedTriggers = findPreference("allowedTriggers");
        if (allowedTriggers != null) {
            allowedTriggers.setOnPreferenceClickListener(new OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    EventEditActivity.this.ShowEventTriggerFiltering();
                    return true;
                }
            });
        }
        Button addConditionButton = (Button) findViewById(R.id.addConditionButton);
        if (this._AnonymousMode) {
            addConditionButton.setVisibility(8);
        } else {
            addConditionButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    EventEditActivity.this.addCondition();
                }
            });
        }
        ((Button) findViewById(R.id.addActionButton)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EventEditActivity.this.addAction();
            }
        });
        this._RemoveButton = (Button) findViewById(R.id.removeButton);
        this._RemoveButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (EventEditActivity.this._ClickToRemoveMode) {
                    EventEditActivity.this._RemoveButton.setText(R.string.hrRemove);
                    EventEditActivity.this._ClickToRemoveMode = false;
                    return;
                }
                EventEditActivity.this._RemoveButton.setText(R.string.hrCancelRemove);
                Helpers.ShowTip(EventEditActivity.this, EventEditActivity.this.getString(R.string.hrNowTapAConditionOrActionToRemoveIt));
                EventEditActivity.this._ClickToRemoveMode = true;
            }
        });
        ((Button) findViewById(R.id.testButton)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                EventEditActivity.this.testActions();
            }
        });
        Instances.StartService(this);
    }

    private static void RecursiveEventConditionAdd(EventCondition c, ArrayList<Tuple<String, EventCondition>> conditions, HashSet<String> alreadyAdded) {
        if (c instanceof GroupOfEventConditions) {
            Iterator i$ = ((GroupOfEventConditions) c)._Conditions.iterator();
            while (i$.hasNext()) {
                RecursiveEventConditionAdd((EventCondition) i$.next(), conditions, alreadyAdded);
            }
        } else if (alreadyAdded.add(c.getId())) {
            conditions.add(Tuple.Create(((EventMeta) EventMeta.All.get(c.getId())).Name, c));
        }
    }

    /* Access modifiers changed, original: protected */
    public void ShowEventTriggerFiltering() {
        updateEventFromDialog();
        final ArrayList<Tuple<String, EventCondition>> conditions = new ArrayList();
        HashSet<String> alreadyAdded = new HashSet();
        Iterator i$ = this._EventToEdit._Conditions.iterator();
        while (i$.hasNext()) {
            RecursiveEventConditionAdd((EventCondition) i$.next(), conditions, alreadyAdded);
        }
        if (conditions.size() == 0) {
            Helpers.ShowSimpleDialogMessage(this, getString(R.string.hrEventFilteringPleaseAddSomeConditions));
            return;
        }
        Collections.sort(conditions, new Comparator<Tuple<String, EventCondition>>() {
            public int compare(Tuple<String, EventCondition> x, Tuple<String, EventCondition> y) {
                return ((String) x.Item1).compareToIgnoreCase((String) y.Item1);
            }
        });
        String[] conditionNames = new String[conditions.size()];
        final boolean[] checkedItems = new boolean[conditions.size()];
        for (int i = 0; i < conditions.size(); i++) {
            Tuple<String, EventCondition> condition = (Tuple) conditions.get(i);
            conditionNames[i] = (String) condition.Item1;
            checkedItems[i] = this._EventToEdit.IsConditionTriggerAllowed((EventTrigger) condition.Item2);
        }
        new Builder(this).setTitle(R.string.hrAllowedTriggers).setMultiChoiceItems(conditionNames, checkedItems, new OnMultiChoiceClickListener() {
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        }).setPositiveButton(R.string.hrOk, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < conditions.size(); i++) {
                    if (!checkedItems[i]) {
                        sb.append(",").append(((EventCondition) ((Tuple) conditions.get(i)).Item2).getId());
                    }
                }
                if (sb.length() > 0) {
                    sb.append(",");
                    EventEditActivity.this._EventToEdit.ProhibitedTriggers = sb.toString();
                } else {
                    EventEditActivity.this._EventToEdit.ProhibitedTriggers = "";
                }
                arg0.dismiss();
            }
        }).setNegativeButton(R.string.hrCancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        }).show();
    }

    /* Access modifiers changed, original: protected */
    public void testActions() {
        updateEventFromDialog();
        if (isValidOrShowError()) {
            Instances.Service.RunSingleEvent(this._EventToEdit, false, (Activity) this, EventMeta.EventEditorTest, 3);
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void initPreference(PreferenceEx<?> pref) {
        pref.setOnPreferenceClick(this._RemoveHandler);
    }

    /* Access modifiers changed, original: 0000 */
    public void addFragmentToGroup(EventFragment<?> ef, boolean showDialog) {
        PreferenceEx<?> pref = ef.CreatePreference(this);
        initPreference(pref);
        final Preference ppref = (Preference) pref;
        (ef.IsCondition() ? this._ConditionsGroup : this._ActionsGroup).addPreference(ppref);
        if (showDialog) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Helpers.ScrollToPreference(EventEditActivity.this, ppref);
                }
            }, 40);
            pref.onClick();
        }
    }

    private void addCondition() {
        final ArrayList<EventMeta> conditions = EventMeta.GetConditions();
        if (!((Boolean) LlamaSettings.ShowAllActionsAndConditions.GetValue(this)).booleanValue()) {
            if (!this._ConditionEditMode) {
                FilterEventFragments(conditions);
            } else if (!this._ConditionOrMode) {
                FilterEventFragments(conditions);
            }
        }
        ListWithHelpDialog.Show(this, getString(R.string.hrSelectConditionToTriggerEvent), GetNamesArray(conditions), new OnListWithHelpClickListener() {
            public void OnHelpClick(int position) {
                EventEditActivity.this.ShowHelpForEventMeta((EventMeta) conditions.get(position));
            }

            public void OnItemSelected(int position) {
                WrappedCreator<?, ?> wc = ((EventMeta) conditions.get(position)).Create;
                String warningMessage = wc.GetWarningMessage(EventEditActivity.this);
                if (warningMessage != null) {
                    Helpers.ShowTip(EventEditActivity.this, warningMessage);
                }
                EventEditActivity.this.addFragmentToGroup(wc.Create(), true);
            }
        });
    }

    private void addAction() {
        final ArrayList<EventMeta> actions = EventMeta.GetActions();
        if (!((Boolean) LlamaSettings.ShowAllActionsAndConditions.GetValue(this)).booleanValue()) {
            FilterEventFragments(actions);
        }
        ListWithHelpDialog.Show(this, getString(R.string.hrSelectActionToPerformWhenTriggered), GetNamesArray(actions), new OnListWithHelpClickListener() {
            public void OnHelpClick(int position) {
                EventEditActivity.this.ShowHelpForEventMeta((EventMeta) actions.get(position));
            }

            public void OnItemSelected(int position) {
                final WrappedCreator<?, ?> wc = ((EventMeta) actions.get(position)).Create;
                String warningMessage = wc.GetWarningMessage(EventEditActivity.this);
                if (warningMessage == null) {
                    EventEditActivity.this.addFragmentToGroup(wc.Create(), true);
                } else if (wc.IsHeftyWarningMessage()) {
                    Helpers.ShowSimpleDialogMessage(EventEditActivity.this, warningMessage, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            EventEditActivity.this.addFragmentToGroup(wc.Create(), true);
                        }
                    });
                } else {
                    Helpers.ShowTip(EventEditActivity.this, warningMessage);
                    EventEditActivity.this.addFragmentToGroup(wc.Create(), true);
                }
            }
        });
    }

    private void ShowHelpForEventMeta(EventMeta meta) {
        new Builder(this).setTitle(meta.Name).setMessage(meta.HelpDescriptionResourceId).setPositiveButton(R.string.hrCool, null).show();
    }

    private void FilterEventFragments(ArrayList<EventMeta> eventMetas) {
        updateEventFromDialog();
        HashSet<String> existingFragmentIDs = new HashSet();
        Iterator i$ = this._EventToEdit._Conditions.iterator();
        while (i$.hasNext()) {
            existingFragmentIDs.add(((EventCondition) i$.next()).getId());
        }
        i$ = this._EventToEdit._Actions.iterator();
        while (i$.hasNext()) {
            existingFragmentIDs.add(((EventAction) i$.next()).getId());
        }
        for (int i = eventMetas.size() - 1; i >= 0; i--) {
            EventMeta newMeta = (EventMeta) eventMetas.get(i);
            String[] disallowedCoFragments = newMeta.DisallowedCoFragments;
            if (disallowedCoFragments != null) {
                if (disallowedCoFragments.length != 0) {
                    for (String coexistingId : disallowedCoFragments) {
                        if (existingFragmentIDs.contains(coexistingId)) {
                            eventMetas.remove(i);
                            break;
                        }
                    }
                } else if (existingFragmentIDs.contains(newMeta.Id)) {
                    eventMetas.remove(i);
                }
            }
        }
    }

    private String[] GetNamesArray(ArrayList<EventMeta> metas) {
        String[] result = new String[metas.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((EventMeta) metas.get(i)).Name;
        }
        return result;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyUp(keyCode, event);
        }
        executeDone(true);
        return true;
    }

    private void IteratePreferences(PreferenceGroup group, Event event) {
        int prefCount = group.getPreferenceCount();
        for (int i = 0; i < prefCount; i++) {
            EventFragment<?> fragment = (EventFragment) ((PreferenceEx) group.getPreference(i)).GetValueEx();
            if (fragment.IsCondition()) {
                event._Conditions.add((EventCondition) fragment);
            } else {
                event._Actions.add((EventAction) fragment);
            }
        }
    }

    private void updateEventFromDialog() {
        int i = 0;
        this._EventToEdit.Name = this._EventName.getEventName();
        this._EventToEdit.GroupName = this._EventName.getGroupName();
        this._EventToEdit.Enabled = this._EventEnabled.isChecked();
        if (this._RepeatEvent.isChecked()) {
            this._EventToEdit.RepeatMinutesInterval = this._RepeatEventMinutes.getValue();
        } else {
            this._EventToEdit.RepeatMinutesInterval = 0;
        }
        if (this._DelayEvent.isChecked()) {
            this._EventToEdit.DelayMinutes = this._DelayEventMinutes.getValue();
            this._EventToEdit.DelaySeconds = this._DelayEventSeconds.getValue();
        } else {
            this._EventToEdit.DelayMinutes = 0;
            this._EventToEdit.DelaySeconds = 0;
        }
        this._EventToEdit.CancelDelayedIfFailed = this._CancelDelayedEvent.isChecked();
        Event event = this._EventToEdit;
        if (this._RequireConfirmation.isChecked()) {
            i = 1;
        }
        event.ConfirmationStatus = i;
        this._EventToEdit.ConfirmationDialog = this._DialogConfirmation.isChecked();
        this._EventToEdit._Actions.clear();
        this._EventToEdit._Conditions.clear();
        if (!this._AnonymousMode) {
            IteratePreferences(this._ConditionsGroup, this._EventToEdit);
        }
        if (!this._ConditionEditMode) {
            IteratePreferences(this._ActionsGroup, this._EventToEdit);
        }
    }

    private void executeDone(boolean success) {
        if (success) {
            updateEventFromDialog();
            if (isValidOrShowError() && isFineWithNoLastMinuteWarnings()) {
                SaveEventAndFinish();
                return;
            }
            return;
        }
        setResult(0, new Intent());
        finish();
    }

    private void SaveEventAndFinish() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("Event", this._EventToEdit);
        resultIntent.putExtra(Constants.OLD_NAME, this._OldName);
        resultIntent.putExtra(Constants.EXTRA_EVENT_QUEUE_DELAY, this._QueueEventMinutes.getValue());
        resultIntent.putExtra(Constants.EXTRA_EVENT_QUEUE_DELAY_SECONDS, this._QueueEventSeconds.getValue());
        resultIntent.putExtra(Constants.EXTRA_EVENT_OR_SHOULD_TRIGGER_EVERY_TIME, this._AllowRetrigger.isChecked());
        setResult(-1, resultIntent);
        finish();
    }

    private boolean isValidOrShowError() {
        String error;
        StringBuffer sb = new StringBuffer();
        Iterator i$ = this._EventToEdit._Conditions.iterator();
        while (i$.hasNext()) {
            error = ((EventCondition) i$.next()).GetIsValidError(this);
            if (error != null) {
                sb.append("-").append(error).append(10);
            }
        }
        i$ = this._EventToEdit._Actions.iterator();
        while (i$.hasNext()) {
            error = ((EventAction) i$.next()).GetIsValidError(this);
            if (error != null) {
                sb.append("-").append(error).append(10);
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
            sb.insert(0, "\n\n");
            sb.insert(0, getString(R.string.hrYourEventIsNotCompleteColon));
            sb.append("\n\n");
            sb.append(getString(R.string.hrPleaseFixTheseErrorsOrRemoveTheOffendingConditionsSlashActions));
            new Builder(this).setMessage(sb).setPositiveButton(R.string.hrIWillIPromise, null).setTitle(R.string.hrProblems).create().show();
            return false;
        } else if (!this._QueuedEventMode || !getString(R.string.hrQueuedEventDefaultName).equals(this._EventToEdit.Name)) {
            return true;
        } else {
            new Builder(this).setMessage(R.string.hrQueuedEventChangeDefaultNameTip).setPositiveButton(R.string.hrIWillIPromise, null).setTitle(R.string.hrProblems).create().show();
            return false;
        }
    }

    private boolean isFineWithNoLastMinuteWarnings() {
        if (this._EventToEdit.RepeatMinutesInterval <= 0 || this._EventToEdit._Conditions.size() != 0) {
            return true;
        }
        new Builder(this).setMessage(R.string.hrRepeatingEventWithNoConditionsWarning).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EventEditActivity.this._EventToEdit._Conditions.add(new PhoneRebootCondition(true));
                EventEditActivity.this.SaveEventAndFinish();
                dialog.dismiss();
            }
        }).setNeutralButton(R.string.hrNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EventEditActivity.this.SaveEventAndFinish();
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.hrCancel, null).setTitle(R.string.hrProblems).create().show();
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Helper.HandleOnDestroy(this._OnDestoryRunnables);
        updateEventFromDialog();
        outState.putParcelable("Event", this._EventToEdit);
    }

    public void onResume() {
        Thread.currentThread().setPriority(5);
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        Thread.currentThread().setPriority(1);
    }

    public void RegisterActivityResult(Intent intent, ResultCallback runnable, Object extraStateInfo) {
        int thisRequestCode = this.requestCode;
        this.requestCode = thisRequestCode + 1;
        this._ActivityRequests.put(Integer.valueOf(thisRequestCode), Tuple.Create(runnable, extraStateInfo));
        startActivityForResult(intent, thisRequestCode);
    }

    /* Access modifiers changed, original: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Tuple<ResultCallback, Object> handler = (Tuple) this._ActivityRequests.get(Integer.valueOf(requestCode));
        if (handler != null && handler.Item1 != null) {
            ((ResultCallback) handler.Item1).HandleResult(resultCode, data, handler.Item2);
        }
    }

    public Activity GetActivity() {
        return this;
    }

    public void AddBeforeOnDestroyHandler(Runnable runnable) {
        Helper.AddBeforeOnDestroyHandler(this._OnDestoryRunnables, runnable);
    }

    public void RemoveBeforeOnDestroyHandler(Runnable runnable) {
        Helper.ClearOnDestroyHandler(this._OnDestoryRunnables, runnable);
    }
}
