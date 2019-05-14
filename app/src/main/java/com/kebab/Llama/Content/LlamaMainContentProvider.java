package com.kebab.Llama.Content;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import com.kebab.HelpersC;
import com.kebab.IterableHelpers;
import com.kebab.Llama.LlamaStorage;
import com.kebab.Llama.Profile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class LlamaMainContentProvider extends ContentProvider {
    public static final String COLUMN_PROFILE_NAME = "profileName";
    public static final Uri CONTENT_URI = Uri.parse("content://com.kebab.llama.main");
    public static final String PATH_SEGMENT_PROFILES = "profiles";
    public static final Uri CONTENT_PROFILES_URI = Uri.withAppendedPath(CONTENT_URI, PATH_SEGMENT_PROFILES);

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    public String getType(Uri uri) {
        return null;
    }

    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    public boolean onCreate() {
        return false;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() == 0) {
            return null;
        }
        if (!((String) pathSegments.get(0)).equals(PATH_SEGMENT_PROFILES)) {
            return null;
        }
        String[] columns = new String[]{COLUMN_PROFILE_NAME};
        verifyProjection(columns, projection);
        Cursor c = new MatrixCursor(columns);
        ArrayList<Profile> profiles = new LlamaStorage().LoadProfiles(getContext());
        if (COLUMN_PROFILE_NAME.equals(sortOrder)) {
            Collections.sort(profiles, Profile.NameComparator);
        }
        Iterator i$ = profiles.iterator();
        while (i$.hasNext()) {
            ((MatrixCursor) c).addRow(new String[]{((Profile) i$.next()).Name});
        }
        return c;
    }

    private void verifyProjection(String[] columns, String[] projection) {
        if (projection != null) {
            if (columns == null) {
                throw new RuntimeException("Internal content provider problem.");
            } else if (columns.length != projection.length) {
                throw new RuntimeException("Content provider programmer was lazy. Must request exact column layout: " + IterableHelpers.ConcatenateString(columns, ","));
            } else {
                int i = 0;
                while (i < columns.length) {
                    if (HelpersC.StringEquals(columns[i], projection[i])) {
                        i++;
                    } else {
                        throw new RuntimeException("Content provider programmer was lazy. Must request exact column layout: " + IterableHelpers.ConcatenateString(columns, ","));
                    }
                }
            }
        }
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
