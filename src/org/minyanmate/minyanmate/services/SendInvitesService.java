package org.minyanmate.minyanmate.services;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.minyanmate.minyanmate.contentprovider.MinyanMateContentProvider;
import org.minyanmate.minyanmate.database.MinyanContactsTable;
import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;
import org.minyanmate.minyanmate.models.InviteStatus;
import org.minyanmate.minyanmate.models.MinyanSchedule;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class SendInvitesService extends WakefulIntentService {

	public SendInvitesService() {
		super("SendInvitesService");
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		
		// TODO this is null, why?
		Bundle b = intent.getExtras();
		
		Log.d("SendInvitesService", "Inside SendInvitesService");
		Log.d("SendInvitesService", "Bundle: " + b);
		Log.d("SendInvitesService", "Trying to get requestCode from bundle");
		
		int id = b.getInt("requestCode");
		
		Cursor c = getContentResolver().query(
				Uri.parse(MinyanMateContentProvider.CONTENT_URI_TIMES + "/"+ id),
				null, null, null, null
				);
		if (c.moveToNext()) {
			MinyanSchedule sched = MinyanSchedule.schedFromCursor(c);
			
			Log.d("SendInvitesService", "Scheduling Minyan "+ sched.getId());
			
			Calendar date = new GregorianCalendar();
			date.set(Calendar.HOUR_OF_DAY, sched.getHour());
			date.set(Calendar.MINUTE, sched.getMinute());
			
			ContentValues eventValues = new ContentValues();
			eventValues.put(MinyanEventsTable.COLUMN_MINYAN_SCHEDULE_TIME, System.currentTimeMillis());
			eventValues.put(MinyanEventsTable.COLUMN_MINYAN_START_TIME, date.getTimeInMillis());
			eventValues.put(MinyanEventsTable.COLUMN_MINYAN_END_TIME, date.getTimeInMillis() + 30*60); // give it 30 minutes
			eventValues.put(MinyanEventsTable.COLUMN_IS_MINYAN_COMPLETE, 0);
			Uri eventUri = getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENTS, eventValues);
			
			long eventId = ContentUris.parseId(eventUri);
			
			Cursor contactsToBeInvited = getContentResolver().query(
					MinyanMateContentProvider.CONTENT_URI_CONTACTS, null, 
					MinyanContactsTable.COLUMN_MINYAN_SCHEDULE_ID + "=?", 
					new String[] { String.valueOf(id) }, null);
			
			SmsManager smsm = SmsManager.getDefault();
			ContentValues inviteValues;
			
			while (contactsToBeInvited.moveToNext()) {
				
				Log.d("SendInvitesService", "Inviting " + contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.NAME));
				
				String number = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.NUM);
				String name = contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.NAME);
				// TODO fire intent to log the invited recipient in the Goers table if the message was received
				smsm.sendTextMessage(number, null, sched.getInviteMessage(), null, null);
				
				inviteValues = new ContentValues();
				inviteValues.put(MinyanGoersTable.COLUMN_GENERAL_NAME, name);
				inviteValues.put(MinyanGoersTable.COLUMN_INVITE_STATUS, InviteStatus.toInteger(InviteStatus.AWAITING_RESPONSE));
				inviteValues.put(MinyanGoersTable.COLUMN_IS_INVITED, 1);
				inviteValues.put(MinyanGoersTable.COLUMN_LOOKUP_KEY, contactsToBeInvited.getString(MinyanMateContentProvider.ContactMatrix.KEY));
				inviteValues.put(MinyanGoersTable.COLUMN_MINYAN_EVENT_ID, eventId);
				getContentResolver().insert(MinyanMateContentProvider.CONTENT_URI_EVENT_GOERS, inviteValues);
				
				
				SystemClock.sleep(1000);
			}
		}
	}

}