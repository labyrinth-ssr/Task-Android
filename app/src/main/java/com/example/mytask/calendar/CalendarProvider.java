package com.example.mytask.calendar;

import static android.provider.BaseColumns._ID;

import static com.example.mytask.Strings.isNullOrEmpty;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import timber.log.Timber;

public class CalendarProvider {

    private static final String CAN_MODIFY =
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL
                    + ">= "
                    + CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR;
    private static final String SORT = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " ASC";
    private static final String[] COLUMNS = {
            _ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CalendarContract.Calendars.CALENDAR_COLOR
    };

    private final PermissionChecker permissionChecker;
    private final ContentResolver contentResolver;

    @Inject
    public CalendarProvider(@ApplicationContext Context context, PermissionChecker permissionChecker) {
        this.permissionChecker = permissionChecker;
        contentResolver = context.getContentResolver();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public List<AndroidCalendar> getCalendars() {
        return getCalendars(CalendarContract.Calendars.CONTENT_URI, CAN_MODIFY);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    public AndroidCalendar getCalendar(String id) {
        if (isNullOrEmpty(id)) {
            return null;
        }
        List<AndroidCalendar> calendars =
                getCalendars(
                        CalendarContract.Calendars.CONTENT_URI, CAN_MODIFY + " AND Calendars._id=" + id);
        return calendars.isEmpty() ? null : calendars.get(0);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private List<AndroidCalendar> getCalendars(Uri uri, String selection) {
        if (!permissionChecker.canAccessCalendars()) {
            return Collections.emptyList();
        }

        List<AndroidCalendar> calendars = new ArrayList<>();
        try (Cursor cursor = contentResolver.query(uri, COLUMNS, selection, null, SORT)) {
            if (cursor != null && cursor.getCount() > 0) {
                int idColumn = cursor.getColumnIndex(_ID);
                int nameColumn = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME);
                int colorColumn = cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_COLOR);
                while (cursor.moveToNext()) {
                    calendars.add(
                            new AndroidCalendar(
                                    cursor.getString(idColumn),
                                    cursor.getString(nameColumn),
                                    cursor.getInt(colorColumn)));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return calendars;
    }
}
