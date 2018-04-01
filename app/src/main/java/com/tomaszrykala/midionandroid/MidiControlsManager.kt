package com.tomaszrykala.midionandroid

import android.widget.Button
import android.widget.SeekBar
import android.widget.ToggleButton
import com.tomaszrykala.midionandroid.control.Keys
import com.tomaszrykala.midionandroid.control.MidiButton
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiPot
import com.tomaszrykala.midionandroid.midi.MidiController
import com.tomaszrykala.midionandroid.midi.MidiEvent
import com.tomaszrykala.midionandroid.midi.MidiEventType
import com.triggertrap.seekarc.SeekArc

class MidiControlsManager(val midiController: MidiController) {

    private val keys = Keys.Octave0.Key.values()
    private val midiPots: MutableList<MidiPot> = mutableListOf()
    private val midiButtons: MutableList<MidiButton> = mutableListOf()

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

    fun open(toggleButtons: List<ToggleButton>, buttons: List<Button>, seekArcs: List<SeekArc>, seekBars: List<SeekBar>) {
        initMidiPots(seekArcs, seekBars)
        initMidiButtons(toggleButtons, buttons)
    }

    fun close() {
        midiButtons.clear()
        midiPots.clear()
    }

    private fun initMidiButtons(toggleButtons: List<ToggleButton>, buttons: List<Button>) {
        var index = 0
        toggleButtons.forEach {
            MidiButton(index.toByte()).apply {
                it.setOnCheckedChangeListener { _, isChecked ->
                    midiEventListener.onNote(this, isChecked)
                    midiButtons.add(this)
                }
            }
            index++
        }
        buttons.forEach {
            MidiButton(index.toByte()).apply {
                it.setOnClickListener {
                    midiEventListener.onNote(this, true)
                    midiButtons.add(this)
                }
            }
            index++
        }
    }

    private fun initMidiPots(seekArcs: List<SeekArc>, seekBars: List<SeekBar>) {
        var index = 0
        seekArcs.forEach {
            MidiPot(midiEventListener, midiPotsChannel, keys[index].pitch.toByte()).apply {
                it.setOnSeekArcChangeListener(object : MidiPotListener() {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
                    override fun onProgressChanged(p0: SeekArc?, progress: Int, p2: Boolean) {
                        onChange(progress)
                    }
                })
                midiPots.add(this)
            }
            index++
        }
        seekBars.forEach {
            MidiPot(midiEventListener, midiPotsChannel, keys[index].pitch.toByte()).apply {
                it.setOnSeekBarChangeListener(
                        object : SeekBar.OnSeekBarChangeListener {
                            override fun onStartTrackingTouch(seekBar: SeekBar?) {} // no-op
                            override fun onStopTrackingTouch(seekBar: SeekBar?) {} // no-op
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                                this@apply.onChange(progress)
                            }
                        })
                midiPots.add(this)
            }
            index++
        }
    }

    companion object {
        const val midiPotsChannel: Byte = 11
    }

    abstract class MidiPotListener : SeekBar.OnSeekBarChangeListener, SeekArc.OnSeekArcChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStartTrackingTouch(p0: SeekArc?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(p0: SeekArc?) {}
    }
}