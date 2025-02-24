package com.myvet.myvet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime


/*
Code Review Shay:
1. Missing comments and documentation for the code.
2. Ambiguous variable names - like `auth` and `db` - should be more descriptive.
3. Multiple call of `FirebaseFirestore.getInstance()` can be avoided by storing the instance in a variable.
4. The function `displayAvailabilityWindows` can be broken down into smaller functions (Single Responsibility Principle).
    you can spilt it to `processAvailabilityWindows` (data), and renderAppointmentSlots (UI) - this will make the code more readable and maintainable.
5. Manually adding views in a loop - can be avoided by using RecyclerView - this way the code will be more efficient and scalable.

*/
class MakeAppointment : AppCompatActivity() {
    private lateinit var vetId: String
    private lateinit var vetTitle: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var appointmentList: LinearLayout
    // CR: add this var `val db = FirebaseFirestore.getInstance()` to avoid multiple calls to `FirebaseFirestore.getInstance()`

    private fun makeAppointment(date: LocalDate, time: LocalTime) {

        // CR: change this variable name to something more descriptive
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance() // CR: use the instance stored in a variable

        val appointmentData = hashMapOf(
            "user" to auth.currentUser!!.uid,
            "vet" to vetId,
            "date" to date.toString(),
            "time" to time.toSecondOfDay(),
            "creationTime" to LocalDateTime.now().toString(),
        )

        db.collection("appointments")
            .document()
            .set(appointmentData)
            .addOnSuccessListener {
                Log.i(
                    "Appointment creation",
                    "Appointment created successfully"
                )
                Toast.makeText(this, "Appointment made successfully", Toast.LENGTH_SHORT).show()
            }
    }


    // CR: brake this function into two functions - one for data processing and one for UI rendering
    private fun displayAvailabilityWindows(date: LocalDate, windows: QuerySnapshot) {
        appointmentList.removeAllViews()

        val db = FirebaseFirestore.getInstance() // CR: use the instance stored in a variable

        val appointmentTimes = mutableListOf<LocalTime>()

        for (window in windows) {
            val startTime = LocalTime.ofSecondOfDay(window.getLong("startTime")!!)
            val endTime = LocalTime.ofSecondOfDay(window.getLong("endTime")!!)

            var currentTime = startTime
            while (currentTime.isBefore(endTime)) {
                val nextTime = currentTime.plusMinutes(15)

                appointmentTimes.add(currentTime)

                currentTime = nextTime
            }
        }

        val existingAppointmentTasks = mutableListOf<Task<QuerySnapshot>>()
        for (appointmentTime in appointmentTimes) {
            existingAppointmentTasks.add(
                db.collection("appointments")
                    .whereEqualTo("date", date.toString())
                    .whereEqualTo("time", appointmentTime.toSecondOfDay())
                    .get()
            )
        }

        Tasks.whenAllSuccess<QuerySnapshot>(existingAppointmentTasks)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("MakeAppointment", "Error checking existing appointments", task.exception)
                    return@addOnCompleteListener
                }

                for (snapshot in task.result) {
                    for (existingAppointment in snapshot) {
                        appointmentTimes.remove(LocalTime.ofSecondOfDay(existingAppointment.getLong("time")!!))
                    }
                }   


                // CR: change this code to use RecyclerView instead of adding views manually
                for (appointmentTime in appointmentTimes) {
                    val appointmentContainer = LinearLayout(this)
                        appointmentContainer.orientation = LinearLayout.HORIZONTAL

                        val appointmentText = TextView(this)
                        appointmentText.text = "$appointmentTime - ${appointmentTime.plusMinutes(15)}"
                        appointmentText.layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.7f
                        )

                        val selectButton = Button(this)
                        selectButton.text = "Select"
                        selectButton.layoutParams = LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            0.3f
                        )
                        selectButton.setOnClickListener {
                            makeAppointment(date, appointmentTime)
                            finish()
                        }

                        appointmentContainer.addView(appointmentText)
                        appointmentContainer.addView(selectButton)

                        appointmentList.addView(appointmentContainer)
                }
            }
            .addOnFailureListener { exception ->
                // Handle failure (e.g., logging)
                Log.e("Firestore", "Error fetching appointments", exception)
            }
    }

    private fun queryAvailabilityWindows(date: LocalDate) {
        val db = FirebaseFirestore.getInstance() // CR: use the instance stored in a variable
        db
            .collection("users")
            .document(vetId)
            .collection("availability")
            .whereEqualTo("date", date.toString())
            .get()
            .addOnSuccessListener { result ->
                displayAvailabilityWindows(date, result)
            }
            .addOnFailureListener { exception ->
                Log.e("MakeAppointment", "Error fetching vet availability windows", exception)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_make_appointment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        vetId = intent.getStringExtra("vetId")!!

        val db = FirebaseFirestore.getInstance() // CR: use the instance stored in a variable
        db
            .collection("users")
            .document(vetId)
            .get()
            .addOnSuccessListener { document ->
                vetTitle = findViewById(R.id.vetTitle)
                vetTitle.text = document.getString("name")
            }

        calendarView = findViewById(R.id.calendarView)
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            queryAvailabilityWindows(LocalDate.of(year, month + 1, dayOfMonth))
        }

        appointmentList = findViewById(R.id.appointmentList)

        queryAvailabilityWindows(LocalDate.now())

//        db.collection("users")
//            .document(vetId)
//            .collection("availability")
//            .get()
//            .addOnSuccessListener { result ->
//                val dates = result.documents
//                    .mapNotNull { it.getString("date") }
//                    .distinct() // Get unique dates
//                processDates(dates)
//            }
    }
}