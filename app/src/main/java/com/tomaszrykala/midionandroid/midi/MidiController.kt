package com.tomaszrykala.midionandroid.midi

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiInputPort
import android.media.midi.MidiManager
import android.os.Handler
import android.util.Log

class MidiController(
        context: Context,
        private val midiManager: MidiManager = context.getSystemService(Context.MIDI_SERVICE) as MidiManager,
        private val midiDeviceMonitor: MidiDeviceMonitor = MidiDeviceMonitor(context, midiManager)
) : AndroidViewModel(context.applicationContext as Application) {

    companion object {
        const val TAG: String = "MidiController"
    }

    private var midiInputPort: MidiInputPort? = null
    private var midiDevice: MidiDevice? = null

    fun open(midiDeviceInfo: MidiDeviceInfo) =
            close().also {
                midiDeviceInfo
                        .ports.first { it.type == MidiDeviceInfo.PortInfo.TYPE_INPUT }
                        .portNumber.also { portNumber ->
                    midiManager.openDevice(midiDeviceInfo, {
                        midiDevice = it
                        midiInputPort = it.openInputPort(portNumber)
                    }, Handler())
                }
            }

    fun send(event: MidiEvent) {
        Log.d(TAG, event.toString())
        byteArrayOf(event.type.byte, event.channel, event.note, event.pressure)
                .apply { midiInputPort?.send(this, 0, size) }
    }

    fun observeDevices(lifecycleOwner: LifecycleOwner, observer: Observer<MutableList<MidiDeviceInfo>>) =
            midiDeviceMonitor.observe(lifecycleOwner, observer)

    fun removeObserver(observer: Observer<MutableList<MidiDeviceInfo>>) =
            midiDeviceMonitor.removeObserver(observer).also { close() }

    fun close() {
        midiInputPort?.close()
        midiInputPort = null
        midiDevice?.close()
        midiDevice = null
    }
}
