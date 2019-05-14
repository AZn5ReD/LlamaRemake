package com.kebab.Llama;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

public class QueuedSound extends QueuedSoundBase {
    MediaPlayer _MediaPlayer;
    String _ToneName;
    String _ToneUri;

    public QueuedSound(String toneUri, String toneName, int streamId, String eventName) {
        super(streamId, eventName);
        this._ToneUri = toneUri;
        this._ToneName = toneName;
    }

    public void PlaySound(final QueuedSoundPlayer host, AudioManager audioManager) {
        try {
            int volume = audioManager.getStreamVolume(this._StreamId);
            if (volume != 0) {
                Logging.Report(QueuedSoundPlayer.TAG, "Playing " + this._ToneUri + " through " + this._StreamId + " with volume " + volume, host._Service);
                this._MediaPlayer = host.getMediaPlayerInstance();
                this._MediaPlayer.setWakeMode(host._Service, 1);
                this._MediaPlayer.setDataSource(host._Service, Uri.parse(this._ToneUri));
                this._MediaPlayer.setAudioStreamType(this._StreamId);
                this._MediaPlayer.setLooping(false);
                this._MediaPlayer.prepare();
                this._MediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        QueuedSound.this.OnSoundStopped(host);
                    }
                });
                this._MediaPlayer.start();
                return;
            }
            Logging.Report(QueuedSoundPlayer.TAG, "Not playing " + this._ToneUri + " through " + this._StreamId + " with volume " + volume, host._Service);
            OnSoundStopped(host);
        } catch (Exception ex) {
            Logging.Report(ex, host._Service);
            host._Service.HandleFriendlyError("Failed to play a ringtone for event '" + this._EventName + "'", false);
            OnSoundErrored(host);
        }
    }

    private void OnSoundStopped(QueuedSoundPlayer host) {
        host.onMediaFinished(this);
    }

    private void OnSoundErrored(QueuedSoundPlayer host) {
        host.onMediaErrored(this);
    }

    public void StopSound(QueuedSoundPlayer host) {
        if (this._MediaPlayer != null) {
            this._MediaPlayer.stop();
            host.onMediaFinished(this);
        }
    }

    public String getSimpleDescription() {
        return this._ToneName;
    }
}
