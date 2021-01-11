package com.tofukma.orderapp.Utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

public  final class Decryption {

    companion object{

        public fun decrypt(data: Any): Any{

            var key = "covanxinhgai";

            Security.addProvider(BouncyCastleProvider())
            var keyBytes: ByteArray

            try {
                keyBytes = key.toByteArray(charset("UTF8"))
                val skey = SecretKeySpec(keyBytes, "AES")
                val input = org.bouncycastle.util.encoders.Base64
                    .decode(data.toString()?.trim { it <= ' ' }?.toByteArray(charset("UTF8")))

                synchronized(Cipher::class.java) {
                    val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                    cipher.init(Cipher.DECRYPT_MODE, skey)

                    val plainText = ByteArray(cipher.getOutputSize(input.size))
                    var ptLength = cipher.update(input, 0, input.size, plainText, 0)
                    ptLength += cipher.doFinal(plainText, ptLength)
                    val decryptedString = String(plainText)
                    return decryptedString.trim { it <= ' ' }
                }
            } catch (uee: UnsupportedEncodingException) {
                uee.printStackTrace()
            } catch (ibse: IllegalBlockSizeException) {
                ibse.printStackTrace()
            } catch (bpe: BadPaddingException) {
                bpe.printStackTrace()
            } catch (ike: InvalidKeyException) {
                ike.printStackTrace()
            } catch (nspe: NoSuchPaddingException) {
                nspe.printStackTrace()
            } catch (nsae: NoSuchAlgorithmException) {
                nsae.printStackTrace()
            }
            return "";
        }
    }


}
