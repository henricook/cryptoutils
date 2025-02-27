package com.henricook.tls.internal

import java.io._
import java.net.{URI, URL}
import java.nio.ByteBuffer
import java.nio.file.Path

import org.apache.commons.io.IOUtils

trait ObjectLoader[+T] {
  def fromInputStream(inputStream: InputStream): T

  def fromResource(resource: String): T = {
    val stream = getClass.getClassLoader.getResourceAsStream(resource)
    try {
      fromInputStream(stream)
    } finally {
      IOUtils.closeQuietly(stream)
    }
  }

  def fromFile(file: File): T = concurrent.blocking {
    val inputStream = new FileInputStream(file)
    try {
      fromInputStream(inputStream)
    } finally {
      IOUtils.closeQuietly(inputStream)
    }
  }

  final def fromFile(file: Path): T = fromFile(file.toFile)

  final def fromFile(file: String): T = fromFile(new File(file))

  def fromURL(url: URL): T = concurrent.blocking {
    val inputStream = url.openStream()
    try {
      fromInputStream(inputStream)
    } finally {
      IOUtils.closeQuietly(inputStream)
    }
  }

  final def fromURL(url: String): T = fromURL(new URL(url))

  final def fromURI(uri: URI): T = fromURL(uri.toURL)

  def fromBytes(bytes: Array[Byte]): T = {
    val inputStream = new ByteArrayInputStream(bytes)
    try {
      fromInputStream(inputStream)
    } finally {
      IOUtils.closeQuietly(inputStream)
    }
  }

  final def fromByteBuffer(byteBuffer: ByteBuffer): T = {
    fromBytes(byteBuffer.array())
  }

  final def fromString(str: String, encoding: String): T = {
    fromBytes(str.getBytes(encoding))
  }

  final def fromString(str: String): T = {
    fromBytes(str.getBytes)
  }
}
