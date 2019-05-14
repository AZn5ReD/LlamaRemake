package com.kebab;

import android.app.Activity;
import android.content.Intent;
import java.util.HashSet;
import java.util.Iterator;

public interface ResultRegisterableActivity {

    public interface ResultCallback {
        void HandleResult(int i, Intent intent, Object obj);
    }

    public static class Helper {
        public static void AddBeforeOnDestroyHandler(HashSet<Runnable> activitysHashMap, Runnable runnable) {
            activitysHashMap.add(runnable);
        }

        public static void ClearOnDestroyHandler(HashSet<Runnable> activitysHashMap, Runnable runnable) {
            activitysHashMap.remove(runnable);
        }

        public static void HandleOnDestroy(HashSet<Runnable> onDestroyRunnables) {
            Iterator i$ = onDestroyRunnables.iterator();
            while (i$.hasNext()) {
                ((Runnable) i$.next()).run();
            }
        }
    }

    void AddBeforeOnDestroyHandler(Runnable runnable);

    Activity GetActivity();

    void RegisterActivityResult(Intent intent, ResultCallback resultCallback, Object obj);

    void RemoveBeforeOnDestroyHandler(Runnable runnable);
}
