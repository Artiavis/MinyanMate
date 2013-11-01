package org.minyanmate.minyanmate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanTimesTable;
import org.minyanmate.minyanmate.models.Prayer;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

public class MinyanSettingsActivity extends FragmentActivity
	implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public static final int TIME_LOADER = 1;
	public static final int CONTACT_LOADER = 2;
	public final static int PICK_CONTACT = 10;
	public final static int PICK_MULTIPLE_CONTACTS = 20;
	
	private int prayerId;
	private Prayer prayer;
	TextView timeTextView;
	ListView contactList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_minyan_settings);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				prayerId = extras.getInt("prayerId");
			}
		}
		
		getSupportLoaderManager().initLoader(TIME_LOADER, null, this); // for times
		getSupportLoaderManager().initLoader(CONTACT_LOADER, null, this); // for contacts
		
		timeTextView = (TextView) findViewById(R.id.minyan_setting_timeTextView);
		contactList = (ListView) findViewById(R.id.minyan_setting_contactsList);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button by going back
		case android.R.id.home:
//			NavUtils.navigateUpFromSameTask(this);
			Intent intent = new Intent(getBaseContext(), MinyanMateActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			MinyanSettingsActivity.this.finish();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	public static String formatTimeTextView(Context context, int hour, int minute) {
		SimpleDateFormat format;
		if (DateFormat.is24HourFormat(context)) {
			format = new SimpleDateFormat("HH:mm", Locale.getDefault());
		} else {
			format = new SimpleDateFormat("h:mm aa", Locale.getDefault());
		}
		Calendar time = Calendar.getInstance(Locale.getDefault());
		time.set(Calendar.HOUR_OF_DAY, hour);
		time.set(Calendar.MINUTE, minute);
		return format.format(time.getTime());
	}
	
	
	public void pickNewContact(View view) {
		// TODO stuff
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT);
	}
	
	public void pickNewGroup(View view) {
		// TODO stuff
	}
	
	public void removeContact(View view) {
		// TODO stuff
	}
	
	
	public void pickNewTime(View view) {
		TimePickerFragment newFragment = new TimePickerFragment();
		newFragment.initialize(prayer.getId(), timeTextView, prayer.getHour(), prayer.getMinute());
		newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		
		switch(reqCode) {
		case (PICK_CONTACT) :
			if (resultCode == Activity.RESULT_OK) {
				// TODO make sure this is the right code
				Uri contactData = data.getData();
				
				// THese are probably not necessary since we're writing to the database anyway
//				String id = null;
//				Cursor cr = getContentResolver().query(contactData, null, null, null, null);
//				if (cr.moveToFirst()) {
//					id = cr.getString(cr.getColumnIndex(ContactsContract.Contacts._ID));
//					buildContactData(id);
//					contactIds.add(id);
//				}
			}
			break;
			
		case (PICK_MULTIPLE_CONTACTS):
			if (resultCode == Activity.RESULT_OK) {
				
				// do stuff
			}
		}
	}
	
	public static class TimePickerFragment extends DialogFragment 
		implements TimePickerDialog.OnTimeSetListener {
		
		private int id;
		private int minute;
		private int hour;
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
		
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			ContentValues values = new ContentValues();
			values.put(MinyanTimesTable.COLUMN_PRAYER_HOUR, hourOfDay);
			values.put(MinyanTimesTable.COLUMN_PRAYER_MIN, minute);
			view.getContext().getContentResolver().update(
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + id),
					values,
//					MinyanTimesTable.COLUMN_ID + "=?", new String[] { String.valueOf(childPrayer.getId()) }
					null, null
					);
			
		}
		
		public void initialize(int id, TextView txt, int hour, int minute) {
			this.minute = minute;
			this.hour = hour;
			this.id = id;
		}
		
	}


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		
		switch (id) {
		case TIME_LOADER:
			return new CursorLoader(this, 
					Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/" + this.prayerId), 
					null, null, null, 
					null);
			
		case CONTACT_LOADER:
			return new CursorLoader(this,
					MinyanMateContentProvider.CONTENT_URI_CONTACTS,
					null, MinyanContactsTable.COLUMN_MINYAN_TIME_ID + "=?", 
					new String[] { String.valueOf(this.prayerId) }, null);
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		
		switch( loader.getId()) {
		
		case TIME_LOADER:
			cursor.moveToFirst();
			prayer = Prayer.prayerFromCursor(cursor);
			timeTextView.setText(formatTimeTextView(this, prayer.getHour(), prayer.getMinute()));
			setTitle(prayer.getDay() + " - " + prayer.getPrayerName());
		
			break;
			
		case CONTACT_LOADER:
			
			CursorAdapter adapter = new CursorAdapter(this,
					cursor, false) {

						@Override
						public void bindView(View view, Context context,
								Cursor cur) {
							// TODO Auto-generated method stub
							cur = (MatrixCursor) cur;
							
						}

						@Override
						public View newView(Context context, Cursor cur,
								ViewGroup viewGroup) {
							return LayoutInflater.from(context).
									inflate(R.layout.fragment_deletable_contact, viewGroup, false);
						}
				
			};
			contactList.setAdapter(adapter);
			
			break;
		
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
}
