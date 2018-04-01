package com.tomaszrykala.midionandroid

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiManager
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.SeekBar
import android.widget.Spinner
import com.tomaszrykala.midionandroid.control.MidiButton
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiPot
import com.tomaszrykala.midionandroid.midi.MidiEvent
import com.tomaszrykala.midionandroid.midi.MidiEventType
import com.tomaszrykala.midionandroid.midi.MidiController
import com.tomaszrykala.midionandroid.ui.DeviceAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**
     * STANDARD
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    /**
     * MIDI-SPECIFIC
     */
    private var midiManager: MidiManager? = null

    private val deviceAdapter: DeviceAdapter by lazy {
        DeviceAdapter(this, { it.inputPortCount > 0 })
    }
    @Suppress("UNCHECKED_CAST")
    private val midiController: MidiController by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>) =
                    ({ MidiController(application) })() as T // unchecked cast
        }.let<ViewModelProvider.Factory, MidiController> {
            ViewModelProviders.of(this, it).get(MidiController::class.java)
        }
    }
    private val midiEventListener: MidiEventListener by lazy {
        object : MidiEventListener {
            override fun onNoteOn(midiButton: MidiButton, pressed: Boolean) {
                if (pressed) {
                    midiController.send(MidiEvent(
                            MidiEventType.STATUS_NOTE_ON, midiButton.midiChannel, midiButton.key, midiButton.velocity))
                } // else STATUS_NOTE_OFF ??
            }

            override fun onControlChange(midiPot: MidiPot, velocity: Byte) {
                midiController.send(MidiEvent(
                        MidiEventType.STATUS_CONTROL_CHANGE, midiPot.midiChannel, midiPot.key, velocity))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        applicationContext?.let {
            it.packageManager?.let {
                if (it.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                    openMidi()
                } else {
                    showNoMidiError()
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        midiController.close()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.app_bar_selector)?.actionView?.apply {
            findViewById<Spinner>(R.id.output_selector)?.apply {
                adapter = deviceAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        midiController.close()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        deviceAdapter[position].apply {
                            midiController.open(this)
                        }
                    }
                }
            }
        }
        return true
    }

    private fun openMidi() {
        applicationContext?.run {
            midiManager = getSystemService(Context.MIDI_SERVICE) as MidiManager?
            midiManager?.run {
                midiController.observeDevices(this@MainActivity, deviceAdapter)
                snack("Midi OK", Snackbar.LENGTH_LONG)

                // init controls and set the listener
                val midiButton = MidiButton(0)
                val midiPot = MidiPot(midiEventListener, 11, 261.626.toByte()) // TODO C4

                button.setOnClickListener { midiEventListener.onNoteOn(midiButton, true) }
                seek_bar.setOnSeekBarChangeListener(
                        object : SeekBar.OnSeekBarChangeListener {
                            override fun onStartTrackingTouch(seekBar: SeekBar?) {} // no-op
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {} // no-op
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                midiPot.onChange(progress)
                            }
                        }
                )
            }
        }
    }

    private fun showNoMidiError() {
        snack("No MIDI Support.", Snackbar.LENGTH_INDEFINITE)
    }

    private fun snack(text: String, length: Int) {
        applicationContext?.run {
            findViewById<ConstraintLayout>(R.id.rootView)?.run {
                Snackbar.make(this, text, length).show()
            }
        }
    }
}