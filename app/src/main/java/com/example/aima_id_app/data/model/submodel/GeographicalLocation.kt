package com.example.aima_id_app.data.model.submodel

/**
 * Class to represent the geographical coordinates (latitude and longitude) of an AIMA Unit's location.
 *
 * Latitude and longitude values must be provided in decimal degrees.
 *
 * @param latitude The latitude of the location, in decimal degrees, ranging from -90.0 to 90.0.
 * @param longitude The longitude of the location, in decimal degrees, ranging from -180.0 to 180.0.
 */
class GeographicalLocation (val latitude: Double, val longitude: Double)
