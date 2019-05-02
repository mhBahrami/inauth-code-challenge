cmake_minimum_required(VERSION 3.4.1)

add_library(tiny-aes ../tiny-aes/aes.c)

target_include_directories(tiny-aes PRIVATE ../tiny-aes)

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp CryptoHelper.h CryptoHelper.cpp)
target_include_directories(native-lib PRIVATE ./)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        log-lib

        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log)

target_link_libraries( # Specifies the target library.
        native-lib
        tiny-aes
        #cryptopp-lib
        # Links the target library to the log library
        # included in the NDK.
        #cpufeatures
        android
        ${log-lib})