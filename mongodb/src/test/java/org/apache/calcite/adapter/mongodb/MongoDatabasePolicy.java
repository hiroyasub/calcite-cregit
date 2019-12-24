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
name|test
operator|.
name|MongoAssertions
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
name|util
operator|.
name|Closer
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
name|client
operator|.
name|MongoDatabase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|extension
operator|.
name|AfterAllCallback
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|extension
operator|.
name|ExtensionContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
import|import
name|de
operator|.
name|bwaldvogel
operator|.
name|mongo
operator|.
name|MongoServer
import|;
end_import

begin_import
import|import
name|de
operator|.
name|bwaldvogel
operator|.
name|mongo
operator|.
name|backend
operator|.
name|memory
operator|.
name|MemoryBackend
import|;
end_import

begin_comment
comment|/**  * Instantiates a new connection to a embedded (but fake) or real mongo database  * depending on current profile (unit or integration tests).  *  *<p>By default, this rule is executed as part of a unit test and  *<a href="https://github.com/bwaldvogel/mongo-java-server">in-memory database</a> is used.  *  *<p>However, if the maven profile is set to {@code IT} (eg. via command line  * {@code $ mvn -Pit install}) this rule will connect to an existing (external)  * Mongo instance ({@code localhost}).  */
end_comment

begin_class
class|class
name|MongoDatabasePolicy
implements|implements
name|AfterAllCallback
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DB_NAME
init|=
literal|"test"
decl_stmt|;
specifier|private
specifier|final
name|MongoDatabase
name|database
decl_stmt|;
specifier|private
specifier|final
name|MongoClient
name|client
decl_stmt|;
specifier|private
specifier|final
name|Closer
name|closer
decl_stmt|;
specifier|private
name|MongoDatabasePolicy
parameter_list|(
name|MongoClient
name|client
parameter_list|,
name|Closer
name|closer
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|client
argument_list|,
literal|"client"
argument_list|)
expr_stmt|;
name|this
operator|.
name|database
operator|=
name|client
operator|.
name|getDatabase
argument_list|(
name|DB_NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|closer
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|closer
argument_list|,
literal|"closer"
argument_list|)
expr_stmt|;
name|closer
operator|.
name|add
argument_list|(
name|client
operator|::
name|close
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterAll
parameter_list|(
name|ExtensionContext
name|context
parameter_list|)
block|{
name|closer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates an instance based on current maven profile (as defined by {@code -Pit}).    *    * @return new instance of the policy to be used by unit tests    */
specifier|static
name|MongoDatabasePolicy
name|create
parameter_list|()
block|{
specifier|final
name|MongoClient
name|client
decl_stmt|;
specifier|final
name|Closer
name|closer
init|=
operator|new
name|Closer
argument_list|()
decl_stmt|;
if|if
condition|(
name|MongoAssertions
operator|.
name|useMongo
argument_list|()
condition|)
block|{
comment|// use to real client (connects to default mongo instance)
name|client
operator|=
operator|new
name|MongoClient
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|MongoAssertions
operator|.
name|useFake
argument_list|()
condition|)
block|{
specifier|final
name|MongoServer
name|server
init|=
operator|new
name|MongoServer
argument_list|(
operator|new
name|MemoryBackend
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|address
init|=
name|server
operator|.
name|bind
argument_list|()
decl_stmt|;
name|closer
operator|.
name|add
argument_list|(
operator|(
name|Closeable
operator|)
name|server
operator|::
name|shutdownNow
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|MongoClient
argument_list|(
literal|"127.0.0.1"
argument_list|,
name|address
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"I can only connect to Mongo or Fake instances"
argument_list|)
throw|;
block|}
return|return
operator|new
name|MongoDatabasePolicy
argument_list|(
name|client
argument_list|,
name|closer
argument_list|)
return|;
block|}
name|MongoDatabase
name|database
parameter_list|()
block|{
return|return
name|database
return|;
block|}
block|}
end_class

end_unit

