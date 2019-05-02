#ifndef INAUTH_CODE_CHALLENGE_CRYPTOHELPER_H
#define INAUTH_CODE_CHALLENGE_CRYPTOHELPER_H

#include <jni.h>
#include <string>
#include <zconf.h>

class CryptoHelper {
public:
    static std::string EncryptData(std::string &plainData, const uint8_t *key, const uint8_t *iv);

    static std::string DecryptData(std::string &encryptedData, const uint8_t *key, const uint8_t *iv);

    static jstring StringToJString(JNIEnv *env, const std::string &str);

    static std::string JStringToString(JNIEnv *env, jobject thiz/* this */, jstring data);

    static std::string UnsignedCharToString(const unsigned char *in, unsigned long len);

    static Byte *StringToByteArray(const std::string &data, const unsigned char *in);

    static uint8_t *JByteArrayToUint8(JNIEnv *env, _jbyteArray *jByteArray);

    static jbyteArray StringToJByteArray(JNIEnv *env, const std::string &encryptedData);
};

#endif //INAUTH_CODE_CHALLENGE_CRYPTOHELPER_H