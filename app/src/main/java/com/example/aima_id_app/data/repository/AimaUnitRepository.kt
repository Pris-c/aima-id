package com.example.aima_id_app.data.repository

import com.example.aima_id_app.data.model.db_model.AimaUnit
import com.example.aima_id_app.data.model.submodel.Address
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

/**
 * AimaUnitRepository is responsible for managing the CRUD operations
 * for the `AimaUnit` objects stored in Firestore.
 *
 * It provides methods to create, read, update, and delete `AimaUnit`
 * entries in the Firestore database, along with querying by specific criteria
 * such as city.
 */
class AimaUnitRepository {

    private val db = FirebaseFirestore.getInstance().collection("aimaUnits")
    private val aimaUnitsMap = mutableMapOf<String, AimaUnit>()

    /**
     * Creates a new `AimaUnit` in the Firestore database.
     *
     * @param unit The `AimaUnit` object to be created.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   the success of the operation.
     */
    fun createAimaUnit(unit: AimaUnit, onComplete: (Boolean) -> Unit) {
        db.add(unit)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Finds an `AimaUnit` by its unique identifier (ID).
     *
     * @param id The unique ID of the `AimaUnit` to be retrieved.
     * @param onComplete A callback function that takes an optional `AimaUnit`
     *                   object. If the unit is found, it returns the unit;
     *                   otherwise, it returns null.
     */
    fun findAimaUnitById(id: String,  onComplete: (AimaUnit?) -> Unit) {
        db.document(id).get()
            .addOnSuccessListener { document ->
                onComplete(document.toObject<AimaUnit>())
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }

    /**
     * Finds `AimaUnit` objects that are located in a specified city.
     *
     * @param city The city to filter `AimaUnit` objects.
     * @param onComplete A callback function that takes a mutable list of
     *                   `AimaUnit` objects found in the specified city.
     */
    fun findAimaUnitByCity(city: String, onComplete: (MutableList<AimaUnit>) -> Unit) {
        db.whereEqualTo("address.city", city).get()
            .addOnSuccessListener { list ->
                val aimaUnits = mutableListOf<AimaUnit>()
                for (document in list){
                    val aimaUnit = document.toObject<AimaUnit>()
                    aimaUnits.add(aimaUnit)
                }
                onComplete(aimaUnits)
            }
            .addOnFailureListener{
                onComplete(mutableListOf())
            }
    }


    /**
     * Retrieves `AimaUnit` objects that match the specified address.
     *
     * @param address An `Address` object containing the street, number, city, and postal code.
     * @param onComplete A callback function that receives a mutable list of
     *                   `AimaUnit` objects found at the specified address.
     *
     */
    fun findAimaUnitByAddress(address: Address, onComplete: (MutableList<AimaUnit>) -> Unit) {
        db.whereEqualTo("address.street", address.street)
            .whereEqualTo("address.number", address.number)
            .whereEqualTo("address.city", address.city)
            .whereEqualTo("address.postalCode", address.postalCode)
            .get()
            .addOnSuccessListener { list ->
                val aimaUnits = mutableListOf<AimaUnit>()
                for (document in list){
                    val aimaUnit = document.toObject<AimaUnit>()
                    aimaUnits.add(aimaUnit)
                }
                onComplete(aimaUnits)
            }
            .addOnFailureListener{
                onComplete(mutableListOf())
            }
    }



    /**
     * Deletes an `AimaUnit` from the Firestore database.
     *
     * @param id The unique ID of the `AimaUnit` to be deleted.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   whether the deletion was successful or not.
     */
    fun deleteAimaUnit(id: String,  onComplete: (Boolean?) -> Unit) {
        db.document(id).delete()
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener {
                onComplete(false)
            }

    }

    /**
     * Updates an existing `AimaUnit` in the Firestore database.
     *
     * @param id The unique ID of the `AimaUnit` to be updated.
     * @param unit The updated `AimaUnit` object.
     * @param onComplete A callback function that takes a Boolean indicating
     *                   whether the update was successful or not.
     */
    fun updateAimaUnit(id: String, unit: AimaUnit, onComplete: (Boolean) -> Unit) {
        db.document(id).set(unit)
            .addOnSuccessListener {
                onComplete(true)
            }

            .addOnFailureListener {
                onComplete(false)
            }
    }

    /**
     * Retrieves all Aima Units from Firestore as a map, where keys are document IDs
     * and values are AimaUnit objects.
     * The `onComplete` callback is invoked with the map upon success, or an empty map
     * upon failure.
     *
     * @param onComplete Callback receiving the map of Aima Units.
     */
    fun findAllAimaUnits(onComplete: (Map<String, AimaUnit>) -> Unit) {
        db.get()
            .addOnSuccessListener { list ->
                aimaUnitsMap.clear()
                for (document in list){
                    val aimaUnit = document.toObject<AimaUnit>()
                    aimaUnitsMap[document.id] = aimaUnit
                }
                onComplete(aimaUnitsMap)
            }
            .addOnFailureListener{
                onComplete(mutableMapOf())
            }
    }


    /**
     * Counts the number of staff members for each AimaUnit in the database.
     *
     * @param onComplete A callback that receives a map of document IDs (Strings)
     *                   and their corresponding staff counts (Ints).
     *
     * On success, it populates the map and calls `onComplete`.
     * On failure, it calls `onComplete` with an empty map.
     */
    fun mapStaffNumberByUnit(onComplete: (MutableMap<String, Int>) -> Unit){
        var staffByUnitMap: MutableMap<String, Int> = mutableMapOf()
        db.get()
            .addOnSuccessListener { aimaUnits ->
                for (unit in aimaUnits){
                    val aimaUnit = unit.toObject<AimaUnit>()
                    staffByUnitMap[unit.id] = aimaUnit.staffIds.size
                }
                onComplete(staffByUnitMap)
            }
            .addOnFailureListener(){
                onComplete(staffByUnitMap)
            }
    }


    fun countStaff(aimaUnitId: String, onComplete: (Int) -> Unit){
        db.document(aimaUnitId).get()
            .addOnSuccessListener { unit ->
                val aimaUnit = unit.toObject<AimaUnit>()
                if (aimaUnit != null) {
                    onComplete(aimaUnit.staffIds.size)
                } else {
                    onComplete(0)
                }
            }
    }
}