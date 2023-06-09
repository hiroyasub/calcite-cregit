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
name|chinook
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
name|adapter
operator|.
name|java
operator|.
name|AbstractQueryableTable
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|QueryProvider
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
name|linq4j
operator|.
name|Queryable
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|QueryableTable
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Base64
import|;
end_import

begin_comment
comment|/**  * Example Table Function for lateral join checks.  */
end_comment

begin_class
specifier|public
class|class
name|CodesFunction
block|{
specifier|private
name|CodesFunction
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|QueryableTable
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|AbstractQueryableTable
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"TYPE"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
literal|"CODEVALUE"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Queryable
argument_list|<
name|String
index|[]
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
name|Linq4j
operator|.
expr|<
name|String
index|[]
operator|>
name|emptyEnumerable
argument_list|()
operator|.
name|asQueryable
argument_list|()
return|;
block|}
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
operator|new
name|String
index|[]
index|[]
block|{
operator|new
name|String
index|[]
block|{
literal|"HASHCODE"
block|,
literal|""
operator|+
name|name
operator|.
name|hashCode
argument_list|()
block|}
block|,
operator|new
name|String
index|[]
block|{
literal|"BASE64"
block|,
name|Base64
operator|.
name|getEncoder
argument_list|()
operator|.
name|encodeToString
argument_list|(
name|name
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
block|}
block|}
argument_list|)
operator|.
name|asQueryable
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

