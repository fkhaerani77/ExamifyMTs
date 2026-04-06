package com.example.examifymts

import android.content.Intent
import android.widget.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.examifymts.DashboardActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val username = findViewById<EditText>(R.id.etUsername)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val userInput = username.text.toString()
            val passInput = password.text.toString()

            // USER & PASSWORD SEMENTARA (hardcode)
            val userBenar = "admin"
            val passBenar = "123"

            if (userInput == userBenar && passInput == passBenar) {
                // pindah ke Dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Username atau Password salah!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}