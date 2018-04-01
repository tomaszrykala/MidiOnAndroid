package com.tomaszrykala.midionandroid.control

sealed class Keys {

   class Octave0 : Keys() {

       enum class Key(val pitch : Double) {
           C0(16.3516),
           Cs0(17.3239),
           D0(18.3540),
           Ds0(19.4454),
           E0(20.6017),
           F0(21.8268),
           Fs0(23.1247),
           G0(24.4997),
           Gs0(25.9565),
           A0(27.5000),
           As0(29.1352),
           B0(30.8677)
       }
    }
}