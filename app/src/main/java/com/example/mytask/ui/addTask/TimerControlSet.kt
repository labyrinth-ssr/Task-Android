package com.example.mytask.ui.addTask

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.example.mytask.R
import com.example.mytask.compose.collectAsStateLifecycleAware
import com.example.mytask.database.Task
//import com.example.mytask.fragments.TimeDurationControlSet
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TimerControlSet : TaskEditControlFragment() {
    @Inject lateinit var activity: Activity
    private lateinit var callback: TimerControlSetCallback

    override fun createView(savedInstanceState: Bundle?) {
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        callback = activity as TimerControlSetCallback
    }

    private fun onRowClick() {
    }

    private fun timerClicked() {
        lifecycleScope.launch {
            if (timerActive()) {
                val task = callback.stopTimer()
                viewModel.elapsedSeconds.value = task.elapsedSeconds
                viewModel.timerStarted.value = 0
            } else {
                val task = callback.startTimer()
                viewModel.timerStarted.value = task.timerStart
            }
        }
    }

    override fun bind(parent: ViewGroup?): View =
        (parent as ComposeView).apply {
            setContent {
                MdcTheme {
                            TimerRow(
                                started = viewModel.timerStarted.collectAsStateLifecycleAware().value?:0,
                                estimated = 0,
                                elapsed = viewModel.elapsedSeconds.collectAsStateLifecycleAware().value?:0,
                                timerClicked = this@TimerControlSet::timerClicked,
                                onClick = this@TimerControlSet::onRowClick,
                            )
                }
            }
        }

    override fun controlId() = TAG

    private fun timerActive() = viewModel.timerStarted.value!=null && viewModel.timerStarted.value!! > 0


    interface TimerControlSetCallback {
        suspend fun stopTimer(): Task
        suspend fun startTimer(): Task
    }

    companion object {
        const val TAG = R.string.ctrl_timer_pref
    }
}
