package alibaba.httpdns_android_demo.best_practice

import android.net.SSLCertificateSocketFactory
import android.os.Build
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.*

/**
 * @author allen.wy
 * @date 2023/5/26
 */
class TLSSNISocketFactory(connection: HttpsURLConnection) : SSLSocketFactory() {

    private var mConnection: HttpsURLConnection
    private var hostnameVerifier: HostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()

    init {
        mConnection = connection
    }

    override fun createSocket(
        plainSocket: Socket?,
        host: String?,
        port: Int,
        autoClose: Boolean
    ): Socket? {
        var peerHost: String? = mConnection.getRequestProperty("Host")
        if (peerHost == null) peerHost = host
        val address = plainSocket!!.inetAddress
        if (autoClose) {
            // we don't need the plainSocket
            plainSocket.close()
        }

        // create and connect SSL socket, but don't do hostname/certificate verification yet
        // create and connect SSL socket, but don't do hostname/certificate verification yet
        val sslSocketFactory =
            SSLCertificateSocketFactory.getDefault(0) as SSLCertificateSocketFactory
        val ssl = sslSocketFactory.createSocket(address, port) as SSLSocket

        // enable TLSv1.1/1.2 if available

        // enable TLSv1.1/1.2 if available
        ssl.enabledProtocols = ssl.supportedProtocols
        // set up SNI before the handshake

        // set up SNI before the handshake
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            sslSocketFactory.setHostname(ssl, peerHost)
        }

        // verify hostname and certificate

        // verify hostname and certificate
        val session = ssl.session

        if (!hostnameVerifier.verify(peerHost, session)) throw SSLPeerUnverifiedException(
            "Cannot verify hostname: $peerHost"
        )

        return ssl
    }

    override fun createSocket(host: String?, port: Int): Socket? {
        return null
    }

    override fun createSocket(
        host: String?,
        port: Int,
        inetAddress: InetAddress?,
        localPort: Int
    ): Socket? {
        return null
    }

    override fun createSocket(host: InetAddress?, port: Int): Socket? {
        return null
    }

    override fun createSocket(
        host: InetAddress?,
        port: Int,
        localHost: InetAddress?,
        localPot: Int
    ): Socket? {
        return null
    }

    override fun getDefaultCipherSuites(): Array<String?> {
        return arrayOfNulls(0)
    }

    override fun getSupportedCipherSuites(): Array<String?> {
        return arrayOfNulls(0)
    }
}