# Chaves do simulador M-Pesa

**Chave só para o simulador. Nunca usar fora do desenvolvimento.**

Par RSA 2048 gerado no S5-01 (o S5-03 usa este par; ficha S5-03 §2):

```bash
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out priv.pem      # PKCS#8 PEM
openssl pkey -in priv.pem -pubout -out pub.pem                                   # pública em PEM
openssl pkey -pubin -in pub.pem -outform DER | base64 -w0 > mock-public.b64      # Base64 DER numa linha
```

- `mock-private.pem` — chave privada (PKCS#8 PEM); o simulador decifra o token com ela.
- `mock-public.pem` — chave pública em PEM; cópia exacta em `backend/src/main/resources/dev/mpesa-mock-public.pem`
  (segredo `MPESA/publicKey` de desenvolvimento, `DevPaymentSecretsSeeder`).
- `mock-public.b64` — a mesma chave pública em Base64 DER numa linha (o formato em que a Vodacom entrega a chave).

Os ficheiros `.pem` começam com uma linha de texto com o aviso acima (texto explicativo antes do bloco PEM,
permitido pela RFC 7468); quem os lê usa só o bloco `-----BEGIN … -----END …-----`.
