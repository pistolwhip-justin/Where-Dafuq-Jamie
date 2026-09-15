package com.pistolwhip.wheredafuqjamie

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var activate: EditText
    private lateinit var deactivate: EditText
    private lateinit var voice: EditText
    private lateinit var alarmSummary: TextView
    private lateinit var alpha: SeekBar
    private lateinit var alarmSpinner: Spinner
    private lateinit var volUp: CheckBox
    private lateinit var volDown: CheckBox
    private lateinit var swipe: CheckBox
    private var alarmUri: Uri? = null
    private var logUri: Uri? = null
    private var bgColor = Color.rgb(16,16,16)
    private var popupColor = Color.rgb(46,125,50)

    private val ringtonePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            alarmUri = result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            alarmSummary.text = if (alarmUri == null) "Default alarm tone" else RingtoneManager.getRingtone(this, alarmUri)?.getTitle(this) ?: "Selected alarm tone"
        }
    }
    private val folderPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) result.data?.data?.let {
            contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            logUri = it; findViewById<TextView>(1001).text = it.toString()
        }
    }
    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); buildUi(); requestRuntimePermissions() }

    private fun requestRuntimePermissions() {
        val needed = mutableListOf<String>()
        if (android.os.Build.VERSION.SDK_INT >= 33) needed += Manifest.permission.POST_NOTIFICATIONS
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) needed += Manifest.permission.RECEIVE_SMS
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) needed += Manifest.permission.CAMERA
        if (needed.isNotEmpty()) permissionLauncher.launch(needed.toTypedArray())
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(24, 28, 24, 24); setBackgroundColor(Prefs.bg(this@MainActivity)) }
        val title = TextView(this).apply { text = "Where Dafuq Jamie!?"; textSize = 28f; setTextColor(Color.WHITE); gravity = Gravity.CENTER; setPadding(0,0,0,20) }
        root.addView(title)
        root.addView(label("Trigger phrase")); activate = edit(Prefs.activate(this)); root.addView(activate)
        root.addView(label("Persistent deactivation phrase")); deactivate = edit(Prefs.deactivate(this)); root.addView(deactivate)
        root.addView(TextView(this).apply { text = "Trigger source: incoming SMS containing the exact trigger phrase"; textSize = 14f; setTextColor(Color.LTGRAY); setPadding(0,12,0,18) })
        root.addView(Button(this).apply { text = "Settings"; setOnClickListener { showSettings(root, title) } })
        setContentView(root)
    }

    private fun showSettings(root: LinearLayout, title: TextView) {
        root.removeAllViews(); root.addView(title)
        root.addView(label("Trigger phrase")); root.addView(activate)
        root.addView(label("Deactivation phrase")); root.addView(deactivate)
        root.addView(label("Alarm type"))
        alarmSpinner = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, arrayOf("Alarm tone", "Custom voice message")); setSelection(if (Prefs.alarmType(this@MainActivity) == "voice") 1 else 0) }
        root.addView(alarmSpinner)
        root.addView(label("Deactivation methods"))
        volUp = CheckBox(this).apply { text = "Double-tap Volume Up"; isChecked = Prefs.deactVolUp(this@MainActivity); setTextColor(Color.WHITE) }; root.addView(volUp)
        volDown = CheckBox(this).apply { text = "Double-tap Volume Down"; isChecked = Prefs.deactVolDown(this@MainActivity); setTextColor(Color.WHITE) }; root.addView(volDown)
        swipe = CheckBox(this).apply { text = "On-screen swipe"; isChecked = Prefs.deactSwipe(this@MainActivity); setTextColor(Color.WHITE) }; root.addView(swipe)
        alarmSummary = TextView(this).apply { text = "No alarm tone selected"; setTextColor(Color.WHITE); setPadding(0,12,0,6) }; root.addView(alarmSummary)
        root.addView(Button(this).apply { text = "Choose device alarm tone"; setOnClickListener { ringtonePicker.launch(Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply { putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM); putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true); putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false); putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, alarmUri) }) } })
        root.addView(label("Spoken phrase")); voice = edit(Prefs.voice(this)); root.addView(voice)
        root.addView(label("App background color")); root.addView(colorButton(Prefs.bg(this), true))
        root.addView(label("Popup color")); root.addView(colorButton(Prefs.popup(this), false))
        root.addView(label("Transparency")); alpha = SeekBar(this).apply { max = 100; progress = Prefs.alpha(this@MainActivity) }; root.addView(alpha)
        root.addView(label("Log file location"))
        root.addView(TextView(this).apply { id = 1001; text = Prefs.logUri(this@MainActivity)?.toString() ?: "Not selected"; setTextColor(Color.LTGRAY) })
        root.addView(Button(this).apply { text = "Change log folder"; setOnClickListener { folderPicker.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)) } })
        root.addView(Button(this).apply { text = "Save settings"; setOnClickListener { saveAndHome() } })
        root.addView(Button(this).apply { text = "Back"; setOnClickListener { buildUi() } })
    }

    private fun saveAndHome() {
        val type = if (alarmSpinner.selectedItemPosition == 1) "voice" else "tone"
        Prefs.save(this, activate.text.toString(), deactivate.text.toString(), type, alarmUri, voice.text.toString(), bgColor, popupColor, alpha.progress, logUri, volUp.isChecked, volDown.isChecked, swipe.isChecked)
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show(); buildUi()
    }

    private fun colorButton(initial: Int, background: Boolean): View = Button(this).apply {
        setBackgroundColor(initial); text = "Open color wheel"
        setOnClickListener {
            val wheel = ColorWheelView(this@MainActivity).apply { selectedColor = initial }
            android.app.AlertDialog.Builder(this@MainActivity).setTitle("Choose color").setView(wheel).setNegativeButton("Cancel", null).setPositiveButton("Use color") { _, _ ->
                if (background) bgColor = wheel.selectedColor else popupColor = wheel.selectedColor; setBackgroundColor(wheel.selectedColor)
            }.show()
        }
    }

    private fun edit(value: String) = EditText(this).apply { setText(value); setTextColor(Color.WHITE); setSingleLine(true) }
    private fun label(s: String) = TextView(this).apply { text = s; setTextColor(Color.WHITE); textSize = 16f; setPadding(0,10,0,4) }
}
