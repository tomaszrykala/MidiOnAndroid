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
import com.tomaszrykala.midionandroid.ui.MidiControlsManager
import com.triggertrap.seekarc.SeekArc
import kotlinx.android.synthetic.main.appbar.*
import kotlinx.android.synthetic.main.mixer_deck_0.*
import kotlinx.android.synthetic.main.mixer_deck_1.*

class MainActivity : AppCompatActivity() {

    private var adapterPosition: Int = 0
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

        applicationContext?.let {
            it.packageManager?.let {
                if (it.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
                    (getSystemService(Context.MIDI_SERVICE) as MidiManager?)?.run {
                        openMidiControls()
                        observeDevices()
                    }
                } else showNoMidiError()
            }
        }
    }

    override fun onDestroy() {
        midiController.removeObserver(deviceAdapter)
        midiControlsManager.close()
        super.onDestroy()
    }

    private fun openMidiControls() {
        midiControlsManager.open(listOf(fx_1, fx_2, play), listOf(cue), seekArcList(), listOf(volume))
    }

    private fun observeDevices() {
        midiController.observeDevices(this, deviceAdapter)
    }

    private fun showNoMidiError() {
        findViewById<LinearLayout>(R.id.rootView)?.run {
            Snackbar.make(this, "No MIDI Support.", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    private fun seekArcList(): List<SeekArc> {
        return listOf(eq_lo, eq_mid, eq_hi, gain, filter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.findItem(R.id.app_bar_selector)?.actionView?.apply {
            findViewById<Spinner>(R.id.output_selector)?.apply {
                adapter = deviceAdapter
                setSelection(adapterPosition)
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        midiController.close()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        deviceAdapter[position].apply {
                            midiController.open(this)
                            adapterPosition = position
                        }
                    }
                }
            }
        }
        return true
    }

    companion object {
        const val seekArcsProgressKey = "seekArcsProgress"
        const val adapterPositionKey = "adapterPosition"
    }

    override fun onSaveInstanceState(state: Bundle?) {
        state?.run {
            putIntArray(seekArcsProgressKey, seekArcList().map { it.progress }.toIntArray())
            putInt(adapterPositionKey, adapterPosition)
        }
        super.onSaveInstanceState(state)
    }

    override fun onRestoreInstanceState(state: Bundle?) {
        state?.run {
            getIntArray(seekArcsProgressKey)?.run {
                seekArcList().forEachIndexed { index, seekArc -> seekArc.progress = this[index] }
            }
            adapterPosition = getInt(adapterPositionKey)
        }
        super.onRestoreInstanceState(state)
    }
}