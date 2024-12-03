package com.example.cardapio_pizzaria.ui

import ProductAdapter
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapio_pizzaria.databinding.ActivityMainBinding
import com.example.cardapio_pizzaria.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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
            val currentUser = auth.currentUser
            currentUser?.let { user ->
                // Acessa o documento do usuário no Firestore
                val userRef = FirebaseFirestore.getInstance().collection("user").document(user.uid)

                // Remove o array de pedidos se existir
                userRef.update("pedidos", FieldValue.delete())
                    .addOnSuccessListener {
                        auth.signOut()

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        println("Erro ao remover os pedidos: ${e.message}")
                        auth.signOut()

                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
            } ?: run {
                auth.signOut()

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // Ação do botão de perfil
        binding.iconUser.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Ação do botão do carrinho
        binding.cartButton.setOnClickListener {
            val intent = Intent(this, CarrinhoActivity::class.java)
            startActivity(intent)
        }

        // Filtro todos
        binding.btnTodos.setOnClickListener {
            productList.clear()
            carregarTodosProdutos()
        }
        // Filtro Pizzas salgadas
        binding.btnPizzas.setOnClickListener {
            productList.clear()
            carregarPizzasSalgadas()
        }
        // Filtro Pizzas doces
        binding.btnDoces.setOnClickListener {
            productList.clear()
            carregarPizzasDoces()
        }
        // Filtro Bebidas
        binding.btnBebidas.setOnClickListener {
            productList.clear()
            carregarBebidas()
        }

        // Configuração da RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductAdapter(productList)
        binding.recyclerView.adapter = adapter

        // Chamada para carregar as bebidas
        carregarBebidas()
        carregarPizzasDoces()
        carregarPizzasSalgadas()
    }

    private fun carregarTodosProdutos() {
        productList.clear()
        carregarBebidas()
        carregarPizzasDoces()
        carregarPizzasSalgadas()
    }

    private fun carregarBebidas() {
        val db = FirebaseFirestore.getInstance()

        db.collection("produtos").document("bebidas")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val itens = document.get("Itens") as? List<Map<String, Any>>
                    val produtos = itens?.map { item ->
                        Produto(
                            id = item["id"] as? String ?: "",
                            nome = item["nome"] as? String ?: "",
                            preco = (item["preco"] as? Number)?.toDouble() ?: 0.0,
                            ingredientes = item["ingredientes"] as? String ?: "",
                            url = item["url"] as? String ?: ""
                        )
                    } ?: emptyList()

                    productList.addAll(produtos)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao carregar bebidas: ${e.message}")
            }
    }

    private fun carregarPizzasDoces() {
        val db = FirebaseFirestore.getInstance()

        db.collection("produtos").document("pizzas-doce")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val itens = document.get("itens") as? List<Map<String, Any>>
                    val produtos = itens?.map { item ->
                        Produto(
                            id = item["id"] as? String ?: "",
                            nome = item["nome"] as? String ?: "",
                            preco = (item["preco"] as? Number)?.toDouble() ?: 0.0,
                            ingredientes = item["ingredientes"] as? String ?: "",
                            url = item["url"] as? String ?: ""
                        )
                    } ?: emptyList()

                    productList.addAll(produtos)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao carregar pizzas doces: ${e.message}")
            }
    }

    private fun carregarPizzasSalgadas() {
        val db = FirebaseFirestore.getInstance()

        db.collection("produtos").document("pizzas-salgadas")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val itens = document.get("itens") as? List<Map<String, Any>>
                    val produtos = itens?.map { item ->
                        Produto(
                            id = item["id"] as? String ?: "",
                            nome = item["nome"] as? String ?: "",
                            preco = (item["preco"] as? Number)?.toDouble() ?: 0.0,
                            ingredientes = item["ingredientes"] as? String ?: "",
                            url = item["url"] as? String ?: ""
                        )
                    } ?: emptyList()

                    productList.addAll(produtos)
                    adapter.notifyDataSetChanged()
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao carregar pizzas salgadas: ${e.message}")
            }
    }

}
