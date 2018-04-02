package com.tomaszrykala.midionandroid

import android.media.midi.MidiDeviceService
import android.media.midi.MidiDeviceStatus
import android.media.midi.MidiReceiver
import android.util.Log
import com.tomaszrykala.midionandroid.midi.MidiEvent
import com.tomaszrykala.midionandroid.midi.MidiEventType

class MidiListeningService : MidiDeviceService() {

    companion object {
        const val TAG: String = "MidiListeningService"
    }

    private var isRunning: Boolean = false

    override fun onGetInputPortReceivers(): Array<MidiReceiver> = arrayOf(midiReceiver)

    private val midiEventTypes: MutableMap<Byte, MidiEventType> by lazy {
        mutableMapOf<Byte, MidiEventType>().apply {
            MidiEventType.values().asIterable().map {
                this.put(it.byte, it)
            }
        }
    }

    private val midiReceiver = object : MidiReceiver() {

        fun start() {
            Log.d(TAG, "start")
        }

        fun stop() {
            Log.d(TAG, "stop")
        }

        override fun onSend(msg: ByteArray?, offset: Int, count: Int, timestamp: Long) {
            msg?.run {
                midiEventTypes[msg[offset + 0]]?.run {
                    Log.d(TAG, MidiEvent(this, msg[offset + 1], msg[offset + 2], msg[offset + 3]).toString())
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        midiReceiver.stop()
    }

    override fun onDeviceStatusChanged(status: MidiDeviceStatus?) {
        super.onDeviceStatusChanged(status)
        status?.run {
            if (status.isInputPortOpen(0) && !isRunning) {
                midiReceiver.start()
                isRunning = true
            } else if (!status.isInputPortOpen(0) && isRunning) {
                midiReceiver.stop()
                isRunning = false
            }
        }
    }
}