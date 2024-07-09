package com.trinitydevelopers.realgemsadmin.start

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.trinitydevelopers.realgemsadmin.MainActivity
import com.trinitydevelopers.realgemsadmin.R

class SplashActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        // Initialize LottieAnimationView
        val animationView = findViewById<LottieAnimationView>(R.id.three_dot_animation)
        val loading = findViewById<LottieAnimationView>(R.id.loading_animation)
        // Start animation
        animationView.playAnimation()
        loading.playAnimation()
        loading.speed=3f
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        },1000)
    }
}