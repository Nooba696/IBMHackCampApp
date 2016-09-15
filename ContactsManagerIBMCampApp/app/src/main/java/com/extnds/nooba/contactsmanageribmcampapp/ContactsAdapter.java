package com.extnds.nooba.contactsmanageribmcampapp;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Nooba(PratickRoy) on 11-09-2016.
 */
public class ContactsAdapter extends  RecyclerView.Adapter<ContactsAdapter.ViewHolder>
{
    public static String TAG = "ContactsAdapter";
    ArrayList<ContactData> data;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView contactName;
        public final TextView contactEmail;
        public final TextView contactPhone;
        public final View itemView;
        public ViewHolder(View v) {
            super(v);
            itemView =v;
            contactName = (TextView) v.findViewById(R.id.contact_name);
            contactEmail = (TextView) v.findViewById(R.id.contact_email);
            contactPhone = (TextView) v.findViewById(R.id.contact_phone);
        }
    }

    public ContactsAdapter(Context context, ArrayList<ContactData> data) {
        this.data=data;
        this.context=context;
    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        ContactData contactData = data.get(position);
        viewHolder.contactName.setText(contactData.getName());
        viewHolder.contactEmail.setText(contactData.getEmail());
        viewHolder.contactPhone.setText(contactData.getPhone());

        final int positionCopy =position;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this contact.")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteContact(positionCopy);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();


            }
        });
    }

    private void deleteContact(int position) {
        Cursor cursor = context.getContentResolver()
                .query(ContactsContract.Contacts.CONTENT_URI,
                        new String[]{ContactsContract.Contacts._ID},
                        ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " = ?",
                        new String[]{data.get(position).getName()},
                        ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        assert cursor != null;
        cursor.moveToNext();
        int contactId = cursor.getInt(0);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)})
                .build());
        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            Toast.makeText(context,"Contact Could not be deleted Successfully",Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context,"Contact Deleted Successfully",Toast.LENGTH_SHORT).show();
        data.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int arg1) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_contact, parent, false);
        return new ViewHolder(v);
    }
}
