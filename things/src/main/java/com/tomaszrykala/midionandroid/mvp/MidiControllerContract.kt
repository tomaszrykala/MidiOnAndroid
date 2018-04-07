package com.tomaszrykala.midionandroid.mvp

import com.tomaszrykala.midi.MidiEvent
import com.tomaszrykala.midionandroid.control.MidiEventListener

interface MidiControllerContract {

    interface View {

//        fun start()
//
//        fun stop()
////
//        fun startDiscovery(service: String)
//
//        fun stopDiscovery(service: String)
//
//        fun requestConnection(endpointId: String?, serviceId: String)
//
//        fun acceptConnection(endpointId: String?)
//
//        fun sendPayload(event: MidiEvent)

        fun log(log: String)
    }

    interface Presenter : MidiEventListener {

//        fun onStart()
//
//        fun onStop()

//        fun onResultCallback(result: Status)
//
//        fun onEndpointFound(endpointId: String?, discoveredEndpointInfo: DiscoveredEndpointInfo?)
//
//        fun onEndpointLost(endpointId: String?)
//
//        fun onConnectionInitiated(endpointId: String?, info: ConnectionInfo?)

//        fun onConnectionResult(endpointId: String?, p1: ConnectionResolution?)
//
//        fun onDisconnected(endpointId: String?)
//
//        fun onConnected()

//        fun onNote(midiButton: MidiThingsButton, pressed: Boolean)
//
//        fun onControlChange(midiPot: MidiThingsPot, velocity: Byte)
    }
}