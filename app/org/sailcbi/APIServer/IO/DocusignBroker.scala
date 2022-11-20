package org.sailcbi.APIServer.IO

import play.api.libs.ws.WSClient

import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.nio.charset.StandardCharsets
import java.security.{KeyFactory, Signature}
import java.security.spec.PKCS8EncodedKeySpec
import java.time.ZonedDateTime
import java.util.Base64

object DocusignBroker {
	private val integrationKey = "ffcd8445-d6b2-4ab1-a9be-f77703992c20"
	private val secretKey = "792dfc16-4ae2-4fa0-9f21-d85702301395"

	private val privateKeyPKCS1 =
		"""-----BEGIN RSA PRIVATE KEY-----
		  |MIIEowIBAAKCAQEAjZvVEQF9dj3Eh1XRKo7gjlp73DSwVkt89RxOfnjms4J6iQRP
		  |akzyc2xmdIUiOF5jj5iwnkbgsnOgZczrxw5gO393mzcVeVyezCpANOQcCy8F92nH
		  |lD+moYYnq0KsWoW5ppVZZcRZkbEM1ZySj6XRl0On/fqfvKaMC/jkll6RflzRriIZ
		  |d480K+Z5FBDv3UEerLOMRokhZeCuXxMcbtSlNk+/LktaihGes1dS+v/5alg+ftC7
		  |Gl8WmULvuoJllcAkW7ZFnJWQrREKArsGeUfKxuaZnQkfXxNiFW5uE7k3Q0tqXbmE
		  |2k0XE21Mv+s0k4p2ejRvo6XJVsfXwOe8o6oY1wIDAQABAoIBAClHqoJVFV5BHke3
		  |YAyQbuyAewfTwi29vrTzJfyfQN1GZXMIsx/RQ4IbMPi5kKKu/UXwZFgXYEwVAcZ7
		  |SKYTzkC55UN1eMiSk+X/UQ06C+TEtfAMQXY5YdlHMswtWPHgdqCVe+R7KwMaAhxJ
		  |Er5WP7VhLxkOFL+ZK00YJoahFbeoZrfBYVPcKBwq90h/dhN+TQKZcNns40XNkzhD
		  |HnyV7fpYh5lVGay1K/P44ktsVCB4zfrJQgQBhFDpFIQFqogd1rR2TzY3NpLqUqq4
		  |AgxcdV72V/kTj565cPjrJI/6mPLgT0j0An3LzuBXUw8o+cwVNAMXgCrKhEkB9H9G
		  |CdWX7RECgYEAz7kl/u//0NxDew1ciqVzXS27IWtZ8U7PYV2BL/IPBO1yGhqOwIJf
		  |GiahPCKt24DaGsjhdIb4wYMxASv3rl4XHqsY3Ya+jN4cZFrWve9aK1fidQ7WUs3t
		  |ho/aWWhm1p3i4DVx1NlmLW4LJQWNyE6bdto9Bqr8c4x7hpf12lyQEQcCgYEAroUW
		  |PvKNcEqTwdV9bXrMcU4bpYlY8IZUZqLCtbScX5evNTqxy2Q4NsYzDBZf25yT5tcM
		  |r0J2oQ7zCFEglkvsObz0wIZeBcURXm16ll/VArcXgbMntvSnxIk3Cce1H3t7qMeM
		  |XPkeilflM6CcFPKOo1JjFr7AP4F3Smxi8d+hVbECgYAihdb2w9R0VecxMEtND6pY
		  |thz/tOPB4yM8P1oVKdmtK/lmtTs8a2563ii14d2bOZMGGJS/1R7Kqo8ebrw/Uovt
		  |IOqFBrvslY2KcD54WqtVwMl2Qv0Dzg3H62iSq3NL//mQ4dEFWwxMSJm5kW6697WG
		  |z0aNMSW73oE5IVBO9ktLSQKBgQCXpIjp9XS05/hv6wPrx1Ix+f7H3gpsJSzdafvq
		  |S5+86F6T0AaBhvnZjmr4y9BLUUiqwp4BA6100TqYNXYtYpdGrEu3pom4Vb574IEu
		  |fOLoUxOX3YZa0Ued3OT4GJHnYzLWO15ZwxWofCtqqto00Xbjr0jukJ0YEXUINBnd
		  |tcXIYQKBgHtr6la8ZQLsM422aCn0TyU9HJHhklKdFTUkuzIXojjGhc5YlQQvkLrY
		  |KJjlLL7jkjQkrA2Z1yyuOQHuYqxItiWjerkzyNiDxKdB2b4GZKAt2RcKvGoWxlCO
		  |VOb4MZDTRCIaCYAkzullY1C+zs8ZRH0MKX/6G37TGOder1ewIuC/
		  |-----END RSA PRIVATE KEY-----
		  |""".stripMargin

