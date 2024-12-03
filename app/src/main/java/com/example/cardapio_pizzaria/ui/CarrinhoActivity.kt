package com.example.cardapio_pizzaria.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapio_pizzaria.Adapter.CarrinhoAdapter
import com.example.cardapio_pizzaria.databinding.ActivityCarrinhoBinding
import com.example.cardapio_pizzaria.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CarrinhoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarrinhoBinding
    private lateinit var adapter: CarrinhoAdapter
    private val db = FirebaseFirestore.getInstance()
    private val user = FirebaseAuth.getInstance().currentUser
    private var valorTotal: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarrinhoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewCarrinho.layoutManager = LinearLayoutManager(this)

        // Recupera os pedidos do Firestore
        user?.let {
            val userRef = db.collection("user").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    if (pedidos.isEmpty()) {
                        Toast.makeText(this, "Nenhum pedido encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        val pedidosConvertidos = pedidos.map { pedido ->
                            Produto(
                                id = pedido["id"] as? String ?: "",
                                nome = pedido["nome"] as? String ?: "",
                                preco = (pedido["preco"] as? Number)?.toDouble() ?: 0.0,
                                quantidade = (pedido["quantidade"] as? Number)?.toInt() ?: 1,
                                url = pedido["url"] as? String ?: "",
                                ingredientes = pedido["ingredientes"] as? String ?: ""
                            )
                        }

                        // Calcula o valor total do pedido
                        valorTotal = pedidosConvertidos.sumOf {
                            it.preco * it.quantidade
                        }

                        binding.textViewTotal.text = "Total: R$${"%.2f".format(valorTotal)}"

                        // Configura o adapter do RecyclerView
                        adapter = CarrinhoAdapter(
                            pedidosConvertidos.toMutableList(),
                            onQuantidadeAlterada = { produto, novaQuantidade ->
                                atualizarQuantidadeNoFirestore(produto.id, novaQuantidade)
                            },
                            onProdutoRemovido = { produtoId ->
                                removerProdutoDoFirestore(produtoId)
                            }
                        )

                        // Define o adapter no RecyclerView
                        binding.recyclerViewCarrinho.adapter = adapter
                    }
                } else {
                    Toast.makeText(this, "Nenhum pedido encontrado", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar os pedidos", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }

        // Configuração do botão voltar para a MainActivity
        binding.BackIcon.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Configuração do botão Confirmar Pedido
        binding.buttonConfirmarPedido.setOnClickListener {
            user?.let { user ->
                val userRef = db.collection("user").document(user.uid)
                userRef.update("pedidos", FieldValue.delete())
                    .addOnSuccessListener {
                        Toast.makeText(this, "Pedido realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao confirmar pedido!", Toast.LENGTH_SHORT).show()
                    }
            } ?: run {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Atualizar quantidade do item no pedido
    private fun atualizarQuantidadeNoFirestore(produtoId: String, novaQuantidade: Int) {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    val produto = pedidos.find { it["id"] == produtoId }

                    produto?.let {
                        val produtoAtualizado = it.toMutableMap()
                        produtoAtualizado["quantidade"] = novaQuantidade

                        userRef.update("pedidos", FieldValue.arrayRemove(it))
                        userRef.update("pedidos", FieldValue.arrayUnion(produtoAtualizado))

                        atualizarValorTotal()
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar quantidade!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Remover produto do pedido
    private fun removerProdutoDoFirestore(produtoId: String) {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    val produtoParaRemover = pedidos.find { it["id"] == produtoId }

                    produtoParaRemover?.let {
                        userRef.update("pedidos", FieldValue.arrayRemove(it))
                            .addOnSuccessListener {
                                val pedidosAtualizados = pedidos.filter { it["id"] != produtoId }
                                adapter.atualizarLista(pedidosAtualizados.map { produto ->
                                    Produto(
                                        id = produto["id"] as? String ?: "",
                                        nome = produto["nome"] as? String ?: "",
                                        preco = (produto["preco"] as? Number)?.toDouble() ?: 0.0,
                                        quantidade = produto["quantidade"] as? Int ?: 1,
                                        url = produto["url"] as? String ?: "",
                                        ingredientes = produto["ingredientes"] as? String ?: ""
                                    )
                                })
                                atualizarValorTotal()
                                Toast.makeText(this, "Produto removido do pedido!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao remover o produto!", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
        }
    }

    // Atualizar valor total do pedido
    private fun atualizarValorTotal() {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()
                    valorTotal = pedidos.sumOf {
                        val preco = (it["preco"] as? Number)?.toDouble() ?: 0.0
                        val quantidade = (it["quantidade"] as? Number)?.toInt() ?: 1
                        preco * quantidade
                    }
                    binding.textViewTotal.text = "Total: R$${"%.2f".format(valorTotal)}"
                }
            }
        }
    }
}
