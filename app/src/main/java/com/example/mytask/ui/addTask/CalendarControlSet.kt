package com.example.mytask.ui.addTask

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import com.example.mytask.Context.toast
import com.example.mytask.R
import com.example.mytask.Strings.isNullOrEmpty
import com.example.mytask.calendar.CalendarPicker
import com.example.mytask.calendar.CalendarProvider
import com.example.mytask.calendar.PermissionChecker
import com.example.mytask.compose.collectAsStateLifecycleAware
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class CalendarControlSet : TaskEditControlFragment() {
    @Inject
    lateinit var activity: Activity
    @Inject
    lateinit var calendarProvider: CalendarProvider
    @Inject
    lateinit var permissionChecker: PermissionChecker

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()

        val canAccessCalendars = permissionChecker.canAccessCalendars()
        viewModel.eventUri.value?.let {
            if (canAccessCalendars && !calendarEntryExists(it)) {
                viewModel.eventUri.value = null
            }
        }
        if (!canAccessCalendars) {
            viewModel.selectedCalendar.value = null
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(parent: ViewGroup?): View =
        (parent as ComposeView).apply {
            setContent {
                MdcTheme {
                    CalendarRow(
                        eventUri = viewModel.eventUri.collectAsStateLifecycleAware().value,
                        selectedCalendar = viewModel.selectedCalendar.collectAsStateLifecycleAware().value?.let {
                            calendarProvider.getCalendar(it)?.name
                        },
                        onClick = {
                            if (viewModel.eventUri.value.isNullOrBlank()) {
                                CalendarPicker
                                    .newCalendarPicker(
                                        requireParentFragment(),
                                        70,
                                        viewModel.selectedCalendar.value,
                                    )
                                    .show(
                                        requireParentFragment().parentFragmentManager,
                                        "frag_tag_calendar_picker"
                                    )
                            } else {
                                openCalendarEvent()
                            }
                        },
                        clear = {
                            viewModel.selectedCalendar.value = null
                            viewModel.eventUri.value = null
                        }
                    )
                }
            }
        }

    override fun controlId() = TAG

    private fun openCalendarEvent() {
        val cr = activity.contentResolver
        val uri = Uri.parse(viewModel.eventUri.value)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            cr.query(
                uri, arrayOf(CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND),
                null,
                null,
                null).use { cursor ->
                if (cursor!!.count == 0) {
                    activity.toast(R.string.calendar_event_not_found, duration = Toast.LENGTH_SHORT)
                    viewModel.eventUri.value = null
                } else {
                    cursor.moveToFirst()
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cursor.getLong(0))
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cursor.getLong(1))
                    startActivity(intent)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
            activity.toast(R.string.gcal_TEA_error)
        }
    }

    private fun calendarEntryExists(eventUri: String?): Boolean {
        if (isNullOrEmpty(eventUri)) {
            return false
        }
        try {
            val uri = Uri.parse(eventUri)
            val contentResolver = activity.contentResolver
            contentResolver.query(
                uri, arrayOf(CalendarContract.Events.DTSTART), null, null, null).use { cursor ->
                if (cursor!!.count != 0) {
                    return true
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "%s: %s", eventUri, e.message)
        }
        return false
    }

    companion object {
        const val TAG = R.string.TEA_ctrl_gcal
    }
}
