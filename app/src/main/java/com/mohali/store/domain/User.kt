package com.mohali.store.domain

data class User(
    val uid: String,
    val username: String,
    val email: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: Long
)
