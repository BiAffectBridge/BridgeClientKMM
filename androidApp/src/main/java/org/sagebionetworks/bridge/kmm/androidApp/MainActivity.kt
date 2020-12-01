package org.sagebionetworks.bridge.kmm.androidApp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.MainScope
import org.sagebionetworks.bridge.kmm.androidApp.ui.login.LoginActivity
import org.sagebionetworks.bridge.kmm.shared.cache.AccountDAO
import org.sagebionetworks.bridge.kmm.shared.cache.DatabaseDriverFactory
import org.sagebionetworks.bridge.kmm.shared.repo.AssessmentConfigRepo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!AccountDAO().hasSignedIn) {
            val launchIntent = Intent(this, LoginActivity::class.java)
                .setData(intent.data)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

            startActivity(launchIntent)
        }

    }

    override fun onResume() {
        super.onResume()
        val tv: TextView = findViewById(R.id.text_view)

        if (AccountDAO().hasSignedIn) {
            val repo = AssessmentConfigRepo(DatabaseDriverFactory(this), MainScope())
            repo.getAssessmentById("eGhiQTT2a6SCCmjTod6CDb0t").asLiveData()
                .observe(this, Observer { t ->
                    tv.text = t
                })
        }
    }
}
