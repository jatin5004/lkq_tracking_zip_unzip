package com.ebctech.web.control.service


import org.slf4j.{Logger, LoggerFactory}
import java.io._

import java.util.zip.{ZipEntry, ZipInputStream}

object UnzipFiles {
  private val BUFFER_SIZE: Int = 8192
  private final val logger = LoggerFactory.getLogger(this.getClass).asInstanceOf[Logger]

  @throws[IOException]
  def unzipFiles(zipFilePath: String, destDirectory: String): Unit = {
    val destDir: File = new File(destDirectory)
    if (!destDir.exists()) destDir.mkdirs()

    val extractedFiles = scala.collection.mutable.Set[String]()

    try {
      val zipIn: ZipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))
      var entry = zipIn.getNextEntry

      while (entry != null) {
        val entryIdentifier: String = s"${entry.getName}_${entry.getCompressedSize}_${entry.getTime}"

        if (!extractedFiles.contains(entryIdentifier)) {
          val filePath: String = destDirectory + File.separator + entry.getName

          if (!entry.isDirectory) {
            extractFile(zipIn, filePath)
            println(s"Extracted file: $filePath")
          } else {
            val dir: File = new File(filePath)
            dir.mkdirs()
          }

          extractedFiles.add(entryIdentifier)
        } else {
          println(s"File already extracted: ${entry.getName}")
        }

        entry = zipIn.getNextEntry
      }
      zipIn.close()
    } catch {
      case e: IOException =>
        println("Error reading the zip file: " + e.getMessage)
        e.printStackTrace()
    }
  }

  def extractFile(zipIn: ZipInputStream, filePath: String): Unit = {
    val bos = new BufferedOutputStream(new FileOutputStream(filePath))
    val buffer = new Array[Byte](1024)
    var len = zipIn.read(buffer)
    while (len > 0) {
      bos.write(buffer, 0, len)
      len = zipIn.read(buffer)
    }
    bos.close()
  }
}


