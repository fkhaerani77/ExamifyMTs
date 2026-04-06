package com.example.examifymts

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.content.Intent
import android.os.CountDownTimer
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.card.MaterialCardView

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
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

    private lateinit var menuBtn: ImageView
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button

    private var currentIndex = 0
    private lateinit var questions: List<Question>
    private val answers = mutableMapOf<Int, Int>()

    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exam)

        // 🔥 TAMBAHAN WAKTU MULAI
        val sharedPref = getSharedPreferences("DATA_UJIAN", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong("matematika_mulai", System.currentTimeMillis())
        editor.apply()

        initView()
        initData()
        showQuestion()
        setupClick()
        startTimer()
    }

    private fun initView() {
        tvSoalNo = findViewById(R.id.soal_1_textview)
        tvSoal = findViewById(R.id.soal)
        tvTimer = findViewById(R.id.tv_timer)
        progressBar = findViewById(R.id.progressBar)

        optionA = findViewById(R.id.optionA)
        optionB = findViewById(R.id.optionB)
        optionC = findViewById(R.id.optionC)
        optionD = findViewById(R.id.optionD)

        menuBtn = findViewById(R.id.menu_btn)
        btnNext = findViewById(R.id.bt_next)
        btnPrev = findViewById(R.id.bt_prev)
    }

    private fun initData() {
        questions = listOf(
            Question("2 + 2 = ?", listOf("3","4","5","6"), 1),
            Question("5 x 2 = ?", listOf("10","12","8","6"), 0),
            Question("10 - 3 = ?", listOf("5","6","7","8"), 2)
        )
    }

    private fun showQuestion() {
        val q = questions[currentIndex]

        tvSoalNo.text = "Soal ${currentIndex + 1} dari ${questions.size}"
        tvSoal.text = q.question

        val options = listOf(optionA, optionB, optionC, optionD)

        options.forEachIndexed { index, card ->
            val tv = card.getChildAt(0) as TextView
            tv.text = q.options[index]

            // reset warna
            card.setCardBackgroundColor(Color.WHITE)

            // jika sudah dipilih
            if (answers[currentIndex] == index) {
                card.setCardBackgroundColor(
                    ContextCompat.getColor(this, R.color.green_primary)
                )
            }
        }

        progressBar.progress = ((currentIndex + 1) * 100) / questions.size

        // tombol next berubah
        if (currentIndex == questions.size - 1) {
            btnNext.text = "Selesai"
        } else {
            btnNext.text = "Berikutnya"
        }
    }

    private fun setupClick() {
        val options = listOf(optionA, optionB, optionC, optionD)

        options.forEachIndexed { index, card ->
            card.setOnClickListener {
                answers[currentIndex] = index
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

    private fun startTimer() {
        val waktuMenit = 90
        val durasi = waktuMenit * 60 * 1000L

        timer = object : CountDownTimer(durasi, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val h = millisUntilFinished / 3600000
                val m = (millisUntilFinished % 3600000) / 60000
                val s = (millisUntilFinished % 60000) / 1000
                tvTimer.text = String.format("%02d:%02d:%02d", h, m, s)
            }

            override fun onFinish() {
                tvTimer.text = "Waktu habis"
                showSubmitDialog() // auto submit
            }
        }.start()
    }

    private fun showSubmitDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_submit)

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

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

        // 🔥 SIMPAN KE SharedPreferences
        val sharedPref = getSharedPreferences("DATA_UJIAN", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putBoolean("matematika_selesai", true)
        editor.putInt("matematika_nilai", nilai)
        editor.putInt("matematika_benar", benar)
        editor.putInt("matematika_salah", salah)

        val waktuSelesai = System.currentTimeMillis()
        editor.putLong("matematika_selesai_waktu", waktuSelesai)

        editor.apply()

        // 🔥 PINDAH KE HALAMAN RESULT
        val intent = Intent(this, ActivityResult::class.java)
        startActivity(intent)
        finish()
    }

    private fun showDialogNomor() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_soal)

        // biar rounded & gak ada background putih
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

            // 🔥 DEFAULT: belum dikerjakan (PUTIH)
            btn.setBackgroundColor(Color.WHITE)
            btn.setTextColor(Color.BLACK)

            // 🔥 SUDAH DIKERJAKAN (HIJAU)
            if (answers[i] != null) {
                btn.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.green_primary)
                )
                btn.setTextColor(Color.WHITE)
            }

            // 🔥 SOAL AKTIF (BIRU) → PALING PRIORITAS
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