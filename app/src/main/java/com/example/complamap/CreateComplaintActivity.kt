package com.example.complamap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.complamap.databinding.CreateComplaintActivityBinding

class CreateComplaintActivity : AppCompatActivity() {
    private lateinit var binding: CreateComplaintActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide();
        binding = CreateComplaintActivityBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    override fun onStart() {
        super.onStart()
        binding.textMock.text = "create complaint"
    }
}
