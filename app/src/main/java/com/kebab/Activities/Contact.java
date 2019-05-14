package com.kebab.Activities;

import java.util.Comparator;

public class Contact {
    public static Comparator<? super Contact> CASE_INSENSITIVE_NAME_COMPARER = new Comparator<Contact>() {
        public int compare(Contact object1, Contact object2) {
            return String.CASE_INSENSITIVE_ORDER.compare(object1.ContactName, object2.ContactName);
        }
    };
    public String ContactName;
    public long Id;
    public String LookupKey;

    public Contact(String contactName, long id, String lookupKey) {
        this.ContactName = contactName;
        this.Id = id;
        this.LookupKey = lookupKey;
    }
}
