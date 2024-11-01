    package com.example.aima_id_app.ui.viewmodel

    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import com.example.aima_id_app.data.model.db_model.AimaUnit
    import com.example.aima_id_app.data.model.db_model.StaffUser
    import com.example.aima_id_app.data.repository.AimaUnitRepository
    import com.example.aima_id_app.data.repository.AuthRepository
    import com.example.aima_id_app.data.repository.UserRepository
    import com.example.aima_id_app.util.enums.UserRole
    import com.example.aima_id_app.util.validators.UserValidator
    import java.time.LocalDate
    import kotlin.random.Random

    /**
     * ViewModel for managing staff registration in the AIMA ID application.
     * This ViewModel handles user input validation, registration processes,
     * and interaction with various repositories.
     */
    class StaffRegisterViewModel(
        private val userRepository: UserRepository = UserRepository(),
        private val userValidator: UserValidator = UserValidator(),
        private val aimaUnitRepository: AimaUnitRepository = AimaUnitRepository(),
        private val authRepository: AuthRepository = AuthRepository()
    ) : ViewModel(){


        private val _nameErrorMessage= MutableLiveData<String?>()
        val nameErrorMessage: LiveData<String?> = _nameErrorMessage

        private val _nifErrorMessage = MutableLiveData<String?>()
        val nifErrorMessage: LiveData<String?> = _nifErrorMessage

        private val _birthdateErrorMessage = MutableLiveData<String?>()
        val birthdateErrorMessage: LiveData<String?> = _birthdateErrorMessage

        private val _aimaUnitErrorMessage = MutableLiveData<String?>()
        val aimaUnitErrorMessage: LiveData<String?> = _aimaUnitErrorMessage

        private val _registerUserMessage = MutableLiveData<String?>()
        val registerUserMessage: LiveData<String?> = _registerUserMessage

        private val _updateAimaUnitMessage = MutableLiveData<String?>()
        val updateAimaUnitMessage: LiveData<String?> = _updateAimaUnitMessage

        private val _conflictErrorMessage = MutableLiveData<String?>()
        val conflictErrorMessage: LiveData<String?> = _conflictErrorMessage

        private val _staffLogin = MutableLiveData<String?>()
        val staffLogin: LiveData<String?> = _staffLogin

        private val _staffPassword = MutableLiveData<String?>()
        val staffPassword: LiveData<String?> = _staffPassword

        /**
         * Registers a new staff member with the provided details.
         *
         * @param name The name of the staff member.
         * @param email The email of the staff member.
         * @param nif The NIF (tax identification number) of the staff member.
         * @param birthdate The birthdate of the staff member.
         * @param aimaUnitId The ID of the AIMA unit the staff member will be associated with.
         */
        fun register(name: String, email: String, nif: String, birthdate: LocalDate, aimaUnitId: String) {

            // Validate Input Info
            if (!validateUser(name, email, nif, birthdate)) {
                return
            }

            // TODO: Check Gov Database

            // Check weather the staff is already registered
            checkDatabaseForStaff(nif){ isUnique ->
                if (isUnique){

                    // Check the aimaUnit existence
                    findAimaUnit(aimaUnitId) { response ->
                        if (response != null) {
                            val aimaUnit = response
                            val password = generateRandomPassword()

                            // Register Staff Longin with FirebaseAuthentication
                            registerStaffLogin(email, password) { userID ->
                                if (userID != null){

                                    // Register staff User in Firebase Firestore
                                    val staff = StaffUser(email, nif, name, birthdate.toString(), aimaUnitId)
                                    registerStaffUser(userID, staff) { response ->
                                        if (response){

                                            // Add staffId on aimaUnit staff list
                                            addStaffOnAimaUnit(userID, aimaUnitId, aimaUnit)
                                            informLoginAndPassword(email, password)
                                        }
                                    }
                                }
                            }
                        }
                    }
               }
            }
        }


        /**
         * Informs the user of the newly registered staff member's login credentials.
         *
         * This method updates the LiveData objects for staff login and password,
         * allowing observers to react to the new values (e.g., display them in a Toast).
         *
         * @param email The email address of the registered staff member.
         * @param password The generated password for the registered staff member.
         */
        private fun informLoginAndPassword(email: String, password: String){
            _staffLogin.value = email
            _staffPassword.value = password
        }

        /**
         * Registers a staff user in the Firestore database.
         *
         * @param userId The user ID generated during authentication.
         * @param staffUser The staff user object to be registered.
         * @param callback A callback function indicating success or failure.
         */
        private fun registerStaffUser(userId: String, staffUser: StaffUser, callback: (Boolean) -> Unit){
            userRepository.createUser(userId, staffUser){ response ->
                if (response == false){
                    _registerUserMessage.value = "Falha ao salvar funcionário"
                    callback(false)
                } else {
                    callback(true)
                    _registerUserMessage.value = "Funcionário registado com sucesso"
                    _staffLogin
                    _staffPassword
                }
            }
        }

        /**
         * Registers a staff login using the provided email and password.
         *
         * @param email The email for authentication.
         * @param password The password for authentication.
         * @param callback A callback function returning the user ID or null if failed.
         */
        private fun registerStaffLogin(email: String, password: String, callback: (String?) -> Unit){
            authRepository.register(email, password) { userId ->
                if (userId == null) {
                    _registerUserMessage.value = "Falha ao registar autenticação."
                }
                callback(userId)
            }
        }

        /**
         * Validates the provided user details.
         *
         * @param name The name of the staff member.
         * @param email The email of the staff member.
         * @param nif The NIF of the staff member.
         * @param birthdate The birthdate of the staff member.
         * @return True if all validations pass, false otherwise.
         */
        private fun validateUser(name: String, email: String, nif: String, birthdate: LocalDate): Boolean{
            if(!userValidator.isValidName(name)){
                _nameErrorMessage.value = "O nome do funcionário deve ter entre 3 e 150 caracteres"
                return false
            }
            if(!userValidator.isValidEmail(email)){
                _nameErrorMessage.value = "Email inválido!"
                return false
            }
            if (!userValidator.isValidNIF(nif)){
                _nifErrorMessage.value = "NIF inválido!"
                return false
            }
            if (!userValidator.isValidDateOfBirth(birthdate)){
                _birthdateErrorMessage.value = "Data de nascimento inválida!"
                return false
            }
            return true
        }

        /**
         * Finds an AIMA unit by its ID.
         *
         * @param aimaUnitId The ID of the AIMA unit to find.
         * @param callback A callback function returning the AIMA unit or null if not found.
         */
        private fun findAimaUnit(aimaUnitId: String, callback: (AimaUnit?) -> Unit){
            aimaUnitRepository.findAimaUnitById(aimaUnitId){ response ->
                if (response == null) {
                    _aimaUnitErrorMessage.value = "Unidade $aimaUnitId não encontrada"
                }
                callback(response)
            }
        }

        /**
         * Checks if a staff member is already registered by NIF.
         *
         * @param nif The NIF to check for existing staff.
         * @param callback A callback function returning true if unique, false if already registered.
         */
        private fun checkDatabaseForStaff(nif: String, callback: (Boolean) -> Unit){
            userRepository.findUserByNif(nif){ user ->
                if (user != null){
                    if (user.role == UserRole.STAFF){
                        _conflictErrorMessage.value = "O funcionário já se encontra registado."
                        callback(false)
                    } else{
                        callback(true)
                    }
                } else{
                    callback(true)
                }
            }
        }

        /**
         * Adds a staff ID to the list of staff in an AIMA unit.
         *
         * @param staffId The ID of the staff member to add.
         * @param aimaUnitId The ID of the AIMA unit.
         * @param aimaUnit The AIMA unit object to update.
         */
        private fun addStaffOnAimaUnit(staffId: String, aimaUnitId: String, aimaUnit: AimaUnit){
            aimaUnit.staffIds.add(staffId)
            aimaUnitRepository.updateAimaUnit(aimaUnitId, aimaUnit){ response ->
                if (response){
                    _updateAimaUnitMessage.value = "Lista de funcionários da unidade ${aimaUnit.name} " +
                            "atualizada com sucesso"
                } else {
                    _updateAimaUnitMessage.value = "Falha ao atualizar lista de funcionários " +
                            "da unidade ${aimaUnit.name}"
                }
            }

        }

        /**
         * Generates a random password of length 10.
         *
         * @return A randomly generated password.
         */
        private fun generateRandomPassword(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            return (1..10)
                .map { chars[Random.nextInt(chars.length)] }
                .joinToString("")
        }
    }