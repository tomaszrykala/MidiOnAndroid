package com.tomaszrykala.midionandroid.control

sealed class Keys {

    companion object {
        val octave0: Keys = Octave0()
        val octave1: Keys = Octave1()
        val octave2: Keys = Octave2()
        val octave3: Keys = Octave3()
        val octave4: Keys = Octave4()
        val octave5: Keys = Octave5()
        val octave6: Keys = Octave6()
        val octave7: Keys = Octave7()
        val octave8: Keys = Octave8()
    }

    abstract fun c(): Double
    abstract fun cS(): Double
    abstract fun d(): Double
    abstract fun dS(): Double
    abstract fun e(): Double
    abstract fun f(): Double
    abstract fun fS(): Double
    abstract fun g(): Double
    abstract fun gS(): Double
    abstract fun a(): Double
    abstract fun aS(): Double
    abstract fun b(): Double

    fun all(): List<Double> = listOf(c(), cS(), d(), dS(), e(), f(), fS(), g(), gS(), a(), aS(), b())

    final fun allOctaves(): List<Keys> = listOf(
            octave0, octave1, octave2, octave3, octave4, octave5, octave6, octave7, octave8)

    private class Octave0 : Keys() {

        override fun c(): Double = 16.3516
        override fun cS(): Double = 17.3239
        override fun d(): Double = 18.3540
        override fun dS(): Double = 19.4454
        override fun e(): Double = 20.6017
        override fun f(): Double = 21.8268
        override fun fS(): Double = 23.1247
        override fun g(): Double = 24.4997
        override fun gS(): Double = 25.9565
        override fun a(): Double = 27.5000
        override fun aS(): Double = 29.1352
        override fun b(): Double = 30.8677
    }

    private class Octave1 : Keys() {
        override fun c(): Double = 32.7032
        override fun cS(): Double = 34.6478
        override fun d(): Double = 36.7081
        override fun dS(): Double = 38.8909
        override fun e(): Double = 41.2034
        override fun f(): Double = 43.6535
        override fun fS(): Double = 46.2493
        override fun g(): Double = 48.9994
        override fun gS(): Double = 51.9131
        override fun a(): Double = 55.0000
        override fun aS(): Double = 58.2705
        override fun b(): Double = 61.7354
    }

    private class Octave2 : Keys() {
        override fun c(): Double = 65.4064
        override fun cS(): Double = 69.2957
        override fun d(): Double = 73.4162
        override fun dS(): Double = 77.7817
        override fun e(): Double = 82.4069
        override fun f(): Double = 87.3071
        override fun fS(): Double = 92.4986
        override fun g(): Double = 97.9989
        override fun gS(): Double = 103.826
        override fun a(): Double = 110.000
        override fun aS(): Double = 116.541
        override fun b(): Double = 123.471
    }

    private class Octave3 : Keys() {
        override fun c(): Double = 130.813
        override fun cS(): Double = 138.591
        override fun d(): Double = 146.832
        override fun dS(): Double = 155.563
        override fun e(): Double = 164.814
        override fun f(): Double = 174.614
        override fun fS(): Double = 184.997
        override fun g(): Double = 195.998
        override fun gS(): Double = 207.652
        override fun a(): Double = 220.000
        override fun aS(): Double = 233.082
        override fun b(): Double = 246.942
    }

    private class Octave4 : Keys() {
        override fun c(): Double = 261.6262
        override fun cS(): Double = 277.1832
        override fun d(): Double = 293.6652
        override fun dS(): Double = 311.1272
        override fun e(): Double = 329.6282
        override fun f(): Double = 349.2282
        override fun fS(): Double = 369.9942
        override fun g(): Double = 391.9952
        override fun gS(): Double = 415.3052
        override fun a(): Double = 440.0002
        override fun aS(): Double = 466.1642
        override fun b(): Double = 493.8832
    }

    private class Octave5 : Keys() {
        override fun c(): Double = 523.251
        override fun cS(): Double = 554.365
        override fun d(): Double = 587.330
        override fun dS(): Double = 622.254
        override fun e(): Double = 659.255
        override fun f(): Double = 698.456
        override fun fS(): Double = 739.989
        override fun g(): Double = 783.991
        override fun gS(): Double = 830.609
        override fun a(): Double = 880.000
        override fun aS(): Double = 932.328
        override fun b(): Double = 987.767
    }

    private class Octave6 : Keys() {
        override fun c(): Double = 1046.50
        override fun cS(): Double = 1108.73
        override fun d(): Double = 1174.66
        override fun dS(): Double = 1244.51
        override fun e(): Double = 1318.51
        override fun f(): Double = 1396.91
        override fun fS(): Double = 1479.98
        override fun g(): Double = 1567.98
        override fun gS(): Double = 1661.22
        override fun a(): Double = 1760.00
        override fun aS(): Double = 1864.66
        override fun b(): Double = 1975.53
    }

    private class Octave7 : Keys() {
        override fun c(): Double = 2093.00
        override fun cS(): Double = 2217.46
        override fun d(): Double = 2349.32
        override fun dS(): Double = 2489.02
        override fun e(): Double = 2637.02
        override fun f(): Double = 2793.83
        override fun fS(): Double = 2959.96
        override fun g(): Double = 3135.96
        override fun gS(): Double = 3322.44
        override fun a(): Double = 3520.00
        override fun aS(): Double = 3729.31
        override fun b(): Double = 3951.07
    }

    private class Octave8 : Keys() {
        override fun c(): Double = 4186.01
        override fun cS(): Double = 4434.92
        override fun d(): Double = 4698.64
        override fun dS(): Double = 4978.03
        override fun e(): Double = 5274.04
        override fun f(): Double = 5587.65
        override fun fS(): Double = Double.MIN_VALUE
        override fun g(): Double = Double.MIN_VALUE
        override fun gS(): Double = Double.MIN_VALUE
        override fun a(): Double = Double.MIN_VALUE
        override fun aS(): Double = Double.MIN_VALUE
        override fun b(): Double = Double.MIN_VALUE
    }
}