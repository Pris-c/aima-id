
# Project Structure Overview

## 1. Data Layer
- **Model:** This package contains data structures representing the entities in your database.
- **Repository:** This package manages all database operations, serving as a bridge between the data sources (like Firestore) and the rest of your application.
- **Service:** This package handles interactions with external APIs or services.

---

## Detailed Breakdown

### 1. Data Layer

#### Model Package
- **Purpose:** Contains data structures.
- **Example Classes:**

    - **AimaUnit:**
      ```kotlin
      package com.example.aima_id_app.data.model
  
      data class AimaUnit(
          val nome: String,
          val address: Address,
          val phone: String
      )
      ```

    - **Address:**
      ```kotlin
      package com.example.aima_id_app.data.model
  
      data class Address(
          val street: String,
          val number: String,
          val neighborhood: String,
          val city: String
      )
      ```

#### Repository Package
- **Purpose:** Manages database operations.
- **Example Class:**
  ```kotlin
  class AimaUnitRepository {
      private val db = FirebaseFirestore.getInstance()

      fun saveAimaUnit(aimaUnit: AimaUnit, callback: (Boolean) -> Unit) {
          db.collection("aima_units")
              .add(aimaUnit)
              .addOnSuccessListener { callback(true) }
              .addOnFailureListener { callback(false) }
      }
      
      // Additional methods for fetching and updating AimaUnit
  }

#### Service Package

-   **Purpose:** Interacts with external APIs.
-   **Example Class:**

```kotlin
  class UserValidationService {
    private val client = OkHttpClient()

    fun validateUserData(userId: String): Boolean {
        val request = Request.Builder()
            .url("https://api.example.com/validate/$userId")
            .build()

        val response: Response = client.newCall(request).execute()
        return response.isSuccessful // Returns true if the response is successful
    }
}
```

_______________________


### 2. UI Layer

#### View Package

-   **Purpose:** Contains the user interface components.
-   **Example Class:**

```kotlin
 class AimaUnitActivity : AppCompatActivity() {
    private val viewModel: AimaUnitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unidade)

        viewModel.aimaUnit.observe(this, Observer { aimaUnit ->
            aimaUnit?.let {
                Toast.makeText(this, "AimaUnit Created: ${it.nome}", Toast.LENGTH_SHORT).show()
            }
        })

        btn_create.setOnClickListener {
            // Collect data from input fields
            val name = et_name.text.toString()
            val street = et_street.text.toString()
            val number = et_number.text.toString()
            val neighborhood = et_neighborhood.text.toString()
            val city = et_city.text.toString()
            val phone = et_phone.text.toString()

            viewModel.createAimaUnit(name, street, number, neighborhood, city, phone)
        }
    }
}
```

#### ViewModel Package

-   **Purpose:** Manages UI-related data and user actions.
-   **Example Class:**
```kotlin
class AimaUnitViewModel : ViewModel() {
    private val _aimaUnit = MutableLiveData<AimaUnit>()
    val aimaUnit: LiveData<AimaUnit> get() = _aimaUnit

    fun createAimaUnit(name: String, street: String, number: String, neighborhood: String, city: String, phone: String) {
        val address = Address(street, number, neighborhood, city)
        val newAimaUnit = AimaUnit(name, address, phone)
        _aimaUnit.value = newAimaUnit // Set the created AimaUnit
    }
}
```
