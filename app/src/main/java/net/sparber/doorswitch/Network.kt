package net.sparber.doorswitch

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.ByteString.Companion.encodeUtf8
import org.json.JSONObject
import java.io.IOException
import java.security.MessageDigest
import java.time.Instant
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


class Network {
    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    @RequiresApi(Build.VERSION_CODES.O)
    public fun switch(device: Device, state: Boolean) {
        val values = JSONObject()
        values.put("selfApikey", "123")
        values.put("deviceid", device.id)
        values.put("sequence", Instant.now().epochSecond.toString());

        val data = JSONObject();
        if (state)
            data.put("switch", "on")
        else
            data.put("switch", "off")
        values.put ("data", data)

        val url = (device.address + "/zeroconf/switch").toHttpUrl();
        val encrypted = encrypt(values, device.key)
        send (url, encrypted);
    }

    private fun send(url: HttpUrl, data: JSONObject) {
        val body = data.toString().toRequestBody(JSON)

        val request: Request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        // TODO check if there was an error and report it
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w("Network", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                Log.w("Network", response.toString() + response.body!!.string())
            }
        })
    }

    // The following code was inspired by https://github.com/AlexxIT/SonoffLAN/blob/7dc7f75676bb091ad69bf34bfcf7a77133f0dd5f/custom_components/sonoff/core/ewelink/local.py
    @OptIn(ExperimentalStdlibApi::class)
    fun pad(dataToPad: ByteArray, blockSize: Int): ByteArray {
        val dataToPadLen = dataToPad.count()
        val paddingLen = blockSize - dataToPadLen % blockSize
        val padded = dataToPad.copyOf(dataToPadLen + paddingLen);
        padded.fill(blockSize.toByte(), dataToPadLen)
        return padded
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun encrypt(payload: JSONObject, deviceKey: String): JSONObject {
        val md = MessageDigest.getInstance("MD5");
        // For some reason we need to have a space after ":"
        val plaintext = payload.get("data").toString().replace(":", ": ").encodeUtf8().toByteArray()

        val key = SecretKeySpec(md.digest(deviceKey.toByteArray()), "AES")
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val ciphertext: ByteArray = cipher.doFinal(pad(plaintext, cipher.blockSize))

        payload.put("encrypt", true)
        payload.put("data", Base64.encode(ciphertext))
        payload.put("iv", Base64.encode(cipher.iv))

        return payload
    }


    /* Since we don't read any information from the device we don't need the decryption part, therefore this is also untested
    fun unpad(paddedData: ByteArray, blockSize: Int): ByteArray {
        val paddingLen = paddedData[-1]
        return paddedData.copyOf(paddedData.count() - paddingLen)
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun decrypt(payload: JSONObject, deviceKey: String) : JSONObject  {
        val md = MessageDigest.getInstance("MD5");

        val iv = IvParameterSpec(Base64.decode(payload.get("iv") as ByteArray))

        val key = SecretKeySpec(md.digest(deviceKey.toByteArray()), "AES")
        val cipher = Cipher.getInstance("AES/CBC/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, iv)

        val ciphertext = Base64.decode(payload.get("data1") as ByteArray)

        return JSONObject(unpad(cipher.doFinal(ciphertext), cipher.blockSize).decodeToString())
    }
    */
}