package com.kebab.Llama.EventConditions;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.ListPreferenceMultiselect;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public abstract class WifiNetworkConditionBase<T> extends EventCondition<T> {
    protected static final String ANY_WIFI_NETWORK = ":ANY:";
    String[] _WifiNamesOrAddresses;

    public abstract T CreateSelf(String[] strArr);

    public abstract String GetFormattedConditionDescription(Context context, String str);

    public abstract String GetPrefrenceDialogTitle(Context context);

    public WifiNetworkConditionBase(String[] wifiNamesOrAddresses) {
        this._WifiNamesOrAddresses = wifiNamesOrAddresses;
    }

    protected WifiNetworkConditionBase(String[] parts, int currentPart) {
        this._WifiNamesOrAddresses = LlamaStorage.SimpleUnescape(parts[currentPart + 1]).split("\\|");
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        ArrayList<String> names = new ArrayList(Arrays.asList(this._WifiNamesOrAddresses));
        for (int i = 0; i < names.size(); i++) {
            if (((String) names.get(i)).equals(ANY_WIFI_NETWORK)) {
                names.set(i, context.getString(R.string.hrAnyWifiNetwork));
            }
        }
        sb.append(GetFormattedConditionDescription(context, Helpers.ConcatenateListOfStrings(names, ", ", " " + context.getString(R.string.hrOr) + " ")));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(IterableHelpers.ConcatenateString(Arrays.asList(this._WifiNamesOrAddresses), "|")));
    }

    public PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        int i;
        WifiManager wifi = (WifiManager) context.getSystemService("wifi");
        final String friendlyAnyWifiNetwork = context.getString(R.string.hrAnyWifiNetwork);
        List<WifiConfiguration> networks = wifi.getConfiguredNetworks();
        List<ScanResult> scanResults = wifi.getScanResults();
        HashSet<String> pickerValues = new HashSet();
        if (networks != null) {
            for (WifiConfiguration network : networks) {
                if (network.SSID != null) {
                    String value = network.SSID;
                    if (value.startsWith("\"")) {
                        value = value.substring(1);
                    }
                    if (value.endsWith("\"")) {
                        value = value.substring(0, value.length() - 1);
                    }
                    pickerValues.add(value);
                }
                if (network.BSSID != null) {
                    pickerValues.add(network.BSSID);
                }
            }
        }
        if (scanResults != null) {
            for (ScanResult s : scanResults) {
                if (s.SSID != null) {
                    pickerValues.add(s.SSID);
                }
                if (s.BSSID != null) {
                    pickerValues.add(s.BSSID);
                }
            }
        }
        for (String existingAddress : this._WifiNamesOrAddresses) {
            if (!(pickerValues.contains(existingAddress) || existingAddress.equals(ANY_WIFI_NETWORK))) {
                pickerValues.add(existingAddress);
            }
        }
        List<String> orderedNames = IterableHelpers.OrderBy(pickerValues);
        final String[] names = new String[(orderedNames.size() + 1)];
        names[0] = friendlyAnyWifiNetwork;
        for (i = 1; i < names.length; i++) {
            names[i] = (String) orderedNames.get(i - 1);
        }
        ArrayList<String> selectedItems = new ArrayList(Arrays.asList(this._WifiNamesOrAddresses));
        for (i = 0; i < selectedItems.size(); i++) {
            if (((String) selectedItems.get(i)).equals(ANY_WIFI_NETWORK)) {
                selectedItems.set(i, friendlyAnyWifiNetwork);
            }
        }
        return CreateListPreferenceMultiselect(context, GetPrefrenceDialogTitle(context), names, names, selectedItems, new OnGetValueEx<T>() {
            public T GetValue(Preference preference) {
                List<Integer> selectedIndexes = ((ListPreferenceMultiselect) preference).getSelectedValueIndexes();
                String[] selectedVales = new String[selectedIndexes.size()];
                for (int index = 0; index < selectedIndexes.size(); index++) {
                    int i = ((Integer) selectedIndexes.get(index)).intValue();
                    if (names[i].equals(friendlyAnyWifiNetwork)) {
                        selectedVales[index] = WifiNetworkConditionBase.ANY_WIFI_NETWORK;
                    } else {
                        selectedVales[index] = names[i];
                    }
                }
                return WifiNetworkConditionBase.this.CreateSelf(selectedVales);
            }
        });
    }

    public String GetIsValidError(Context context) {
        return this._WifiNamesOrAddresses.length > 0 ? null : context.getString(R.string.hrPleaseChooseAtLeastOneWifiNetwork);
    }
}
