package com.kebab;

import android.text.SpannableStringBuilder;
import java.io.IOException;

public class AppendableCharSequence implements CharSequence, Appendable {
    Appendable _Appendable;
    CharSequence _CharSequence;

    public static AppendableCharSequence Wrap(StringBuilder sb) {
        AppendableCharSequence result = new AppendableCharSequence();
        result._CharSequence = sb;
        result._Appendable = sb;
        return result;
    }

    public static AppendableCharSequence Wrap(SpannableStringBuilder sb) {
        AppendableCharSequence result = new AppendableCharSequence();
        result._CharSequence = sb;
        result._Appendable = sb;
        return result;
    }

    public char charAt(int paramInt) {
        return this._CharSequence.charAt(paramInt);
    }

    public int length() {
        return this._CharSequence.length();
    }

    public CharSequence subSequence(int paramInt1, int paramInt2) {
        return this._CharSequence.subSequence(paramInt1, paramInt2);
    }

    public Appendable append(char paramChar) throws IOException {
        return this._Appendable.append(paramChar);
    }

    public Appendable append(CharSequence paramCharSequence) throws IOException {
        return this._Appendable.append(paramCharSequence);
    }

    public Appendable append(CharSequence paramCharSequence, int paramInt1, int paramInt2) throws IOException {
        return this._Appendable.append(paramCharSequence, paramInt1, paramInt2);
    }
}
