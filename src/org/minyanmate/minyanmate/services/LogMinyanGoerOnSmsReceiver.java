package org.minyanmate.minyanmate.services;

import org.minyanmate.minyanmate.database.MinyanEventsTable;
import org.minyanmate.minyanmate.database.MinyanGoersTable;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Checks whether a Minyan is active against the time stamps in 
 * {@link MinyanEventsTable} and whether the received Sms is from
 * someone in the {@link MinyanGoersTable}; if so, and if the
 * invited party has not previously responded (
 * {@link MinyanGoersTable#COLUMN_INVITE_STATUS} is 1), log their
 * attendance. 
 */
public class LogMinyanGoerOnSmsReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	}

}