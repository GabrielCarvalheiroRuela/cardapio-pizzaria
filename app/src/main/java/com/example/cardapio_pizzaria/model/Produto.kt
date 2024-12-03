package com.example.cardapio_pizzaria.model

data class Produto(
    var id: String,
    val nome: String,
    val preco: Double,
    val ingrediente: String,
    val url: String,
    var quantidade: Int = 1
)
