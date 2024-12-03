package com.example.cardapio_pizzaria.ui

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

        binding.recyclerViewCarrinho.layoutManager = LinearLayoutManager(this)

        // Recuperar os pedidos do Firestore
        user?.let {
            val userRef = db.collection("user").document(it.uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val pedidos = document.get("pedidos") as? List<Map<String, Any>> ?: emptyList()

                    val pedidosConvertidos = pedidos.map { pedido ->
                        Produto(
                            id = pedido["id"] as String,
                            nome = pedido["nome"] as String,
                            preco = pedido["preco"] as Double,
                            quantidade = pedido["quantidade"] as? Int ?: 1,
                            url = pedido["url"] as String,
                            ingrediente = pedido["ingrediente"] as? String ?: ""
                        )
                    }

                    adapter = CarrinhoAdapter(
                        pedidosConvertidos.toMutableList(),
                        onQuantidadeAlterada = { produto, novaQuantidade ->
                            produto.quantidade = novaQuantidade
                            atualizarPedidoNoFirestore(produto)
                        },
                        onProdutoRemovido = { produto ->
                            removerProdutoDoFirestore(produto)
                        }
                    )

                    binding.recyclerViewCarrinho.adapter = adapter
                } else {
                    Toast.makeText(this, "Nenhum pedido encontrado", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar os pedidos", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarPedidoNoFirestore(produto: Produto) {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            // Atualizar a quantidade do produto no Firestore
            userRef.update("pedidos", FieldValue.arrayUnion(produto))
                .addOnSuccessListener {
                    Toast.makeText(this, "Quantidade atualizada!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar quantidade!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun removerProdutoDoFirestore(produto: Produto) {
        user?.let { usuario ->
            val userRef = db.collection("user").document(usuario.uid)

            // Remover o produto do array "pedidos" no Firestore
            userRef.update("pedidos", FieldValue.arrayRemove(produto))
                .addOnSuccessListener {
                    Toast.makeText(this, "Produto removido do pedido!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao remover o produto!", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
