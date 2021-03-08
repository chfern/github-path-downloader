package org.fc.githubpathdownloader.service

import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL


class FileDownloaderService {
    fun download(url: String, fileName: String) {
        println("Downloading $fileName")
        val u = URL(url)
        val `is`: InputStream = u.openStream()
        val dis = DataInputStream(`is`)

        val buffer = ByteArray(1024)
        var length: Int

        val fos = FileOutputStream(File(fileName))
        while (dis.read(buffer).also { length = it } > 0) {
            fos.write(buffer, 0, length)
        }
    }
}