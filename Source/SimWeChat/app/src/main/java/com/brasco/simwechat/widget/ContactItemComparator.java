package com.brasco.simwechat.widget;

import com.brasco.simwechat.contact.Contact;
import com.brasco.simwechat.model.UserData;

import java.util.Comparator;

public class ContactItemComparator implements Comparator<UserData> {
	@Override
	public int compare(UserData contact1, UserData contact2) {
		return contact1.getUserId().compareTo(contact2.getUserId());
	}
}
