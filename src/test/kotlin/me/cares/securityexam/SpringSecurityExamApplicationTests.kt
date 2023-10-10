package me.cares.securityexam

import org.junit.jupiter.api.Test

//@SpringBootTest
class SpringSecurityExamApplicationTests {

    @Test
    fun contextLoads() {

        val map = mapOf<String, String>(
            "realm" to "realmName",
            "Test" to "Test",
            "test2" to "test2"
        )

        println(computeWWWAuthenticateHeaderValue(map));

    }


    private fun computeWWWAuthenticateHeaderValue(parameters: Map<String, String>): String? {
        val wwwAuthenticate = StringBuilder()
        wwwAuthenticate.append("Bearer")
        if (!parameters.isEmpty()) {
            wwwAuthenticate.append(" ")
            var i = 0
            for ((key, value) in parameters) {
                wwwAuthenticate.append(key).append("=\"").append(value).append("\"")
                if (i != parameters.size - 1) {
                    wwwAuthenticate.append(", ")
                }
                i++
            }
        }
        return wwwAuthenticate.toString()
    }

}
