package com.example.aima_id_app.util.enums

import java.time.LocalTime


enum class PossibleScheduling(val time: LocalTime) {
    TIME_09_00(LocalTime.parse("09:00")),
    TIME_09_30(LocalTime.parse("09:30")),
    TIME_10_00(LocalTime.parse("10:00")),
    TIME_10_30(LocalTime.parse("10:30")),
    TIME_11_00(LocalTime.parse("11:00")),
    TIME_11_30(LocalTime.parse("11:30")),
    TIME_12_00(LocalTime.parse("12:00")),
    TIME_12_30(LocalTime.parse("12:30")),
    TIME_13_00(LocalTime.parse("13:00")),
    TIME_13_30(LocalTime.parse("13:30")),
    TIME_14_00(LocalTime.parse("14:00")),
    TIME_14_30(LocalTime.parse("14:30")),
    TIME_15_00(LocalTime.parse("15:00")),
    TIME_15_30(LocalTime.parse("15:30")),
    TIME_16_00(LocalTime.parse("16:00")),
    TIME_16_30(LocalTime.parse("16:30")),
    TIME_17_00(LocalTime.parse("17:00"));
}