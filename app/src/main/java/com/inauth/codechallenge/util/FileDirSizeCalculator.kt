//package com.inauth.codechallenge.util
//
//import android.annotation.TargetApi
//import android.os.Build
//import java.io.File
//import java.io.IOException
//import java.nio.file.FileVisitResult
//import java.nio.file.Files
//import java.nio.file.Path
//import java.nio.file.SimpleFileVisitor
//import java.nio.file.attribute.BasicFileAttributes
//import java.util.concurrent.atomic.AtomicLong
//
//
//class FileDirSizeCalculator {
//    companion object {
//        fun getSize(path: Path, file: File) : Long {
//            if(Build.VERSION.SDK_INT >= 26) {
//                val size = AtomicLong(0)
//                try {
//                    Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
//                        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
//                            size.addAndGet(attrs.size())
//                            return FileVisitResult.CONTINUE
//                        }
//
//                        override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult {
//                            println("skipped: $file ($exc)")
//                            // Skip folders that can't be traversed
//                            return FileVisitResult.CONTINUE
//                        }
//
//                        override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult {
//                            if (exc != null)
//                                println("had trouble traversing: $dir ($exc)")
//                            // Ignore errors traversing a folder
//                            return FileVisitResult.CONTINUE
//                        }
//                    })
//                }
//                catch (e: IOException) {
//                    throw AssertionError("walkFileTree will not throw IOException if the FileVisitor does not")
//                }
//
//                val size2 = calculateSize(path)
//                //val size3 = folderSize(file)
//
//                return size.get()
//            }
//            else {
//                val size3 = folderSize(file)
//                return 0L
//            }
//        }
//
//
//        @TargetApi(26)
//        private fun calculateSize(path: Path): Long {
//            return try {
//                    if (Files.isRegularFile(path)) {
//                        Files.size(path)
//                    } else Files.list(path).mapToLong { calculateSize(it) }.sum()
//
//                } catch (e: IOException) {
//                    0L
//                }
//        }
//
//        private fun folderSize(directory: File): Long {
//            var length: Long = 0
//            for (file in directory.listFiles()) {
//                length += if (file.isFile) file.length()
//                else folderSize(file)
//            }
//            return length
//        }
//    }
//}