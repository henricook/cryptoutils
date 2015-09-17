package com.karasiq.tls.internal

import java.io.ByteArrayInputStream
import java.security.cert.CertificateFactory
import java.security.spec.{PKCS8EncodedKeySpec, X509EncodedKeySpec}
import java.security.{KeyFactory, PrivateKey, PublicKey}

import com.karasiq.tls.TLS
import org.apache.commons.io.IOUtils
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.crypto.AsymmetricCipherKeyPair
import org.bouncycastle.crypto.params.{AsymmetricKeyParameter, DSAKeyParameters, ECKeyParameters, RSAKeyParameters}
import org.bouncycastle.crypto.util.{PrivateKeyFactory, PrivateKeyInfoFactory, PublicKeyFactory, SubjectPublicKeyInfoFactory}
import org.bouncycastle.jce.provider.BouncyCastleProvider

import scala.util.control.Exception

private[tls] object BCConversions {
  private val provider = new BouncyCastleProvider

  implicit class JavaKeyOps(key: java.security.Key) {
    private def convertPKCS8Key(data: Array[Byte], public: SubjectPublicKeyInfo): AsymmetricCipherKeyPair = {
      new AsymmetricCipherKeyPair(PublicKeyFactory.createKey(public), PrivateKeyFactory.createKey(data))
    }

//    private def convertRsaKey(rsa: RSAPrivateCrtKey): AsymmetricCipherKeyPair = {
//      val publicParameters = new RSAKeyParameters(false, rsa.getModulus, rsa.getPublicExponent)
//      val privateParameters = new RSAPrivateCrtKeyParameters(rsa.getModulus, rsa.getPublicExponent,
//        rsa.getPrivateExponent, rsa.getPrimeP, rsa.getPrimeQ, rsa.getPrimeExponentP, rsa.getPrimeExponentQ, rsa.getCrtCoefficient)
//      new AsymmetricCipherKeyPair(publicParameters, privateParameters)
//    }

    def toAsymmetricCipherKeyPair(public: SubjectPublicKeyInfo): AsymmetricCipherKeyPair = key match {
      // case rsa: java.security.interfaces.RSAPrivateCrtKey ⇒
      //  convertRsaKey(rsa)

      case privateKey: java.security.PrivateKey ⇒
        convertPKCS8Key(privateKey.getEncoded, public)

      case _ ⇒
        throw new IllegalArgumentException("Not supported")
    }

    def toSubjectPublicKeyInfo: SubjectPublicKeyInfo = {
      SubjectPublicKeyInfo.getInstance(key.getEncoded)
    }
  }

  implicit class JavaKeyPairOps(keyPair: java.security.KeyPair) {
    def toAsymmetricCipherKeyPair: AsymmetricCipherKeyPair = {
      keyPair.getPrivate.toAsymmetricCipherKeyPair(keyPair.getPublic.toSubjectPublicKeyInfo)
    }
  }

  implicit class AsymmetricCipherKeyPairOps(keyPair: AsymmetricCipherKeyPair) {
    def toKeyPair: java.security.KeyPair = {
      new java.security.KeyPair(keyPair.getPublic.toPublicKey, keyPair.getPrivate.toPrivateKey)
    }
  }

  implicit class AsymmetricKeyParameterOps(key: AsymmetricKeyParameter) {
    def algorithm(): String = {
      key match {
        case _: ECKeyParameters ⇒
          "ECDSA"

        case _: RSAKeyParameters ⇒
          "RSA"

        case _: DSAKeyParameters ⇒
          "DSA"

        case _ ⇒
          throw new IllegalArgumentException("Unknown key algorithm: " + key)
      }
    }

    def toPrivateKey: PrivateKey = {
      val keyGenerator = KeyFactory.getInstance(this.algorithm(), provider)
      keyGenerator.generatePrivate(new PKCS8EncodedKeySpec(PrivateKeyInfoFactory.createPrivateKeyInfo(key).getEncoded))
    }

    def toPublicKey: PublicKey = {
      val keyGenerator = KeyFactory.getInstance(this.algorithm(), provider)
      keyGenerator.generatePublic(new X509EncodedKeySpec(SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(key).getEncoded))
    }
  }

  implicit class JavaCertificateOps(certificate: java.security.cert.Certificate) {
    def toTlsCertificate: TLS.Certificate = {
      org.bouncycastle.asn1.x509.Certificate.getInstance(certificate.getEncoded)
    }

    def toTlsCertificateChain: TLS.CertificateChain = {
      toTlsCertificate.toTlsCertificateChain
    }
  }

  implicit class CertificateOps(certificate: TLS.Certificate) {
    def toTlsCertificateChain: TLS.CertificateChain = {
      new TLS.CertificateChain(Array(certificate))
    }

    def toJavaCertificate: java.security.cert.Certificate = {
      val certificateFactory = CertificateFactory.getInstance("X.509")
      val inputStream = new ByteArrayInputStream(certificate.getEncoded)
      Exception.allCatch.andFinally(IOUtils.closeQuietly(inputStream)) {
        certificateFactory.generateCertificate(inputStream)
      }
    }
  }

  implicit class CertificateChainOps(chain: TLS.CertificateChain) {
    def toTlsCertificate: TLS.Certificate = {
      chain.getCertificateList.headOption
        .getOrElse(throw new NoSuchElementException("Empty certificate chain"))
    }

    def toJavaCertificateChain: Array[java.security.cert.Certificate] = {
      chain.getCertificateList.map(_.toJavaCertificate)
    }
  }
}
