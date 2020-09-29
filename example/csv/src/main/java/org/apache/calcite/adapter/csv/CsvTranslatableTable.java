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
name|csv
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
name|DataContext
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
name|adapter
operator|.
name|file
operator|.
name|CsvEnumerator
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
name|AbstractEnumerable
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
name|Enumerable
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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
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
name|plan
operator|.
name|RelOptTable
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
name|RelNode
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
name|RelProtoDataType
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
name|schema
operator|.
name|Schemas
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
name|TranslatableTable
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
name|ImmutableIntList
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
name|Source
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Table based on a CSV file.  */
end_comment

begin_class
specifier|public
class|class
name|CsvTranslatableTable
extends|extends
name|CsvTable
implements|implements
name|QueryableTable
implements|,
name|TranslatableTable
block|{
comment|/** Creates a CsvTable. */
name|CsvTranslatableTable
parameter_list|(
name|Source
name|source
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|protoRowType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CsvTranslatableTable"
return|;
block|}
comment|/** Returns an enumerable over a given projection of the fields. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
comment|// called from generated code
specifier|public
name|Enumerable
argument_list|<
name|Object
argument_list|>
name|project
parameter_list|(
specifier|final
name|DataContext
name|root
parameter_list|,
specifier|final
name|int
index|[]
name|fields
parameter_list|)
block|{
specifier|final
name|AtomicBoolean
name|cancelFlag
init|=
name|DataContext
operator|.
name|Variable
operator|.
name|CANCEL_FLAG
operator|.
name|get
argument_list|(
name|root
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|Object
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|CsvEnumerator
argument_list|<>
argument_list|(
name|source
argument_list|,
name|cancelFlag
argument_list|,
name|getFieldTypes
argument_list|(
name|root
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
argument_list|,
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|fields
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|tableName
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Schemas
operator|.
name|tableExpression
argument_list|(
name|schema
argument_list|,
name|getElementType
argument_list|()
argument_list|,
name|tableName
argument_list|,
name|clazz
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
comment|// Request all fields.
specifier|final
name|int
name|fieldCount
init|=
name|relOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|fields
init|=
name|CsvEnumerator
operator|.
name|identityList
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
return|return
operator|new
name|CsvTableScan
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
end_class

end_unit

