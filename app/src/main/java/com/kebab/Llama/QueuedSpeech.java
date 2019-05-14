package com.kebab.Llama;

import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import com.kebab.RunnableArg;
import java.util.HashMap;

public class QueuedSpeech extends QueuedSoundBase {
    String _Text;
    TextToSpeech _Tts;

    public QueuedSpeech(String text, int streamId, String eventName) {
        super(streamId, eventName);
        this._Text = text;
    }

    public void PlaySound(final QueuedSoundPlayer host, AudioManager audioManager) {
        try {
            int volume = audioManager.getStreamVolume(this._StreamId);
            if (volume != 0) {
                Logging.Report(QueuedSoundPlayer.TAG, "Saying " + this._Text + " through " + this._StreamId + " with volume " + volume, host._Service);
                host.getTextToSpeechInstance(new RunnableArg<TextToSpeech>() {
                    public void Run(TextToSpeech tts) {
                        QueuedSpeech.this._Tts = tts;
                        HashMap<String, String> params = new HashMap();
                        params.put("streamType", String.valueOf(QueuedSpeech.this._StreamId));
                        params.put("utteranceId", "utter");
                        QueuedSpeech.this._Tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
                            public void onUtteranceCompleted(String utteranceId) {
                                QueuedSpeech.this.OnSoundStopped(host);
                            }
                        });
                        QueuedSpeech.this._Tts.speak(QueuedSpeech.this._Text, 1, params);
                    }
                });
                return;
            }
            Logging.Report(QueuedSoundPlayer.TAG, "Not saying " + this._Text + " through " + this._StreamId + " with volume " + volume, host._Service);
            OnSoundStopped(host);
        } catch (Exception ex) {
            Logging.Report(ex, host._Service);
            host._Service.HandleFriendlyError("Failed to play a ringtone for event '" + this._EventName + "'", false);
        }
    }

    private void OnSoundStopped(QueuedSoundPlayer host) {
        host.onMediaFinished(this);
    }

    public void StopSound(QueuedSoundPlayer host) {
        if (this._Tts != null) {
            this._Tts.stop();
            host.onMediaFinished(this);
        }
    }

    public String getSimpleDescription() {
        return this._Text;
    }
}
