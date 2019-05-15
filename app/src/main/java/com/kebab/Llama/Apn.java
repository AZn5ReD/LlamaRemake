package com.kebab.Llama;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

public class Apn {
    private static final String COLUMN_APN = "apn";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TYPE = "type";
    private static final Uri CURRENT_APNS = Uri.parse("content://telephony/carriers/current");
    private static final String MMS = "mms";
    private static final String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_APN, COLUMN_TYPE};
    public static final int STATE_DISABLED = 2;
    public static final int STATE_ENABLED = 1;
    public static final int STATE_NO_APNS = 0;
    public static final String SUFFIX_APN_DROID = "apndroid";

    public boolean IsApnEnabled(Context context) {
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        Cursor cursor = null;
        boolean result = true;
        String[] args = new String[1];
        try {
            cursor = resolver.query(CURRENT_APNS, PROJECTION, null, null, null);
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int apnIndex = cursor.getColumnIndex(COLUMN_APN);
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String originalApnValue = cursor.getString(apnIndex);
                if (originalApnValue != null && originalApnValue.endsWith(SUFFIX_APN_DROID)) {
                    result = false;
                }
                cursor.moveToNext();
            }
            if (cursor != null) {
                cursor.close();
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return false;
    }

    public int SetApn(Context context, SharedPreferences prefs, boolean enabled, boolean shouldDisableMms, String modifier) {
        int apnsChanged = 0;
        ContentResolver resolver = context.getContentResolver();
        ContentValues values = new ContentValues();
        Cursor cursor = null;
        if (modifier == null) {
            modifier = SUFFIX_APN_DROID;
        }
        String[] args = new String[1];
        try {
            cursor = resolver.query(CURRENT_APNS, PROJECTION, null, null, null);
            if (cursor != null) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int apnIndex = cursor.getColumnIndex(COLUMN_APN);
                int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String originalTypeValue = cursor.getString(typeIndex);
                    if (enabled || !isMms(originalTypeValue) || shouldDisableMms) {
                        String originalApnValue = cursor.getString(apnIndex);
                        if (originalApnValue != null) {
                            if (originalTypeValue == null) {
                                originalTypeValue = "";
                            }
                            String modifiedApnValue = getAdaptedValue(originalApnValue, enabled, modifier);
                            String modifiedTypeValue = getAdaptedValue(originalTypeValue, enabled, modifier);
                            if (originalApnValue.equals(modifiedApnValue) && originalTypeValue.equals(modifiedTypeValue)) {
                                Logging.Report("APN", "Found APN '" + originalApnValue + "','" + originalTypeValue + ", not changed", context);
                            } else {
                                args[0] = String.valueOf(cursor.getInt(idIndex));
                                values.put(COLUMN_APN, modifiedApnValue);
                                if (modifiedTypeValue.length() == 0) {
                                    values.put(COLUMN_TYPE, (String) null);
                                } else {
                                    values.put(COLUMN_TYPE, modifiedTypeValue);
                                }
                                resolver.update(CURRENT_APNS, values, "_id=?", args);
                                Logging.Report("APN", "Found APN '" + originalApnValue + "','" + originalTypeValue + "', changed to '" + modifiedApnValue + "','" + modifiedTypeValue + "'", context);
                            }
                            apnsChanged++;
                        }
                        cursor.moveToNext();
                    } else {
                        cursor.moveToNext();
                    }
                }
                if (cursor != null) {
                    cursor.close();
                }
                return apnsChanged;
            } else if (cursor == null) {
                return -1;
            } else {
                cursor.close();
                return -1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }

    public int getValue(Context context, boolean shouldDisableMms, String modifier) {
        int counter = 0;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(CURRENT_APNS, PROJECTION, null, null, null);
            int typeIndex = cursor.getColumnIndex(COLUMN_TYPE);
            cursor.moveToNext();
            while (!cursor.isAfterLast()) {
                String type = cursor.getString(typeIndex);
                if (isDisabled(type, modifier)) {
                    return 2;
                }
                if (!isMms(type) || shouldDisableMms) {
                    counter++;
                }
                cursor.moveToNext();
            }
            if (cursor != null) {
                cursor.close();
            }
            return counter == 0 ? 0 : 1;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static boolean isMms(String type) {
        return type != null && type.toLowerCase().endsWith(MMS);
    }

    private static boolean isDisabled(String value, String modifier) {
        return value != null && value.endsWith(modifier);
    }

    private static String getAdaptedValue(String value, boolean enable, String modifier) {
        if (value == null) {
            return enable ? value : modifier;
        } else {
            value = removeModifiers(value, modifier);
            if (enable) {
                return value;
            }
            String[] parts = value.split(",");
            StringBuffer sb = new StringBuffer();
            boolean needComma = false;
            for (String part : parts) {
                if (needComma) {
                    sb.append(",");
                } else {
                    needComma = true;
                }
                sb.append(part);
                sb.append(modifier);
            }
            return sb.toString();
        }
    }

    private static String removeModifiers(String value, String modifier) {
        String[] parts = value.split(",");
        StringBuffer sb = new StringBuffer();
        boolean needComma = false;
        for (String part : parts) {
            if (needComma) {
                sb.append(",");
            } else {
                needComma = true;
            }
            if (part.endsWith(modifier)) {
                sb.append(part.substring(0, part.length() - modifier.length()));
            } else {
                sb.append(part);
            }
        }
        return sb.toString();
    }
}
