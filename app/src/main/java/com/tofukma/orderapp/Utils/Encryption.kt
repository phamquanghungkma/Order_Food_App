package com.tofukma.orderapp.Utils

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.util.encoders.Base64
import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.Security
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

public final class Encryption {
    companion object{
        public  fun encrypt(data: Any) : Any {

            var secret_key = "covanxinhgai";

            Security.addProvider(BouncyCastleProvider())
            var keyBytes: ByteArray

            try {
                keyBytes = secret_key.toByteArray(charset("UTF8"))
                val skey = SecretKeySpec(keyBytes, "AES")
                val input = data.toString().toByteArray(charset("UTF8"))

                synchronized(Cipher::class.java) {
                    val cipher = Cipher.getInstance("AES/ECB/PKCS7Padding")
                    cipher.init(Cipher.ENCRYPT_MODE, skey)

                    val cipherText = ByteArray(cipher.getOutputSize(input.size))
                    var ctLength = cipher.update(
                        input, 0, input.size,
                        cipherText, 0
                    )
                    ctLength += cipher.doFinal(cipherText, ctLength)
                    return String(
                        Base64.encode(cipherText)
                    )
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
            } catch (e: ShortBufferException) {
                e.printStackTrace()
            }

            return ""
        }
    }

}