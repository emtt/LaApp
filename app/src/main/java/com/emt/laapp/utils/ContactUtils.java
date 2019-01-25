package com.emt.laapp.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import laapp.emt.com.core.model.Contacto;


public class ContactUtils {

    public static ArrayList<Contacto> readContacts(ContentResolver contentResolver) {
        ArrayList<Contacto> mContacts = new ArrayList<>();

        ContentResolver cr = contentResolver;
        String controlName = "";
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Contacto contacto = new Contacto();

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {


                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    while (pCur.moveToNext()) {

                        String phone = pCur.getString(
                                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                        phone = phone.trim().replace(" ", "").replace("+", "");

                        if (phone.length() == 10 && !controlName.equals(name)) {

                            contacto.setTelefono(phone);
                            contacto.setNombre(name);
                            mContacts.add(contacto);

                        } else if (phone.length() > 10 && !controlName.equals(name)) {
                            contacto.setTelefono(phone.substring(phone.length() - 10));
                            contacto.setNombre(name);
                            mContacts.add(contacto);
                        } else {
                            /**
                             *We dont add local phone numbers, only cell phones.
                             */

                        }
                    }
                    pCur.close();

                }
            }
        }

        /**
         * order the results before return.
         */
        Collections.sort(mContacts, new Comparator<Contacto>() {
            public int compare(Contacto c1, Contacto c2) {
                return c1.getNombre().compareTo(c2.getNombre());
            }
        });
        return mContacts;
    }

}
