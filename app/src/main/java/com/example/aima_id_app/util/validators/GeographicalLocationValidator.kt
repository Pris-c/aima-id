package com.example.aima_id_app.util.validators

import kotlin.math.abs

/**
 * A class responsible for validating geographical coordinates
 *
 * This class provides methods to validate both latitude and longitude values individually
 * and together as a location.
 */
class GeographicalLocationValidator {

    /**
     * Validates whether the provided latitude and longitude values constitute a valid geographical location.
     *
     * @param latitude the latitude to validate, must be within the range of -90.0 to 90.0 decimal degrees.
     * @param longitude the longitude to validate, must be within the range of -180.0 to 180.0 decimal degrees.
     * @return `true` if both latitude and longitude are valid, `false` otherwise.
     */
    fun isValidLocation(latitude: Double, longitude: Double) : Boolean {
        return isValidLatitude(latitude) && isValidLongitude(longitude);
    }

    /**
     * Validates whether the provided latitude is within the valid range (-90.0 to 90.0 decimal degrees).
     *
     * @param latitude the latitude value to validate.
     * @return `true` if the latitude is valid, `false` otherwise.
     */
    private fun isValidLatitude(latitude: Double) : Boolean {
        return abs(latitude) in 0.0..90.0;
    }

    /**
     * Validates whether the provided longitude is within the valid range (-180.0 to 180.0 decimal degrees).
     *
     * @param longitude the longitude value to validate.
     * @return `true` if the longitude is valid, `false` otherwise.
     */
    private fun isValidLongitude(latitude: Double) : Boolean {
        return abs(latitude) in 0.0..180.0;
    }

}