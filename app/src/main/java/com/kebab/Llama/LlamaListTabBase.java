package com.kebab.Llama;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.ApiCompat.DeviceCompat;
import com.kebab.ArrayHelpers;
import com.kebab.CachedBooleanSetting;
import com.kebab.CachedSetting;
import com.kebab.DateHelpers;
import com.kebab.Helpers;
import com.kebab.Llama.Instances.HelloableListActivity;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public abstract class LlamaListTabBase extends HelloableListActivity {
    LlamaListTabBaseImpl _Impl;

    public static abstract class LlamaListTabBaseImpl {
        public static String[] _CommonTips;
        static Random _Random = new Random();
        static boolean _StopServiceSoon;
        Activity _Activity;
        AlertDialog _DisclaimerDialog;
        int _HelpTextId;
        boolean _IsDonateMessage;
        int _LayoutId;
        TextView _Message;
        CachedBooleanSetting _SeenHelpSettings;
        boolean disclaimerShowed;

        public abstract String[] InitAndGetTabRandomTips();

        public abstract void Update();

        public abstract CharSequence[] getContextSensitiveMenuItems();

        public abstract boolean handleContextSensitiveItem(CharSequence charSequence);

        public LlamaListTabBaseImpl(int layoutId, CachedBooleanSetting seenHelpSettings, int helpTextId) {
            this._SeenHelpSettings = seenHelpSettings;
            this._HelpTextId = helpTextId;
            this._LayoutId = layoutId;
        }

        /* Access modifiers changed, original: protected */
        public String getString(int resId) {
            return this._Activity.getString(resId);
        }

        public void onCreate(Bundle savedInstanceState) {
            LocalisationInit.ContextConfigInit(this._Activity);
            this._Activity.setContentView(this._LayoutId);
            LinearLayout view = (LinearLayout) this._Activity.findViewById(R.id.llama_bottom_buttons_holder);
            if (view != null && DeviceCompat.HasStupidMenuButtonInBottomRight(this._Activity)) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), (int) TypedValue.applyDimension(1, 30.0f, this._Activity.getResources().getDisplayMetrics()), view.getPaddingBottom());
            }
            this._Message = (TextView) this._Activity.findViewById(R.id.message);
            this._Message.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    LlamaListTabBaseImpl.this.UpdateRandomTip();
                }
            });
            Drawable background = ((ImageButton) this._Activity.findViewById(R.id.helpButton)).getBackground();
            ((ImageButton) this._Activity.findViewById(R.id.helpButton)).setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    CharSequence[] baseItems2 = new CharSequence[]{LlamaListTabBaseImpl.this.getString(R.string.hrLlamaInstructions), LlamaListTabBaseImpl.this.getString(R.string.hrLlamaAndKebabAppsWebsite), LlamaListTabBaseImpl.this.getString(R.string.hrSendEmail), LlamaListTabBaseImpl.this.getString(R.string.hrSettings), LlamaListTabBaseImpl.this.getString(R.string.hrImportExportData), LlamaListTabBaseImpl.this.getString(R.string.hrDonate), LlamaListTabBaseImpl.this.getString(R.string.hrAboutTheApp), LlamaListTabBaseImpl.this.getString(R.string.hrQuit)};
                    final CharSequence[] items = (CharSequence[]) ArrayHelpers.SpliceArrays(ArrayHelpers.SpliceArrays(new CharSequence[]{LlamaListTabBaseImpl.this.getString(R.string.hrCurrentTabInfo)}, LlamaListTabBaseImpl.this.getContextSensitiveMenuItems(), CharSequence.class), baseItems2, CharSequence.class);
                    new Builder(LlamaListTabBaseImpl.this._Activity).setTitle(R.string.hrHowCanIHelpYou).setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            CharSequence selected = items[which];
                            if (LlamaListTabBaseImpl.this.getString(R.string.hrCurrentTabInfo).equals(selected)) {
                                LlamaListTabBaseImpl.this.ShowTabHelp();
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrLlamaInstructions).equals(selected)) {
                                Instances.Service.ShowHelp(LlamaListTabBaseImpl.this._Activity);
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrLlamaAndKebabAppsWebsite).equals(selected)) {
                                Instances.Service.ViewWebsite();
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrSendEmail).equals(selected)) {
                                Instances.Service.SendEmail();
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrDonate).equals(selected)) {
                                Instances.Service.ShowDonation(LlamaListTabBaseImpl.this._Activity);
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrAboutTheApp).equals(selected)) {
                                Instances.Service.ShowAbout(LlamaListTabBaseImpl.this._Activity);
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrSettings).equals(selected)) {
                                LlamaListTabBaseImpl.this._Activity.startActivity(new Intent(LlamaListTabBaseImpl.this._Activity, GlobalSettingsActivity.class));
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrImportExportData).equals(selected)) {
                                LlamaListTabBaseImpl.ImportExport(LlamaListTabBaseImpl.this._Activity);
                            } else if (LlamaListTabBaseImpl.this.getString(R.string.hrQuit).equals(selected)) {
                                LlamaListTabBaseImpl.this.ShowQuitMessage();
                            } else {
                                LlamaListTabBaseImpl.this.handleContextSensitiveItem(selected);
                            }
                        }
                    }).show();
                }
            });
        }

        public void onPause() {
            if (this._DisclaimerDialog != null) {
                this._DisclaimerDialog.dismiss();
            }
            Instances.CurrentTab = null;
            if (this.disclaimerShowed && !((Boolean) LlamaSettings.AcceptedDisclaimerMessage.GetValue(this._Activity)).booleanValue()) {
                _StopServiceSoon = true;
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (LlamaListTabBaseImpl._StopServiceSoon && !((Boolean) LlamaSettings.AcceptedDisclaimerMessage.GetValue(LlamaListTabBaseImpl.this._Activity)).booleanValue() && Instances.Service != null) {
                            Instances.Service.Quit(true);
                            Instances.Service.ShowFriendlyInfo(false, "Llama service was stopped.", true);
                        }
                    }
                }, 5000);
            }
        }

        public void onResume() {
            Instances.CurrentTab = this;
            UpdateRandomTip();
            _StopServiceSoon = false;
            if (!((Boolean) LlamaSettings.AcceptedDisclaimerMessage.GetValue(this._Activity)).booleanValue()) {
                ShowDisclaimer();
            } else if (!((Boolean) LlamaSettings.HadFirstRunMessage.GetValue(this._Activity)).booleanValue()) {
                ShowFirstRunMessage();
            } else if (((Integer) LlamaSettings.LastMessageVersion.GetValue(this._Activity)).intValue() < 2) {
                ShowVersionIncrementHelp();
                LlamaSettings.LastMessageVersion.SetValueAndCommit(this._Activity, Integer.valueOf(2), new CachedSetting[0]);
            } else if (!((Boolean) this._SeenHelpSettings.GetValue(this._Activity)).booleanValue()) {
                ShowTabHelp();
            }
        }

        private void ShowVersionIncrementHelp() {
        }

        /* Access modifiers changed, original: 0000 */
        public void ShowTabHelp() {
            new Builder(this._Activity).setTitle(R.string.hrWhatDoesThisTabDo).setMessage(getString(this._HelpTextId)).setPositiveButton(R.string.hrGroovy, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            }).show();
            this._SeenHelpSettings.SetValueAndCommit(this._Activity, Boolean.valueOf(true), new CachedSetting[0]);
        }

        public void UpdateRandomTip() {
            if (Instances.Service != null) {
                InitRandomTips();
                String[] tabTips = InitAndGetTabRandomTips();
                int totalTips = (_CommonTips.length + (tabTips.length * 2)) + 2;
                this._IsDonateMessage = false;
                String tip = null;
                for (int i = 0; i < 10; i++) {
                    int randomIndex = _Random.nextInt(totalTips);
                    if (randomIndex >= _CommonTips.length) {
                        if (randomIndex - _CommonTips.length >= tabTips.length * 2) {
                            this._IsDonateMessage = true;
                            tip = GetDonateMessageOrNull();
                            if (!(tip == null || tip.equals(this._Message.getText()))) {
                                break;
                            }
                        }
                        this._IsDonateMessage = false;
                        tip = tabTips[(randomIndex - _CommonTips.length) / 2];
                        if (!tip.equals(this._Message.getText())) {
                            break;
                        }
                    } else {
                        this._IsDonateMessage = false;
                        tip = _CommonTips[randomIndex];
                        if (!tip.equals(this._Message.getText())) {
                            break;
                        }
                    }
                }
                TextView textView = this._Message;
                if (tip == null) {
                    tip = "";
                }
                textView.setText(tip);
            }
        }

        public void UpdateDonateMessage() {
            if (this._IsDonateMessage && this._Message != null) {
                String message = GetDonateMessageOrNull();
                if (message != null) {
                    this._Message.setText(message);
                }
            }
        }

        /* Access modifiers changed, original: 0000 */
        public String GetDonateMessageOrNull() {
            Date installDate = (Date) LlamaSettings.InstallDate.GetValue(this._Activity);
            if (installDate == null) {
                return null;
            }
            int days = DateHelpers.GetSimpleDifferenceInDays(Calendar.getInstance().getTime(), installDate);
            Integer runs = (Integer) LlamaSettings.EventRuns.GetValue(this._Activity);
            int runsInt;
            if (runs == null) {
                runsInt = 0;
            } else {
                runsInt = runs.intValue();
            }
            return String.format(getString(R.string.hrPerformed1ActionsSince2DaysAgo), new Object[]{runs, Integer.valueOf(days)});
        }

        private void InitRandomTips() {
            if (_CommonTips == null) {
                _CommonTips = new String[]{getString(R.string.hrCommonTip1), getString(R.string.hrCommonTip2), getString(R.string.hrCommonTip3), getString(R.string.hrCommonTip4), getString(R.string.hrCommonTip5), getString(R.string.hrCommonTip6)};
            }
        }

        public final boolean onPrepareOptionsMenu(Menu menu) {
            LocalisationInit.ContextConfigInit(this._Activity);
            menu.clear();
            CharSequence[] contextItems = getContextSensitiveMenuItems();
            for (int i = 0; i < contextItems.length; i++) {
                menu.add(0, i + 1000, 0, contextItems[i]);
            }
            menu.add(0, Constants.MENU_SETTINGS, 0, R.string.hrSettings);
            menu.add(0, Constants.MENU_IMPORT_EXPORT, 0, R.string.hrImportExportData);
            switch (((Integer) LlamaSettings.MobileDataMenuMode.GetValue(this._Activity)).intValue()) {
                case 1:
                    menu.add(0, Constants.MENU_MOBILE_DATA_TOGGLE, 0, R.string.hrToggleMobileData);
                    break;
                case 2:
                    menu.add(0, Constants.MENU_APN_TOGGLE, 0, R.string.hrToggleApn);
                    break;
                case 3:
                    menu.add(0, Constants.MENU_APN_TOGGLE, 0, R.string.hrToggleApn);
                    menu.add(0, Constants.MENU_MOBILE_DATA_TOGGLE, 0, R.string.hrToggleMobileData);
                    break;
            }
            menu.add(0, Constants.MENU_DONATE, 0, R.string.hrDonate);
            menu.add(0, 100, 0, R.string.hrQuit);
            return true;
        }

        /* Access modifiers changed, original: 0000 */
        public void ShowQuitMessage() {
            new Builder(this._Activity).setTitle(R.string.hrQuitLlama).setMessage(R.string.hrCloseLlamaExplanation).setPositiveButton(R.string.hrPutLlamaToSleep, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (Instances.Service != null) {
                        Instances.Service.Quit(true);
                    } else {
                        LlamaListTabBaseImpl.this._Activity.finish();
                    }
                }
            }).setNegativeButton(R.string.hrCancel, null).show();
        }

        public final boolean onOptionsItemSelected(MenuItem item) {
            if (handleContextSensitiveItem(item.getTitle())) {
                return true;
            }
            if (item.getItemId() == 100) {
                ShowQuitMessage();
                return true;
            } else if (Instances.Service == null) {
                Instances.StartService(this._Activity);
                return true;
            } else {
                switch (item.getItemId()) {
                    case Constants.MENU_SETTINGS /*101*/:
                        this._Activity.startActivity(new Intent(this._Activity, GlobalSettingsActivity.class));
                        return true;
                    case Constants.MENU_IMPORT_EXPORT /*113*/:
                        ImportExport(this._Activity);
                        return true;
                    case Constants.MENU_APN_TOGGLE /*114*/:
                        String str;
                        boolean apnEnabled = Instances.Service.GetApnStatus();
                        Builder builder = new Builder(this._Activity);
                        String[] strArr = new String[2];
                        StringBuilder append = new StringBuilder().append(getString(R.string.hrEnableApn));
                        if (apnEnabled) {
                            str = " (" + getString(R.string.hrCurrent) + ")";
                        } else {
                            str = "";
                        }
                        strArr[0] = append.append(str).toString();
                        StringBuilder append2 = new StringBuilder().append(getString(R.string.hrDisableApn));
                        if (apnEnabled) {
                            str = "";
                        } else {
                            str = "(" + getString(R.string.hrCurrent) + ")";
                        }
                        strArr[1] = append2.append(str).toString();
                        builder.setItems(strArr, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Instances.Service.ToggleApn(true);
                                } else {
                                    Instances.Service.ToggleApn(false);
                                }
                            }
                        }).show();
                        return true;
                    case Constants.MENU_DONATE /*115*/:
                        Instances.Service.ShowDonation(this._Activity);
                        return true;
                    case Constants.MENU_MOBILE_DATA_TOGGLE /*121*/:
                        new Builder(this._Activity).setItems(new String[]{getString(R.string.hrEnableMobileData), getString(R.string.hrDisableMobileData)}, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    Instances.Service.ToggleMobileData(true);
                                } else {
                                    Instances.Service.ToggleMobileData(false);
                                }
                            }
                        }).show();
                        return true;
                    default:
                        return false;
                }
            }
        }

        private static void ImportFromUrl(final Activity activity) {
            TextEntryDialog.Show(activity, activity.getString(R.string.hrPasteLlamaUrl), new ButtonHandler() {
                public void Do(String result) {
                    SocialLlama.HandleSharedUrl(activity, result);
                }
            });
        }

        private static void Import(Activity activity) {
            AlertDialog.Builder builder = new Builder(activity);
            builder.setMessage(R.string.hrImportWarning).setCancelable(false).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    Instances.Service.ImportSettings();
                }
            }).setNegativeButton(R.string.hrNo, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }

        private static void Export(final Activity activity) {
            AlertDialog.Builder builder = new Builder(activity);
            builder.setMessage(R.string.hrExportWarning).setCancelable(false).setPositiveButton(R.string.hrYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Instances.Service.ExportSettings(activity);
                    dialog.dismiss();
                }
            }).setNegativeButton(R.string.hrNo, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Helpers.ShowSimpleDialogMessage(activity, activity.getString(R.string.hrDataNotExported));
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }

        private static void ImportExport(final Activity activity) {
            new Builder(activity).setItems(new String[]{activity.getString(R.string.hrImportFromUrl), activity.getString(R.string.hrImportFromSdCard), activity.getString(R.string.hrExportToSdCard)}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            LlamaListTabBaseImpl.ImportFromUrl(activity);
                            return;
                        case 1:
                            LlamaListTabBaseImpl.Import(activity);
                            return;
                        case 2:
                            LlamaListTabBaseImpl.Export(activity);
                            return;
                        default:
                            return;
                    }
                }
            }).show();
        }

        private void ShowFirstRunMessage() {
            AlertDialog.Builder builder = new Builder(this._Activity);
            builder.setMessage(getString(R.string.hrIntroText));
            builder.setCancelable(false).setPositiveButton(R.string.hrOkeyDoke, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    LlamaSettings.HadFirstRunMessage.SetValueAndCommit(LlamaListTabBaseImpl.this._Activity, Boolean.valueOf(true), new CachedSetting[0]);
                }
            });
            builder.create().show();
            LlamaSettings.LastMessageVersion.SetValueAndCommit(this._Activity, Integer.valueOf(2), new CachedSetting[0]);
        }

        private void ShowDisclaimer() {
            _StopServiceSoon = false;
            AlertDialog.Builder builder = new Builder(this._Activity);
            builder.setTitle("First, some boring stuff");
            builder.setMessage("-You use Llama at your own risk; Llama is provided \"as-is\" with no warranties nor fitness for purpose of any kind.\n\n-Llama is free. You may not sell Llama.\n\n-If you want to distribute Llama, contact me first.\n\n-Use the Llama? button for help.");
            builder.setCancelable(false).setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    LlamaListTabBaseImpl._StopServiceSoon = false;
                    dialog.dismiss();
                    LlamaSettings.LlamaWasExitted.SetValueAndCommit(LlamaListTabBaseImpl.this._Activity, Boolean.valueOf(false), new CachedSetting[0]);
                    Instances.StartService(LlamaListTabBaseImpl.this._Activity);
                    LlamaSettings.AcceptedDisclaimerMessage.SetValueAndCommit(LlamaListTabBaseImpl.this._Activity, Boolean.valueOf(true), new CachedSetting[0]);
                    if (!((Boolean) LlamaSettings.HadFirstRunMessage.GetValue(LlamaListTabBaseImpl.this._Activity)).booleanValue()) {
                        LlamaListTabBaseImpl.this.ShowFirstRunMessage();
                    }
                }
            }).setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    LlamaListTabBaseImpl._StopServiceSoon = false;
                    dialog.dismiss();
                    LlamaListTabBaseImpl.this._Activity.finish();
                    if (Instances.Service != null) {
                        Instances.Service.Quit(true);
                    }
                }
            });
            this.disclaimerShowed = true;
            this._DisclaimerDialog = builder.create();
            this._DisclaimerDialog.show();
        }

        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
            LocalisationInit.ContextConfigInit(this._Activity);
        }
    }

    public interface LlamaListTabInterface {
        void SetImpl(LlamaListTabBaseImpl llamaListTabBaseImpl);

        void onCreate(Bundle bundle);

        void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenuInfo contextMenuInfo);

        boolean onOptionsItemSelected(MenuItem menuItem);

        void onPause();

        boolean onPrepareOptionsMenu(Menu menu);

        void onResume();
    }

    protected LlamaListTabBase() {
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        this._Impl.onCreateContextMenu(menu, v, menuInfo);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this._Impl.onCreate(savedInstanceState);
    }

    /* Access modifiers changed, original: protected */
    public void onPause() {
        super.onPause();
        this._Impl.onPause();
    }

    /* Access modifiers changed, original: protected */
    public void onResume() {
        super.onResume();
        this._Impl.onResume();
    }

    public final boolean onPrepareOptionsMenu(Menu menu) {
        this._Impl.onPrepareOptionsMenu(menu);
        return super.onPrepareOptionsMenu(menu);
    }

    public final boolean onOptionsItemSelected(MenuItem item) {
        return this._Impl.onOptionsItemSelected(item);
    }

    public void SetImpl(LlamaListTabBaseImpl impl) {
        this._Impl = impl;
        impl._Activity = this;
    }
}
