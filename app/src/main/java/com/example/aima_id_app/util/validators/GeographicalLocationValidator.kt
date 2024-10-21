package com.example.aima_id_app.util.validators

import kotlin.math.abs

class GeographicalLocationValidator {


    fun isValidLocation(latitude: Double, longitude: Double) : Boolean {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    private fun isValidLatitude(latitude: Double) : Boolean {
        return abs(latitude) in 0.0..90.0;
    }

    private fun isValidLongitude(latitude: Double) : Boolean {
        return abs(latitude) in 0.0..180.0;
    }

}