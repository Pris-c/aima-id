package com.example.aima_id_app.util.enums

/**
 * Enum class representing possible scheduling times throughout the day.
 *
 * Each enum constant corresponds to a specific time, formatted as "HH:mm".
 *
 * @property time The string representation of the scheduled time.
 */
enum class PossibleScheduling(val time: String) {
    TIME_09_00("09:00"),
    TIME_09_30("09:30"),
    TIME_10_00("10:00"),
    TIME_10_30("10:30"),
    TIME_11_00("11:00"),
    TIME_11_30("11:30"),
    TIME_12_00("12:00"),
    TIME_12_30("12:30"),
    TIME_13_00("13:00"),
    TIME_13_30("13:30"),
    TIME_14_00("14:00"),
    TIME_14_30("14:30"),
    TIME_15_00("15:00"),
    TIME_15_30("15:30"),
    TIME_16_00("16:00"),
    TIME_16_30("16:30"),
    TIME_17_00("17:00");



    companion object {
        fun fromTime(time: String): PossibleScheduling? {
            return PossibleScheduling.values().find { it.time.equals(time, ignoreCase = true) }
        }
    }
}