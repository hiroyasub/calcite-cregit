begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|jdbc
operator|.
name|JdbcMeta
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|remote
operator|.
name|Driver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|remote
operator|.
name|LocalService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|server
operator|.
name|HttpServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x500
operator|.
name|X500Name
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x500
operator|.
name|style
operator|.
name|IETFUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x500
operator|.
name|style
operator|.
name|RFC4519Style
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|BasicConstraints
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|Extension
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|asn1
operator|.
name|x509
operator|.
name|KeyUsage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|cert
operator|.
name|CertIOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|cert
operator|.
name|X509CertificateHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|cert
operator|.
name|jcajce
operator|.
name|JcaX509ExtensionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|cert
operator|.
name|jcajce
operator|.
name|JcaX509v3CertificateBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|jce
operator|.
name|provider
operator|.
name|BouncyCastleProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|jce
operator|.
name|provider
operator|.
name|X509CertificateObject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|OperatorCreationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|operator
operator|.
name|jcajce
operator|.
name|JcaContentSignerBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPair
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyPairGenerator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyStore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchProviderException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PublicKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Security
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|Certificate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|CertificateException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test case for Avatica with TLS connectors.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SslDriverTest
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SslDriverTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|File
name|keystore
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|KEYSTORE_PASSWORD
init|=
literal|"avaticasecret"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ConnectionSpec
name|CONNECTION_SPEC
init|=
name|ConnectionSpec
operator|.
name|HSQLDB
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|HttpServer
argument_list|>
name|SERVERS_TO_STOP
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Parameters
specifier|public
specifier|static
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|ArrayList
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Create a self-signed cert
name|File
name|target
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|,
literal|"target"
argument_list|)
decl_stmt|;
name|keystore
operator|=
operator|new
name|File
argument_list|(
name|target
argument_list|,
literal|"avatica-test.jks"
argument_list|)
expr_stmt|;
if|if
condition|(
name|keystore
operator|.
name|isFile
argument_list|()
condition|)
block|{
name|assertTrue
argument_list|(
literal|"Failed to delete keystore: "
operator|+
name|keystore
argument_list|,
name|keystore
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
block|}
operator|new
name|CertTool
argument_list|()
operator|.
name|createSelfSignedCert
argument_list|(
name|keystore
argument_list|,
literal|"avatica"
argument_list|,
name|KEYSTORE_PASSWORD
argument_list|)
expr_stmt|;
comment|// Create a LocalService around HSQLDB
specifier|final
name|JdbcMeta
name|jdbcMeta
init|=
operator|new
name|JdbcMeta
argument_list|(
name|CONNECTION_SPEC
operator|.
name|url
argument_list|,
name|CONNECTION_SPEC
operator|.
name|username
argument_list|,
name|CONNECTION_SPEC
operator|.
name|password
argument_list|)
decl_stmt|;
specifier|final
name|LocalService
name|localService
init|=
operator|new
name|LocalService
argument_list|(
name|jdbcMeta
argument_list|)
decl_stmt|;
for|for
control|(
name|Driver
operator|.
name|Serialization
name|serialization
range|:
operator|new
name|Driver
operator|.
name|Serialization
index|[]
block|{
name|Driver
operator|.
name|Serialization
operator|.
name|JSON
block|,
name|Driver
operator|.
name|Serialization
operator|.
name|PROTOBUF
block|}
control|)
block|{
comment|// Build and start the server, using TLS
name|HttpServer
name|httpServer
init|=
operator|new
name|HttpServer
operator|.
name|Builder
argument_list|()
operator|.
name|withPort
argument_list|(
literal|0
argument_list|)
operator|.
name|withTLS
argument_list|(
name|keystore
argument_list|,
name|KEYSTORE_PASSWORD
argument_list|,
name|keystore
argument_list|,
name|KEYSTORE_PASSWORD
argument_list|)
operator|.
name|withHandler
argument_list|(
name|localService
argument_list|,
name|serialization
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|SERVERS_TO_STOP
operator|.
name|add
argument_list|(
name|httpServer
argument_list|)
expr_stmt|;
specifier|final
name|String
name|url
init|=
literal|"jdbc:avatica:remote:url=https://localhost:"
operator|+
name|httpServer
operator|.
name|getPort
argument_list|()
operator|+
literal|";serialization="
operator|+
name|serialization
operator|+
literal|";truststore="
operator|+
name|keystore
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|";truststore_password="
operator|+
name|KEYSTORE_PASSWORD
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JDBC URL {}"
argument_list|,
name|url
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|url
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|parameters
return|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|stopKdc
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|HttpServer
name|server
range|:
name|SERVERS_TO_STOP
control|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
name|String
name|jdbcUrl
decl_stmt|;
specifier|public
name|SslDriverTest
parameter_list|(
name|String
name|jdbcUrl
parameter_list|)
block|{
name|this
operator|.
name|jdbcUrl
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|jdbcUrl
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReadWrite
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|tableName
init|=
literal|"testReadWrite"
decl_stmt|;
try|try
init|(
name|Connection
name|conn
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|jdbcUrl
argument_list|)
init|;
name|Statement
name|stmt
init|=
name|conn
operator|.
name|createStatement
argument_list|()
init|)
block|{
name|assertFalse
argument_list|(
name|stmt
operator|.
name|execute
argument_list|(
literal|"DROP TABLE IF EXISTS "
operator|+
name|tableName
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|stmt
operator|.
name|execute
argument_list|(
literal|"CREATE TABLE "
operator|+
name|tableName
operator|+
literal|"(pk integer)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|tableName
operator|+
literal|" VALUES(1)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|tableName
operator|+
literal|" VALUES(2)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|stmt
operator|.
name|executeUpdate
argument_list|(
literal|"INSERT INTO "
operator|+
name|tableName
operator|+
literal|" VALUES(3)"
argument_list|)
argument_list|)
expr_stmt|;
name|ResultSet
name|results
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
literal|"SELECT count(1) FROM "
operator|+
name|tableName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|results
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Utility class for creating certificates for testing.    */
specifier|private
specifier|static
class|class
name|CertTool
block|{
specifier|private
specifier|static
specifier|final
name|String
name|SIGNING_ALGORITHM
init|=
literal|"SHA256WITHRSA"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ENC_ALGORITHM
init|=
literal|"RSA"
decl_stmt|;
static|static
block|{
name|Security
operator|.
name|addProvider
argument_list|(
operator|new
name|BouncyCastleProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createSelfSignedCert
parameter_list|(
name|File
name|targetKeystore
parameter_list|,
name|String
name|keyName
parameter_list|,
name|String
name|keystorePassword
parameter_list|)
block|{
if|if
condition|(
name|targetKeystore
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Keystore already exists: "
operator|+
name|targetKeystore
argument_list|)
throw|;
block|}
try|try
block|{
name|KeyPair
name|kp
init|=
name|generateKeyPair
argument_list|()
decl_stmt|;
name|X509CertificateObject
name|cert
init|=
name|generateCert
argument_list|(
name|keyName
argument_list|,
name|kp
argument_list|,
literal|true
argument_list|,
name|kp
operator|.
name|getPublic
argument_list|()
argument_list|,
name|kp
operator|.
name|getPrivate
argument_list|()
argument_list|)
decl_stmt|;
name|char
index|[]
name|password
init|=
name|keystorePassword
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|KeyStore
name|keystore
init|=
name|KeyStore
operator|.
name|getInstance
argument_list|(
literal|"JKS"
argument_list|)
decl_stmt|;
name|keystore
operator|.
name|load
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|keystore
operator|.
name|setCertificateEntry
argument_list|(
name|keyName
operator|+
literal|"Cert"
argument_list|,
name|cert
argument_list|)
expr_stmt|;
name|keystore
operator|.
name|setKeyEntry
argument_list|(
name|keyName
operator|+
literal|"Key"
argument_list|,
name|kp
operator|.
name|getPrivate
argument_list|()
argument_list|,
name|password
argument_list|,
operator|new
name|Certificate
index|[]
block|{
name|cert
block|}
argument_list|)
expr_stmt|;
try|try
init|(
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|targetKeystore
argument_list|)
init|)
block|{
name|keystore
operator|.
name|store
argument_list|(
name|fos
argument_list|,
name|password
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|KeyPair
name|generateKeyPair
parameter_list|()
throws|throws
name|NoSuchAlgorithmException
throws|,
name|NoSuchProviderException
block|{
name|KeyPairGenerator
name|gen
init|=
name|KeyPairGenerator
operator|.
name|getInstance
argument_list|(
name|ENC_ALGORITHM
argument_list|)
decl_stmt|;
name|gen
operator|.
name|initialize
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
return|return
name|gen
operator|.
name|generateKeyPair
argument_list|()
return|;
block|}
specifier|private
name|X509CertificateObject
name|generateCert
parameter_list|(
name|String
name|keyName
parameter_list|,
name|KeyPair
name|kp
parameter_list|,
name|boolean
name|isCertAuthority
parameter_list|,
name|PublicKey
name|signerPublicKey
parameter_list|,
name|PrivateKey
name|signerPrivateKey
parameter_list|)
throws|throws
name|IOException
throws|,
name|CertIOException
throws|,
name|OperatorCreationException
throws|,
name|CertificateException
throws|,
name|NoSuchAlgorithmException
block|{
name|Calendar
name|startDate
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Calendar
name|endDate
init|=
name|Calendar
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|endDate
operator|.
name|add
argument_list|(
name|Calendar
operator|.
name|YEAR
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|BigInteger
name|serialNumber
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|startDate
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
decl_stmt|;
name|X500Name
name|issuer
init|=
operator|new
name|X500Name
argument_list|(
name|IETFUtils
operator|.
name|rDNsFromString
argument_list|(
literal|"cn=localhost"
argument_list|,
name|RFC4519Style
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
name|JcaX509v3CertificateBuilder
name|certGen
init|=
operator|new
name|JcaX509v3CertificateBuilder
argument_list|(
name|issuer
argument_list|,
name|serialNumber
argument_list|,
name|startDate
operator|.
name|getTime
argument_list|()
argument_list|,
name|endDate
operator|.
name|getTime
argument_list|()
argument_list|,
name|issuer
argument_list|,
name|kp
operator|.
name|getPublic
argument_list|()
argument_list|)
decl_stmt|;
name|JcaX509ExtensionUtils
name|extensionUtils
init|=
operator|new
name|JcaX509ExtensionUtils
argument_list|()
decl_stmt|;
name|certGen
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|subjectKeyIdentifier
argument_list|,
literal|false
argument_list|,
name|extensionUtils
operator|.
name|createSubjectKeyIdentifier
argument_list|(
name|kp
operator|.
name|getPublic
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|certGen
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|basicConstraints
argument_list|,
literal|false
argument_list|,
operator|new
name|BasicConstraints
argument_list|(
name|isCertAuthority
argument_list|)
argument_list|)
expr_stmt|;
name|certGen
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|authorityKeyIdentifier
argument_list|,
literal|false
argument_list|,
name|extensionUtils
operator|.
name|createAuthorityKeyIdentifier
argument_list|(
name|signerPublicKey
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCertAuthority
condition|)
block|{
name|certGen
operator|.
name|addExtension
argument_list|(
name|Extension
operator|.
name|keyUsage
argument_list|,
literal|true
argument_list|,
operator|new
name|KeyUsage
argument_list|(
name|KeyUsage
operator|.
name|keyCertSign
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|X509CertificateHolder
name|cert
init|=
name|certGen
operator|.
name|build
argument_list|(
operator|new
name|JcaContentSignerBuilder
argument_list|(
name|SIGNING_ALGORITHM
argument_list|)
operator|.
name|build
argument_list|(
name|signerPrivateKey
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|X509CertificateObject
argument_list|(
name|cert
operator|.
name|toASN1Structure
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SslDriverTest.java
end_comment

end_unit

