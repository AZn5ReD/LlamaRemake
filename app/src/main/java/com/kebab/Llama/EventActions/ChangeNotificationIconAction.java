package com.kebab.Llama.EventActions;

import com.kebab.Llama.Constants;
import com.kebab.Tuple;

public class ChangeNotificationIconAction extends EventFragmentCompat<ChangeNotificationIconAction> {
    String _IconName;
    int _NotificationIcon;

    public ChangeNotificationIconAction(int notificationIcon) {
        this._NotificationIcon = notificationIcon;
    }

    /* Access modifiers changed, original: protected */
    public int GetPartsConsumption() {
        return 1;
    }

    public static ChangeNotificationIconAction CreateFrom(String[] parts, int currentPart) {
        return new ChangeNotificationIconAction(Integer.parseInt(parts[currentPart + 1]));
    }

    public static Tuple<Integer, Integer> ConvertLegacy(ChangeNotificationIconAction notificationIcon) {
        return ConvertLegacy(notificationIcon._NotificationIcon);
    }

    public static Tuple<Integer, Integer> ConvertLegacy(int value) {
        switch (value) {
            case 1:
                return new Tuple(Integer.valueOf(0), Integer.valueOf(1));
            case 2:
                return new Tuple(Integer.valueOf(0), Integer.valueOf(2));
            case 3:
                return new Tuple(Integer.valueOf(0), Integer.valueOf(3));
            case 4:
                return new Tuple(Integer.valueOf(0), Integer.valueOf(4));
            case Constants.LLAMA_ICON_RED /*51*/:
                return new Tuple(Integer.valueOf(1), Integer.valueOf(0));
            case Constants.LLAMA_ICON_ORANGE /*52*/:
                return new Tuple(Integer.valueOf(2), Integer.valueOf(0));
            case Constants.LLAMA_ICON_YELLOW /*53*/:
                return new Tuple(Integer.valueOf(3), Integer.valueOf(0));
            case Constants.LLAMA_ICON_GREEN /*54*/:
                return new Tuple(Integer.valueOf(4), Integer.valueOf(0));
            case Constants.LLAMA_ICON_BLUE /*55*/:
                return new Tuple(Integer.valueOf(5), Integer.valueOf(0));
            case Constants.LLAMA_ICON_PURPLE /*56*/:
                return new Tuple(Integer.valueOf(6), Integer.valueOf(0));
            case Constants.LLAMA_ICON_PINK /*57*/:
                return new Tuple(Integer.valueOf(7), Integer.valueOf(0));
            case Constants.LLAMA_ICON_BLACK /*58*/:
                return new Tuple(Integer.valueOf(9), Integer.valueOf(0));
            case Constants.LLAMA_ICON_WHITE /*59*/:
                return new Tuple(Integer.valueOf(10), Integer.valueOf(0));
            default:
                return new Tuple(Integer.valueOf(0), Integer.valueOf(0));
        }
    }
}
