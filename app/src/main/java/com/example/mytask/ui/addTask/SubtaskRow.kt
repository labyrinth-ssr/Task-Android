package com.example.mytask.ui.addTask

import android.icu.util.UniversalTimeScale.toLong
import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mytask.CheckBoxProvider.Companion.getCheckboxRes
import com.example.mytask.database.Task
import com.example.mytask.themes.ColorProvider
import com.example.mytask.ui.home.Node
import com.google.android.material.composethemeadapter.MdcTheme

@Composable
fun SubtaskRow(
    existingSubtasks: List<Node>,
    newSubtasks: List<Task>,
    openSubtask: (Task) -> Unit,
    completeExistingSubtask: (Task, Boolean) -> Unit,
    completeNewSubtask: (Task) -> Unit,
    toggleSubtask: (Long, Boolean) -> Unit,
    addSubtask: () -> Unit,
    deleteSubtask: (Task) -> Unit,
) {
    TaskEditRow(
        icon = {
            TaskEditIcon(
                id = com.example.mytask.R.drawable.ic_baseline_subdirectory_arrow_right_24,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        top = 20.dp,
                        end = 20.dp,
                        bottom = 20.dp
                    )
                    .alpha(ContentAlpha.medium),
            )
        },
        content = {
            Column {
                    Spacer(modifier = Modifier.height(height = 8.dp))
                    existingSubtasks?.forEach { task ->
                        ExistingSubtaskRow(
                            task = task,
                            indent = task.level,
                            onRowClick = { openSubtask(task.task) },
                            onCompleteClick = { completeExistingSubtask(task.task, !task.task.isCompleted) },
                            onToggleSubtaskClick = { toggleSubtask(task.id.toLong(), !task.isExpand) }
                        )
                    }
                    newSubtasks.forEach { subtask ->
                        NewSubtaskRow(
                            subtask = subtask,
                            addSubtask = addSubtask,
                            onComplete = completeNewSubtask,
                            onDelete = deleteSubtask,
                        )
                    }
                    DisabledText(
                        text = stringResource(id = com.example.mytask.R.string.add_subtask),
                        modifier = Modifier
                            .clickable { addSubtask() }
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
            }
        },
    )
}

@Composable
fun NewSubtaskRow(
    subtask: Task,
    addSubtask: () -> Unit,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CheckBox(
            task = subtask,
            onCompleteClick = { onComplete(subtask) },
            modifier = Modifier.align(Alignment.Top),
            desaturate = true,
        )
        var text by remember { mutableStateOf(subtask.taskName ?: "") }
        val focusRequester = remember { FocusRequester() }
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                subtask.taskName = it
            },
            cursorBrush = SolidColor(MaterialTheme.colors.onSurface),
            modifier = Modifier
                .weight(1f)
                .focusable(enabled = true)
                .focusRequester(focusRequester)
                .alpha(if (subtask.isCompleted) ContentAlpha.disabled else ContentAlpha.high)
                .align(Alignment.Top)
                .padding(top = 12.dp),
            textStyle = MaterialTheme.typography.body1.copy(
                textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = MaterialTheme.colors.onSurface,
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.Sentences,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (text.isNotBlank()) {
                        addSubtask()
                    }
                }
            ),
            singleLine = false,
            maxLines = Int.MAX_VALUE,
        )
        ClearButton { onDelete(subtask) }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
fun ClearButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Outlined.Clear,
            modifier = Modifier.alpha(ContentAlpha.medium),
            contentDescription = stringResource(id = com.example.mytask.R.string.delete)
        )
    }
}


@Composable
fun ExistingSubtaskRow(
    task: Node, indent: Int,
    onRowClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onToggleSubtaskClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onRowClick() }
            .padding(end = 16.dp)
    ) {
        Spacer(modifier = Modifier.width((indent * 20).dp))
        CheckBox(
            task = task.task,
            onCompleteClick = onCompleteClick,
            desaturate = true,
            modifier = Modifier.align(Alignment.Top),
        )
        Text(
            text = task.task.taskName,
            modifier = Modifier
                .weight(1f)
                .alpha(if (task.isChecked || (!task.isParentExpand)) ContentAlpha.disabled else ContentAlpha.high)
                .align(Alignment.Top)
                .padding(top = 12.dp),
            style = MaterialTheme.typography.body1.copy(
                textDecoration = if (task.isChecked) TextDecoration.LineThrough else TextDecoration.None
            )
        )
    }
}

@Composable
fun TaskEditIcon(@DrawableRes id: Int, modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(id = id),
        contentDescription = null,
        modifier = modifier.alpha(ContentAlpha.medium),
    )
}

@Composable
fun TaskEditRow(
    iconRes: Int = 0,
    icon: @Composable () -> Unit = {
        TaskEditIcon(
            id = iconRes,
            modifier = Modifier
                .alpha(ContentAlpha.medium)
                .padding(
                    start = 16.dp,
                    top = 20.dp,
                    end = 32.dp,
                    bottom = 20.dp
                )
        )
    },
    content: @Composable () -> Unit,
    onClick: (() -> Unit)? = null,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable(
            enabled = onClick != null,
            onClick = { onClick?.invoke() }
        )
    ) {
        icon()
        content()
    }
}

@Composable
fun DisabledText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.body1,
        modifier = modifier
            .alpha(alpha = ContentAlpha.disabled)
            .padding(end = 16.dp)
            .defaultMinSize(minHeight = 24.dp),
    )
}

@Composable
fun CheckBox(
    task: Task,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    desaturate: Boolean,
) {
    IconButton(onClick = onCompleteClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = task.getCheckboxRes()),
            tint = Color(
                ColorProvider.priorityColor(
                    priority = task.priority,
                    isDarkMode = isSystemInDarkTheme(),
                    desaturate = desaturate,
                )
            ),
            contentDescription = null,
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun NoSubtasks() {
    MdcTheme {
        SubtaskRow(
            existingSubtasks = emptyList(),
            newSubtasks = emptyList(),
            openSubtask = {},
            completeExistingSubtask = { _, _ -> },
            completeNewSubtask = {},
            toggleSubtask = { _, _ -> },
            addSubtask = {},
        ) {}
    }
}

@Preview(showBackground = true, widthDp = 320)
//@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, widthDp = 320)
@Composable
fun SubtasksPreview() {
    MdcTheme {
        SubtaskRow(
            existingSubtasks = listOf(
                Node().apply {
                    task = Task().apply {
                        taskName = "Existing subtask 1"
                        priority = Task.Priority.HIGH
                    }
                    level = 0
                },
                Node().apply {
                    task = Task().apply {
                        taskName = "Existing subtask 2 with a really long title"
                        priority = Task.Priority.LOW
                    }
                    level = 1
                }
            ),
            newSubtasks = listOf(
                Task().apply {
                    taskName = "New subtask 1"
                },
                Task().apply {
                    taskName = "New subtask 2 with a really long title"
                },
                Task(),
            ),
            openSubtask = {},
            completeExistingSubtask = { _, _ -> },
            completeNewSubtask = {},
            toggleSubtask = { _, _ -> },
            addSubtask = {},
        ) {}
    }
}

