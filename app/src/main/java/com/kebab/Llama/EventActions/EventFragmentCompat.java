package com.kebab.Llama.EventActions;

import android.content.Context;
import android.preference.PreferenceActivity;
import com.kebab.Llama.EventFragment;
import com.kebab.PreferenceEx;

public abstract class EventFragmentCompat<T> extends EventFragment<T> {
    public abstract int GetPartsConsumption();

    public PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        throw new RuntimeException("Cannot create a preference for an old fragment type.");
    }

    public String GetIsValidError(Context context) {
        throw new RuntimeException("Cannot check validity for an old fragment type.");
    }

    /* Access modifiers changed, original: protected */
    public boolean IsCondition() {
        throw new RuntimeException("Cannot IsCondition for an old fragment type.");
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        throw new RuntimeException("Cannot store an old fragment type.");
    }

    /* Access modifiers changed, original: protected */
    public String getId() {
        throw new RuntimeException("Cannot get the ID of an old fragment type.");
    }
}
