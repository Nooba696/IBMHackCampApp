package com.extnds.nooba.contactsmanageribmcampapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;


/**
 * Created by Nooba(PratickRoy) on 11-09-2016.
 */
public class MainActivity extends AppCompatActivity implements ContactsViewFragment.ContactsViewFragmentCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar actionBar = (Toolbar)findViewById(R.id.action_bar);
        setSupportActionBar(actionBar);

        if (savedInstanceState == null){

            ContactsViewFragment contactsViewFragment = (ContactsViewFragment)getSupportFragmentManager().findFragmentByTag(ContactsViewFragment.TAG);
            if(contactsViewFragment==null)
                contactsViewFragment = ContactsViewFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_holder,contactsViewFragment)
                    .commit();
        }
    }

    @Override
    public void startAddFragment() {
        ContactsAddFragment contactsAddFragment = (ContactsAddFragment)getSupportFragmentManager().findFragmentByTag(ContactsAddFragment.TAG);
        if(contactsAddFragment==null)
            contactsAddFragment = ContactsAddFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment_holder,contactsAddFragment)
                .addToBackStack(ContactsAddFragment.TAG)
                .commit();
    }
}
