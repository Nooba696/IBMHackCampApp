package com.extnds.nooba.contactsmanageribmcampapp;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;



/**
 * Created by Nooba(PratickRoy) on 11-09-2016.
 */

public class ContactsViewFragment extends Fragment {

    public interface ContactsViewFragmentCallback{
        void startAddFragment();
    }
    public static String TAG = "ContactsViewFragment";
    public static ContactsViewFragment newInstance() {

        Bundle args = new Bundle();
        ContactsViewFragment fragment = new ContactsViewFragment();
        fragment.setArguments(args);
        return fragment;
    }
    static final String[] DATA_PROJECTION = new String[] {
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.DATA1
    };

    ArrayList<ContactData> allData;
    boolean dataLoaded = false;
    String searchString = "";

    ContactsAdapter adapter;
    RecyclerView contactsRecyclerView;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_view_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar)getActivity().findViewById(R.id.progress_bar);

        contactsRecyclerView = (RecyclerView) getActivity().findViewById(R.id.contacts_list);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        FloatingActionButton floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.add_contact);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ContactsViewFragmentCallback)getActivity()).startAddFragment();
            }
        });
        loadContacts();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                searchString=query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                searchString=newText;
                return false;
            }
        });

    }

    public void loadContacts(){
        contactsRecyclerView.setAdapter(null);
        setAdapter();
        if(!searchString.equals(""))
            search(searchString);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh : {
                loadContacts();
                return true;
            }
            default:{
                return true;
            }
        }
    }

    private void setAdapter() {

        new AsyncTask<Object,Object,ArrayList<ContactData>>() {
            @Override
            protected ArrayList<ContactData> doInBackground(Object[] objects) {

                try {

                    Cursor dataCursor = getContext().getContentResolver()
                            .query(ContactsContract.Data.CONTENT_URI, DATA_PROJECTION,null,null, ContactsContract.Data.DISPLAY_NAME_PRIMARY);
                    assert dataCursor != null;

                    allData = new ArrayList<>();
                    String currentDataName;

                    while (dataCursor.moveToNext()){

                        ContactData contactData = new ContactData();
                        contactData.setName(dataCursor.getString(0));

                        while (true) {
                            currentDataName=dataCursor.getString(0);
                            if (contactData.getEmail() == null && dataCursor.getString(1) != null) {
                                if (android.util.Patterns.EMAIL_ADDRESS.matcher(dataCursor.getString(1)).matches())
                                    contactData.setEmail(dataCursor.getString(1));
                            }
                            if (contactData.getPhone() == null && dataCursor.getString(1) != null) {
                                if (Patterns.PHONE.matcher(dataCursor.getString(1)).matches())
                                    contactData.setPhone(dataCursor.getString(1));
                            }
                            if(!dataCursor.moveToNext())
                                break;

                            if(!currentDataName.equals(dataCursor.getString(0))){
                                dataCursor.moveToPrevious();
                                break;
                            }
                        }

                        if (contactData.getEmail() == null)
                            contactData.setEmail("NA");
                        if (contactData.getPhone() == null)
                            contactData.setPhone("NA");

                        allData.add(contactData);
                    }
                    dataCursor.close();
                    return allData;
                }
                catch (Exception e){
                     cancel(true);
                     return null;
                }
            }

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(ArrayList<ContactData> data) {
                progressBar.setVisibility(View.GONE);
                adapter = new ContactsAdapter(getContext(),data);
                contactsRecyclerView.setAdapter(adapter);
                dataLoaded=true;
            }
        }.execute();
    }

    private void search(String searchString){
        new AsyncTask<String,Object,ArrayList<ContactData>>() {
            @Override
            protected ArrayList<ContactData> doInBackground(String[] objects) {

                try {
                    if (!dataLoaded)
                        return null;

                    String searchString = objects[0];
                    ArrayList<ContactData> selectionData = new ArrayList<>();
                    for (ContactData data : allData){
                        if(data.getName().contains(searchString) || data.getEmail().contains(searchString) || data.getPhone().contains(searchString))
                            selectionData.add(data);
                    }
                    return selectionData;
                }
                catch (Exception e){
                    cancel(true);
                    return null;
                }
            }

            @Override
            protected void onPreExecute() {

                if(dataLoaded)
                    progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            protected void onPostExecute(ArrayList<ContactData> data) {

                if(data==null)
                    return;

                progressBar.setVisibility(View.GONE);
                adapter = new ContactsAdapter(getContext(),data);
                contactsRecyclerView.setAdapter(adapter);
            }
        }.execute(searchString);
    }



}
