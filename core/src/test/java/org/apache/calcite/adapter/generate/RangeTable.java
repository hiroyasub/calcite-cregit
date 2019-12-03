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
name|generate
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
name|TableFactory
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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Table that returns a range of integers.  */
end_comment

begin_class
specifier|public
class|class
name|RangeTable
extends|extends
name|AbstractQueryableTable
block|{
specifier|private
specifier|final
name|String
name|columnName
decl_stmt|;
specifier|private
specifier|final
name|int
name|start
decl_stmt|;
specifier|private
specifier|final
name|int
name|end
decl_stmt|;
specifier|protected
name|RangeTable
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|elementType
parameter_list|,
name|String
name|columnName
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|super
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
name|this
operator|.
name|columnName
operator|=
name|columnName
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
comment|/** Creates a RangeTable. */
specifier|public
specifier|static
name|RangeTable
name|create
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|elementType
parameter_list|,
name|String
name|columnName
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|RangeTable
argument_list|(
name|elementType
argument_list|,
name|columnName
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
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
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|columnName
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
return|;
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
return|return
operator|new
name|AbstractTableQueryable
argument_list|<
name|T
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
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
comment|//noinspection unchecked
return|return
operator|(
name|Enumerator
argument_list|<
name|T
argument_list|>
operator|)
name|RangeTable
operator|.
name|this
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|int
name|current
init|=
name|start
operator|-
literal|1
decl_stmt|;
specifier|public
name|Integer
name|current
parameter_list|()
block|{
if|if
condition|(
name|current
operator|>=
name|end
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|current
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
operator|++
name|current
expr_stmt|;
return|return
name|current
operator|<
name|end
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|current
operator|=
name|start
operator|-
literal|1
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
comment|/** Implementation of {@link org.apache.calcite.schema.TableFactory} that    * allows a {@link RangeTable} to be included as a custom table in a Calcite    * model file. */
specifier|public
specifier|static
class|class
name|Factory
implements|implements
name|TableFactory
argument_list|<
name|RangeTable
argument_list|>
block|{
specifier|public
name|RangeTable
name|create
parameter_list|(
name|SchemaPlus
name|schema
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
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|String
name|columnName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"column"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|start
init|=
operator|(
name|Integer
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"start"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|end
init|=
operator|(
name|Integer
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"end"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|elementType
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"elementType"
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
if|if
condition|(
literal|"array"
operator|.
name|equals
argument_list|(
name|elementType
argument_list|)
condition|)
block|{
name|type
operator|=
name|Object
index|[]
operator|.
name|class
expr_stmt|;
block|}
if|else if
condition|(
literal|"object"
operator|.
name|equals
argument_list|(
name|elementType
argument_list|)
condition|)
block|{
name|type
operator|=
name|Object
operator|.
name|class
expr_stmt|;
block|}
if|else if
condition|(
literal|"integer"
operator|.
name|equals
argument_list|(
name|elementType
argument_list|)
condition|)
block|{
name|type
operator|=
name|Integer
operator|.
name|class
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal 'elementType' value: "
operator|+
name|elementType
argument_list|)
throw|;
block|}
return|return
name|RangeTable
operator|.
name|create
argument_list|(
name|type
argument_list|,
name|columnName
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

