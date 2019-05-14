package com.kebab.Nfc;

import android.content.Context;
import android.nfc.NfcManager;

public class NfcHelper9Plus {
    public static boolean isSupported(Context context) {
        return isSupportedInternal(context.getSystemService("nfc"));
    }

    private static boolean isSupportedInternal(Object nfcManagerObject) {
        return ((NfcManager) nfcManagerObject).getDefaultAdapter() != null;
    }

    public static boolean isEnabled(Context context) {
        return ((NfcManager) context.getSystemService("nfc")).getDefaultAdapter().isEnabled();
    }
}
