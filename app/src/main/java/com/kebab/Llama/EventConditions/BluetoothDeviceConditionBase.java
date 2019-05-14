package com.kebab.Llama.EventConditions;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import com.kebab.Helpers;
import com.kebab.IterableHelpers;
import com.kebab.ListPreferenceMultiselect;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.R;
import com.kebab.OnGetValueEx;
import com.kebab.PreferenceEx;
import com.kebab.Selector;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public abstract class BluetoothDeviceConditionBase<T> extends EventCondition<T> {
    String[] _BluetoothAddresses;
    String[] _BluetoothNames;

    public abstract T CreateSelf(String[] strArr, String[] strArr2);

    public abstract String GetFormattedConditionDescription(Context context, String str);

    public abstract String GetPrefrenceDialogTitle(Context context);

    public BluetoothDeviceConditionBase(String[] bluetoothAddresses, String[] bluetoothNames) {
        this._BluetoothAddresses = bluetoothAddresses;
        this._BluetoothNames = bluetoothNames;
    }

    protected BluetoothDeviceConditionBase(String[] parts, int currentPart) {
        this._BluetoothNames = LlamaStorage.SimpleUnescape(parts[currentPart + 1]).split("\\|");
        this._BluetoothAddresses = LlamaStorage.SimpleUnescape(parts[currentPart + 2]).split("\\|");
    }

    public boolean RenameArea(String oldName, String newName) {
        return false;
    }

    public void AppendConditionSimple(Context context, Appendable sb) throws IOException {
        sb.append(GetFormattedConditionDescription(context, Helpers.ConcatenateListOfStrings(Arrays.asList(this._BluetoothNames), ", ", " " + context.getString(R.string.hrOr) + " ")));
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 2;
    }

    /* Access modifiers changed, original: protected */
    public void ToPsvInternal(StringBuilder sb) {
        sb.append(LlamaStorage.SimpleEscape(IterableHelpers.ConcatenateString(Arrays.asList(this._BluetoothNames), "|")));
        sb.append("|");
        sb.append(LlamaStorage.SimpleEscape(IterableHelpers.ConcatenateString(Arrays.asList(this._BluetoothAddresses), "|")));
    }

    public PreferenceEx<T> CreatePreference(PreferenceActivity context) {
        Set<BluetoothDevice> devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        BluetoothDevice[] devicesArray = (BluetoothDevice[]) devices.toArray(new BluetoothDevice[devices.size()]);
        Arrays.sort(devicesArray, new Comparator<BluetoothDevice>() {
            public int compare(BluetoothDevice x, BluetoothDevice y) {
                String xName = x.getName();
                String yName = y.getName();
                if (xName == null) {
                    xName = "";
                }
                if (yName == null) {
                    yName = "";
                }
                return xName.compareToIgnoreCase(yName);
            }
        });
        List<String> namesList = IterableHelpers.Select(Arrays.asList(devicesArray), new Selector<BluetoothDevice, String>() {
            public String Do(BluetoothDevice value) {
                return value.getName();
            }
        });
        List<String> addressesList = IterableHelpers.Select(Arrays.asList(devicesArray), new Selector<BluetoothDevice, String>() {
            public String Do(BluetoothDevice value) {
                return value.getAddress();
            }
        });
        for (int i = 0; i < this._BluetoothAddresses.length; i++) {
            String existingAddress = this._BluetoothAddresses[i];
            if (addressesList.indexOf(existingAddress) < 0) {
                addressesList.add(existingAddress);
                namesList.add(this._BluetoothNames[i]);
            }
        }
        final String[] names = (String[]) IterableHelpers.ToArray(namesList, String.class);
        final String[] addresses = (String[]) IterableHelpers.ToArray(addressesList, String.class);
        return CreateListPreferenceMultiselect(context, GetPrefrenceDialogTitle(context), names, addresses, Arrays.asList(this._BluetoothAddresses), new OnGetValueEx<T>() {
            public T GetValue(Preference preference) {
                List<Integer> selectedIndexes = ((ListPreferenceMultiselect) preference).getSelectedValueIndexes();
                String[] selectedAddresses = new String[selectedIndexes.size()];
                String[] selectedNames = new String[selectedIndexes.size()];
                for (int index = 0; index < selectedIndexes.size(); index++) {
                    int i = ((Integer) selectedIndexes.get(index)).intValue();
                    selectedAddresses[index] = addresses[i];
                    selectedNames[index] = names[i];
                }
                return BluetoothDeviceConditionBase.this.CreateSelf(selectedAddresses, selectedNames);
            }
        });
    }

    public String GetIsValidError(Context context) {
        return this._BluetoothAddresses.length > 0 ? null : context.getString(R.string.hrPleaseChooseAtLeastOneBluetoothDevice);
    }
}
