package com.example.cardapio_pizzaria.Adapter

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

            // Configura dados do produto
            binding.carrinhoNomeProduto.text = produto.nome
            binding.carrinhoPrecoProduto.text = "R$ ${produto.preco}"
            binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString() // Mostra a quantidade no TextView

            // Ação de aumentar a quantidade
            binding.carrinhoAumentarQuantidade.setOnClickListener {
                produto.quantidade += 1
                binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString() // Atualiza a quantidade no TextView
                onQuantidadeAlterada(produto, produto.quantidade)
            }

            // Ação de diminuir a quantidade
            binding.carrinhoDiminuirQuantidade.setOnClickListener {
                if (produto.quantidade > 1) {
                    produto.quantidade -= 1
                    binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString() // Atualiza a quantidade no TextView
                    onQuantidadeAlterada(produto, produto.quantidade)
                }
            }

            // Ação para remover o produto
            binding.carrinhoRemoverProduto.setOnClickListener {
                onProdutoRemovido(produto.id) // Passa o ID do produto para remoção
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
