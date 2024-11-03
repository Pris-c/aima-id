package com.example.aima_id_app.util.enums


/**
 * Enum class representing different user roles within the application.
 *
 * Each enum constant corresponds to a specific role,
 * providing a string representation for that role.
 *
 * @property role The name of the user role.
 */
enum class UserRole(val role: String) {
    ADMIN("Administrador"),
    SERVICE_USER("Utilizador de Serviços"),
    STAFF("Funcionário");

    companion object {
        fun fromRole(role: String): UserRole? {
            return values().find { it.role.equals(role, ignoreCase = true) }
        }
    }
}