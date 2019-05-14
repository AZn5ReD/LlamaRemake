package com.kebab.Llama;

public abstract class WifiBeacon extends Beacon {
    public boolean CanSimpleDetectArea() {
        return true;
    }

    public boolean IsMapBased() {
        return false;
    }
}
