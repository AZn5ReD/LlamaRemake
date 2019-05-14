package com.kebab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.kebab.Llama.R;

public class AlertDialogEx {

    public static class Builder extends android.app.AlertDialog.Builder {
        protected Activity _Activity;

        public Builder(Activity activity) {
            super(CreateCompatibleTheme(activity));
            this._Activity = activity;
        }

        public Builder(Context context, boolean tryInferActivity) {
            super(CreateCompatibleTheme(context));
            if (tryInferActivity && (context instanceof Activity)) {
                this._Activity = (Activity) context;
            }
        }

        public AlertDialog create() {
            AlertDialog result = super.create();
            if (this._Activity != null) {
                result.setOwnerActivity(this._Activity);
            }
            return result;
        }

        static ContextThemeWrapper CreateCompatibleTheme(Context context) {
            return new ContextThemeWrapper(context, R.style.LlamaThemeCompat);
        }
    }
}
