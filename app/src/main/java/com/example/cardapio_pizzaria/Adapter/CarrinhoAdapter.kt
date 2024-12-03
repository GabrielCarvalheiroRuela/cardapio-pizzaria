package com.example.cardapio_pizzaria.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cardapio_pizzaria.databinding.ItemCardCarrinhoBinding
import com.example.cardapio_pizzaria.model.Produto

class CarrinhoAdapter(
    private val pedidos: MutableList<Produto>,
    private val onQuantidadeAlterada: (Produto, Int) -> Unit,
    private val onProdutoRemovido: (String) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    inner class CarrinhoViewHolder(private val binding: ItemCardCarrinhoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(produto: Produto) {
            // Carrega a imagem com Glide
            Glide.with(binding.carrinhoImagemProduto.context)
                .load(produto.url)
                .placeholder(com.example.cardapio_pizzaria.R.drawable.ic_placeholder)
                .error(com.example.cardapio_pizzaria.R.drawable.ic_placeholder)
                .into(binding.carrinhoImagemProduto)

            // Configura o nome, preço e quantidade
            binding.carrinhoNomeProduto.text = produto.nome
            binding.carrinhoPrecoProduto.text = "R$ ${produto.preco}"
            binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString() // Exibe a quantidade do Firestore

            // Ações de alterar quantidade
            binding.carrinhoDiminuirQuantidade.setOnClickListener {
                if (produto.quantidade > 1) {
                    produto.quantidade -= 1
                    binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString() // Atualiza o texto na interface
                    onQuantidadeAlterada(produto, produto.quantidade) // Atualiza no Firestore
                }
            }

            binding.carrinhoAumentarQuantidade.setOnClickListener {
                produto.quantidade += 1
                binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString() // Atualiza o texto na interface
                onQuantidadeAlterada(produto, produto.quantidade) // Atualiza no Firestore
            }

            // Ação para remover o produto
            binding.carrinhoRemoverProduto.setOnClickListener {
                onProdutoRemovido(produto.id) // Passa o ID para remoção
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val binding = ItemCardCarrinhoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CarrinhoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val produto = pedidos[position]
        holder.bind(produto)
    }

    override fun getItemCount(): Int = pedidos.size

    // Atualiza a lista de pedidos no adaptador
    fun atualizarLista(novaLista: List<Produto>) {
        pedidos.clear()
        pedidos.addAll(novaLista)
        notifyDataSetChanged()
    }
}
