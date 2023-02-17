package com.example.mytask.calendar

import android.Manifest
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mytask.R
import com.example.mytask.compose.collectAsStateLifecycleAware
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.material.composethemeadapter.MdcTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CalendarPicker(
    viewModel: CalendarPickerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    selected: String?,
    onSelected: (AndroidCalendar?) -> Unit,
) {
    val hasPermissions = rememberMultiplePermissionsState(
        permissions = listOf(Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR),
        onPermissionsResult = { result ->
            if (result.values.all { it }) {
                viewModel.loadCalendars()
            }
        }
    )
    if (hasPermissions.allPermissionsGranted) {
        CalendarPickerList(
            calendars = viewModel.viewState.collectAsStateLifecycleAware().value.calendars,
            selected = selected,
            onSelected = onSelected,
        )
    }
    LaunchedEffect(hasPermissions) {
        if (!hasPermissions.allPermissionsGranted) {
            hasPermissions.launchMultiplePermissionRequest()
        }
    }
}

@Composable
fun CalendarPickerList(
    calendars: List<AndroidCalendar>,
    selected: String?,
    onSelected: (AndroidCalendar?) -> Unit,
) {
    val selectedCalendar = calendars.find { it.id == selected }
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp)
    ) {
        CheckableIconRow(
            icon = painterResource(id = R.drawable.ic_baseline_delete_24),
            tint = MaterialTheme.colors.onSurface,
            text = stringResource(id = R.string.dont_add_to_calendar),
            selected = selectedCalendar == null,
            onClick = { onSelected(null) },
        )
        calendars.forEach {
            CheckableIconRow(
                icon = painterResource(id = R.drawable.ic_baseline_calendar_today_24),
                tint = Color(it.color),
                text = it.name,
                selected = selectedCalendar == it,
                onClick = { onSelected(it) }
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun CalendarPickerPreview() {
    MdcTheme {
        CalendarPickerList(
            calendars = listOf(
                AndroidCalendar("1", "Home", -765666),
                AndroidCalendar("2", "Work", -5434281),
                AndroidCalendar("3", "Personal", -10395295),
            ),
            selected = "2",
            onSelected = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun CalendarPickerNoneSelected() {
    MdcTheme {
        CalendarPickerList(
            calendars = listOf(
                AndroidCalendar("1", "Home", -765666),
                AndroidCalendar("2", "Work", -5434281),
                AndroidCalendar("3", "Personal", -10395295),
            ),
            selected = null,
            onSelected = {},
        )
    }
}

@Composable
fun CheckableIconRow(
    icon: Painter,
    tint: Color,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    CheckableIconRow(
        icon = icon,
        tint = tint,
        selected = selected,
        onClick = onClick,
        content = {
            Text(
                text = text,
                style = MaterialTheme.typography.body1,
            )
        }
    )
}

@Composable
fun CheckableIconRow(
    icon: Painter,
    tint: Color,
    selected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            tint = tint.copy(alpha = ContentAlpha.medium),
            modifier = Modifier.padding(start = 16.dp, end = 32.dp, top = 12.dp, bottom = 12.dp),
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        if (selected) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        } else {
            Spacer(modifier = Modifier.width(56.dp))
        }
    }
}