package com.tomaszrykala.midionandroid.midi

import android.media.midi.MidiDeviceInfo
import com.tomaszrykala.midi.BaseMidiController
import com.tomaszrykala.midi.MidiEvent
import com.tomaszrykala.midi.MidiEventType
import com.tomaszrykala.midionandroid.control.MidiButton
import com.tomaszrykala.midionandroid.control.MidiEventListener
import com.tomaszrykala.midionandroid.control.MidiPot

class MidiController(private val controller: BaseMidiController) : MidiEventListener {

    override fun onNote(midiButton: MidiButton, pressed: Boolean) {
        controller.send(MidiEvent(
                if (pressed) MidiEventType.STATUS_NOTE_ON
                else MidiEventType.STATUS_NOTE_OFF,
                midiButton.midiChannel, midiButton.key, midiButton.velocity))
    }

    override fun onControlChange(midiPot: MidiPot, velocity: Byte) {
        controller.send(MidiEvent(MidiEventType.STATUS_CONTROL_CHANGE, midiPot.midiChannel, midiPot.key, velocity))
    }

    fun open(midiDeviceInfo: MidiDeviceInfo) = controller.open(midiDeviceInfo)

    fun close() {
        controller.close()
    }
}