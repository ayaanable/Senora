package com.example.domain

import android.content.Context
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class SleepTimerManager(
    private val context: Context,
    private val onFadeVolume: (volume: Float) -> Unit,
    private val onTimerExpired: () -> Unit
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    private var targetDeadlineMs: Long = 0L
    private val isExpired = AtomicBoolean(false)

    // State flow for remaining time in seconds (0 = inactive)
    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _isTimerActive = MutableStateFlow(false)
    val isTimerActive: StateFlow<Boolean> = _isTimerActive.asStateFlow()

    var fadeOutEnabled: Boolean = true
    var fadeDurationSeconds: Int = 30

    fun startTimer(minutes: Int) {
        if (minutes <= 0) {
            cancelTimer()
            return
        }
        val durationMs = minutes * 60 * 1000L
        targetDeadlineMs = SystemClock.elapsedRealtime() + durationMs
        isExpired.set(false)
        _isTimerActive.value = true

        timerJob?.cancel()
        timerJob = scope.launch {
            while (isActive) {
                val now = SystemClock.elapsedRealtime()
                val diffMs = targetDeadlineMs - now
                if (diffMs <= 0) {
                    _remainingSeconds.value = 0
                    _isTimerActive.value = false
                    if (isExpired.compareAndSet(false, true)) {
                        onFadeVolume(0f)
                        onTimerExpired()
                        // restore volume scale for next play
                        onFadeVolume(1f)
                    }
                    break
                }

                val secLeft = (diffMs / 1000).toInt() + 1
                _remainingSeconds.value = secLeft

                // Apply gradual fade out if enabled during the final window
                if (fadeOutEnabled && secLeft <= fadeDurationSeconds && fadeDurationSeconds > 0) {
                    val progress = secLeft.toFloat() / fadeDurationSeconds.toFloat()
                    val volume = progress.coerceIn(0.05f, 1.0f)
                    onFadeVolume(volume)
                } else {
                    onFadeVolume(1.0f)
                }

                delay(500)
            }
        }
    }

    fun addMinutes(minutes: Int) {
        if (!_isTimerActive.value) {
            startTimer(minutes)
        } else {
            targetDeadlineMs += minutes * 60 * 1000L
            val now = SystemClock.elapsedRealtime()
            _remainingSeconds.value = ((targetDeadlineMs - now) / 1000).toInt().coerceAtLeast(0)
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        targetDeadlineMs = 0L
        isExpired.set(false)
        _remainingSeconds.value = 0
        _isTimerActive.value = false
        onFadeVolume(1.0f)
    }

    val formattedRemainingTime: String
        get() {
            val totalSec = _remainingSeconds.value
            if (totalSec <= 0) return "Off"
            val mins = totalSec / 60
            val secs = totalSec % 60
            return if (mins > 0) "${mins}m remaining" else "${secs}s remaining"
        }
}
