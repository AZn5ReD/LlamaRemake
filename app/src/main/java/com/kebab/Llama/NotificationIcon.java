package com.kebab.Llama;

public class NotificationIcon {
    public static final int COLOUR_AUTO = 0;
    public static final int COLOUR_BLACK = 9;
    public static final int COLOUR_BLUE = 5;
    public static final int COLOUR_CYAN = 8;
    public static final int COLOUR_GREEN = 4;
    public static final int COLOUR_ORANGE = 2;
    public static final int COLOUR_PINK = 7;
    public static final int COLOUR_PURPLE = 6;
    public static final int COLOUR_RED = 1;
    public static final int COLOUR_WHITE = 10;
    public static final int COLOUR_YELLOW = 3;

    public static int GetResourceId(int colour, int dots, boolean locked, boolean alternativeIconColour) {
        if (colour == 0) {
            colour = alternativeIconColour ? 9 : 10;
        }
        switch (colour) {
            case 1:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1red_locked : R.drawable.llamanotify1red;
                    case 2:
                        return locked ? R.drawable.llamanotify2red_locked : R.drawable.llamanotify2red;
                    case 3:
                        return locked ? R.drawable.llamanotify3red_locked : R.drawable.llamanotify3red;
                    case 4:
                        return locked ? R.drawable.llamanotify4red_locked : R.drawable.llamanotify4red;
                    default:
                        return locked ? R.drawable.llamanotify0red_locked : R.drawable.llamanotify0red;
                }
            case 2:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1orange_locked : R.drawable.llamanotify1orange;
                    case 2:
                        return locked ? R.drawable.llamanotify2orange_locked : R.drawable.llamanotify2orange;
                    case 3:
                        return locked ? R.drawable.llamanotify3orange_locked : R.drawable.llamanotify3orange;
                    case 4:
                        return locked ? R.drawable.llamanotify4orange_locked : R.drawable.llamanotify4orange;
                    default:
                        return locked ? R.drawable.llamanotify0orange_locked : R.drawable.llamanotify0orange;
                }
            case 3:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1yellow_locked : R.drawable.llamanotify1yellow;
                    case 2:
                        return locked ? R.drawable.llamanotify2yellow_locked : R.drawable.llamanotify2yellow;
                    case 3:
                        return locked ? R.drawable.llamanotify3yellow_locked : R.drawable.llamanotify3yellow;
                    case 4:
                        return locked ? R.drawable.llamanotify4yellow_locked : R.drawable.llamanotify4yellow;
                    default:
                        return locked ? R.drawable.llamanotify0yellow_locked : R.drawable.llamanotify0yellow;
                }
            case 4:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1green_locked : R.drawable.llamanotify1green;
                    case 2:
                        return locked ? R.drawable.llamanotify2green_locked : R.drawable.llamanotify2green;
                    case 3:
                        return locked ? R.drawable.llamanotify3green_locked : R.drawable.llamanotify3green;
                    case 4:
                        return locked ? R.drawable.llamanotify4green_locked : R.drawable.llamanotify4green;
                    default:
                        return locked ? R.drawable.llamanotify0green_locked : R.drawable.llamanotify0green;
                }
            case 5:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1blue_locked : R.drawable.llamanotify1blue;
                    case 2:
                        return locked ? R.drawable.llamanotify2blue_locked : R.drawable.llamanotify2blue;
                    case 3:
                        return locked ? R.drawable.llamanotify3blue_locked : R.drawable.llamanotify3blue;
                    case 4:
                        return locked ? R.drawable.llamanotify4blue_locked : R.drawable.llamanotify4blue;
                    default:
                        return locked ? R.drawable.llamanotify0blue_locked : R.drawable.llamanotify0blue;
                }
            case 6:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1purple_locked : R.drawable.llamanotify1purple;
                    case 2:
                        return locked ? R.drawable.llamanotify2purple_locked : R.drawable.llamanotify2purple;
                    case 3:
                        return locked ? R.drawable.llamanotify3purple_locked : R.drawable.llamanotify3purple;
                    case 4:
                        return locked ? R.drawable.llamanotify4purple_locked : R.drawable.llamanotify4purple;
                    default:
                        return locked ? R.drawable.llamanotify0purple_locked : R.drawable.llamanotify0purple;
                }
            case 7:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1pink_locked : R.drawable.llamanotify1pink;
                    case 2:
                        return locked ? R.drawable.llamanotify2pink_locked : R.drawable.llamanotify2pink;
                    case 3:
                        return locked ? R.drawable.llamanotify3pink_locked : R.drawable.llamanotify3pink;
                    case 4:
                        return locked ? R.drawable.llamanotify4pink_locked : R.drawable.llamanotify4pink;
                    default:
                        return locked ? R.drawable.llamanotify0pink_locked : R.drawable.llamanotify0pink;
                }
            case 8:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1cyan_locked : R.drawable.llamanotify1cyan;
                    case 2:
                        return locked ? R.drawable.llamanotify2cyan_locked : R.drawable.llamanotify2cyan;
                    case 3:
                        return locked ? R.drawable.llamanotify3cyan_locked : R.drawable.llamanotify3cyan;
                    case 4:
                        return locked ? R.drawable.llamanotify4cyan_locked : R.drawable.llamanotify4cyan;
                    default:
                        return locked ? R.drawable.llamanotify0cyan_locked : R.drawable.llamanotify0cyan;
                }
            case 9:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1black_locked : R.drawable.llamanotify1black;
                    case 2:
                        return locked ? R.drawable.llamanotify2black_locked : R.drawable.llamanotify2black;
                    case 3:
                        return locked ? R.drawable.llamanotify3black_locked : R.drawable.llamanotify3black;
                    case 4:
                        return locked ? R.drawable.llamanotify4black_locked : R.drawable.llamanotify4black;
                    default:
                        return locked ? R.drawable.llamanotify0black_locked : R.drawable.llamanotify0black;
                }
            case 10:
                switch (dots) {
                    case 1:
                        return locked ? R.drawable.llamanotify1white_locked : R.drawable.llamanotify1white;
                    case 2:
                        return locked ? R.drawable.llamanotify2white_locked : R.drawable.llamanotify2white;
                    case 3:
                        return locked ? R.drawable.llamanotify3white_locked : R.drawable.llamanotify3white;
                    case 4:
                        return locked ? R.drawable.llamanotify4white_locked : R.drawable.llamanotify4white;
                    default:
                        return locked ? R.drawable.llamanotify0white_locked : R.drawable.llamanotify0white;
                }
            default:
                return R.drawable.llamanotify1black;
        }
    }
}
