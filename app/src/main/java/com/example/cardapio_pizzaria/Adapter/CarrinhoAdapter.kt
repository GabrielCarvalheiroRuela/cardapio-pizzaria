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
    private val onProdutoRemovido: (Produto) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    inner class CarrinhoViewHolder(private val binding: ItemCardCarrinhoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(produto: Produto) {
            // Configura o produto
            Glide.with(binding.carrinhoImagemProduto.context)
                .load(produto.url)
                .into(binding.carrinhoImagemProduto)

            binding.carrinhoNomeProduto.text = produto.nome
            binding.carrinhoPrecoProduto.text = "R$ ${produto.preco}"
            binding.carrinhoQuantidadeProduto.text = produto.quantidade.toString()

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

    // Atualiza a lista de pedidos
    fun atualizarLista(novaLista: List<Produto>) {
        pedidos.clear()
        pedidos.addAll(novaLista)
        notifyDataSetChanged()
    }
}
