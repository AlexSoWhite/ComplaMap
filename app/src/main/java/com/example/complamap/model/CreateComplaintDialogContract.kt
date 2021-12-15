package com.example.complamap.model

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.net.toUri
import com.example.complamap.views.activities.CreateComplaintPopUpActivity

class CreateComplaintDialogContract : ActivityResultContract<String?, Uri?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        return Intent(context, CreateComplaintPopUpActivity::class.java).apply {
            if (input != null) {
                putExtra("uri", input)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (resultCode == Activity.RESULT_OK) {
            "created".toUri()
        } else {
            null
        }
    }
}
