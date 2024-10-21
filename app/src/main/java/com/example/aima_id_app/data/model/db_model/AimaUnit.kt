package com.example.aima_id_app.data.model.db_model

import com.example.aima_id_app.data.model.submodel.Address
import com.example.aima_id_app.data.model.submodel.GeographicalLocation

class AimaUnit (
    name: String,
    address: Address,
    geograficalLocation: GeographicalLocation,
    staffIds: MutableList<String>
)