	// openssl pkcs8 -topk8 -nocrypt -in privkey

	private val privateKeyPKCS8 =
		"""MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCNm9URAX12PcSH
		  |VdEqjuCOWnvcNLBWS3z1HE5+eOazgnqJBE9qTPJzbGZ0hSI4XmOPmLCeRuCyc6Bl
		  |zOvHDmA7f3ebNxV5XJ7MKkA05BwLLwX3aceUP6ahhierQqxahbmmlVllxFmRsQzV
		  |nJKPpdGXQ6f9+p+8powL+OSWXpF+XNGuIhl3jzQr5nkUEO/dQR6ss4xGiSFl4K5f
		  |Exxu1KU2T78uS1qKEZ6zV1L6//lqWD5+0LsaXxaZQu+6gmWVwCRbtkWclZCtEQoC
		  |uwZ5R8rG5pmdCR9fE2IVbm4TuTdDS2pduYTaTRcTbUy/6zSTinZ6NG+jpclWx9fA
		  |57yjqhjXAgMBAAECggEAKUeqglUVXkEeR7dgDJBu7IB7B9PCLb2+tPMl/J9A3UZl
		  |cwizH9FDghsw+LmQoq79RfBkWBdgTBUBxntIphPOQLnlQ3V4yJKT5f9RDToL5MS1
		  |8AxBdjlh2UcyzC1Y8eB2oJV75HsrAxoCHEkSvlY/tWEvGQ4Uv5krTRgmhqEVt6hm
		  |t8FhU9woHCr3SH92E35NAplw2ezjRc2TOEMefJXt+liHmVUZrLUr8/jiS2xUIHjN
		  |+slCBAGEUOkUhAWqiB3WtHZPNjc2kupSqrgCDFx1XvZX+ROPnrlw+Oskj/qY8uBP
		  |SPQCfcvO4FdTDyj5zBU0AxeAKsqESQH0f0YJ1ZftEQKBgQDPuSX+7//Q3EN7DVyK
		  |pXNdLbsha1nxTs9hXYEv8g8E7XIaGo7Agl8aJqE8Iq3bgNoayOF0hvjBgzEBK/eu
		  |Xhceqxjdhr6M3hxkWta971orV+J1DtZSze2Gj9pZaGbWneLgNXHU2WYtbgslBY3I
		  |Tpt22j0GqvxzjHuGl/XaXJARBwKBgQCuhRY+8o1wSpPB1X1tesxxThuliVjwhlRm
		  |osK1tJxfl681OrHLZDg2xjMMFl/bnJPm1wyvQnahDvMIUSCWS+w5vPTAhl4FxRFe
		  |bXqWX9UCtxeBsye29KfEiTcJx7Ufe3uox4xc+R6KV+UzoJwU8o6jUmMWvsA/gXdK
		  |bGLx36FVsQKBgCKF1vbD1HRV5zEwS00Pqli2HP+048HjIzw/WhUp2a0r+Wa1Ozxr
		  |bnreKLXh3Zs5kwYYlL/VHsqqjx5uvD9Si+0g6oUGu+yVjYpwPnhaq1XAyXZC/QPO
		  |DcfraJKrc0v/+ZDh0QVbDExImbmRbrr3tYbPRo0xJbvegTkhUE72S0tJAoGBAJek
		  |iOn1dLTn+G/rA+vHUjH5/sfeCmwlLN1p++pLn7zoXpPQBoGG+dmOavjL0EtRSKrC
		  |ngEDrXTROpg1di1il0asS7emibhVvnvggS584uhTE5fdhlrRR53c5PgYkedjMtY7
		  |XlnDFah8K2qq2jTRduOvSO6QnRgRdQg0Gd21xchhAoGAe2vqVrxlAuwzjbZoKfRP
		  |JT0ckeGSUp0VNSS7MheiOMaFzliVBC+QutgomOUsvuOSNCSsDZnXLK45Ae5irEi2
		  |JaN6uTPI2IPEp0HZvgZkoC3ZFwq8ahbGUI5U5vgxkNNEIhoJgCTO6WVjUL7OzxlE
		  |fQwpf/obftMY516vV7Ai4L8=""".stripMargin.replace("\n", "")

