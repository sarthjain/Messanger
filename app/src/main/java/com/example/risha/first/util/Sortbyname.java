package com.example.risha.first.util;

import com.example.risha.first.model.Contact;

import java.util.Comparator;

public class Sortbyname implements Comparator<Contact>
{
    // Used for sorting in ascending order of name
    public int compare(Contact a, Contact b)
    {
        return a.name.compareTo(b.name);
    }
}
