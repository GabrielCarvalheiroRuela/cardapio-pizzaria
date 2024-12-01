package com.example.cardapio_pizzaria.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pizzaria.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Ação do botão de deslogar usando ViewBinding
        binding.logoutButton.setOnClickListener {
            auth.signOut() // Realiza o logout

            // Redireciona para a tela de login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Ação do botão de perfil
        binding.iconUser.setOnClickListener {
            // Redireciona para a tela de perfil
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        // botão do carrinho
        binding.cartButton.setOnClickListener {
            val intent = Intent(this, CarrinhoActivity::class.java)
            startActivity(intent)
        }

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