	private val publicKey =
		"""-----BEGIN PUBLIC KEY-----
		  |MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjZvVEQF9dj3Eh1XRKo7g
		  |jlp73DSwVkt89RxOfnjms4J6iQRPakzyc2xmdIUiOF5jj5iwnkbgsnOgZczrxw5g
		  |O393mzcVeVyezCpANOQcCy8F92nHlD+moYYnq0KsWoW5ppVZZcRZkbEM1ZySj6XR
		  |l0On/fqfvKaMC/jkll6RflzRriIZd480K+Z5FBDv3UEerLOMRokhZeCuXxMcbtSl
		  |Nk+/LktaihGes1dS+v/5alg+ftC7Gl8WmULvuoJllcAkW7ZFnJWQrREKArsGeUfK
		  |xuaZnQkfXxNiFW5uE7k3Q0tqXbmE2k0XE21Mv+s0k4p2ejRvo6XJVsfXwOe8o6oY
		  |1wIDAQAB
		  |-----END PUBLIC KEY-----
		  |""".stripMargin


	val header =
		"""{
		  |  "alg": "RS256",
		  |  "typ": "JWT"
		  |}""".stripMargin

	val getBody = (now: ZonedDateTime) =>
		s"""{
		  |  "iss": "$integrationKey",
		  |  "sub": "17ec3f3d-ec54-492f-81e1-b75978c90950",
		  |  "aud": "account-d.docusign.com",
		  |  "iat": ${now.toEpochSecond},
		  |  "exp": ${now.toEpochSecond + 300},
		  |  "scope": "signature"
		  |}""".stripMargin

	// click.manage click.send


	private def getPrivateKey = {
		// Base64 decode the result
		val pkcs8EncodedBytes = java.util.Base64.getDecoder.decode(privateKeyPKCS8)
		// extract the private key
		val keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes)
		val kf = KeyFactory.getInstance("RSA")
		kf.generatePrivate(keySpec)
	}

	def makeJwt() = {
		val token: StringBuffer = new StringBuffer()

		//Encode the JWT Header and add it to our string to sign
		token.append(Base64.getEncoder.encodeToString(header.getBytes(StandardCharsets.UTF_8)))

		token.append(".")

		val body = getBody(ZonedDateTime.now())
		println(body)

		token.append(Base64.getEncoder.encodeToString(body.getBytes(StandardCharsets.UTF_8)))

		val signature = Signature.getInstance("SHA256withRSA")
		signature.initSign(getPrivateKey)
		signature.update(token.toString.getBytes(StandardCharsets.UTF_8))

		token.append(".")

		token.append(Base64.getEncoder.encodeToString(signature.sign()))

		token.toString
	}

	def getToken(jwt: String) = {
		val body = s"grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=$jwt"
		val builder = HttpRequest.newBuilder
			.uri(new URI("https://account-d.docusign.com/oauth/token"))
//			.uri(new URI("http://localhost:3000"))
			.method("POST", HttpRequest.BodyPublishers.ofByteArray(body.getBytes(StandardCharsets.UTF_8)))

		val req = builder.build
		val res = HttpClient.newBuilder.build.send(req, HttpResponse.BodyHandlers.ofByteArray)
		new String(res.body(), StandardCharsets.UTF_8)
	}

	def main(args: Array[String]): Unit = {
		println(privateKeyPKCS8)
		val jwt = makeJwt()
		println(jwt)
		println(getToken(jwt))
	}
}
