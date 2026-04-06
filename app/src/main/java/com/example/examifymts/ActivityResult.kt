package com.example.examifymts

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class ActivityResult : AppCompatActivity() {

    fun openDashboard(view: android.view.View) {
        finish()
    }

    fun openProfile(view: android.view.View) {
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val tvMapel = findViewById<TextView>(R.id.tvMapel)
        val tvNilai = findViewById<TextView>(R.id.tvNilai)
        val tvStatus = findViewById<TextView>(R.id.tvStatus)
        val tvBenar = findViewById<TextView>(R.id.tvBenar)
        val tvSalah = findViewById<TextView>(R.id.tvSalah)

        val tvMulai = findViewById<TextView>(R.id.tvMulai)
        val tvSelesai = findViewById<TextView>(R.id.tvSelesai)

        val sharedPref = getSharedPreferences("DATA_UJIAN", MODE_PRIVATE)

        val selesai = sharedPref.getBoolean("matematika_selesai", false)

        val mulai = sharedPref.getLong("matematika_mulai", 0)
        val selesaiWaktu = sharedPref.getLong("matematika_selesai_waktu", 0)
        val tvTanggal = findViewById<TextView>(R.id.tvTanggal)

        val formatTanggal = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val tanggalSekarang = formatTanggal.format(Date())

        tvTanggal.text = tanggalSekarang

        if (selesai) {
            val nilai = sharedPref.getInt("matematika_nilai", 0)
            val benar = sharedPref.getInt("matematika_benar", 0)
            val salah = sharedPref.getInt("matematika_salah", 0)

            tvMapel.text = "Matematika"
            tvNilai.text = "Nilai : $nilai"
            tvBenar.text = "Benar : $benar"
            tvSalah.text = "Salah : $salah"

            if (nilai >= 75) {
                tvStatus.text = "Status : Lulus"
                tvStatus.setTextColor(Color.parseColor("#2E7D32"))
            } else {
                tvStatus.text = "Status : Tidak Lulus"
                tvStatus.setTextColor(Color.RED)
            }

            // 🔥 TAMBAHAN WAKTU
            val format = SimpleDateFormat("HH:mm", Locale.getDefault())

            if (mulai != 0L) {
                tvMulai.text = "Mulai : ${format.format(Date(mulai))}"
            }

            if (selesaiWaktu != 0L) {
                tvSelesai.text = "Selesai : ${format.format(Date(selesaiWaktu))}"
            }

        } else {
            tvMapel.text = "Belum ada ujian"
            tvNilai.text = ""
            tvStatus.text = ""
            tvBenar.text = ""
            tvSalah.text = ""
            tvMulai.text = ""
            tvSelesai.text = ""
        }
    }
}