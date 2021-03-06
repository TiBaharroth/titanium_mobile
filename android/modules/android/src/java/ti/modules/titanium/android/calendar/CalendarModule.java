/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2011-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */

package ti.modules.titanium.android.calendar;

import java.util.ArrayList;

import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiContext;
import org.appcelerator.kroll.common.Log;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import ti.modules.titanium.android.AndroidModule;

@Deprecated
@Kroll.module(parentModule=AndroidModule.class)
public class CalendarModule extends KrollModule
{
	private static final String TAG = "CalendarModule";

	@Kroll.constant public static final int STATUS_TENTATIVE = EventProxy.STATUS_TENTATIVE;
	@Kroll.constant public static final int STATUS_CONFIRMED = EventProxy.STATUS_CONFIRMED;
	@Kroll.constant public static final int STATUS_CANCELED = EventProxy.STATUS_CANCELED;

	@Kroll.constant public static final int VISIBILITY_DEFAULT = EventProxy.VISIBILITY_DEFAULT;
	@Kroll.constant public static final int VISIBILITY_CONFIDENTIAL = EventProxy.VISIBILITY_CONFIDENTIAL;
	@Kroll.constant public static final int VISIBILITY_PRIVATE = EventProxy.VISIBILITY_PRIVATE;
	@Kroll.constant public static final int VISIBILITY_PUBLIC = EventProxy.VISIBILITY_PUBLIC;

	@Kroll.constant public static final int METHOD_ALERT = ReminderProxy.METHOD_ALERT;
	@Kroll.constant public static final int METHOD_DEFAULT = ReminderProxy.METHOD_DEFAULT;
	@Kroll.constant public static final int METHOD_EMAIL = ReminderProxy.METHOD_EMAIL;
	@Kroll.constant public static final int METHOD_SMS = ReminderProxy.METHOD_SMS;

	@Kroll.constant public static final int STATE_DISMISSED = AlertProxy.STATE_DISMISSED;
	@Kroll.constant public static final int STATE_FIRED = AlertProxy.STATE_FIRED;
	@Kroll.constant public static final int STATE_SCHEDULED = AlertProxy.STATE_SCHEDULED;

	public static final String EVENT_LOCATION = "eventLocation";

	public CalendarModule()
	{
		super();
		Log.w(TAG, "The Titanium.Android.Calendar module is deprecated. Use Titanium.Calendar instead.");
	}

	public CalendarModule(TiContext context)
	{
		this();
	}
	
	@Kroll.method
	public boolean hasCalendarPermissions()
	{
		return CalendarProxy.hasCalendarPermissions();
	}

	@Kroll.method
	public void requestCalendarPermissions(@Kroll.argument(optional=true)KrollFunction permissionCallback)
	{
		if (hasCalendarPermissions()) {
			return;
		}

		TiBaseActivity.registerPermissionRequestCallback(TiC.PERMISSION_CODE_OLD_CALENDAR, permissionCallback, getKrollObject());

		Activity currentActivity  = TiApplication.getInstance().getCurrentActivity();
		currentActivity.requestPermissions(new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR}, TiC.PERMISSION_CODE_OLD_CALENDAR);
		
	}

	@Kroll.getProperty @Kroll.method
	public CalendarProxy[] getAllCalendars() {
		ArrayList<CalendarProxy> calendars = CalendarProxy.queryCalendars(null, null);
		return calendars.toArray(new CalendarProxy[calendars.size()]);
	}

	@Kroll.getProperty @Kroll.method
	public CalendarProxy[] getSelectableCalendars() {
		ArrayList<CalendarProxy> calendars;
		// selectable calendars are "visible"
		if (Build.VERSION.SDK_INT >= 14) { // ICE_CREAM_SANDWICH, 4.0
			calendars = CalendarProxy.queryCalendars(
				"Calendars.visible = ?", new String[] {"1"});
		}
		// selectable calendars are "selected"
		else if (Build.VERSION.SDK_INT >= 11) { // HONEYCOMB, 3.0
			calendars = CalendarProxy.queryCalendars(
				"Calendars.selected = ?", new String[] {"1"});
		}
		// selectable calendars are "selected" && !"hidden"
		else {
			calendars = CalendarProxy.queryCalendars(
				"Calendars.selected = ? AND Calendars.hidden = ?", new String[] { "1", "0" });
		}
		return calendars.toArray(new CalendarProxy[calendars.size()]);
	}

	@Kroll.method
	public CalendarProxy getCalendarById(int id) {
		ArrayList<CalendarProxy> calendars = CalendarProxy.queryCalendars(
			"Calendars._id = ?", new String[] { ""+id });

		if (calendars.size() > 0) {
			return calendars.get(0);
		} else {
			return null;
		}
	}

	@Kroll.getProperty @Kroll.method
	public AlertProxy[] getAllAlerts() {
		ArrayList<AlertProxy> alerts = AlertProxy.queryAlerts(null, null, null);
		return alerts.toArray(new AlertProxy[alerts.size()]);
	}

	@Override
	public String getApiName()
	{
		return "Ti.Android.Calendar";
	}
}
