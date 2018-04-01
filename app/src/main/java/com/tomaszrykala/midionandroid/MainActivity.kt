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
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Spinner
import com.tomaszrykala.midionandroid.midi.MidiController
import com.tomaszrykala.midionandroid.ui.DeviceAdapter
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.mixer_deck_0.*
import kotlinx.android.synthetic.main.mixer_deck_1.*

class MainActivity : AppCompatActivity() {

    private var midiManager: MidiManager? = null
    private val deviceAdapter: DeviceAdapter by lazy {
        DeviceAdapter(this, { it.inputPortCount > 0 })
    }
    private val midiControlsManager: MidiControlsManager by lazy {
        MidiControlsManager(midiController)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onDestroy() {
        midiController.removeObserver(deviceAdapter)
        super.onDestroy()
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
        midiControlsManager.close()
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
                openMidiControls()
                midiController.observeDevices(this@MainActivity, deviceAdapter)
            }
        }
    }

    private fun openMidiControls() {
        midiControlsManager.open(
                listOf(fx_1, fx_2, play), listOf(cue),
                listOf(eq_lo, eq_mid, eq_hi, gain, filter), listOf(volume))
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