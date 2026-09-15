package com.pistolwhip.wheredafuqjamie

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val body = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            .joinToString(" ") { it.messageBody.orEmpty() }
            .trim()

        when {
            TriggerLogic.shouldDeactivate(body, Prefs.deactivate(context)) -> {
                context.stopService(Intent(context, AlarmService::class.java))
                AlarmNotification.cancel(context)
            }
            TriggerLogic.shouldActivate(body, Prefs.activate(context), Prefs.deactivate(context)) -> {
                AlarmNotification.show(context)
            }
        }
    }
}
