package ai.tech.core.misc.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import java.io.InputStream

public object FirebaseAdmin {

    private val serviceAccount: InputStream? =
        this::class.java.classLoader.getResourceAsStream("ktor-firebase-auth-admin-sdk.json")

    private val options: FirebaseOptions = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
        .build()

    public fun init(): FirebaseApp = FirebaseApp.initializeApp(options)
}
