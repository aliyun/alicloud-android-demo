package alibaba.httpdns_android_demo.best_practice

import android.net.SSLCertificateSocketFactory
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.*

/**
 * @author allen.wy
 * @date 2023/6/14
 */
class SNISocketFactory(private val conn: HttpsURLConnection) : SSLSocketFactory() {
    private val hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()

    override fun createSocket(
        plainSocket: Socket?,
        host: String?,
        port: Int,
        autoClose: Boolean
    ): Socket {
        var peerHost: String? = conn.getRequestProperty("Host")
        if (peerHost == null) {
            peerHost = host
        }
        val address = plainSocket?.inetAddress
        if (autoClose) {
            plainSocket?.close()
        }
        val sslSocketFactory: SSLCertificateSocketFactory =
            SSLCertificateSocketFactory.getDefault(0) as SSLCertificateSocketFactory
        val ssl: SSLSocket =
            sslSocketFactory.createSocket(address, port) as SSLSocket

        ssl.enabledProtocols = ssl.supportedProtocols

        // set up SNI before the handshake
        sslSocketFactory.setHostname(ssl, peerHost)
        // verify hostname and certificate
        val session: SSLSession = ssl.session

        if (!hostnameVerifier.verify(peerHost, session)
        ) throw SSLPeerUnverifiedException("Cannot verify hostname: $peerHost")

        return ssl
    }

    override fun createSocket(host: String?, port: Int): Socket? {
        return null
    }

    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket? {
        return null
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket? {
        return null
    }

    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket? {
        return null
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return arrayOf()
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return arrayOf()
    }

}
