package com.example.aima_id_app.util.validators

import com.example.aima_id_app.util.enums.PortugueseCities
import java.util.Locale


/**
 * Class that represents an address validator.
 *
 *
 */
class AddressValidator (

) {
    /**
     * Function that validates if the street name is valid.
     * The street name must have a length between 2 and 150 characters.
     *
     * @param street The street name as a string.
     * @return Returns true if the street name length is between 2 and 150 characters.
     */
    fun isValidStreet(street: String): Boolean {
        return street.length in 4..150
    }

    /**
     * Function that validates if a house or building number is valid.
     * The number must be a positive integer with at most 4 digits.
     *
     * @param number The house/building number as an integer.
     * @return Returns true if the number is between 1 and 9999.
     */
    fun isValidNumber(number: Int): Boolean {
        return number in 1..9999
    }

    /**
     * Validates if the provided city name is recognized as a valid city within Portugal.
     *
     * This function checks if the given `city` matches any entry in the `PortugueseCities` enum,
     * ignoring case differences. It compares the provided city name to entries in the enum
     * to determine if it exists in the list of valid Portuguese cities.
     *
     * @param city The name of the city to validate, provided as a string.
     * @return `true` if the city exists in the `PortugueseCities` enum, `false` otherwise.
     */
    fun isValidCity(city: String): Boolean {
        val portugueseCities: List<String> = PortugueseCities.entries.map { it.city }
        return portugueseCities.any { it.equals(city, ignoreCase = true) }
    }

    /**
     * Function that validates if a postal code is valid.
     * The postal code must be exactly 7 digits long, and all characters must be numeric.
     *
     * @param postalCode The postal code as a string.
     * @return Returns true if the postal code length is 7 and all characters are digits.
     */
    fun isValidPostalCode(postalCode: String) : Boolean {
        return postalCode.length == 7 && postalCode.all { it.isDigit() }
    }



}