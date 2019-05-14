package com.kebab.Llama.EventActions;

import com.kebab.Llama.LlamaStorage;

public class ChangeProfileAction extends EventFragmentCompat<ChangeProfileAction> {
    String _ProfileName;

    public ChangeProfileAction(String profileName) {
        this._ProfileName = profileName;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static ChangeProfileAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeProfileAction(LlamaStorage.SimpleUnescape(parts[currentPart + 1]));
    }
}
