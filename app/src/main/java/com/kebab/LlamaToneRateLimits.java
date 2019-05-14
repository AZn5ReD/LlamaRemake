package com.kebab;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import java.util.HashMap;
import java.util.Map.Entry;

public class LlamaToneRateLimits {
    private static LlamaToneRateLimits _Instance;
    private static Object _Locker = new Object();
    HashMap<Integer, Long> _LlamaToneRateLimits = new HashMap();

    public static LlamaToneRateLimits Instance(Context context) {
        if (_Instance == null) {
            synchronized (_Locker) {
                if (_Instance == null) {
                    _Instance = new LlamaToneRateLimits(context);
                }
            }
        }
        return _Instance;
    }

    private LlamaToneRateLimits(Context context) {
        long currentMillis = System.currentTimeMillis();
        for (Entry<String, ?> kvp : context.getApplicationContext().getSharedPreferences("LLAMATONERATES", 0).getAll().entrySet()) {
            int rateLimitId = Helpers.ParseIntOrNull((String) kvp.getKey()).intValue();
            long millisSinceEpoch = ((Long) kvp.getValue()).longValue();
            if (millisSinceEpoch >= currentMillis) {
                this._LlamaToneRateLimits.put(Integer.valueOf(rateLimitId), Long.valueOf(millisSinceEpoch));
            }
        }
    }

    public int GetNewRateLimitId() {
        int i = 1;
        Integer freshValue = null;
        do {
            if (this._LlamaToneRateLimits.get(Integer.valueOf(i)) == null) {
                freshValue = Integer.valueOf(i);
                continue;
            } else {
                i++;
                continue;
            }
        } while (freshValue == null);
        return freshValue.intValue();
    }

    public boolean CanLlamaTonePlay(int rateLimitId) {
        Long rateLimitMillis = (Long) this._LlamaToneRateLimits.get(Integer.valueOf(rateLimitId));
        if (rateLimitMillis == null) {
            return true;
        }
        if (rateLimitMillis.longValue() > System.currentTimeMillis()) {
            return false;
        }
        return true;
    }

    public void RegisterLlamaTonePlayed(Context context, int rateLimitId, int rateLimitSeconds) {
        long limitUntilMillis = System.currentTimeMillis() + ((long) (rateLimitSeconds * 1000));
        this._LlamaToneRateLimits.put(Integer.valueOf(rateLimitId), Long.valueOf(limitUntilMillis));
        Editor editor = context.getApplicationContext().getSharedPreferences("LLAMATONERATES", 0).edit();
        editor.putLong(String.valueOf(rateLimitId), limitUntilMillis);
        HelpersC.CommitPrefs(editor, context);
    }
}
