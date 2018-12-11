package com.example.risha.first.ui;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.risha.first.R;
import com.example.risha.first.model.Contact;
import com.example.risha.first.util.Sortbyname;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends DialogFragment implements ContactsAdapter.ContactListener{

    @Override
    public void onContactClick(Contact contact) {
        onContactSelectListener.callAddFunc(contact);
        dismiss();
    }

    public interface OnContactSelectListener {
        void callAddFunc(Contact contact);
    }

    private static final int PERMS_REQUEST_CODE = 140;
    OnContactSelectListener onContactSelectListener;
    RecyclerView recyclerView;
    ArrayList<Contact> contactsList;
    SearchView searchView;
    ContactsAdapter adapter;
    EditText numberEditText;
    ImageButton button;

    public ContactsFragment() {
        // Required empty public constructor
    }

    public static ContactsFragment newInstance(FriendsFragment.FragFriendClickFloatButton buttonContext) {
        ContactsFragment contactsFragment = new ContactsFragment();
        contactsFragment.onContactSelectListener = (OnContactSelectListener) buttonContext;
        return contactsFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button = (ImageButton) view.findViewById(R.id.selectContact);
        numberEditText = (EditText) view.findViewById(R.id.enterNumber);
        searchView = (SearchView) view.findViewById(R.id.search_View);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = numberEditText.getText().toString();
                if(num.trim().length()<1)
                    Toast.makeText(getContext(),"Enter Valid Number",Toast.LENGTH_LONG).show();
                else {
                    onContactSelectListener.callAddFunc(new Contact("", num));
                    dismiss();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            getContacts();
        else
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMS_REQUEST_CODE);
    }

    private void getContacts() {
        contactsList = new ArrayList<Contact>();
        Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
            String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            contactsList.add(new Contact(name,phoneNumber));
        }
        phones.close();
        Collections.sort(contactsList,new Sortbyname());
        adapter = new ContactsAdapter(ContactsFragment.this,contactsList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int x: grantResults) {
            if (x != PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(),"Permission Required",Toast.LENGTH_LONG).show();
                onContactSelectListener.callAddFunc(null);
                return;
            }
        }
        if (requestCode == PERMS_REQUEST_CODE)
        {
            getContacts();
        }
    }
}

class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements Filterable {

    ArrayList<Contact> list = new ArrayList<>();
    ArrayList<Contact> filteredList = new ArrayList<>();
    ContactsCustomFilter filter;

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new ContactsCustomFilter(list,this);
        }
        return filter;
    }

    public interface ContactListener {
        void onContactClick(Contact contact);
    }

    ContactListener listener;


    public ContactsAdapter( ContactsFragment context, ArrayList<Contact> list) {
        listener = (ContactListener) context;
        this.list = list;
        filteredList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.name.setText(filteredList.get(position).name);
        holder.number.setText(filteredList.get(position).number);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onContactClick(filteredList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView number;
        CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);

            number = (TextView) itemView.findViewById(R.id.number);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
        }
    }
}

class ContactsCustomFilter extends Filter{

    ContactsAdapter adapter;
    ArrayList<Contact> filterList;

    public ContactsCustomFilter(ArrayList<Contact> filterList,ContactsAdapter adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;

    }

    //FILTERING OCCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED Contacts
            ArrayList<Contact> filteredContacts=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).name.toLowerCase().contains(((String) constraint).toLowerCase()))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredContacts.add(filterList.get(i));
                }
            }

            results.count=filteredContacts.size();
            results.values=filteredContacts;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {


        if(results.values == filterList){
            adapter.filteredList = adapter.list;
        }else {
            adapter.filteredList = (ArrayList<Contact>) results.values;
        }
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}


