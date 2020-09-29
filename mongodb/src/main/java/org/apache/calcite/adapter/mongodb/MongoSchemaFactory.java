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
name|adapter
operator|.
name|mongodb
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
name|schema
operator|.
name|Schema
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
name|schema
operator|.
name|SchemaFactory
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
name|schema
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|AuthenticationMechanism
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClientOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoCredential
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory that creates a {@link MongoSchema}.  *  *<p>Allows a custom schema to be included in a model.json file.</p>  */
end_comment

begin_class
specifier|public
class|class
name|MongoSchemaFactory
implements|implements
name|SchemaFactory
block|{
comment|// public constructor, per factory contract
specifier|public
name|MongoSchemaFactory
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
specifier|final
name|String
name|host
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"host"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|database
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"database"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|authMechanismName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"authMechanism"
argument_list|)
decl_stmt|;
specifier|final
name|MongoClientOptions
operator|.
name|Builder
name|options
init|=
name|MongoClientOptions
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|MongoCredential
name|credential
decl_stmt|;
if|if
condition|(
name|authMechanismName
operator|!=
literal|null
condition|)
block|{
name|credential
operator|=
name|createCredential
argument_list|(
name|operand
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|credential
operator|=
literal|null
expr_stmt|;
block|}
return|return
operator|new
name|MongoSchema
argument_list|(
name|host
argument_list|,
name|database
argument_list|,
name|credential
argument_list|,
name|options
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|MongoCredential
name|createCredential
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
block|{
specifier|final
name|String
name|authMechanismName
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"authMechanism"
argument_list|)
decl_stmt|;
specifier|final
name|AuthenticationMechanism
name|authenticationMechanism
init|=
name|AuthenticationMechanism
operator|.
name|fromMechanismName
argument_list|(
name|authMechanismName
argument_list|)
decl_stmt|;
specifier|final
name|String
name|username
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"username"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|authDatabase
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"authDatabase"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|password
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"password"
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|authenticationMechanism
condition|)
block|{
case|case
name|PLAIN
case|:
return|return
name|MongoCredential
operator|.
name|createPlainCredential
argument_list|(
name|username
argument_list|,
name|authDatabase
argument_list|,
name|password
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
case|case
name|SCRAM_SHA_1
case|:
return|return
name|MongoCredential
operator|.
name|createScramSha1Credential
argument_list|(
name|username
argument_list|,
name|authDatabase
argument_list|,
name|password
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
case|case
name|SCRAM_SHA_256
case|:
return|return
name|MongoCredential
operator|.
name|createScramSha256Credential
argument_list|(
name|username
argument_list|,
name|authDatabase
argument_list|,
name|password
operator|.
name|toCharArray
argument_list|()
argument_list|)
return|;
case|case
name|GSSAPI
case|:
return|return
name|MongoCredential
operator|.
name|createGSSAPICredential
argument_list|(
name|username
argument_list|)
return|;
case|case
name|MONGODB_X509
case|:
return|return
name|MongoCredential
operator|.
name|createMongoX509Credential
argument_list|(
name|username
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported authentication mechanism "
operator|+
name|authMechanismName
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

