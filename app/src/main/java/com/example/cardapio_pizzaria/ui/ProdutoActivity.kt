package com.example.cardapio_pizzaria.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cardapio_pizzaria.databinding.ActivityProdutoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProdutoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProdutoBinding
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receber os dados do produto via Intent
        val nome = intent.getStringExtra("nome") ?: "Produto"
        val preco = intent.getDoubleExtra("preco", 0.0)
        val url = intent.getStringExtra("url") ?: ""
        val ingrediente = intent.getStringExtra("ingrediente") ?: ""

        // Configurar dados na tela
        binding.productNameTextView.text = nome
        binding.productPriceTextView.text = "R$ %.2f".format(preco)
        Glide.with(this).load(url).into(binding.productImageView)

        // Botão de voltar
        binding.backButton.setOnClickListener {
            finish()
        }

        // Botão Adicionar ao Pedido
        binding.adionarItemPedido.setOnClickListener {
            val quantidade = binding.quantityEditText.text.toString().toIntOrNull()

            if (quantidade != null && quantidade > 0) {
                adicionarAoPedido(nome, preco, ingrediente, quantidade, url)
            } else {
                Toast.makeText(this, "Por favor, insira uma quantidade válida!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarAoPedido(nome: String, preco: Double, ingrediente: String, quantidade: Int, url: String) {
        val produtoId = System.currentTimeMillis().toString()

        val pedidoItem = mapOf(
            "id" to produtoId,
            "nome" to nome,
            "preco" to preco,
            "ingrediente" to ingrediente,
            "quantidade" to quantidade,
            "url" to url
        )

        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Recupera a lista de pedidos atual
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    // Verifica se o produto já existe no array de pedidos
                    val produtoExistente = pedidos.find { it["nome"] == nome }

                    if (produtoExistente != null) {
                        // Produto já existe, então só atualiza a quantidade
                        val quantidadeAtual = produtoExistente["quantidade"] as? Int ?: 0
                        val novaQuantidade = quantidadeAtual + quantidade

                        // Atualiza o produto com a nova quantidade
                        val produtoAtualizado = produtoExistente.toMutableMap()
                        produtoAtualizado["quantidade"] = novaQuantidade

                        // Atualiza o pedido no Firestore
                        val pedidosAtualizados = pedidos.toMutableList().apply {
                            remove(produtoExistente)
                            add(produtoAtualizado)
                        }

                        userRef.update("pedidos", pedidosAtualizados)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Quantidade do produto atualizada!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao atualizar a quantidade!", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        // Produto não existe, então adiciona como um novo item
                        userRef.update("pedidos", FieldValue.arrayUnion(pedidoItem))
                            .addOnSuccessListener {
                                Toast.makeText(this, "Produto adicionado ao pedido!", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao adicionar ao pedido!", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    userRef.set(mapOf("pedidos" to listOf(pedidoItem)))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Produto adicionado ao pedido!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao criar pedido!", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        } ?: run {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }
    }
}
