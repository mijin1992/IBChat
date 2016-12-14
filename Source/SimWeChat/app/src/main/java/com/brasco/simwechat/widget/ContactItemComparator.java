package com.brasco.simwechat.widget;

import com.brasco.simwechat.contact.Contact;

import java.util.Comparator;

public class ContactItemComparator implements Comparator<Contact> {
	@Override
	public int compare(Contact contact1, Contact contact2) {
		return contact1.getUserName().compareTo(contact2.getUserName());
	}
}
