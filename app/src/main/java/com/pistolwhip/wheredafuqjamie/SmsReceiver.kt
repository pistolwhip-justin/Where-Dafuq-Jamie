package com.pistolwhip.wheredafuqjamie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        val body = Telephony.Sms.Intents.getMessagesFromIntent(intent).joinToString(" ") { it.messageBody ?: "" }.trim()
        when {
            body.isNotEmpty() && body.equals(Prefs.activate(context).trim(), ignoreCase = true) -> AlarmNotification.show(context)
            body.isNotEmpty() && body.equals(Prefs.deactivate(context).trim(), ignoreCase = true) -> {
                context.stopService(Intent(context, AlarmService::class.java))
                AlarmNotification.cancel(context)
            }
        }
    }
}
