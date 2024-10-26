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

    private fun validateGeoLocation(latitude: Double, longitude: Double): Boolean{
        if (!geoLocationValidator.isValidLocation(latitude, longitude)){
            _geoLocationValidationErrorMessage.value = "Localização Geográfica inválida."
            return false
        }
        return true
    }

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