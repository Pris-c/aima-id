package com.example.aima_id_app.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.aima_id_app.data.model.db_model.AimaUnit
import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.model.submodel.GeographicalLocation
import com.example.aima_id_app.data.repository.AimaUnitRepository
import com.example.aima_id_app.util.validators.AddressValidator
import com.example.aima_id_app.util.validators.GeographicalLocationValidator

/**
 * ViewModel for managing the registration of Aima Units.
 *
 * This ViewModel handles the validation of addresses and geographical locations
 * for Aima Unit registration. It communicates with the AimaUnitRepository to
 * perform database operations and provides error messages to the UI.
 *
 * @property aimaUnitRepository An instance of AimaUnitRepository to handle database operations.
 * @property addressValidator An instance of AddressValidator for validating address inputs.
 * @property geoLocationValidator An instance of GeographicalLocationValidator for validating geographical coordinates.
 *
 * LiveData Properties:
 * - addressErrorMessage: Holds error messages related to address validation.
 * - geolocationErrorMessage: Holds error messages related to geographical location validation.
 * - conflictErrorMessage: Holds error messages if the address conflicts with an existing unit.
 * - createAimaUnitMessage: Holds success or failure messages related to the creation of Aima Units.
 */
class AimaUnitRegisterViewModel(
    private val aimaUnitRepository: AimaUnitRepository = AimaUnitRepository(),
    private val addressValidator: AddressValidator = AddressValidator(),
    private val geoLocationValidator: GeographicalLocationValidator = GeographicalLocationValidator()
) : ViewModel() {

    private val _addressValidationErrorMessage = MutableLiveData<String?>()
    val addressErrorMessage: LiveData<String?> = _addressValidationErrorMessage

    private val _geoLocationValidationErrorMessage = MutableLiveData<String?>()
    val geolocationErrorMessage: LiveData<String?> = _geoLocationValidationErrorMessage

    private val _conflictErrorMessage = MutableLiveData<String?>()
    val conflictErrorMessage: LiveData<String?> = _conflictErrorMessage

    private val _createAimaUnitMessage = MutableLiveData<String?>()
    val createAimaUnitMessage: LiveData<String?> = _createAimaUnitMessage

    private val _aimaUnits = MutableLiveData<List<AimaUnit>>()
    val aimaUnits: LiveData<List<AimaUnit>> = _aimaUnits


    /**
     * Registers a new Aima Unit with the provided details.
     *
     * This method performs validation on the name, address, and geographical location
     * of the Aima Unit. If any validation fails, an appropriate error message is set.
     * If all validations pass, it checks for conflicts in the database regarding the address.
     * If the address is unique, it proceeds to create the Aima Unit in the repository.
     *
     * @param name The name of the Aima Unit to be registered.
     * @param street The street address of the Aima Unit.
     * @param number The street number of the Aima Unit.
     * @param city The city where the Aima Unit is located.
     * @param postalCode The postal code of the Aima Unit's address.
     * @param latitude The geographical latitude of the Aima Unit's location.
     * @param longitude The geographical longitude of the Aima Unit's location.
     */
    fun register(name: String, street: String, number: Int,
                 city: String, postalCode: String, latitude: Double,
                 longitude: Double){

        if (name.isBlank()) {
            _createAimaUnitMessage.value = "Nome da unidade não pode ser vazio."
            return
        }

        val aimaUnitAddress: Address
        val geoLocation: GeographicalLocation

        val validAddress = validateAddress(street, number, city, postalCode)
        val validGeoLocation = validateGeoLocation(latitude, longitude)

        if (validAddress && validGeoLocation){
            aimaUnitAddress = Address(street, number, city, postalCode)
            geoLocation = GeographicalLocation(latitude, longitude)

            checkDatabaseForSameAddress(aimaUnitAddress) { isUnique ->
                if (isUnique){
                   val newAimaUnit = AimaUnit(name, aimaUnitAddress, geoLocation)
                    aimaUnitRepository.createAimaUnit(newAimaUnit) { response ->
                        _createAimaUnitMessage.value = if (response) {
                            "Unidade $name registrada com sucesso."
                        } else {
                            "Falha ao registrar unidade."
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks the database for existing Aima Units with the same address.
     *
     * This method queries the repository to find Aima Units that share the same address
     * as the provided parameter. It invokes the given callback with true if no conflicts
     * are found, or false if an existing unit is found, setting the appropriate error message.
     *
     * @param address The address to check for conflicts in the database.
     * @param callback A callback function that receives a Boolean indicating
     *                 whether the address is unique (true) or not (false).
     */
    private fun checkDatabaseForSameAddress(address: Address, callback: (Boolean) -> Unit) {
        aimaUnitRepository.findAimaUnitByAddress(address) { response ->
            if (response.isNotEmpty()) {
                val unitName = response[0].name
                _conflictErrorMessage.value = "Unidade $unitName já registrada neste endereço"
                callback(false)
            } else {
                callback(true)
            }
        }
    }

    /**
     * Validates the geographical location provided by latitude and longitude.
     *
     * This method uses the geoLocationValidator to check if the given coordinates are valid.
     * If invalid, an error message is set. Returns true if the location is valid, otherwise false.
     *
     * @param latitude The latitude to validate.
     * @param longitude The longitude to validate.
     * @return True if the geographical location is valid, otherwise false.
     */
    private fun validateGeoLocation(latitude: Double, longitude: Double): Boolean{
        if (!geoLocationValidator.isValidLocation(latitude, longitude)){
            _geoLocationValidationErrorMessage.value = "Localização Geográfica inválida."
            return false
        }
        return true
    }

    /**
     * Validates the provided address details.
     *
     * This method checks the validity of the street, number, city, and postal code
     * using the addressValidator. If any validation fails, an appropriate error message is set.
     * Returns true if all validations pass, otherwise false.
     *
     * @param street The street of the address to validate.
     * @param number The street number of the address to validate.
     * @param city The city of the address to validate.
     * @param postalCode The postal code of the address to validate.
     * @return True if the address is valid, otherwise false.
     */
    private fun validateAddress(street: String, number: Int,
                                city: String, postalCode: String) : Boolean {

        if (!addressValidator.isValidCity(city)){
            _addressValidationErrorMessage.value = "Cidade inválida."
            return false
        }
        if (!addressValidator.isValidStreet(street)){
            _addressValidationErrorMessage.value = "Morada inválida."
            return false
        }
        if (!addressValidator.isValidNumber(number)){
            _addressValidationErrorMessage.value = "Valor inválido para número da morada."
            return false
        }
        if (!addressValidator.isValidPostalCode(postalCode)){
            _addressValidationErrorMessage.value = "Código Postal inválido"
            return false
        }
        return true;
    }



}