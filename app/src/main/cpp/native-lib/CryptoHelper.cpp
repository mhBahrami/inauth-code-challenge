#include <vector>
#include <sstream>

#include "CryptoHelper.h"
#include "../tiny-aes/aes.hpp"

std::string CryptoHelper::EncryptData(std::string &plainData, const uint8_t *key, const uint8_t *iv) {
    std::__ndk1::vector<uint8_t> v(plainData.length());
    copy(plainData.begin(), plainData.end(), v.begin());
    uint8_t *in = &v[0];

    struct AES_ctx ctx{};

    /* Initialize context */
    AES_init_ctx_iv(&ctx, key, iv);

    /* Then start encrypting */
    AES_CBC_encrypt_buffer(&ctx, in, static_cast<uint32_t>(plainData.length()));

    std::string result = CryptoHelper::UnsignedCharToString(in, plainData.length());

    return result;
}

std::string CryptoHelper::DecryptData(std::string &encryptedData, const uint8_t *key, const uint8_t *iv) {
    std::__ndk1::vector<uint8_t> v(encryptedData.length());
    copy(encryptedData.begin(), encryptedData.end(), v.begin());
    uint8_t *in = &v[0];

    struct AES_ctx ctx{};

    /* Initialize context */
    AES_init_ctx_iv(&ctx, key, iv);

    /* Then start decrypting */
    AES_CBC_decrypt_buffer(&ctx, in, static_cast<uint32_t>(encryptedData.length()));
    std::string result = CryptoHelper::UnsignedCharToString(in, encryptedData.length());
    return result;
}

jstring CryptoHelper::StringToJString(JNIEnv *env, const std::string &str) {
    jbyteArray array = env->NewByteArray(static_cast<jsize>(str.size()));
    env->SetByteArrayRegion(array, 0, static_cast<jsize>(str.size()), (const jbyte*)str.c_str());
    jstring strEncode = env->NewStringUTF("UTF-8");
    jclass cls = env->FindClass("java/lang/String");
    jmethodID ctor = env->GetMethodID(cls, "<init>", "([BLjava/lang/String;)V");
    auto object = (jstring) env->NewObject(cls, ctor, array, strEncode);
    return object;
}

std::string CryptoHelper::JStringToString(JNIEnv *env, jobject thiz, jstring data) {
    const auto stringClass = env->GetObjectClass(data);
    const auto getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const auto stringJbytes = (jbyteArray) env->CallObjectMethod(data, getBytes, env->NewStringUTF("UTF-8"));

    auto length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, nullptr);

    std::__ndk1::string plainData = std::__ndk1::string((char *)pBytes, length);
    // Release resources
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);
    return plainData;
}

std::string CryptoHelper::UnsignedCharToString(const unsigned char *in, unsigned long len) {
    std::__ndk1::stringstream convert;
    for (int a = 0; a < len; a++) {
        convert << in[a];
    }

    std::__ndk1::string result = convert.str();
    return result;
}

Byte *CryptoHelper::StringToByteArray(const std::string &data, const unsigned char *in) {
    auto lenData = data.length();
    std::__ndk1::stringstream convert;
    for (int a = 0; a < lenData; a++) {
        convert << in[a];
    }

    size_t len = convert.str().length();
    Byte* bytes = new Byte[len+1];
    if (len > 0) strcpy((char*)bytes, convert.str().c_str());
    bytes[len] = '\0';
    return bytes;
}

uint8_t *CryptoHelper::JByteArrayToUint8(JNIEnv *env, _jbyteArray *jByteArray) {
    jsize length = env->GetArrayLength(jByteArray);
    auto * buffer = new jbyte[length + 1];

    env->GetByteArrayRegion(jByteArray, 0, length, buffer);
    buffer[length] = '\0';

    const auto _Byte = (unsigned char*) buffer;
    uint8_t * _uint8 = _Byte;
    return _uint8;
}

jbyteArray CryptoHelper::StringToJByteArray(JNIEnv *env, const std::string &encryptedData) {
    const char* encryptedDataChar = encryptedData.c_str();
    const auto len = static_cast<const jsize>(encryptedData.length());
    jbyteArray array = env->NewByteArray (len);
    env->SetByteArrayRegion (array, 0, len, (jbyte*)encryptedDataChar);
    return array;
}