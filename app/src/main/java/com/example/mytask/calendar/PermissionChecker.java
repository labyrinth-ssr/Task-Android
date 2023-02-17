package com.example.mytask.calendar;

import static java.util.Arrays.asList;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import timber.log.Timber;
import android.Manifest.permission;
import android.os.Build;

import static com.example.mytask.calendar.AndroidUtilities.atLeastOreo;
import static com.example.mytask.calendar.AndroidUtilities.atLeastQ;
import static com.example.mytask.calendar.AndroidUtilities.preTiramisu;

import androidx.annotation.RequiresApi;


public class PermissionChecker {

    private final Context context;

    @Inject
    public PermissionChecker(@ApplicationContext Context context) {
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean canAccessCalendars() {
        return checkPermissions(permission.READ_CALENDAR, permission.WRITE_CALENDAR);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean canAccessAccounts() {
        return atLeastOreo() || checkPermissions(permission.GET_ACCOUNTS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean canAccessForegroundLocation() {
        return checkPermissions(permission.ACCESS_FINE_LOCATION);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean canAccessBackgroundLocation() {
        return checkPermissions(backgroundPermissions().toArray(new String[0]));
    }

    public boolean canNotify() {
        return preTiramisu() || checkPermissions(permission.POST_NOTIFICATIONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean checkPermissions(String... permissions) {
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                Timber.w("Request for %s denied", permission);
                return false;
            }
        }
        return true;
    }

    public static List<String> backgroundPermissions() {
        return atLeastQ()
                ? asList(permission.ACCESS_FINE_LOCATION, permission.ACCESS_BACKGROUND_LOCATION)
                : Collections.singletonList(permission.ACCESS_FINE_LOCATION);
    }
}
