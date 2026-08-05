package com.samim.jarvis.voice

import android.content.Context
import android.provider.ContactsContract

object ContactSearch {
    /**
     * Returns a list of pairs (displayName, phoneNumber) matching the name query.
     */
    fun findContactsByName(context: Context, nameQuery: String): List<Pair<String, String>> {
        val out = mutableListOf<Pair<String, String>>()
        val uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$nameQuery%")
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val display = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val phones = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id), null
                )
                phones?.use { p ->
                    if (p.moveToFirst()) {
                        val num = p.getString(p.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        out.add(Pair(display ?: "Unknown", num))
                    }
                }
            }
        }
        return out
    }
}
