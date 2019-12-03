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
name|Table
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
name|impl
operator|.
name|AbstractSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|MongoClient
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
name|com
operator|.
name|mongodb
operator|.
name|ServerAddress
import|;
end_import

begin_import
import|import
name|com
operator|.
name|mongodb
operator|.
name|client
operator|.
name|MongoDatabase
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Schema mapped onto a directory of MONGO files. Each table in the schema  * is a MONGO file in that directory.  */
end_comment

begin_class
specifier|public
class|class
name|MongoSchema
extends|extends
name|AbstractSchema
block|{
specifier|final
name|MongoDatabase
name|mongoDb
decl_stmt|;
comment|/**    * Creates a MongoDB schema.    *    * @param host Mongo host, e.g. "localhost"    * @param credential Optional credentials (null for none)    * @param options Mongo connection options    * @param database Mongo database name, e.g. "foodmart"    */
name|MongoSchema
parameter_list|(
name|String
name|host
parameter_list|,
name|String
name|database
parameter_list|,
name|MongoCredential
name|credential
parameter_list|,
name|MongoClientOptions
name|options
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|MongoClient
name|mongo
init|=
name|credential
operator|==
literal|null
condition|?
operator|new
name|MongoClient
argument_list|(
operator|new
name|ServerAddress
argument_list|(
name|host
argument_list|)
argument_list|,
name|options
argument_list|)
else|:
operator|new
name|MongoClient
argument_list|(
operator|new
name|ServerAddress
argument_list|(
name|host
argument_list|)
argument_list|,
name|credential
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|this
operator|.
name|mongoDb
operator|=
name|mongo
operator|.
name|getDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
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
comment|/**    * Allows tests to inject their instance of the database.    *    * @param mongoDb existing mongo database instance    */
annotation|@
name|VisibleForTesting
name|MongoSchema
parameter_list|(
name|MongoDatabase
name|mongoDb
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|mongoDb
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|mongoDb
argument_list|,
literal|"mongoDb"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|collectionName
range|:
name|mongoDb
operator|.
name|listCollectionNames
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|collectionName
argument_list|,
operator|new
name|MongoTable
argument_list|(
name|collectionName
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

