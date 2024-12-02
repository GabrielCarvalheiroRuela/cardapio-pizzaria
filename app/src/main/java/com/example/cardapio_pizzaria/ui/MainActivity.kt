package com.example.cardapio_pizzaria.ui

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapio_pizzaria.databinding.ActivityMainBinding
import com.example.cardapio_pizzaria.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: ProductAdapter
    private val productList = mutableListOf<Produto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Ação do botão de deslogar
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
        }

        // Ação do botão do carrinho
        binding.cartButton.setOnClickListener {
            val intent = Intent(this, CarrinhoActivity::class.java)
            startActivity(intent)
        }

        // Configuração da RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(productList)
        binding.recyclerView.adapter = adapter

        // Chamada para carregar as bebidas
        carregarBebidas()
    }

    private fun carregarBebidas() {
        val db = FirebaseFirestore.getInstance()

        // Pega o documento bebidas na coleção produtos
        db.collection("produtos").document("bebidas")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    // Pega a lista de itens no firestore
                    val itens = document.get("Itens") as? List<Map<String, Any>>

                    // Converte a lista de mapas em objetos do tipo Produto
                    val produtos = itens?.map { item ->
                        Produto(
                            nome = item["nome"] as? String ?: "",
                            preco = (item["preco"] as? Number)?.toDouble() ?: 0.0,
                            ingrediente = item["ingrediente"] as? String ?: "",
                            url = item["url"] as? String ?: ""
                        )
                    } ?: emptyList()

                    // Atualiza a lista de produtos e notifica o Adapter
                    productList.clear()
                    productList.addAll(produtos)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao carregar bebidas: ${e.message}")
            }
    }
}
