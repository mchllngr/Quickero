package de.mchllngr.quickero.module.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.commit
import com.google.android.material.appbar.MaterialToolbar
import com.mikepenz.aboutlibraries.LibsBuilder
import de.mchllngr.quickero.R

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        findViewById<MaterialToolbar>(R.id.toolbar)?.let { toolbar ->
            setSupportActionBar(toolbar)
            toolbar.navigationIcon = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }

        supportFragmentManager.commit {
            replace(
                R.id.fragment_container_view,
                LibsBuilder()
                    .supportFragment()
            )
        }
    }

    companion object {

        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, AboutActivity::class.java))
        }
    }
}
