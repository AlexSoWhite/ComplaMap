package com.example.complamap

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.ColorUtils
import com.example.complamap.databinding.ActivityPopUpGuideBinding
import com.example.complamap.databinding.FragmentMockBinding

class PopUpGuide : AppCompatActivity() {
    private lateinit var binding: ActivityPopUpGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPopUpGuideBinding.inflate(layoutInflater)
        overridePendingTransition(0, 0)
        setContentView(binding.root)


    }
}