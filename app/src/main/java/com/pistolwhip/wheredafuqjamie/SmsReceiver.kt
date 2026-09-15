package com.pistolwhip.wheredafuqjamie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val body = messages.joinToString(" ") { it.messageBody ?: "" }.trim()
        val activate = Prefs.activate(context).trim()
        val deactivate = Prefs.deactivate(context).trim()
        if (activate.isNotEmpty() && body.equals(activate, ignoreCase = true)) {
            context.startForegroundService(Intent(context, AlarmService::class.java).setAction(AlarmService.ACTION_ACTIVATE))
        } else if (deactivate.isNotEmpty() && body.equals(deactivate, ignoreCase = true)) {
            context.startService(Intent(context, AlarmService::class.java).setAction(AlarmService.ACTION_DEACTIVATE))
        }
    }
}
