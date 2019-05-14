package com.kebab.Llama;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.accessibility.AccessibilityEvent;

public class LlamaAccessiblityService extends AccessibilityService {
    public void onAccessibilityEvent(AccessibilityEvent ae) {
        final StringBuilder sb = new StringBuilder();
        if (((Boolean) LlamaSettings.DebugAccessiblity.GetValue(this)).booleanValue()) {
            sb.append("getBeforeText=").append(ae.getBeforeText()).append("\n");
            sb.append("getAddedCount=").append(ae.getAddedCount()).append("\n");
            sb.append("getCurrentItemIndex=").append(ae.getCurrentItemIndex()).append("\n");
            sb.append("getContentDescription=").append(ae.getContentDescription()).append("\n");
            sb.append("getEventTime=").append(ae.getEventTime()).append("\n");
            sb.append("getEventType=").append(ae.getEventType()).append("\n");
            sb.append("getFromIndex=").append(ae.getFromIndex()).append("\n");
            sb.append("getPackageName=").append(ae.getPackageName()).append("\n");
            sb.append("getRemovedCount=").append(ae.getRemovedCount()).append("\n");
            sb.append("getParcelableData=").append(ae.getParcelableData()).append("\n");
            Parcelable parcelable = ae.getParcelableData();
            if (parcelable != null) {
                sb.append("ParcelDescribe=").append(parcelable.describeContents());
                sb.append("ParcelClass=").append(parcelable.getClass().getName());
            }
            for (CharSequence t : ae.getText()) {
                sb.append("getText=").append(t);
            }
        }
        if (ae.getEventType() == 64) {
            CharSequence packageName = ae.getPackageName();
            if (!getPackageName().equals(packageName)) {
                CharSequence tickerText = null;
                int number = -1000;
                try {
                    Notification n = (Notification) ae.getParcelableData();
                    if (n != null) {
                        tickerText = n.tickerText;
                        number = n.number;
                    }
                } catch (Exception ex) {
                    Logging.Report(ex, (Context) this);
                }
                if (((Boolean) LlamaSettings.DebugAccessiblity.GetValue(this)).booleanValue()) {
                    sb.append("tickerText=").append(tickerText);
                    sb.append("number=").append(number);
                    new Thread(new Runnable() {
                        public void run() {
                            Logging.Report("Accessibility", sb.toString(), LlamaAccessiblityService.this);
                        }
                    }).start();
                }
                Intent i = new Intent(this, LlamaService.class);
                i.setAction(Constants.ACTION_UI_NOTIFICATION);
                i.putExtra(Constants.EXTRA_PACKAGE_NAME, packageName);
                String str = Constants.EXTRA_TICKER_TEXT;
                if (tickerText == null) {
                    tickerText = "";
                }
                i.putExtra(str, tickerText);
                startService(i);
            }
        }
    }

    public void onInterrupt() {
    }

    /* Access modifiers changed, original: protected */
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = 64;
        info.feedbackType = 16;
        setServiceInfo(info);
    }
}
