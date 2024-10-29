package com.example.aima_id_app.ui.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.aima_id_app.R
import com.example.aima_id_app.ui.view.RegisterUnitAdminFragment

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        // Define o listener para o card "card_register_unit"
        findViewById<View>(R.id.card_register_unit).setOnClickListener {
            loadRegisterUnitFragment()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // Função para carregar o fragmento
    private fun loadRegisterUnitFragment() {
        val fragment = RegisterUnitAdminFragment()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.animator.slide_in_up,
                R.animator.slide_out_down,
                R.animator.slide_in_up,
                R.animator.slide_out_down)
            .replace(R.id.fragment_container, fragment) // Substitua 'fragment_container' pelo ID do FrameLayout no layout 'activity_admin'
            .addToBackStack(null) // Permite retornar ao estado anterior com o botão de voltar
            .commit()
    }
}
