import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cardapio_pizzaria.databinding.ItemCardProductBinding
import com.example.cardapio_pizzaria.model.Produto

class ProductAdapter(private val productList: List<Produto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(private val binding: ItemCardProductBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(product: Produto) {
            // Usando o ViewBinding para setar os valores dos elementos
            binding.productName.text = product.nome
            binding.productPrice.text = "R$ ${product.preco}"
            binding.productIngredient.text = product.ingrediente

            // Carregar a imagem com Glide
            Glide.with(binding.productImage.context)
                .load(product.url)
                .into(binding.productImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        // Inflar o layout do item com o ViewBinding
        val binding = ItemCardProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int = productList.size
}
