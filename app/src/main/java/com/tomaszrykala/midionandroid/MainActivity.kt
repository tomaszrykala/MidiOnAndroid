package com.tomaszrykala.midionandroid

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.pm.PackageManager
import android.media.midi.MidiManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.*
import com.tomaszrykala.midionandroid.control.Keys
import com.tomaszrykala.midionandroid.control.MidiButton
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiPot
import com.tomaszrykala.midionandroid.midi.MidiController
import com.tomaszrykala.midionandroid.midi.MidiEvent
import com.tomaszrykala.midionandroid.midi.MidiEventType
import com.tomaszrykala.midionandroid.ui.DeviceAdapter
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.mixer_deck_0.*
import kotlinx.android.synthetic.main.mixer_deck_1.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onDestroy() {
        midiController.removeObserver(deviceAdapter)
        super.onDestroy()
    }

    // TODO MidiControlsManager
    private val midiPots: MutableList<MidiPot> = mutableListOf()
    private val midiButtons: MutableList<MidiButton> = mutableListOf()

    abstract class MidiPotListener : SeekBar.OnSeekBarChangeListener, SeekArc.OnSeekArcChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStartTrackingTouch(p0: SeekArc?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekArc?) {}
    }

    private fun initControls() {
        // init controls and set the listener
        // midiPots
        val keys = Keys.Octave0.Key.values()
        val seekArcs: List<SeekArc> = listOf(eq_lo, eq_mid, eq_hi, gain, filter)
        seekArcs.forEachIndexed { index, seekArc ->
            MidiPot(midiEventListener, 11, keys[index].pitch.toByte()).apply {
                seekArc.setOnSeekArcChangeListener(object : MidiPotListener() {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
                    override fun onProgressChanged(p0: SeekArc?, progress: Int, p2: Boolean) {
                        onChange(progress)
                    }
                })
                midiPots.add(this)
            }
        }
        MidiPot(midiEventListener, 11, keys[seekArcs.size].pitch.toByte()).apply {
            volume.setOnSeekBarChangeListener(
                    object : SeekBar.OnSeekBarChangeListener {
                        override fun onStartTrackingTouch(seekBar: SeekBar?) {} // no-op
                        override fun onStopTrackingTouch(seekBar: SeekBar?) {} // no-op
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            this@apply.onChange(progress)
                        }
                    })
            midiPots.add(this)
        }
        // midiButtons
        val buttons: List<ToggleButton> = listOf(fx_1, fx_2, play)
        buttons.forEachIndexed { index, button ->
            button.setOnCheckedChangeListener { _, isChecked ->
                MidiButton(index.toByte()).apply {
                    midiEventListener.onNote(this, isChecked)
                    midiButtons.add(this)
                }
            }
        }
        cue.setOnClickListener {
            MidiButton(buttons.size.toByte()).apply {
                midiEventListener.onNote(this, true)
                midiButtons.add(this)
            }
        }
    }

    private var midiManager: MidiManager? = null

    private val deviceAdapter: DeviceAdapter by lazy {
        DeviceAdapter(this, { it.inputPortCount > 0 })
    }
    @Suppress("UNCHECKED_CAST")
    private val midiController: MidiController by lazy {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>) =
                    ({ MidiController(application) })() as T
        }.let<ViewModelProvider.Factory, MidiController> {
            ViewModelProviders.of(this, it).get(MidiController::class.java)
        }
    }
    private val midiEventListener: MidiEventListener by lazy {
        object : MidiEventListener {
            override fun onNote(midiButton: MidiButton, pressed: Boolean) {
                midiController.send(
                        MidiEvent(if (pressed) MidiEventType.STATUS_NOTE_ON else MidiEventType.STATUS_NOTE_OFF,
                                midiButton.midiChannel, midiButton.key, midiButton.velocity))
            }

            override fun onControlChange(midiPot: MidiPot, velocity: Byte) {
                midiController.send(
                        MidiEvent(MidiEventType.STATUS_CONTROL_CHANGE, midiPot.midiChannel, midiPot.key, velocity))
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
        midiButtons.clear()
        midiPots.clear()
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
                initControls()
            }
        }
    }

    private fun showNoMidiError() {
        snack("No MIDI Support.", Snackbar.LENGTH_INDEFINITE)
    }

    private fun snack(text: String, length: Int) {
        applicationContext?.run {
            findViewById<LinearLayout>(R.id.rootView)?.run {
                Snackbar.make(this, text, length).show()
            }
        }
    }
}