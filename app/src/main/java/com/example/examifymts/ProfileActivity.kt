package com.example.examifymts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val btnLogout = findViewById<Button>(R.id.btnLogout)

        btnLogout.setOnClickListener {

            // 🔥 HAPUS SEMUA DATA
            val sharedPref = getSharedPreferences("DATA_UJIAN", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // 🔥 PINDAH KE SPLASH / LOGIN
            val intent = Intent(this, SplashscreenActivity::class.java)
            startActivity(intent)

            // 🔥 TUTUP SEMUA ACTIVITY
            finishAffinity()
        }
    }
}