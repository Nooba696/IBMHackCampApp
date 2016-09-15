package com.extnds.nooba.contactsmanageribmcampapp;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Nooba(PratickRoy) on 12-09-2016.
 */
public class ContactsAddFragment extends Fragment {

    public static String TAG = "ContactsAddFragment";
    public static ContactsAddFragment newInstance() {

        Bundle args = new Bundle();

        ContactsAddFragment fragment = new ContactsAddFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private EditText contactName;
    private EditText contactEmail;
    private EditText contactPhone;
    private Button addContact;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactName = (EditText) view.findViewById(R.id.input_name);
        contactEmail = (EditText) view.findViewById(R.id.input_email);
        contactPhone = (EditText) view.findViewById(R.id.input_phone);
        addContact = (Button) view.findViewById(R.id.button_done);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).
                        withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null).
                        withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null).build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).
                        withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,contactName.getText().toString()).build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).
                        withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,contactPhone.getText().toString()).build());

                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).
                        withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).
                        withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).
                        withValue(ContactsContract.CommonDataKinds.Email.ADDRESS,contactEmail.getText().toString()).build());


                try {
                    getActivity().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    Toast.makeText(getContext(),"Contact Could not be added Successfully",Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(),"Contact Added Successfully",Toast.LENGTH_SHORT).show();
                contactName.setText("");
                contactPhone.setText("");
                contactEmail.setText("");
            }
        });

    }
}
