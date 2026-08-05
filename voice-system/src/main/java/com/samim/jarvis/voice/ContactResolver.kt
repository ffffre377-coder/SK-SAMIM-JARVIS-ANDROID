package com.samim.jarvis.voice

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract

/**
 * ContactResolver: simple helper to find a contact's primary phone number by display name.
 * Requires READ_CONTACTS permission. Returns phone number in E.164-ish raw digits if found, or null.
 */
object ContactResolver {
    fun findPhoneNumberByName(context: Context, name: String): String? {
        val uri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)
        val selection = "${ContactsContract.Contacts.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$name%")
        val cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val id = it.getString(it.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                // Now query phone
                val phones = context.contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id), null
                )
                phones?.use { p ->
                    if (p.moveToFirst()) {
                        val num = p.getString(p.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        return num
                    }
                }
            }
        }
        return null
    }

    fun hasContactsPermission(context: Context): Boolean = PermissionUtils.hasPermission(context, android.Manifest.permission.READ_CONTACTS)
}
