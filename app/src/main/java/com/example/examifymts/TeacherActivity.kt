package com.example.examifymts

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

class TeacherActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var btnGenerate: Button
    private lateinit var btnExport: Button
    private lateinit var tvEmail: TextView
    private lateinit var tvPassword: TextView
    private lateinit var tvListAkun: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    // 🔥 SIMPAN DATA AKUN
    private val daftarAkun = mutableListOf<Pair<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher)

        etNama = findViewById(R.id.etNamaSiswa)
        btnGenerate = findViewById(R.id.btnGenerate)
        btnExport = findViewById(R.id.btnExport)
        tvEmail = findViewById(R.id.tvEmail)
        tvPassword = findViewById(R.id.tvPassword)
        tvListAkun = findViewById(R.id.tvListAkun)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        btnGenerate.setOnClickListener {
            val nama = etNama.text.toString().trim()

            if (nama.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                btnGenerate.isEnabled = false
                buatAkunSiswa(nama)
            }
        }

        // 🔥 EXPORT BUTTON
        btnExport.setOnClickListener {
            exportCSV()
        }
    }

    private fun buatAkunSiswa(nama: String) {

        val cleanName = nama.lowercase().replace(" ", "")

        val randomNumber = (10000..99999).random() // 🔥 5 digit
        val email = "$cleanName$randomNumber@examify.com"
        val password = (cleanName.take(5) + "12345").padEnd(6, '1')

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                btnGenerate.isEnabled = true

                // 🔥 tampilkan ke list
                val currentText = tvListAkun.text.toString()
                tvListAkun.text = currentText + "\n$email | $password"

                if (task.isSuccessful) {

                    val userId = auth.currentUser?.uid

                    if (userId != null) {

                        val data = hashMapOf(
                            "nama" to nama,
                            "email" to email,
                            "role" to "siswa"
                        )

                        db.collection("user")
                            .document(userId)
                            .set(data)
                            .addOnSuccessListener {

                                // 🔥 SIMPAN KE LIST
                                daftarAkun.add(Pair(email, password))

                                tvEmail.text = "Email: $email"
                                tvPassword.text = "Password: $password"

                                etNama.text.clear() // biar enak input lagi

                                Toast.makeText(this, "Akun berhasil dibuat 🔥", Toast.LENGTH_SHORT).show()

                                auth.signOut()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Firestore gagal: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                    }

                } else {
                    Toast.makeText(this, "Auth gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    // 🔥 FUNCTION EXPORT CSV
    private fun exportCSV() {
        try {
            val fileName = "akun_siswa.csv"

            val file = java.io.File(
                android.os.Environment.getExternalStoragePublicDirectory(
                    android.os.Environment.DIRECTORY_DOWNLOADS
                ),
                fileName
            )

            file.printWriter().use { out ->
                out.println("Email,Password")

                daftarAkun.forEach {
                    out.println("${it.first},${it.second}")
                }
            }

            Toast.makeText(this, "File tersimpan di Download 🔥", Toast.LENGTH_LONG).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Gagal export: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}