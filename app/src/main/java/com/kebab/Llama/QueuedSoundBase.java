package com.kebab.Llama;

import android.media.AudioManager;

public abstract class QueuedSoundBase {
    protected String _EventName;
    protected int _StreamId;

    public abstract void PlaySound(QueuedSoundPlayer queuedSoundPlayer, AudioManager audioManager);

    public abstract void StopSound(QueuedSoundPlayer queuedSoundPlayer);

    public abstract String getSimpleDescription();

    public QueuedSoundBase(int streamId, String eventName) {
        this._StreamId = streamId;
        this._EventName = eventName;
    }
}
