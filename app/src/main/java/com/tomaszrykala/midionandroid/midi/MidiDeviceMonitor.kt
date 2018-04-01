package com.tomaszrykala.midionandroid.midi

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiManager.DeviceCallback
import android.os.Handler

class MidiDeviceMonitor internal constructor(
        context: Context,
        private val midiManager: MidiManager,
        private val handler: Handler = Handler(context.mainLooper),
        private val data: MutableLiveData<MutableList<MidiDeviceInfo>> = MutableLiveData()
) : MediatorLiveData<MutableList<MidiDeviceInfo>>() {

    override fun onActive() {
        super.onActive()
        midiManager.registerDeviceCallback(deviceCallback, handler)
        // data.addAll(midiManager.devices)
        data.value = mutableListOf(*midiManager.devices)
        midiManager.registerDeviceCallback(
                deviceCallback, handler)
    }

    override fun onInactive() {
        midiManager.unregisterDeviceCallback(deviceCallback)
        // data.clear()
        data.value?.clear()
        super.onInactive()
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<MutableList<MidiDeviceInfo>>) {
        super.observe(owner, observer)
        addSource(data, observer)
    }

    override fun removeObserver(observer: Observer<MutableList<MidiDeviceInfo>>) {
        super.removeObserver(observer)
        if (!hasObservers()) {
            removeSource(data)
        }
    }

    private val deviceCallback = object : DeviceCallback() {
        override fun onDeviceAdded(device: MidiDeviceInfo?) {
            super.onDeviceAdded(device)
            device?.also {
                data.value?.add(it)
            }
        }

        override fun onDeviceRemoved(device: MidiDeviceInfo?) {
            super.onDeviceRemoved(device)
            device?.also {
                data.value?.remove(it)
            }
        }
    }
}
