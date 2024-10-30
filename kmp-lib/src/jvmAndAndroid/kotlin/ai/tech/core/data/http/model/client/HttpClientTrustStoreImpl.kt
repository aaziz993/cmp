package ai.tech.core.data.http.model.client

import java.io.InputStream
import java.security.KeyStore
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

public class HttpClientTrustStoreImpl(
    val keyStoreData: InputStream,
    val keyStorePassword: String
) : HttpClientTrustStore {


    public val keyStore: KeyStore = KeyStore.getInstance(KeyStore.getDefaultType()).also {
        it.load(keyStoreData, keyStorePassword.toCharArray())
    }


    public val trustManagerFactory: TrustManagerFactory? =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).also {
            it.init(keyStore)
        }


    public val sslContext: SSLContext? = SSLContext.getInstance("TLS").also {
        it.init(null, trustManagerFactory?.trustManagers, null)
    }

    public val trustManager: X509TrustManager =
        trustManagerFactory?.trustManagers?.first { it is X509TrustManager } as X509TrustManager

}