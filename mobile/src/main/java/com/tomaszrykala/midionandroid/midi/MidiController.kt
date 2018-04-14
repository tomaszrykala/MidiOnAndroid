package com.tomaszrykala.midionandroid.midi

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import com.tomaszrykala.midi.BaseMidiController
import com.tomaszrykala.midi.MidiEvent

class MidiController(
        context: Context,
        private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager,
        private val monitor: MidiDeviceMonitor = MidiDeviceMonitor(context, midiManager),
        private val controller: BaseMidiController = BaseMidiController(midiManager)
) : AndroidViewModel(context.applicationContext as Application) {

    fun send(event: MidiEvent) = controller.send(event)

    fun observeDevices(owner: LifecycleOwner, observer: Observer<List<MidiDeviceInfo>>) = monitor.observe(owner, observer)

    fun removeObserver(observer: Observer<List<MidiDeviceInfo>>) = monitor.removeObserver(observer).also { close() }

    fun open(midiDeviceInfo: MidiDeviceInfo) = controller.open(midiDeviceInfo)

    fun close() = controller.close()
}
