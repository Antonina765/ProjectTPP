package com.example.myrayon.model

data class Request(
    val id: Int,
    val userId: Int,
    val street: String,
    val text: String,
    val status: String // "New", "In process", "Done"
)