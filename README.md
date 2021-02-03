# Tinybox

A tiny Dropbox clone using Quarkus, Kafka, Kubernetes, and GraalVM.

## Dev Requirements

- JDK 11
- Minikube
- PostgreSQL (dev setup provided)

## Keypair generation for auth

Generate a keypair using these commands:

```shell
openssl genrsa -out rsaPrivateKey.pem 4096
openssl rsa -pubout -in rsaPrivateKey.pem -out publicKey.pem
```

The private key then needs to be converted to the `PKCS#8` format:

```shell
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
```

Then copy the files `privateKey.pem` and `publicKey.pem` into the directory
`tinybox-auth/src/main/resources/META-INF/resources`.

Also copy `publicKey.pem` into the `src/main/resources/META-INF/resources` directories
of the other services. Make sure not to copy the private key to those services!

__Never add the `*.pem` files to the Git repository!!!__
