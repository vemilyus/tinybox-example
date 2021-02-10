package tinybox.common.clients

import org.eclipse.microprofile.config.inject.ConfigProperty
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.specimpl.MultivaluedMapImpl
import org.jose4j.jwt.JwtClaims
import java.time.Instant
import javax.enterprise.context.ApplicationScoped
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MultivaluedMap

@ApplicationScoped
class ApiAuthHeadersFactory(
    @RestClient
    private val loginClient: LoginClient,
    @ConfigProperty(name = "tinybox.auth.api-credentials.client-id")
    private val authApiClientId: String,
    @ConfigProperty(name = "tinybox.auth.api-credentials.client-secret")
    private val authApiClientSecret: String
) : ClientHeadersFactory {
    private var currentBearer: String? = null
    private var currentBearerExpiration: Instant? = null

    override fun update(
        incomingHeaders: MultivaluedMap<String, String>,
        clientOutgoingHeaders: MultivaluedMap<String, String>
    ): MultivaluedMap<String, String> {
        val result = MultivaluedMapImpl<String, String>()
        result.add(HttpHeaders.AUTHORIZATION, "Bearer ${getCurrentBearer()}")

        return result
    }

    @Synchronized
    private fun getCurrentBearer(): String {
        return if (
            currentBearer == null ||
            currentBearerExpiration?.let { Instant.now().plusSeconds(60).isAfter(it) } == true
        ) {
            val loginResult = loginClient.login(
                LoginClient.LoginRequest(
                    authApiClientId,
                    authApiClientSecret
                )
            )

            val jwt = JwtClaims.parse(loginResult.bearer)

            currentBearer = loginResult.bearer
            currentBearerExpiration = Instant.ofEpochMilli(jwt.expirationTime.valueInMillis)

            currentBearer!!
        } else
            currentBearer!!
    }
}
