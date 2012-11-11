#!/bin/bash
# Generates certificates in PEM, P12 and JKS formats.
# Requires openssl and keytool. Tested on Ubuntu.
DIR=`pwd`/certs
mkdir -p $DIR
CONF=openssl.cnf
echo "Generating three certificates: CA, server and client."
openssl genrsa 2048 > $DIR/ca-key.pem
openssl req -new -x509 -nodes -days 3600 -key $DIR/ca-key.pem -out $DIR/ca-cert.pem -config $CONF
echo -n "Enter CA keystore password: "
stty -echo
read CAPASS
stty echo
echo ""
keytool -import -alias cacert -keystore $DIR/ca-cert.jks -file $DIR/ca-cert.pem -noprompt -storepass $CAPASS
echo "-"
echo "Generated CA certificate. Generating server certificate."
echo "-"
openssl req -newkey rsa:2048 -days 3600 -nodes -keyout $DIR/server-key.pem -out $DIR/server-req.pem -config $CONF
openssl rsa -in server-key.pem -out server-key.pem
openssl x509 -req -in $DIR/server-req.pem -days 3600 -CA $DIR/ca-cert.pem -CAkey $DIR/ca-key.pem -set_serial 01 -out $DIR/server-cert.pem
echo -n "Enter server keystore password: "
stty -echo
read SERVERPASS
stty echo
echo ""
openssl pkcs12 -export -in $DIR/server-cert.pem -inkey $DIR/server-key.pem -out $DIR/server.p12 -passout pass:$SERVERPASS
keytool -importkeystore -srckeystore $DIR/server.p12 -srcstoretype PKCS12 -srcstorepass $SERVERPASS -deststoretype JKS -destkeystore $DIR/server.jks -deststorepass $SERVERPASS
echo "-"
echo "Server certificate generated. Creating client certificate."
echo "-"
openssl req -newkey rsa:2048 -days 3600 -nodes -keyout $DIR/client-key.pem -out $DIR/client-req.pem -config $CONF
openssl rsa -in $DIR/client-key.pem -out $DIR/client-key.pem
openssl x509 -req -in $DIR/client-req.pem -days 3600 -CA $DIR/ca-cert.pem -CAkey $DIR/ca-key.pem -set_serial 02 -out $DIR/client-cert.pem
echo -n "Enter client keystore password: "
stty -echo
read CLIENTPASS
stty echo
echo ""
openssl pkcs12 -export -in $DIR/client-cert.pem -inkey $DIR/client-key.pem -out $DIR/client.p12 -passout pass:$CLIENTPASS
keytool -importkeystore -srckeystore $DIR/client.p12 -srcstoretype PKCS12 -srcstorepass $CLIENTPASS -deststoretype JKS -destkeystore $DIR/client.jks -deststorepass $CLIENTPASS
echo "-"
echo "Client certificate generated."
echo "-"
openssl verify -CAfile $DIR/ca-cert.pem $DIR/server-cert.pem $DIR/client-cert.pem
chmod -R 600 $DIR
