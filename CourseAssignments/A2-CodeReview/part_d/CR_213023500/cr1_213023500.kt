/**
 * General Review & Recommendations:
 *
 * 1. **Use a Data Class for Pet Data**  
 *    - Instead of using a `HashMap`, define a `Pet` data class. This improves readability, type safety, and reusability.
 *    
 * 2. **Implement the Repository Pattern for Firestore Operations**  
 *    - Move Firestore-related logic to a separate repository class. This keeps the activity clean and follows separation of concerns.
 *    - The repository should handle Firestore interactions, abstracting the database implementation.
 *
 * 3. **Adopt MVVM Architecture**  
 *    - Introduce a `ViewModel` to handle UI logic and state.
 *    - The activity should observe `LiveData` from the `ViewModel` instead of directly interacting with Firestore.
 *
 * 4. **Additional Improvements**  
 *    - Validate `PetAge` to ensure it contains only numeric values.  
 *    - Handle Firestore errors more gracefully, showing user-friendly messages.  
 *    - Remove unnecessary calls to `enableEdgeToEdge()` unless the design requires it.  
 */





package com.myvet.myvet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UpdatePetDetails : AppCompatActivity() {

    // Consider using a ViewModel (`PetViewModel`) instead of handling UI logic in the Activity.
    // The ViewModel will separate concerns and improve maintainability.

    private lateinit var PetName: EditText
    private lateinit var PetAge: EditText
    private lateinit var PetType: EditText
    private lateinit var Next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Not necessary unless the design requires edge-to-edge layout.
        setContentView(R.layout.activity_update_pet_details)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@UpdatePetDetails, PetOwnerWindow::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
                finish()
            }
        })

        PetName = findViewById(R.id.name)
        PetType = findViewById(R.id.typeOfPet)
        PetAge = findViewById(R.id.age)
        Next = findViewById(R.id.next)

        Next.isEnabled = false

        // Suggestion: Move this function inside a ViewModel and use LiveData to update the UI.
        fun checkInputs() {
            val name = PetName.text.toString()  // Should add trim() to remove leading/trailing whitespaces.
            val typeOfPet = PetType.text.toString()
            val age = PetAge.text.toString()

            // Validate if `age` is a number to prevent crashes
            val isValidAge = age.toIntOrNull() != null 

            Next.isEnabled = name.isNotEmpty() && typeOfPet.isNotEmpty() && isValidAge // Should change to use a list of fields like this: listOf(name, typeOfPet, age).all { it.isNotEmpty() }
        }

        PetName.addTextChangedListener { checkInputs() } // Consider using a TextWatcher instead of addTextChangedListener for better performance.
        PetType.addTextChangedListener { checkInputs() }
        PetAge.addTextChangedListener { checkInputs() }

        val db = FirebaseFirestore.getInstance() // Move Firestore operations to a repository class.
        val user = FirebaseAuth.getInstance().currentUser

        Next.setOnClickListener {
            val petCollection = db.collection("users").document(user!!.uid).collection("petDetails")
            val petDocRef = petCollection.document("Pet")

            petDocRef.get().addOnSuccessListener { document ->
                val enteredPetName = PetName.text.toString()

                if (document.exists()) {
                    val existingPetName = document.getString("petName")

                    if (existingPetName == enteredPetName) {
                        // Suggestion: Use a `Pet` data class instead of a HashMap
                        val updatedData = hashMapOf(
                            "age" to PetAge.text.toString(),
                            "typeOfPet" to PetType.text.toString()
                        )

                        // Firestore update should be handled inside a repository class
                        petDocRef.update(updatedData as Map<String, Any>)
                            .addOnSuccessListener {
                                Log.i("Update pet details", "Pet details updated successfully")
                                Toast.makeText(this, "Pet details updated successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, UpdatePetDetailsContinuation::class.java)
                                Log.d("Navigation", "Attempting to open UpdatePetDetails2") // for check
                                startActivity(intent)

                            }
                            .addOnFailureListener {
                                Log.e("Update pet details", "Pet details update failed")
                                Toast.makeText(this, "Pet details update failed", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Instead of blocking a new pet addition, provide a way to add multiple pets.
                        Toast.makeText(this, "You cannot add a new pet!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Move Firestore `set()` call to a repository class
                    // Use `Pet` data class instead of a HashMap for type safety
                    val newPetData = hashMapOf(
                        "petName" to enteredPetName,
                        "petType" to PetType.text.toString(),
                        "petAge" to PetAge.text.toString()
                    )

                    petDocRef.set(newPetData)
                        .addOnSuccessListener {
                            Log.i("Update pet details", "Pet details saved successfully")
                            Toast.makeText(this, "Pet details saved successfully", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, UpdatePetDetailsContinuation::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Log.e("Update pet details", "Failed to save pet details")
                            Toast.makeText(this, "Failed to save pet details", Toast.LENGTH_SHORT).show()
                        }
                }
            }.addOnFailureListener {
                Log.e("Update pet details", "Failed to check pet existence")
                Toast.makeText(this, "Error checking pet details", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
