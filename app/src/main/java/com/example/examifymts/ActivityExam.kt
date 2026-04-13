package com.example.examifymts

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.content.Intent
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import com.bumptech.glide.Glide
import androidx.core.content.edit

data class Question(
    val question: String = "",
    val imageUrl: String = "",
    val options: List<String> = listOf(),
    val correctAnswer: String = ""
)

class ActivityExam : AppCompatActivity() {

    private lateinit var tvSoalNo: TextView
    private lateinit var tvSoal: TextView
    private lateinit var tvTimer: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var optionA: MaterialCardView
    private lateinit var optionB: MaterialCardView
    private lateinit var optionC: MaterialCardView
    private lateinit var optionD: MaterialCardView

    private lateinit var imgSoal: ImageView

    private lateinit var menuBtn: ImageView
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button

    private var currentIndex = 0
    private var questions = mutableListOf<Question>()
    private val answers = mutableMapOf<Int, String>()
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        val sharedPref = getSharedPreferences("DATA_UJIAN", MODE_PRIVATE)
        sharedPref.edit { putLong("matematika_mulai", System.currentTimeMillis()) }

        initView()
        setupClick()
        startTimer()

        loadSoalDariFirebase()
    }

    // =========================
    // 🔥 LOAD FIREBASE
    // =========================
    private fun loadSoalDariFirebase() {
        val db = FirebaseFirestore.getInstance()

        db.collection("soal")
            .document("matematika")
            .collection("list_soal")
            .get()
            .addOnSuccessListener { result ->

                android.util.Log.d("FIREBASE", "JUMLAH DATA: ${result.size()}")

                questions.clear()

                for (doc in result) {
                    android.util.Log.d("FIREBASE", "DATA: ${doc.data}")

                    val soal = Question(
                        question = doc.getString("pertanyaan") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        options = listOf(
                            doc.getString("jawabanA") ?: "",
                            doc.getString("jawabanB") ?: "",
                            doc.getString("jawabanC") ?: "",
                            doc.getString("jawabanD") ?: ""
                        ),
                        correctAnswer = doc.getString("jawaban") ?: ""
                    )

                    questions.add(soal)
                }

                if (questions.isNotEmpty()) {
                    showQuestion()
                } else {
                    Toast.makeText(this, "Soal kosong!", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                android.util.Log.e("FIREBASE", "Gagal ambil soal", it)
            }
    }

    private fun initView() {
        tvSoalNo = findViewById(R.id.soal_1_textview)
        tvSoal = findViewById(R.id.tvSoal)
        tvTimer = findViewById(R.id.tv_timer)
        progressBar = findViewById(R.id.progressBar)

        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)
        imgSoal = findViewById(R.id.imgSoal)

        menuBtn = findViewById(R.id.menu_btn)
        btnNext = findViewById(R.id.bt_next)
        btnPrev = findViewById(R.id.bt_prev)
    }

    // =========================
    // 🔥 TAMPIL SOAL
    // =========================
    private fun showQuestion() {

        if (questions.isEmpty()) return

        val q = questions[currentIndex]

        tvSoalNo.text = "Soal ${currentIndex + 1} dari ${questions.size}"
        tvSoal.text = q.question

        if (q.imageUrl.isNotEmpty()) {
            imgSoal.visibility = ImageView.VISIBLE

            Glide.with(this)
                .load(q.imageUrl)
                .into(imgSoal)

        } else {
            imgSoal.visibility = ImageView.GONE
        }

        val optionsView = listOf(optionA, optionB, optionC, optionD)

        optionsView.forEachIndexed { index, card ->
            val tv = card.getChildAt(0) as TextView
            tv.text = q.options[index]

            card.setCardBackgroundColor(Color.WHITE)

            val pilihan = listOf("A", "B", "C", "D")

            if (answers[currentIndex] == pilihan[index]) {
                card.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.green_primary)
                )
            }
        }


        progressBar.progress = ((currentIndex + 1) * 100) / questions.size

        btnNext.text = if (currentIndex == questions.size - 1) "Selesai" else "Berikutnya"
    }

    // =========================
    // 🔥 CLICK
    // =========================
    private fun setupClick() {
        val options = listOf(optionA, optionB, optionC, optionD)

        options.forEachIndexed { index, card ->
            card.setOnClickListener {
                val pilihan = listOf("A", "B", "C", "D")
                answers[currentIndex] = pilihan[index]
                showQuestion()
            }
        }

        btnNext.setOnClickListener {
            if (currentIndex == questions.size - 1) {
                showSubmitDialog()
            } else {
                currentIndex++
                showQuestion()
            }
        }

        btnPrev.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                showQuestion()
            }
        }

        menuBtn.setOnClickListener {
            showDialogNomor()
        }
    }

    // =========================
    // 🔥 TIMER
    // =========================
    private fun startTimer() {
        val durasi = 90 * 60 * 1000L

        timer = object : CountDownTimer(durasi, 1000) {
            @SuppressLint("DefaultLocale")
            override fun onTick(millisUntilFinished: Long) {
                val h = millisUntilFinished / 3600000
                val m = (millisUntilFinished % 3600000) / 60000
                val s = (millisUntilFinished % 60000) / 1000
                tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
            }

            override fun onFinish() {
                tvTimer.text = "Waktu habis"
                showSubmitDialog()
            }
        }.start()
    }

    private fun showSubmitDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_submit)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYa = dialog.findViewById<Button>(R.id.btn_ya)
        val btnTidak = dialog.findViewById<Button>(R.id.btn_tidak)

        btnYa.setOnClickListener {
            dialog.dismiss()
            submitExam()
        }

        btnTidak.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun submitExam() {
        var benar = 0

        for (i in questions.indices) {
            if (answers[i] == questions[i].correctAnswer) {
                benar++
            }
        }

        val totalSoal = questions.size
        val salah = totalSoal - benar
        val nilai = (benar * 100) / totalSoal

        val sharedPref = getSharedPreferences("DATA_UJIAN", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putBoolean("matematika_selesai", true)
        editor.putInt("matematika_nilai", nilai)
        editor.putInt("matematika_benar", benar)
        editor.putInt("matematika_salah", salah)
        editor.putLong("matematika_selesai_waktu", System.currentTimeMillis())

        editor.apply()

        startActivity(Intent(this, ActivityResult::class.java))
        finish()
    }

    private fun showDialogNomor() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_soal)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val grid = dialog.findViewById<GridLayout>(R.id.grid_soal)
        grid.removeAllViews()

        for (i in questions.indices) {
            val btn = Button(this)
            btn.text = "${i + 1}"

            val params = GridLayout.LayoutParams()
            params.width = 150
            params.height = 150
            params.setMargins(12, 12, 12, 12)
            btn.layoutParams = params

            btn.setBackgroundColor(Color.WHITE)
            btn.setTextColor(Color.BLACK)

            if (answers[i] != null) {
                btn.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.green_primary)
                )
                btn.setTextColor(Color.WHITE)
            }

            if (i == currentIndex) {
                btn.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.blue_active)
                )
                btn.setTextColor(Color.BLACK)
            }

            btn.setOnClickListener {
                currentIndex = i
                showQuestion()
                dialog.dismiss()
            }

            grid.addView(btn)
        }

        dialog.show()
    }
}