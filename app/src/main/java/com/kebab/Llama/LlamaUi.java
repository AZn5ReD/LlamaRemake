package com.kebab.Llama;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.widget.TabHost;
import com.kebab.Activities.PeoplePickerActivity;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.CachedSetting;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.Llama.Content.LlamaMainContentProvider;
import com.kebab.RunnableArg;
import com.kebab.Tuple;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

public class LlamaUi extends TabActivity {
    Dialog _Dialog;
    boolean _ShownMessage = false;
    boolean _ShownMessagesIfNeeded = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logging.Init(this);
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, 1);

    }

    private void CheckApps2SdStatus() {
        if (!this._ShownMessage && Instances.Service != null && (getApplicationInfo().flags & 262144) != 0) {
            Instances.Service.HandleFriendlyError(Integer.valueOf(5));
        }
    }

    private boolean CheckAndHandleMessageIntent() {
        Intent activityIntent = getIntent();
        if (activityIntent != null && (activityIntent.getFlags() & 1048576) == 0) {
            String dataString = activityIntent.getDataString();
            if (dataString != null) {
                handleLlamaSharedIntent(dataString);
                return true;
            }
            final Integer clearNotificationId;
            final Integer confirmationMessageId;
            String intentMessage = activityIntent.getStringExtra(Constants.EXTRA_NOTIFICATION_MESSAGE);
            String intentTitle = activityIntent.getStringExtra(Constants.EXTRA_NOTIFICATION_TITLE);
            String eventName = activityIntent.getStringExtra(Constants.EXTRA_NOTIFICATION_EVENT_NAME);
            if (activityIntent.hasExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR)) {
                clearNotificationId = Integer.valueOf(activityIntent.getIntExtra(Constants.EXTRA_NOTIFICATION_ID_TO_CLEAR, 0));
            } else {
                clearNotificationId = null;
            }
            if (activityIntent.hasExtra(Constants.EXTRA_NOTIFICATION_CONFIRMATION_MESSAGE_ID)) {
                confirmationMessageId = Integer.valueOf(activityIntent.getIntExtra(Constants.EXTRA_NOTIFICATION_CONFIRMATION_MESSAGE_ID, 0));
            } else {
                confirmationMessageId = null;
            }
            if (intentMessage != null) {
                AlertDialog.Builder _Dialog = new Builder(this).setTitle(intentTitle).setMessage(intentMessage);
                if (confirmationMessageId != null) {
                    _Dialog.setPositiveButton(R.string.hrOkeyDoke, null);
                    _Dialog.setNegativeButton(R.string.hrNeverShowAgain, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ConfirmationMessages.SetAcceptedMessage(LlamaUi.this, confirmationMessageId.intValue());
                        }
                    });
                    final Tuple<Integer, RunnableArg<Activity>> customisedButton = ConfirmationMessages.GetCustomisedDialogButton(confirmationMessageId.intValue());
                    if (customisedButton != null) {
                        _Dialog.setNeutralButton(((Integer) customisedButton.Item1).intValue(), new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ((RunnableArg) customisedButton.Item2).Run(LlamaUi.this);
                            }
                        });
                    }
                } else if (clearNotificationId != null) {
                    _Dialog.setPositiveButton(R.string.hrClearNotification, new OnClickListener() {
                        @SuppressLint("WrongConstant")
                        public void onClick(DialogInterface dialog, int which) {
                            ((NotificationManager) LlamaUi.this.getSystemService("notification")).cancel(clearNotificationId.intValue());
                        }
                    });
                    _Dialog.setNegativeButton(R.string.hrLeaveNotification, null);
                } else {
                    _Dialog.setPositiveButton(R.string.hrOkeyDoke, null);
                }
                _Dialog.show();
                return true;
            } else if (eventName != null) {
                this._Dialog = EventConfirmationActivity.PrepareMessageForEvent(this, eventName, clearNotificationId.intValue());
                if (this._Dialog == null) {
                    return false;
                }
                this._Dialog.show();
                return true;
            }
        }
        return false;
    }

    private void handleLlamaSharedIntent(String dataString) {
        SocialLlama.HandleSharedUrl(this, dataString);
    }

    private void testRingtoneDialog() {
        new Builder(this).setMessage("Ringtone test dialog").setPositiveButton("Start Ringing", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(LlamaUi.this, LlamaService.class);
                i.setAction("android.intent.action.PHONE_STATE");
                i.putExtra("incoming_number", "3103");
                i.putExtra("state", TelephonyManager.EXTRA_STATE_RINGING);
                LlamaUi.this.startService(i);
                LlamaUi.this.testRingtoneDialog();
            }
        }).setNeutralButton("Set Incall", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(LlamaUi.this, LlamaService.class);
                i.setAction("android.intent.action.PHONE_STATE");
                i.putExtra("incoming_number", "3103");
                i.putExtra("state", TelephonyManager.EXTRA_STATE_OFFHOOK);
                LlamaUi.this.startService(i);
                LlamaUi.this.testRingtoneDialog();
            }
        }).setNegativeButton("Set Idle", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PeoplePickerActivity.StartPeopleIds(LlamaUi.this, 1234, new ArrayList());
            }
        });
    }

    public void UpdateCounters() {
        if (LlamaService.IsOnWorkerThread()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    LlamaUi.this.UpdateCounters();
                }
            });
        } else if (Instances.CurrentTab != null) {
            Instances.CurrentTab.UpdateDonateMessage();
        }
    }

    public void onDestroy() {
        Instances.UiActivity = null;
        Thread.currentThread().setPriority(1);
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        DateHelpers.Init(this);
        Thread.currentThread().setPriority(5);
        if (Instances.Service != null) {
            Instances.Service.UiCreatedOrUnpaused();
            Instances.Service.UnregisterNfcWatcherForce();
        }
        if (!this._ShownMessagesIfNeeded) {
            this._ShownMessagesIfNeeded = true;
            boolean messageAlreadyShown = CheckAndHandleMessageIntent();
            this._ShownMessage |= messageAlreadyShown;
            if (!messageAlreadyShown && ((Boolean) LlamaSettings.WriteToLlamaLog.GetValue(this)).booleanValue()) {
                AlertDialog.Builder builder = new Builder(this);
                builder.setMessage(R.string.hrLlamaLogWarning);
                builder.setCancelable(true).setPositiveButton(R.string.hrOkeyDokeExclamation, null).setNegativeButton(R.string.hrTurnItOff, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LlamaSettings.WriteToLlamaLog.SetValueAndCommit(LlamaUi.this, Boolean.valueOf(false), new CachedSetting[0]);
                    }
                });
                this._Dialog = builder.create();
                this._Dialog.show();
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this._Dialog != null) {
            this._Dialog.dismiss();
        }
        Thread.currentThread().setPriority(1);
    }

    public void OnServiceStarted() {
        CheckApps2SdStatus();
    }

    public static void ShowRingerChangeNoVolumesProfiles(Activity context) {
        if (Instances.Service != null) {
            Iterable profileNames = new ArrayList();
            for (Profile p : Instances.Service.GetProfiles()) {
                if (p.HasRingerChangeButNoVolume()) {
                    ((ArrayList) profileNames).add(p.Name);
                }
            }
            Helpers.ShowSimpleDialogMessage(context, IterableHelpers.ConcatenateString(profileNames, "\n"));
        }
    }

    public static void ShowIcsDifferentVolumesProfiles(Activity context) {
        if (Instances.Service != null) {
            Iterable profileNames = new ArrayList();
            for (Profile p : Instances.Service.GetProfiles()) {
                if (!p.HasSameRingerNotificationVolumes()) {
                    ((ArrayList) profileNames).add(p.Name);
                }
            }
            Helpers.ShowSimpleDialogMessage(context, IterableHelpers.ConcatenateString(profileNames, "\n"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            LocalisationInit.Init(getBaseContext(), false);

//        super.onCreate(savedInstanceState);
            Thread.currentThread().setPriority(5);
            DateHelpers.Init(this);
            setContentView(R.layout.main);
            if (((Boolean) LlamaSettings.LlamaWasExitted.GetValue(this)).booleanValue()) {
                LlamaSettings.LlamaWasExitted.SetValueAndCommit(this, Boolean.valueOf(false), new CachedSetting[0]);
                if (((Boolean) LlamaSettings.AcceptedDisclaimerMessage.GetValue(this)).booleanValue()) {
                    new Builder(this).setMessage(R.string.hrServiceRestarted).setPositiveButton("OK", null).show();
                }
            }
            Instances.StartService(getApplicationContext());
            Resources res = getResources();
            TabHost tabHost = getTabHost();
            tabHost.addTab(tabHost.newTabSpec("areas").setIndicator(getString(R.string.hrTabAreas), res.getDrawable(R.drawable.ic_tab_areas)).setContent(new Intent().setClass(this, AreasActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("events").setIndicator(getString(R.string.hrTabEvents), res.getDrawable(R.drawable.ic_tab_events)).setContent(new Intent().setClass(this, EventsActivity.class)));
            tabHost.addTab(tabHost.newTabSpec(LlamaMainContentProvider.PATH_SEGMENT_PROFILES).setIndicator(getString(R.string.hrTabProfiles), res.getDrawable(R.drawable.ic_tab_profiles)).setContent(new Intent().setClass(this, ProfilesActivity.class)));
            tabHost.addTab(tabHost.newTabSpec("cells").setIndicator(getString(R.string.hrTabRecent), res.getDrawable(R.drawable.ic_tab_cells)).setContent(new Intent().setClass(this, CellsActivity.class)));
            if (VERSION.SDK_INT >= 11) {
                int i = 0;
                while (i < tabHost.getTabWidget().getChildCount()) {
                    try {
                        tabHost.getTabWidget().getChildAt(i).setPadding(0, 0, 0, 0);
                        i++;
                    } catch (Exception e) {
                    }
                }
            }
            if (Helpers.IsOnMasterLlamasPhone(this)) {
                tabHost.setCurrentTab(2);
            } else {
                tabHost.setCurrentTab(2);
            }
            UpdateCounters();
            Instances.UiActivity = this;
    }
}
