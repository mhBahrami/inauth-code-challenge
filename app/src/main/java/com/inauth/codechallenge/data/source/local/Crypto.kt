package com.inauth.codechallenge.data.source.local

interface Crypto {
    fun onEncryptAES(data: String) : ByteArray
    fun onDecryptAES(data: ByteArray) : String
}