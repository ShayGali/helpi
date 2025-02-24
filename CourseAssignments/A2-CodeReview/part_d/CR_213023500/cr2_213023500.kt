package com.myvet.myvet

/**
 * General Observations & Suggested Improvements:
 * 
 * 1. **Use a Data Class (`Pet`) Instead of HashMaps** 
 *    - Improves type safety, readability, and maintainability.
 *    - Example: Instead of `hashMapOf(...)`, use `Pet(petWeight, medicalHistory)`.
 * 
 * 2. **Move Firestore Operations to a Repository (`PetRepository.kt`)**
 *    - Keeps database logic separate from UI logic.
 *    - Makes the codebase more modular and easier to test.
 * 
 * 3. **Adopt MVVM Architecture (`PetViewModel.kt`)**
 *    - UI should observe data via `LiveData`, making updates reactive.
 *    - Reduces direct database calls in `Activity` classes.
 * 
 * 4. **Fix Typo: Rename `ErorrMessage` → `ErrorMessage`**
 *    - Ensures consistency and avoids confusion.
 * 
 * 5. **Validate Pet Weight Before Updating**
 *    - Ensure `PetWeight.text.toString()` is a valid number before saving.
 *    - Prevents Firestore from storing invalid data.
 * 
 * 6. **Use LiveData for UI State (`UpdateDetails.isEnabled`)**
 *    - Instead of manually enabling/disabling buttons, use `LiveData` in ViewModel.
 * 
 * 7. **Improve Navigation Handling**
 *    - Consider using the Jetpack Navigation Component for better back navigation handling.
 */

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

class UpdatePetDetailsContinuation : AppCompatActivity() {

    // Consider using a ViewModel (`PetViewModel`) to handle UI logic instead of placing it inside the Activity.

    private lateinit var PetWeight: EditText
    private lateinit var PetGender: EditText
    private lateinit var MedicalHistory: EditText
    private lateinit var ErorrMessage: TextView // Typo: Rename `ErorrMessage` to `ErrorMessage`
    private lateinit var UpdateDetails: Button

    @SuppressLint("MissingInflatedId") // This annotation isn't needed unless there's a known issue with view inflation.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Not necessary unless edge-to-edge layout is required.
        setContentView(R.layout.activity_update_pet_details_continuation)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdatePetDetailsContinuation, UpdatePetDetails::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        PetWeight = findViewById(R.id.weight)
        PetGender = findViewById(R.id.gender)
        MedicalHistory = findViewById(R.id.medicalHistory)
        ErorrMessage = findViewById(R.id.errorMessage) // Rename variable to `ErrorMessage`
        UpdateDetails = findViewById(R.id.update)

        UpdateDetails.isEnabled = false // Consider using LiveData in ViewModel for better state management.

        // Function to check if the user has entered valid data
        fun checkInputs() {
            val medicalHistory = MedicalHistory.text.toString() // Consider using `trim()` to remove leading/trailing whitespaces.
            UpdateDetails.isEnabled = medicalHistory.isNotEmpty()
            // Consider validating `PetWeight.text.toString()` to ensure it's a valid number (positive) before enabling the button.
        }

        MedicalHistory.addTextChangedListener { checkInputs() } // Use LiveData in ViewModel for better state management.

        val db = FirebaseFirestore.getInstance() // Move Firestore operations to a repository class.
        val user = FirebaseAuth.getInstance().currentUser

        UpdateDetails.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener { document ->

                val currentMedicalHistory = document.getString("medicalHistory") ?: ""
                val updatedMedicalHistory = "$currentMedicalHistory\n${MedicalHistory.text}"

                // Consider using a `Pet` data class instead of a HashMap.
                val updatedData = hashMapOf(
                    "petWeight" to PetWeight.text.toString(), // Ensure weight is a valid number before updating.
                    "medicalHistory" to updatedMedicalHistory
                )

                petDocRef.update(updatedData as Map<String, Any>) // Move Firestore update logic to a repository class.
                    .addOnSuccessListener {
                        Log.i("Update pet details", "Pet details updated successfully")
                        Toast.makeText(this, "Pet details updated successfully", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, PetOwnerWindow::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("Update pet details", "Pet details update failed")
                        Toast.makeText(this, "Pet details update failed", Toast.LENGTH_SHORT).show()
                    }

            }.addOnFailureListener {
                Log.e("Update pet details", "Failed to check pet existence")
                Toast.makeText(this, "Error checking pet details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
