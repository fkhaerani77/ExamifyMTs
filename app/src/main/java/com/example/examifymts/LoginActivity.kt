package com.example.examifymts

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        btnLogin.setOnClickListener {

            val emailInput = username.text.toString().trim()
            val passInput = password.text.toString().trim()

            if (emailInput.isEmpty() || passInput.isEmpty()) {
                Toast.makeText(this, "Isi semua field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔥 LOGIN KHUSUS GURU (TIDAK PAKAI FIREBASE)
            if (emailInput == "guru" && passInput == "123") {
                startActivity(Intent(this, TeacherActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // 🔥 LOGIN SISWA (PAKAI FIREBASE)
            auth.signInWithEmailAndPassword(emailInput, passInput)
                .addOnSuccessListener {

                    val userId = auth.currentUser?.uid

                    if (userId != null) {

                        db.collection("user")
                            .document(userId)
                            .get()
                            .addOnSuccessListener { document ->

                                if (document.exists()) {
                                    startActivity(Intent(this, DashboardActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Data siswa tidak ditemukan", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Login gagal: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}