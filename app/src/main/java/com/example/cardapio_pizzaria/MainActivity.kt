package com.example.cardapio_pizzaria

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pizzaria.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // Usando o ViewBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar o ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar o Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Ação do botão de deslogar usando ViewBinding
        binding.logoutButton.setOnClickListener {
            auth.signOut() // Realiza o logout

            // Redireciona para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Finaliza a MainActivity para evitar que o usuário volte para ela
        }


        // Preparando
        binding.iconUser.setOnClickListener {}

        binding.btnTodos.setOnClickListener {}

        binding.btnPizzas.setOnClickListener {}

        binding.btnEspeciais.setOnClickListener {}

        binding.btnDoces.setOnClickListener {}

        binding.btnBebidas.setOnClickListener {}

        binding.btnVinhos.setOnClickListener {}


        // Configuração do RecyclerView
        val recyclerViewMenu = binding.recyclerViewMenu

    }
}
