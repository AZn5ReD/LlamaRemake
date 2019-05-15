package com.kebab.Llama;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import com.kebab.RunnableArg;
import java.util.HashMap;
import java.util.LinkedList;

public class QueuedSoundPlayer {
    public static final int MAX_QUEUED_SOUNDS = 20;
    public static final String TAG = "SoundPlayer";
    QueuedSoundBase _CurrentSound;
    MediaPlayer _MediaPlayer;
    Notification _Notification;
    NotificationManager _NotificationManager;
    LinkedList<QueuedSoundBase> _QueuedSounds = new LinkedList();
    LlamaService _Service;
    TextToSpeech _Tts;

    public QueuedSoundPlayer(LlamaService service) {
        this._Service = service;
    }

    public void EnqueueAndPlay(QueuedSoundBase sound) {
        if (this._QueuedSounds.size() > 20) {
            Logging.Report(TAG, "Too many sounds queued", this._Service);
            return;
        }
        this._QueuedSounds.addLast(sound);
        if (this._CurrentSound == null) {
            PlayNextMedia();
        } else {
            UpdateIconCounts();
        }
    }

    public MediaPlayer getMediaPlayerInstance() {
        if (this._MediaPlayer == null) {
            this._MediaPlayer = new MediaPlayer();
        } else {
            this._MediaPlayer.reset();
        }
        return this._MediaPlayer;
    }

    public void getTextToSpeechInstance(final RunnableArg<TextToSpeech> onInitRunnable) {
        if (this._Tts == null) {
            this._Tts = new TextToSpeech(this._Service, new OnInitListener() {
                public void onInit(int arg0) {
                    Logging.Report("Speech", "Created TTS", QueuedSoundPlayer.this._Service);
                    onInitRunnable.Run(QueuedSoundPlayer.this._Tts);
                }
            });
            return;
        }
        this._Tts.stop();
        onInitRunnable.Run(this._Tts);
    }

    public void onMediaFinished(QueuedSoundBase soundBase) {
        Logging.Report(TAG, "Finished playing sound for " + soundBase._EventName, this._Service);
        this._CurrentSound = null;
        PlayNextMedia();
    }

    public void onMediaErrored(QueuedSound queuedSound) {
        Logging.Report(TAG, "Sound " + queuedSound._ToneUri + " errored. Releasing MediaPlayer", this._Service);
        this._MediaPlayer.release();
        this._MediaPlayer = null;
        onMediaFinished(queuedSound);
    }

    /* Access modifiers changed, original: 0000 */
    public void PlayNextMedia() {
        this._CurrentSound = (QueuedSoundBase) this._QueuedSounds.poll();
        if (this._CurrentSound == null) {
            Logging.Report(TAG, "No more sounds. Cleaning up.", this._Service);
            PerformCleanup();
            RemoveIcon();
            return;
        }
        Logging.Report(TAG, "Playing next sound", this._Service);
        EnsureIconIfNeeded(this._CurrentSound._EventName, this._CurrentSound.getSimpleDescription());
        this._CurrentSound.PlaySound(this, this._Service.getAudioManager());
    }

    public void StopAll() {
        this._QueuedSounds.clear();
        if (this._CurrentSound != null) {
            this._CurrentSound.StopSound(this);
            this._CurrentSound = null;
        }
        RemoveIcon();
    }

    private void RemoveIcon() {
        ((NotificationManager) this._Service.getSystemService("notification")).cancel(Constants.SOUND_PLAYER_NOTIFICATION_ID);
        this._Notification = null;
    }

    private void EnsureIconIfNeeded(String eventName, String soundDescription) {
        if (((Boolean) LlamaSettings.NotificationIconForSounds.GetValue(this._Service)).booleanValue()) {
            Notification notification;
            Intent intent = new Intent(this._Service, LlamaService.class);
            intent.setAction(Constants.ACTION_STOP_ALL_SOUNDS);
            PendingIntent pendingIntent = PendingIntent.getService(this._Service, 0, intent, 0);
            if (this._Notification == null) {
                this._Notification = new Notification();
                this._Notification.icon = ((Boolean) LlamaSettings.BlackIcons.GetValue(this._Service)).booleanValue() ? R.drawable.llamanotifymusicb : R.drawable.llamanotifymusicw;
                notification = this._Notification;
                this._Notification.deleteIntent = pendingIntent;
                notification.contentIntent = pendingIntent;
                this._Notification.flags = 16;
            }
            this._Notification.when = System.currentTimeMillis();
            int soundCount = this._QueuedSounds.size() + 1;
            notification = this._Notification;
            if (soundCount <= 1) {
                soundCount = 0;
            }
            notification.number = soundCount;
//            this._Notification.setLatestEventInfo(this._Service, this._Service.getString(R.string.hrNoisyLlamaTapToStop), this._Service.getString(R.string.hrEventColonSoundDescription, new Object[]{eventName, soundDescription}), pendingIntent);
            ((NotificationManager) this._Service.getSystemService("notification")).notify(Constants.SOUND_PLAYER_NOTIFICATION_ID, this._Notification);
        }
    }

    private void UpdateIconCounts() {
        if (((Boolean) LlamaSettings.NotificationIconForSounds.GetValue(this._Service)).booleanValue()) {
            int soundCount = this._QueuedSounds.size();
            if (this._CurrentSound != null) {
                soundCount++;
            }
            Notification notification = this._Notification;
            if (soundCount <= 1) {
                soundCount = 0;
            }
            notification.number = soundCount;
            ((NotificationManager) this._Service.getSystemService("notification")).notify(Constants.SOUND_PLAYER_NOTIFICATION_ID, this._Notification);
        }
    }

    private void PerformCleanup() {
        if (this._MediaPlayer != null) {
            Logging.Report(TAG, "Cleaning up media player", this._Service);
            this._MediaPlayer.release();
            this._MediaPlayer = null;
        }
        if (this._Tts != null) {
            Logging.Report(TAG, "Cleaning up TTS", this._Service);
            if (((Boolean) LlamaSettings.ExtraTtsCleanup.GetValue(this._Service)).booleanValue()) {
                final TextToSpeech moo = this._Tts;
                this._Tts = null;
                HashMap<String, String> params = new HashMap();
                params.put("streamType", String.valueOf(3));
                params.put("utteranceId", "utter");
                params.put("volume", "0.05");
                moo.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
                    public void onUtteranceCompleted(String utteranceId) {
                        Logging.Report(QueuedSoundPlayer.TAG, "ExtraTtsCleanup - shutting down TTS", QueuedSoundPlayer.this._Service);
                        moo.shutdown();
                    }
                });
                Logging.Report(TAG, "ExtraTtsCleanup - sending blank speak through music stream", this._Service);
                moo.speak((String) LlamaSettings.ExtraTtsCleanupText.GetValue(this._Service), 1, params);
                this._Tts = null;
                return;
            }
            this._Tts.shutdown();
            this._Tts = null;
        }
    }
}
