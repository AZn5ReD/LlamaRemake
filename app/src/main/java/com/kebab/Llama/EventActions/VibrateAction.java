package com.kebab.Llama.EventActions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import com.kebab.AlertDialogEx.Builder;
import com.kebab.AppendableCharSequence;
import com.kebab.DialogHandler;
import com.kebab.DialogPreference;
import com.kebab.Llama.Event;
import com.kebab.Llama.EventFragment;
import com.kebab.Llama.Instances;
import com.kebab.Llama.LlamaService;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Ref;
import com.kebab.Tuple;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VibrateAction extends EventAction<VibrateAction> {
    static final String[] patternValues = new String[]{"0,250", "0,750", "0,250,250,250,250,250", "0,750,500,750,500,750"};
    static String[] patterns;
    String _VibratePattern;

    public static void InitLocalisation(Context context) {
        patterns = new String[]{context.getString(R.string.hrVibrateShort), context.getString(R.string.hrVibrateLong), context.getString(R.string.hrVibrateMultipleShort), context.getString(R.string.hrVibrateMultipleLong)};
    }

    public VibrateAction(String vibratePattern) {
        this._VibratePattern = vibratePattern;
    }

    public void PerformAction(LlamaService service, Activity activity, Event event, long currentTimeRoundedToNextMinute, int eventRunMode) {
        service.Vibrate(this._VibratePattern);
    }

    public boolean RenameProfile(String oldName, String newName) {
        return false;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        return EventFragment.VIBRATE_ACTION;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(this._VibratePattern);
    }

    public static VibrateAction CreateFrom(String[] parts, int currentPart) {
        return new VibrateAction(parts[currentPart + 1]);
    }

    public PreferenceEx<VibrateAction> CreatePreference(PreferenceActivity context) {
        return CreateDialogPreference(context, context.getString(R.string.hrVibratePattern), new DialogHandler<VibrateAction>() {
            EditText _Text;

            public void DialogHasFinished(View view) {
            }

            public boolean HideButtons() {
                return false;
            }

            public VibrateAction GetResultFromView() {
                return new VibrateAction(this._Text.getText().toString());
            }

            public VibrateAction fillValuesFromString(String value) {
                return new VibrateAction(value);
            }

            public String getHumanReadableValue(VibrateAction value) {
                if (value._VibratePattern == null) {
                    return "";
                }
                return value._VibratePattern.replace(",", "-");
            }

            public View getView(VibrateAction value, Context context, DialogPreference<?, VibrateAction> dialogPreference) {
                Tuple<View, EditText> result = VibrateAction.CreateView(context, value._VibratePattern);
                this._Text = (EditText) result.Item2;
                return (View) result.Item1;
            }

            public String serialiseToString(VibrateAction value) {
                return value._VibratePattern;
            }

            public boolean RequiresScrollView() {
                return true;
            }
        }, this, new OnGetValueEx<VibrateAction>() {
            public VibrateAction GetValue(Preference preference) {
                return (VibrateAction) ((DialogPreference) preference).getValue();
            }
        });
    }

    public void AppendActionDescription(Context context, AppendableCharSequence sb) throws IOException {
        for (String equals : patternValues) {
            if (equals.equals(this._VibratePattern)) {
                sb.append(String.format(context.getString(R.string.hrVibrateThe1Pattern), new Object[]{equals.toLowerCase()}));
                return;
            }
        }
        sb.append(context.getString(R.string.hrVibrateACustomisedPattern));
    }

    public String GetIsValidError(Context context) {
        return null;
    }

    public boolean IsHarmful() {
        return false;
    }

    public static Tuple<View, EditText> CreateView(final Context context, String originalValue) {
        @SuppressLint("WrongConstant") View v = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.vibratedialog, null);
        final Ref<Long> _LastTicks = new Ref(Long.valueOf(0));
        final Ref<Boolean> _HadFirstTap = new Ref(Boolean.valueOf(false));
        final EditText _Text = (EditText) v.findViewById(R.id.text);
        Button _StartButton = (Button) v.findViewById(R.id.tapRhythm);
        Button _ResetButton = (Button) v.findViewById(R.id.reset);
        Button _TestButton = (Button) v.findViewById(R.id.test);
        Button _ChoosePresetButton = (Button) v.findViewById(R.id.choosePreset);
        final Pattern regex = Pattern.compile("[^\\d\\s\\x20:;\\.\\-,]+");
        final Pattern regex2 = Pattern.compile("[\\s\\x20:;\\.\\-]+");
        if (originalValue == null || originalValue.equals("")) {
            originalValue = "0";
        }
        _Text.setText(originalValue);
        _Text.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                Matcher matcher = regex.matcher(s.toString());
                if (matcher.find()) {
                    s.clear();
                    s.append(matcher.replaceAll(""));
                }
                Matcher matcher2 = regex2.matcher(s.toString());
                if (matcher2.find()) {
                    s.clear();
                    s.append(matcher2.replaceAll(","));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        _ChoosePresetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new Builder(context, false).setItems(VibrateAction.patterns, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        _Text.setText(VibrateAction.patternValues[arg1]);
                    }
                }).show();
            }
        });
        _ResetButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                _HadFirstTap.Value = Boolean.valueOf(false);
                _Text.setText("0");
            }
        });
        _StartButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case 0:
                    case 1:
                        long currentTicks = System.currentTimeMillis();
                        if (((Boolean) _HadFirstTap.Value).booleanValue()) {
                            _Text.setText(_Text.getText() + "," + (currentTicks - ((Long) _LastTicks.Value).longValue()));
                        } else {
                            _Text.setText("0");
                        }
                        _HadFirstTap.Value = Boolean.valueOf(true);
                        _LastTicks.Value = Long.valueOf(currentTicks);
                        break;
                }
                return false;
            }
        });
        _TestButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Instances.Service.Vibrate(_Text.getText().toString());
            }
        });
        return new Tuple(v, _Text);
    }
}
