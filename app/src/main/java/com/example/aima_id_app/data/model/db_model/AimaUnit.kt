package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.model.submodel.GeographicalLocation

/**
 * A class representing an AIMA Unit, which contains information about its name, address,
 * geographical location, and the list of staff members' IDs.
 *
 * @param name The name of the AIMA Unit.
 * @param address The address of the AIMA Unit, represented by the `Address` class.
 * @param geograficalLocation The geographical coordinates (latitude and longitude) of the AIMA Unit,
 * represented by the `GeographicalLocation` class.
 * @param staffIds A list of staff IDs associated with the unit. The list is mutable,
 * allowing for dynamic changes in the staff composition.
 */

data class AimaUnit (
    val name: String = "",
    val address: Address = Address(),
    val geograficalLocation: GeographicalLocation = GeographicalLocation(),
    val staffIds: MutableList<String> = mutableListOf()
)