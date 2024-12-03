package com.example.cardapio_pizzaria.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.cardapio_pizzaria.databinding.ActivityProdutoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailsActivity : AppCompatActivity() {

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
        binding.addToOrderButton.setOnClickListener {
            val quantidade = binding.quantityEditText.text.toString().toIntOrNull()

            if (quantidade != null && quantidade > 0) {
                adicionarAoPedido(nome, preco, ingrediente, quantidade, url)
            } else {
                Toast.makeText(this, "Por favor, insira uma quantidade válida!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adicionarAoPedido(nome: String, preco: Double, ingrediente: String, quantidade: Int, url: String) {
        // Gerar um ID único para o produto usando o timestamp
        val produtoId = System.currentTimeMillis().toString()

        // Adicionando o campo 'id' ao pedido
        val pedidoItem = mapOf(
            "id" to produtoId, // ID único para cada produto
            "nome" to nome,
            "preco" to preco,
            "ingrediente" to ingrediente,
            "quantidade" to quantidade,
            "url" to url // Aqui está o ajuste, agora o URL da imagem também será salvo
        )

        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Atualiza o array de pedidos no documento do usuário
                    userRef.update("pedidos", FieldValue.arrayUnion(pedidoItem))
                        .addOnSuccessListener {
                            Toast.makeText(this, "Produto adicionado ao pedido!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao adicionar ao pedido!", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Cria um novo documento com o array inicial de pedidos
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
