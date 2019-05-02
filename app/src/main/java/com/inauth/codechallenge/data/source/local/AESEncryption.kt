package com.inauth.codechallenge.data.source.local

import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Cipher
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * This is an encryption class by Java AES 256 Algorithm in CBC Mode.
 */
class AESEncryption {

    companion object {
        private const val encodedKeyString = "z4qZS3bsA1IVoExBGis6cVo62hegDlem"
        private const val ivString = "tuBboNsrXoQ59pMD"

        private const val paddingChar = "#"
        private const val algorithm = "AES"
        private const val transformation = "AES/CBC/NoPadding"

        private var SECRET_KEY_INSTANCE: SecretKey? = null
        private var IV_INSTANCE: ByteArray? = null

        @JvmStatic
        fun getSecretKeyInstance(): SecretKey {
            if (SECRET_KEY_INSTANCE == null) {
                synchronized(AESEncryption::javaClass) {
                    val encodedKey = encodedKeyString.toByteArray(Charsets.UTF_8)

                    SECRET_KEY_INSTANCE =
                        SecretKeySpec(encodedKey, 0, encodedKey.size, algorithm)
                }
            }
            return SECRET_KEY_INSTANCE!!
        }

        @JvmStatic
        fun getIvInstance(): ByteArray {
            if (IV_INSTANCE == null) {
                synchronized(AESEncryption::javaClass) {
                    IV_INSTANCE = ivString.toByteArray(Charsets.UTF_8)
                }
            }
            return IV_INSTANCE!!
        }

        @Throws(Exception::class)
        fun encrypt(plainText: String, keyEncoded: ByteArray, IV: ByteArray): ByteArray {
            //Initialize Cipher for ENCRYPT_MODE
            val plainTextPadding = simplePadding(plainText)
            val cipher = getInitializedCipher(Cipher.ENCRYPT_MODE, keyEncoded, IV)

            //Perform Encryption
            return cipher.doFinal(plainTextPadding.toByteArray(Charsets.UTF_8))
        }

        @Throws(Exception::class)
        fun decrypt(cipherText: ByteArray, keyEncoded: ByteArray, IV: ByteArray): String {
            //Initialize Cipher for DECRYPT_MODE
            val cipher = getInitializedCipher(Cipher.DECRYPT_MODE, keyEncoded, IV)

            //Perform Decryption
            val decryptedText = cipher.doFinal(cipherText)

            return simpleUnpadding(String(decryptedText))
        }

        @Throws(
            NoSuchAlgorithmException::class,
            NoSuchPaddingException::class,
            InvalidKeyException::class,
            InvalidAlgorithmParameterException::class
        )
        fun getInitializedCipher(opmode: Int, keyEncoded: ByteArray, IV: ByteArray): Cipher {
            //Get Cipher Instance
            val cipher = Cipher.getInstance(transformation)

            //Create SecretKeySpec
            val keySpec = SecretKeySpec(keyEncoded, algorithm)

            //Create IvParameterSpec
            val ivSpec = IvParameterSpec(IV)

            //Initialize Cipher for requested mode
            cipher.init(opmode, keySpec, ivSpec)

            return cipher
        }

        fun simplePadding(data: String) : String {
            (data.length % 16).let {
                return when(it) {
                    0 -> data
                    else -> {
                        "$data${paddingChar.repeat(16 - it)}"
                    }
                }
            }
        }

        private fun simpleUnpadding(data: String) : String {
            return data.replace(paddingChar, "")
        }
    }
}