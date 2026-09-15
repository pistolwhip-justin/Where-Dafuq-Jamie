package com.pistolwhip.wheredafuqjamie

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.util.Locale

class AlarmService : Service(), TextToSpeech.OnInitListener {
    companion object {
        const val ACTION_ACTIVATE = "com.pistolwhip.wheredafuqjamie.ACTIVATE"
        const val ACTION_DEACTIVATE = "com.pistolwhip.wheredafuqjamie.DEACTIVATE"
        private const val CHANNEL = "jamie_alarm"
        private const val NOTIFICATION = 73
    }

    private val handler = Handler(Looper.getMainLooper())
    private var active = false
    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var torchId: String? = null
    private var flashOn = false
    private var alarmActivityShown = false

    override fun onCreate() {
        super.onCreate()
        createChannel()
        tts = TextToSpeech(this, this)
        torchId = findTorch()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_DEACTIVATE -> stopAlarm()
            ACTION_ACTIVATE -> startAlarm()
        }
        return START_STICKY
    }

    private fun startAlarm() {
        if (active) return
        active = true
        startForeground(NOTIFICATION, notification())
        maxVolumes()
        val am = getSystemService(AudioManager::class.java)
        am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
        try { SettingsBridge.maxBrightness(this) } catch (_: Exception) { }
        if (!alarmActivityShown) {
            alarmActivityShown = true
            startActivity(Intent(this, AlarmActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
        tick()
        writeLog("ALARM ACTIVATED")
    }

    private fun tick() {
        if (!active) return
        flashOn = !flashOn
        setTorch(flashOn)
        if (Prefs.alarmType(this) == "voice") {
            tts?.speak(Prefs.voice(this), TextToSpeech.QUEUE_FLUSH, null, "jamie_alarm")
        } else {
            playTone()
        }
        handler.postDelayed({ tick() }, 3000L)
    }

    private fun playTone() {
        mediaPlayer?.release()
        val uri = Prefs.alarmUri(this) ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer = MediaPlayer.create(this, uri)?.apply { setVolume(1f, 1f); start() }
    }

    private fun maxVolumes() {
        val am = getSystemService(AudioManager::class.java)
        val streams = intArrayOf(AudioManager.STREAM_ALARM, AudioManager.STREAM_RING, AudioManager.STREAM_NOTIFICATION, AudioManager.STREAM_MUSIC, AudioManager.STREAM_SYSTEM)
        streams.forEach { runCatching { am.setStreamVolume(it, am.getStreamMaxVolume(it), 0) } }
    }

    private fun findTorch(): String? {
        val cm = getSystemService(CameraManager::class.java)
        return runCatching { cm.cameraIdList.firstOrNull { cm.getCameraCharacteristics(it).get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true } }.getOrNull()
    }

    private fun setTorch(on: Boolean) { torchId?.let { runCatching { getSystemService(CameraManager::class.java).setTorchMode(it, on) } } }

    fun stopAlarm() {
        if (!active) { stopSelf(); return }
        active = false; handler.removeCallbacksAndMessages(null); mediaPlayer?.release(); mediaPlayer = null; tts?.stop(); setTorch(false); alarmActivityShown = false; writeLog("ALARM DEACTIVATED"); stopForeground(STOP_FOREGROUND_REMOVE); stopSelf()
    }

    private fun notification(): Notification = NotificationCompat.Builder(this, CHANNEL).setSmallIcon(android.R.drawable.ic_lock_idle_alarm).setContentTitle("Where Dafuq Jamie!? is active").setContentText("Find the phone and swipe to deactivate.").setOngoing(true).build()
    private fun createChannel() { if (Build.VERSION.SDK_INT >= 26) getSystemService(NotificationManager::class.java).createNotificationChannel(NotificationChannel(CHANNEL, "Where Dafuq Jamie!? alarm", NotificationManager.IMPORTANCE_HIGH)) }
    private fun writeLog(message: String) { val uri = Prefs.logUri(this) ?: return; runCatching { contentResolver.openOutputStream(uri, "wa")?.bufferedWriter()?.use { it.appendLine("${System.currentTimeMillis()} $message") } } }
    override fun onBind(intent: Intent?) = null
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts?.language = Locale.US }
    override fun onDestroy() { stopAlarm(); tts?.shutdown(); super.onDestroy() }
}

object SettingsBridge {
    fun maxBrightness(context: Context) {
        val window = (context as? Activity)?.window ?: return
        val params = window.attributes
        params.screenBrightness = 1f
        window.attributes = params
    }
}
