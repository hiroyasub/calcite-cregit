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
name|cassandra
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory that creates a {@link CassandraSchema}  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|CassandraSchemaFactory
implements|implements
name|SchemaFactory
block|{
specifier|public
name|CassandraSchemaFactory
parameter_list|()
block|{
block|}
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
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|operand
decl_stmt|;
name|String
name|host
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"host"
argument_list|)
decl_stmt|;
name|String
name|keyspace
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"keyspace"
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"port"
argument_list|)
condition|)
block|{
name|int
name|port
init|=
operator|(
name|int
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"port"
argument_list|)
decl_stmt|;
return|return
operator|new
name|CassandraSchema
argument_list|(
name|host
argument_list|,
name|port
argument_list|,
name|keyspace
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|parentSchema
argument_list|,
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|CassandraSchema
argument_list|(
name|host
argument_list|,
name|keyspace
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|parentSchema
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CassandraSchemaFactory.java
end_comment

end_unit

