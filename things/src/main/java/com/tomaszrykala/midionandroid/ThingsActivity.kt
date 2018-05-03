package com.tomaszrykala.midionandroid

import android.app.Activity
import android.content.Context
import android.media.midi.MidiManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.ScrollView
import com.tomaszrykala.midi.BaseMidiController
import com.tomaszrykala.midionandroid.control.MidiControls
import com.tomaszrykala.midionandroid.midi.MidiController
import kotlinx.android.synthetic.main.activity_main.*

class ThingsActivity : Activity() {

    companion object {
        const val TAG = "ThingsActivity"
    }

    private lateinit var midiManager: MidiManager

    private val midiController: MidiController by lazy {
        MidiController(BaseMidiController(midiManager))
    }
    private val midiControls: MidiControls by lazy {
        MidiControls(midiController)
    }

    private var scrollView: ScrollView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        scrollView = findViewById(R.id.outputScrollView)
        log("onCreate")
        startMidi()
    }

    private fun startMidi() {
        (getSystemService(Context.MIDI_SERVICE) as MidiManager?).run {
            this?.run {
                midiManager = this
                if (!devices.isEmpty()) {
                    devices.first { it.inputPortCount > 0 }.let {
                        midiController.open(it)
                        midiControls.onStart()
                        this@ThingsActivity.log("Opening $it")
                    }
                } else {
                    log("No MIDI devices")
                }
            }
        }
    }

    override fun onDestroy() {
        midiController.close()
        midiControls.onClose()
        log("onDestroy")
        super.onDestroy()
    }

    private fun log(log: String) {
        Log.d(TAG, log)
        outputTextView?.run {
            outputTextView.text = StringBuilder(log).append("\n").append(outputTextView.text).toString()
            scrollView?.run { fullScroll(ScrollView.FOCUS_DOWN) }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        onKey(keyCode, true)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        onKey(keyCode, false)
        return super.onKeyUp(keyCode, event)
    }

    private fun onKey(keyCode: Int, pressed: Boolean) {
        midiControls.midiButtons
                .filter { keyCode == it.keyCode }
                .forEach { midiController.onNote(it, pressed) }
    }
}
