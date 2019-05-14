package com.kebab;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import java.util.Date;

public class SeekBarDialogView {
    OnSeekBarChangeListener _ChangeListener;
    Context _DEleteMEContext;
    String _TopMostValue;
    long _TouchDown;
    Handler _TouchRepeater;
    Runnable _TouchRepeaterRunnable;
    ValueFormatter _ValueFormatter;
    String mDialogMessage;
    int mMax;
    int mMin;
    SeekBar mSeekBar;
    TextView mSplashText;
    String mSuffix;
    int mValue;
    TextView mValueText;

    public interface ValueFormatter {
        String FormatValue(int i, boolean z, String str);

        int GetTextSize();
    }

    private SeekBarDialogView(int value, int min, int max, String topMostValue, String dialogMessage) {
        this.mValue = value;
        this.mDialogMessage = dialogMessage;
        this._TopMostValue = topMostValue;
        this.mMin = min;
        this.mMax = max;
    }

    public SeekBarDialogView(int value, int min, int max, String topMostValue, String dialogMessage, String textValueSuffix) {
        this(value, min, max, topMostValue, dialogMessage);
        this.mSuffix = textValueSuffix;
    }

    public SeekBarDialogView(int value, int min, int max, String topMostValue, String dialogMessage, ValueFormatter valueFormatter) {
        this(value, min, max, topMostValue, dialogMessage);
        this._ValueFormatter = valueFormatter;
    }

    public View createSeekBarDialogView(Context context) {
        this._DEleteMEContext = context;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(1);
        layout.setPadding(6, 6, 6, 6);
        if (this.mDialogMessage != null) {
            this.mSplashText = new TextView(context);
            if (this.mDialogMessage != null) {
                this.mSplashText.setText(this.mDialogMessage);
            }
            layout.addView(this.mSplashText);
        }
        if (!(this.mSuffix == null && this._ValueFormatter == null)) {
            float f;
            this.mValueText = new TextView(context);
            this.mValueText.setGravity(1);
            TextView textView = this.mValueText;
            if (this._ValueFormatter == null) {
                f = 32.0f;
            } else {
                f = (float) this._ValueFormatter.GetTextSize();
            }
            textView.setTextSize(f);
            layout.addView(this.mValueText, new LayoutParams(-1, -2));
        }
        LinearLayout hLayout = new LinearLayout(context);
        layout.setOrientation(1);
        this.mSeekBar = new SeekBar(context);
        Button minusButton = new Button(context);
        Button plusButton = new Button(context);
        minusButton.setText(" - ");
        minusButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeekBarDialogView.this.mSeekBar.setProgress(SeekBarDialogView.this.mSeekBar.getProgress() - 1);
            }
        });
        minusButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return SeekBarDialogView.this.handleTouch(-1, event);
            }
        });
        plusButton.setText(" + ");
        plusButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SeekBarDialogView.this.mSeekBar.setProgress(SeekBarDialogView.this.mSeekBar.getProgress() + 1);
            }
        });
        plusButton.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return SeekBarDialogView.this.handleTouch(1, event);
            }
        });
        ViewGroup.LayoutParams buttonLayout = new LayoutParams(-2, -2);
        LayoutParams seekBarLayout = new LayoutParams(-1, -2, 1.0f);
        seekBarLayout.gravity = 16;
        hLayout.addView(minusButton, buttonLayout);
        hLayout.addView(this.mSeekBar, seekBarLayout);
        hLayout.addView(plusButton, buttonLayout);
        layout.addView(hLayout);
        if (this._TopMostValue != null && this.mValue > this.mMax) {
            this.mValue = this.mMax + 1;
        }
        this.mSeekBar.setMax((this._TopMostValue != null ? 1 : 0) + (this.mMax - this.mMin));
        this.mSeekBar.setProgress(this.mValue - this.mMin);
        this.mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int value, boolean fromUser) {
                SeekBarDialogView.this.mValue = SeekBarDialogView.this.mMin + value;
                SeekBarDialogView.this.UpdateText();
                if (SeekBarDialogView.this._ChangeListener != null) {
                    SeekBarDialogView.this._ChangeListener.onProgressChanged(seekBar, SeekBarDialogView.this.mValue, fromUser);
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        UpdateText();
        return layout;
    }

    private void UpdateText() {
        if (this.mValueText != null) {
            if (this._ValueFormatter != null) {
                this.mValueText.setText(this._ValueFormatter.FormatValue(this.mValue, this.mValue > this.mMax, this._TopMostValue));
            } else if (this.mValue > this.mMax) {
                this.mValueText.setText(this._TopMostValue);
            } else {
                String t = String.valueOf(this.mValue);
                TextView textView = this.mValueText;
                if (this.mSuffix != null) {
                    t = t.concat(this.mSuffix);
                }
                textView.setText(t);
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean handleTouch(final int delta, MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                Log.i(Constants.TAG, "DOWN");
                cancelTouchRepeater();
                this._TouchDown = new Date().getTime();
                this._TouchRepeaterRunnable = new Runnable() {
                    public void run() {
                        int tickWait;
                        int step;
                        Log.i(Constants.TAG, "TICK");
                        long ticksDifference = new Date().getTime() - SeekBarDialogView.this._TouchDown;
                        if (ticksDifference < 1000) {
                            tickWait = 250;
                            step = 1;
                        } else if (ticksDifference < 2000) {
                            tickWait = 62;
                            step = 1;
                        } else if (ticksDifference < 5000) {
                            tickWait = 1;
                            step = 5;
                        } else {
                            tickWait = 1;
                            step = 20;
                        }
                        SeekBarDialogView.this.mSeekBar.setProgress(SeekBarDialogView.this.mSeekBar.getProgress() + (delta * step));
                        if (SeekBarDialogView.this._ChangeListener != null) {
                            SeekBarDialogView.this._ChangeListener.onProgressChanged(SeekBarDialogView.this.mSeekBar, SeekBarDialogView.this.mValue, true);
                        }
                        SeekBarDialogView.this._TouchRepeater.postDelayed(SeekBarDialogView.this._TouchRepeaterRunnable, (long) tickWait);
                    }
                };
                this._TouchRepeater = new Handler();
                this._TouchRepeaterRunnable.run();
                break;
            case 1:
                Log.i(Constants.TAG, "UP");
                cancelTouchRepeater();
                break;
        }
        return true;
    }

    /* Access modifiers changed, original: 0000 */
    public void cancelTouchRepeater() {
        if (this._TouchRepeater != null) {
            this._TouchRepeater.removeCallbacks(this._TouchRepeaterRunnable);
        }
        this._TouchRepeaterRunnable = null;
        this._TouchRepeater = null;
    }

    public void DialogHasFinished() {
        cancelTouchRepeater();
    }

    public int GetResult() {
        return this.mValue > this.mMax ? Integer.MAX_VALUE : this.mValue;
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this._ChangeListener = onSeekBarChangeListener;
    }

    public void setValue(int value) {
        this.mValue = value;
        this.mSeekBar.setProgress(this.mValue - this.mMin);
        UpdateText();
    }
}
