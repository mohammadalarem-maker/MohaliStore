package com.mohali.store.data

data class UserDto(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val role: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
