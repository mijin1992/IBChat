package com.brasco.simwechat.widget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brasco.simwechat.MainActivity;
import com.brasco.simwechat.R;
import com.brasco.simwechat.contact.Contact;
import com.brasco.simwechat.model.UserData;
import com.brasco.simwechat.quickblox.QBConstants;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class ContactListAdapter extends ArrayAdapter<UserData>{

	private int resource; // store the resource layout id for 1 row
	private boolean inSearchMode = false;
	
	private ContactsSectionIndexer indexer = null;
	private MainActivity mActivity;
	
	public ContactListAdapter(Context _context, int _resource, List<UserData> _items) {
		super(_context, _resource, _items);
		resource = _resource;
		
		// need to sort the items array first, then pass it to the indexer
		Collections.sort(_items, new ContactItemComparator());
		setIndexer(new ContactsSectionIndexer(_items));

		mActivity = (MainActivity)_context;
	
	}
	
	// get the section textview from row view
	// the section view will only be shown for the first item
	public TextView getSectionTextView(View rowView){
		TextView sectionTextView = (TextView)rowView.findViewById(R.id.txt_section);
		return sectionTextView;
	}
	
	public void showSectionViewIfFirstItem(View rowView, UserData item, int position){
		TextView sectionTextView = getSectionTextView(rowView);
		
		// if in search mode then dun show the section header
		if(inSearchMode){
	    	sectionTextView.setVisibility(View.GONE);
	    }
	    else
	    {
		    // if first item then show the header
	    	
		    if(indexer.isFirstItemInSection(position)){
		    	
		    	String sectionTitle = indexer.getSectionTitle(item.getFullName());
		    	sectionTextView.setText(sectionTitle);
		    	sectionTextView.setVisibility(View.VISIBLE);
		    	
		    }
		    else
		    	sectionTextView.setVisibility(View.GONE);
	    }
	}
	
	// do all the data population for the row here
	// subclass overwrite this to draw more items
	public void populateDataForRow(View parentView, UserData item , int position){
		// default just draw the item only
		View infoView = parentView.findViewById(R.id.container_contact);
		TextView nameView = (TextView)infoView.findViewById(R.id.txt_contact);
		nameView.setText(item.getFullName());
		final ImageView userImage = (ImageView) infoView.findViewById(R.id.badge_contact);
		String strUrl= item.getQBUser().getCustomData();
		if(strUrl != null && !strUrl.isEmpty()) {
			Glide.with(mActivity)
					.load(strUrl)
					.listener(new RequestListener<String, GlideDrawable>() {
						@Override
						public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
							Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
							userImage.setImageBitmap(bm);
							return false;
						}
						@Override
						public boolean onResourceReady(GlideDrawable resource, String model,
													   Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
							return false;
						}
					})
					.override(QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW, QBConstants.PREFERRED_IMAGE_SIZE_PREVIEW)
					.error(R.drawable.ic_error)
					.into(userImage);
		} else {
			Bitmap bm = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.button_select_avatar);
			userImage.setImageBitmap(bm);
		}
	}
	
	// this should be override by subclass if necessary
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewGroup parentView;
		
		UserData item = getItem(position);
		
		//Log.i("ContactListAdapter", "item: " + item.getItemForIndex());
		
		if (convertView == null) {
	    	parentView = new LinearLayout(getContext()); // Assumption: the resource parent id is a linear layout
	    	String inflater = Context.LAYOUT_INFLATER_SERVICE;
	    	LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
	    	vi.inflate(resource, parentView, true);
	    } else {
	    	parentView = (LinearLayout) convertView;
	    }
	    
	    // for the very first section item, we will draw a section on top
		showSectionViewIfFirstItem(parentView, item, position);
	    
		// set row items here
		populateDataForRow(parentView, item, position);

		return parentView;
		
	}

	public boolean isInSearchMode() {
		return inSearchMode;
	}

	public void setInSearchMode(boolean inSearchMode) {
		this.inSearchMode = inSearchMode;
	}

	public ContactsSectionIndexer getIndexer() {
		return indexer;
	}

	public void setIndexer(ContactsSectionIndexer indexer) {
		this.indexer = indexer;
	}
	
	
	
}
