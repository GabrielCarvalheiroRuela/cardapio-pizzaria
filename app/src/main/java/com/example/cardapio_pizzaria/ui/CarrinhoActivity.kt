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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCarrinhoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o RecyclerView
        binding.recyclerViewCarrinho.layoutManager = LinearLayoutManager(this)

        // Recupera os pedidos do Firestore
        user?.let {
            val userRef = db.collection("user").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    // Verifique se há pedidos no Firestore
                    if (pedidos.isEmpty()) {
                        Toast.makeText(this, "Nenhum pedido encontrado", Toast.LENGTH_SHORT).show()
                    } else {
                        val pedidosConvertidos = pedidos.map { pedido ->
                            Produto(
                                id = pedido["id"] as? String ?: "",
                                nome = pedido["nome"] as? String ?: "",
                                preco = (pedido["preco"] as? Number)?.toDouble() ?: 0.0,
                                quantidade = pedido["quantidade"] as? Int ?: 1,  // Aqui o valor da quantidade deve ser retirado do Firestore
                                url = pedido["url"] as? String ?: "",
                                ingrediente = pedido["ingrediente"] as? String ?: ""
                            )
                        }

                        // Configura o adapter do RecyclerView
                        adapter = CarrinhoAdapter(
                            pedidosConvertidos.toMutableList(),
                            onQuantidadeAlterada = { produtoId, novaQuantidade ->
                                produtoId.quantidade = novaQuantidade
                                atualizarPedidoNoFirestore(produtoId) // Atualiza a quantidade no Firestore
                            },
                            onProdutoRemovido = { produtoId ->
                                removerProdutoDoFirestore(produtoId) // Passa o ID para remoção
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
            finish() // Finaliza a CarrinhoActivity para não ficar na pilha de atividades
        }
    }

    private fun atualizarPedidoNoFirestore(produto: Produto) {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            // Atualiza a quantidade do produto no Firestore
            userRef.collection("pedidos").document(produto.id).update("quantidade", produto.quantidade)
                .addOnSuccessListener {
                    Toast.makeText(this, "Quantidade atualizada!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar quantidade!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removerProdutoDoFirestore(produtoId: String) {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            // Recupera a lista de pedidos do Firestore
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    // Encontre o produto no array de pedidos usando o id
                    val produtoParaRemover = pedidos.find { it["id"] == produtoId }

                    produtoParaRemover?.let {
                        // Remover o produto com o map completo
                        userRef.update("pedidos", FieldValue.arrayRemove(it))
                            .addOnSuccessListener {
                                // Atualize o adapter após a remoção
                                val pedidosAtualizados = pedidos.filter { it["id"] != produtoId }
                                adapter.atualizarLista(pedidosAtualizados.map { produto ->
                                    Produto(
                                        id = produto["id"] as? String ?: "",
                                        nome = produto["nome"] as? String ?: "",
                                        preco = (produto["preco"] as? Number)?.toDouble() ?: 0.0,
                                        quantidade = produto["quantidade"] as? Int ?: 1,
                                        url = produto["url"] as? String ?: "",
                                        ingrediente = produto["ingrediente"] as? String ?: ""
                                    )
                                })
                                Toast.makeText(this, "Produto removido do pedido!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao remover o produto!", Toast.LENGTH_SHORT).show()
                            }
                    } ?: run {
                        Toast.makeText(this, "Produto não encontrado para remoção.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Erro ao acessar os pedidos.", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar os pedidos.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

