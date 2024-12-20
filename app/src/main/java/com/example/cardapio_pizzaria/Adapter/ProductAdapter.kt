import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cardapio_pizzaria.databinding.ItemCardProductBinding
import com.example.cardapio_pizzaria.model.Produto
import com.example.cardapio_pizzaria.ui.ProdutoActivity

class ProductAdapter(private val productList: List<Produto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(private val binding: ItemCardProductBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Produto) {
            // Configurando os cards
            binding.productName.text = product.nome
            binding.productPrice.text = "R$ ${product.preco}"
            binding.productIngredient.text = product.ingredientes

            Glide.with(binding.productImage.context)
                .load(product.url)
                .into(binding.productImage)

            // Redirecionando para tela de produto de acordo com o card clicado
            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, ProdutoActivity::class.java).apply {
                    putExtra("nome", product.nome)
                    putExtra("preco", product.preco)
                    putExtra("ingredientes", product.ingredientes)
                    putExtra("url", product.url)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemCardProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size
}
