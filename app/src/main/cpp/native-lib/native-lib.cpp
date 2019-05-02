#include <jni.h>

#include "../tiny-aes/aes.hpp"
#include "CryptoHelper.h"

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_inauth_codechallenge_data_source_local_CryptoApi_onEncryptAESNative(
        JNIEnv *env,
        jobject thiz/* this */,
        jstring data,
        jbyteArray jkey,
        jbyteArray jiv) {

    if (!data || !jkey || !jiv)
        return (jbyteArray) "unknown";

    // Convert jbytearray to uint8_t
    uint8_t *key = CryptoHelper::JByteArrayToUint8(env, jkey);
    uint8_t *iv = CryptoHelper::JByteArrayToUint8(env, jiv);

    // Convert java string to a c++ string
    std::__ndk1::string plainData = CryptoHelper::JStringToString(env, thiz/* this */, data);

    // Encrypt the data with given key and iv
    std::string encryptedData = CryptoHelper::EncryptData(plainData, key, iv);
    jbyteArray array = CryptoHelper::StringToJByteArray(env, encryptedData);

    return array;
}