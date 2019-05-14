package com.kebab.Llama.EventActions;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.preference.PreferenceActivity;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.ClickablePreferenceEx;
import com.kebab.ClickablePreferenceEx.GotResultHandler;
import com.kebab.Crypto;
import com.kebab.Helpers;
import com.kebab.HelpersC;
import com.kebab.Llama.Constants;
import com.kebab.Llama.DeviceAdmin.DeviceAdminCompat;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.GlobalSettingsActivity;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.LlamaSettings;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Logging;
import com.kebab.Llama.R;
import com.kebab.PreferenceEx;
import com.kebab.ResultRegisterableActivity;
import com.kebab.ResultRegisterableActivity.ResultCallback;
import com.kebab.TextEntryDialog;
import com.kebab.TextEntryDialog.ButtonHandler;
import java.io.IOException;
import java.util.Random;

public class ChangePasswordAction extends EventAction<ChangePasswordAction> {
    Handler _Handler = new Handler();
    String _Password;
    Random _Random = new Random();

    public ChangePasswordAction(String encryptedPassword) {
        this._Password = encryptedPassword;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        String decrypted = DecryptPassword(this._Password, service);
        if (decrypted == null) {
            service.HandleFriendlyError(service.getString(R.string.hrFailedToDecryptYourPassword), false);
        } else {
            service.ChangePassword(decrypted);
        }
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.CHANGE_PASSWORD_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(this._Password));
    }

    public static ChangePasswordAction CreateFrom(String[] parts, int currentPart) {
        return new ChangePasswordAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]));
    }

    public PreferenceEx<ChangePasswordAction> CreatePreference(PreferenceActivity context) {
        final PreferenceActivity preferenceActivity = context;
        return new ClickablePreferenceEx<ChangePasswordAction>((ResultRegisterableActivity) context, context.getString(R.string.hrActionChangePassword), this) {
            /* Access modifiers changed, original: protected */
            public String GetHumanReadableValue(Context context, ChangePasswordAction value) {
                ChangePasswordAction changePasswordAction = ChangePasswordAction.this;
                if (ChangePasswordAction.DecryptPassword(value._Password, context) != null) {
                    return context.getString(R.string.hrPasswordHidden);
                }
                return context.getString(R.string.hrPasswordErrorPleaseReenterItAgain);
            }

            /* Access modifiers changed, original: protected */
            public void OnPreferenceClicked(final ResultRegisterableActivity host, final ChangePasswordAction existingValue, final GotResultHandler<ChangePasswordAction> gotResultHandler) {
                ResultCallback resultCallback = new ResultCallback() {
                    public void HandleResult(int resultCode, Intent data, Object extraStateInfo) {
                        if (resultCode != 0) {
                            if (((String) LlamaSettings.EncryptionPassword.GetValue(preferenceActivity)).length() == 0) {
                                new Builder(preferenceActivity).setMessage(R.string.hrSetLlamaSecurityPasswordNow).setPositiveButton(R.string.hrYes, new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(preferenceActivity, GlobalSettingsActivity.class);
                                        intent.putExtra(Constants.EXTRA_SCROLL_TO_LLAMA_SECURITY, true);
                                        preferenceActivity.startActivity(intent);
                                    }
                                }).setNegativeButton(R.string.hrNo, new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        AnonymousClass1.this.ShowPasswordEditor(host, existingValue, gotResultHandler);
                                    }
                                }).show();
                            } else {
                                AnonymousClass1.this.ShowPasswordEditor(host, existingValue, gotResultHandler);
                            }
                        }
                    }
                };
                if (!DeviceAdminCompat.IsSupported()) {
                    Helpers.ShowTip(host.GetActivity(), (int) R.string.hrYourDeviceDoesNotSupportDeviceAdmin);
                } else if (DeviceAdminCompat.IsAdminEnabled(preferenceActivity)) {
                    resultCallback.HandleResult(-1, new Intent(), null);
                } else {
                    DeviceAdminCompat.ShowEnableAdmin(host, Constants.REQUEST_CODE_DEVICE_ADMIN, resultCallback);
                }
            }

            /* Access modifiers changed, original: 0000 */
            public void ShowPasswordEditor(final ResultRegisterableActivity host, final ChangePasswordAction existingValue, final GotResultHandler<ChangePasswordAction> gotResultHandler) {
                TextEntryDialog.Show(host.GetActivity(), host.GetActivity().getString(R.string.hrEnterAScreenLockPassword), new ButtonHandler() {
                    public void Do(final String result) {
                        if (result.matches(".*[^A-Za-z0-9].*")) {
                            new Builder(host.GetActivity()).setMessage("WARNING: Your password contains non-alphanumeric characters.\n\nYou will very likely lock your phone forever if your lock screen cannot enter these characters.\n\nAre you sure that you want to use this screen lock password?").setPositiveButton("Use password", new OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    AnonymousClass1.this.ShowPasswordConfirm(result, host, existingValue, gotResultHandler);
                                }
                            }).setNegativeButton("Enter new password", new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    AnonymousClass1.this.ShowPasswordEditor(host, existingValue, gotResultHandler);
                                }
                            }).show();
                        } else {
                            AnonymousClass1.this.ShowPasswordConfirm(result, host, existingValue, gotResultHandler);
                        }
                    }
                }, 144);
            }

            /* Access modifiers changed, original: 0000 */
            public void ShowPasswordConfirm(String original, ResultRegisterableActivity host, ChangePasswordAction existingValue, GotResultHandler<ChangePasswordAction> gotResultHandler) {
                final String str = original;
                final GotResultHandler<ChangePasswordAction> gotResultHandler2 = gotResultHandler;
                final ResultRegisterableActivity resultRegisterableActivity = host;
                final ChangePasswordAction changePasswordAction = existingValue;
                TextEntryDialog.Show(host.GetActivity(), host.GetActivity().getString(R.string.hrConfirmScreenLockPassword), new ButtonHandler() {
                    public void Do(String result) {
                        if (HelpersC.StringEquals(str, result)) {
                            gotResultHandler2.HandleResult(new ChangePasswordAction(ChangePasswordAction.EncryptPassword(str, preferenceActivity)));
                            return;
                        }
                        Helpers.ShowTip(resultRegisterableActivity.GetActivity(), resultRegisterableActivity.GetActivity().getString(R.string.hrPasswordsDontMatch));
                        AnonymousClass1.this.ShowPasswordConfirm(str, resultRegisterableActivity, changePasswordAction, gotResultHandler2);
                    }
                }, 144);
            }
        };
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        sb.append(context.getString(R.string.hrChangeScreenLockPassword));
    }

    public String GetIsValidError(Context context) {
        if (this._Password == null) {
            return context.getString(R.string.hrThereWasAnErrorSavingYourPassword);
        }
        return null;
    }

    public static String EncryptPassword(String password, Context context) {
        if (password == null) {
            return null;
        }
        try {
            return Crypto.encrypt((String) LlamaSettings.EncryptionPassword.GetValue(context), "goat67" + password);
        } catch (Exception ex) {
            Logging.Report(ex, context);
            return null;
        }
    }

    public static String DecryptPassword(String encrypted, Context context) {
        if (encrypted == null) {
            return null;
        }
        String result;
        try {
            result = Crypto.decrypt((String) LlamaSettings.EncryptionPassword.GetValue(context), encrypted);
        } catch (Exception ex) {
            Logging.Report(ex, context);
            result = null;
        }
        if (result == null || !result.startsWith("goat67")) {
            return null;
        }
        return result.substring("goat67".length());
    }

    public boolean IsHarmful() {
        return true;
    }
}
