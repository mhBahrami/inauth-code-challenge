package com.inauth.codechallenge.data.source.local

import androidx.annotation.VisibleForTesting

class CryptoApi : Crypto {

    override fun onEncryptAES(data: String): ByteArray {
        return onEncryptAESNative(
            AESEncryption.simplePadding(data),
            AESEncryption.getSecretKeyInstance().encoded,
            AESEncryption.getIvInstance())
    }

    override fun onDecryptAES(data: ByteArray): String {
        return AESEncryption.decrypt(data,
            AESEncryption.getSecretKeyInstance().encoded,
            AESEncryption.getIvInstance())
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    private external fun onEncryptAESNative(data: String, key: ByteArray, iv: ByteArray): ByteArray

    companion object {
        private var INSTANCE: CryptoApi? = null

        @JvmStatic
        fun getInstance(): CryptoApi {
            if (INSTANCE == null) {
                synchronized(CryptoApi::javaClass) {
                    INSTANCE = CryptoApi()
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }

}