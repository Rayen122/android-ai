package com.example.androidapplication.models

import java.util.Calendar

data class UserDataResponse(
    val id: String,
    val name: String,
    val email: String,
    val roleId: String? = null,
    val dateOfBirth: Int,
    val gender: String = "", // Gender information
    val goal: String = "", // Fitness or health goal
    val currentHeight: Int,
    val selectedDiet: String = "", // Selected diet preference
    val allergy: List<String> = emptyList(), // List of allergies
    val targetWeight: Int,  // Change to Int
    val currentWeight: Int,
    val calories: Int,
// Change to Int
    val age: Int
)

