package com.example.examifymts

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore // 🔥 WAJIB
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Tampilkan tanggal hari ini
        val tvTanggal = findViewById<TextView>(R.id.tvTanggal)
        val currentDate = getCurrentDate()
        tvTanggal.text = currentDate

        // 🔥 FIREBASE TEST
        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "nama" to "Khae"
        )

        db.collection("test")
            .add(data)
            .addOnSuccessListener {
                Log.d("FIREBASE", "BERHASIL MASUK 🔥")
                Toast.makeText(this, "Berhasil kirim ke Firebase", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Log.e("FIREBASE", "GAGAL 😭", it)
                Toast.makeText(this, "Gagal kirim", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        return sdf.format(Date())
    }

    fun openProfile(view: View) {
        startActivity(Intent(this, ProfileActivity::class.java))
    }

    fun openExam(view: View) {
        startActivity(Intent(this, ActivityExam::class.java))
    }

    fun openDashboard(view: View) {
        // sudah di dashboard
    }

    fun openResult(view: View) {
        startActivity(Intent(this, ActivityResult::class.java))
    }
}