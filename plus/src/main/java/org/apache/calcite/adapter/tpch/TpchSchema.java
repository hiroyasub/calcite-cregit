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
name|tpch
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
name|Enumerator
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
name|AbstractTableQueryable
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
name|io
operator|.
name|airlift
operator|.
name|tpch
operator|.
name|TpchColumn
import|;
end_import

begin_import
import|import
name|io
operator|.
name|airlift
operator|.
name|tpch
operator|.
name|TpchEntity
import|;
end_import

begin_import
import|import
name|io
operator|.
name|airlift
operator|.
name|tpch
operator|.
name|TpchTable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Date
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
name|Locale
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
comment|/** Schema that provides TPC-H tables, populated according to a  * particular scale factor. */
end_comment

begin_class
specifier|public
class|class
name|TpchSchema
extends|extends
name|AbstractSchema
block|{
specifier|private
specifier|final
name|double
name|scaleFactor
decl_stmt|;
specifier|private
specifier|final
name|int
name|part
decl_stmt|;
specifier|private
specifier|final
name|int
name|partCount
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|columnPrefix
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|tableMap
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|columnPrefixes
decl_stmt|;
specifier|public
name|TpchSchema
parameter_list|(
name|double
name|scaleFactor
parameter_list|,
name|int
name|part
parameter_list|,
name|int
name|partCount
parameter_list|,
name|boolean
name|columnPrefix
parameter_list|)
block|{
name|this
operator|.
name|scaleFactor
operator|=
name|scaleFactor
expr_stmt|;
name|this
operator|.
name|part
operator|=
name|part
expr_stmt|;
name|this
operator|.
name|partCount
operator|=
name|partCount
expr_stmt|;
name|this
operator|.
name|columnPrefix
operator|=
name|columnPrefix
expr_stmt|;
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
name|TpchTable
argument_list|<
name|?
argument_list|>
name|tpchTable
range|:
name|TpchTable
operator|.
name|getTables
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|tpchTable
operator|.
name|getTableName
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
operator|new
name|TpchQueryableTable
argument_list|(
name|tpchTable
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|tableMap
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|columnPrefixes
operator|=
name|ImmutableMap
operator|.
expr|<
name|String
operator|,
name|String
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"LINEITEM"
argument_list|,
literal|"L_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"CUSTOMER"
argument_list|,
literal|"C_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"SUPPLIER"
argument_list|,
literal|"S_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"PARTSUPP"
argument_list|,
literal|"PS_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"PART"
argument_list|,
literal|"P_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"ORDERS"
argument_list|,
literal|"O_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"NATION"
argument_list|,
literal|"N_"
argument_list|)
operator|.
name|put
argument_list|(
literal|"REGION"
argument_list|,
literal|"R_"
argument_list|)
operator|.
name|build
argument_list|()
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
return|return
name|tableMap
return|;
block|}
comment|/** Definition of a table in the TPC-H schema. */
specifier|private
class|class
name|TpchQueryableTable
parameter_list|<
name|E
extends|extends
name|TpchEntity
parameter_list|>
extends|extends
name|AbstractQueryableTable
block|{
specifier|private
specifier|final
name|TpchTable
argument_list|<
name|E
argument_list|>
name|tpchTable
decl_stmt|;
name|TpchQueryableTable
parameter_list|(
name|TpchTable
argument_list|<
name|E
argument_list|>
name|tpchTable
parameter_list|)
block|{
name|super
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|tpchTable
operator|=
name|tpchTable
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|asQueryable
parameter_list|(
specifier|final
name|QueryProvider
name|queryProvider
parameter_list|,
specifier|final
name|SchemaPlus
name|schema
parameter_list|,
specifier|final
name|String
name|tableName
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Queryable
operator|)
operator|new
name|AbstractTableQueryable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|(
name|queryProvider
argument_list|,
name|schema
argument_list|,
name|this
argument_list|,
name|tableName
argument_list|)
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Enumerator
argument_list|<
name|E
argument_list|>
name|iterator
init|=
name|Linq4j
operator|.
name|iterableEnumerator
argument_list|(
name|tpchTable
operator|.
name|createGenerator
argument_list|(
name|scaleFactor
argument_list|,
name|part
argument_list|,
name|partCount
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|TpchColumn
argument_list|<
name|E
argument_list|>
argument_list|>
name|columns
init|=
name|tpchTable
operator|.
name|getColumns
argument_list|()
decl_stmt|;
specifier|final
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|columns
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TpchColumn
argument_list|<
name|E
argument_list|>
name|column
range|:
name|columns
control|)
block|{
name|objects
index|[
name|i
operator|++
index|]
operator|=
name|value
argument_list|(
name|column
argument_list|,
name|iterator
operator|.
name|current
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
specifier|private
name|Object
name|value
parameter_list|(
name|TpchColumn
argument_list|<
name|E
argument_list|>
name|tpchColumn
parameter_list|,
name|E
name|current
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|realType
argument_list|(
name|tpchColumn
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|String
operator|.
name|class
condition|)
block|{
return|return
name|tpchColumn
operator|.
name|getString
argument_list|(
name|current
argument_list|)
return|;
block|}
if|else if
condition|(
name|type
operator|==
name|Double
operator|.
name|class
condition|)
block|{
return|return
name|tpchColumn
operator|.
name|getDouble
argument_list|(
name|current
argument_list|)
return|;
block|}
if|else if
condition|(
name|type
operator|==
name|Date
operator|.
name|class
condition|)
block|{
return|return
name|Date
operator|.
name|valueOf
argument_list|(
name|tpchColumn
operator|.
name|getString
argument_list|(
name|current
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|tpchColumn
operator|.
name|getLong
argument_list|(
name|current
argument_list|)
return|;
block|}
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|moveNext
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|iterator
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
block|}
return|;
block|}
block|}
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|columnPrefix
condition|)
block|{
specifier|final
name|String
name|t
init|=
name|tpchTable
operator|.
name|getTableName
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|prefix
operator|=
name|columnPrefixes
operator|.
name|get
argument_list|(
name|t
argument_list|)
expr_stmt|;
assert|assert
name|prefix
operator|!=
literal|null
operator|:
name|t
assert|;
block|}
for|for
control|(
name|TpchColumn
argument_list|<
name|E
argument_list|>
name|column
range|:
name|tpchTable
operator|.
name|getColumns
argument_list|()
control|)
block|{
specifier|final
name|String
name|c
init|=
operator|(
name|prefix
operator|+
name|column
operator|.
name|getColumnName
argument_list|()
operator|)
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|c
argument_list|,
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|realType
argument_list|(
name|column
argument_list|)
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
specifier|private
name|Class
argument_list|<
name|?
argument_list|>
name|realType
parameter_list|(
name|TpchColumn
argument_list|<
name|E
argument_list|>
name|column
parameter_list|)
block|{
if|if
condition|(
name|column
operator|.
name|getColumnName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"date"
argument_list|)
condition|)
block|{
return|return
name|java
operator|.
name|sql
operator|.
name|Date
operator|.
name|class
return|;
block|}
return|return
name|column
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End TpchSchema.java
end_comment

end_unit

