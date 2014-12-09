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
name|prepare
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
name|enumerable
operator|.
name|EnumerableConvention
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
name|enumerable
operator|.
name|EnumerableInterpreter
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
name|enumerable
operator|.
name|EnumerableTableScan
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
name|jdbc
operator|.
name|CalciteSchema
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
name|RelOptCluster
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
name|RelOptSchema
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
name|RelCollation
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
name|RelDataTypeField
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
name|ExtensibleTable
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
name|FilterableTable
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
name|ProjectableFilterableTable
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
name|ScannableTable
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
name|sql
operator|.
name|SqlAccessType
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
name|validate
operator|.
name|SqlMonotonicity
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
name|ImmutableBitSet
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
name|Util
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
name|base
operator|.
name|Function
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
name|base
operator|.
name|Functions
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
name|ImmutableList
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
name|Collections
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

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.plan.RelOptTable}.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptTableImpl
implements|implements
name|Prepare
operator|.
name|PreparingTable
block|{
specifier|private
specifier|final
name|RelOptSchema
name|schema
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|Table
name|table
decl_stmt|;
specifier|private
specifier|final
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
name|expressionFunction
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
comment|/** Estimate for the row count, or null.    *    *<p>If not null, overrides the estimate from the actual table.    *    *<p>Useful when a table that contains a materialized query result is being    * used to replace a query expression that wildly underestimates the row    * count. Now the materialized table can tell the same lie. */
specifier|private
specifier|final
name|Double
name|rowCount
decl_stmt|;
specifier|private
name|RelOptTableImpl
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|Table
name|table
parameter_list|,
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
name|expressionFunction
parameter_list|,
name|Double
name|rowCount
parameter_list|)
block|{
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|names
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
comment|// may be null
name|this
operator|.
name|expressionFunction
operator|=
name|expressionFunction
expr_stmt|;
name|this
operator|.
name|rowCount
operator|=
name|rowCount
expr_stmt|;
assert|assert
name|expressionFunction
operator|!=
literal|null
assert|;
assert|assert
name|rowType
operator|!=
literal|null
assert|;
block|}
specifier|public
specifier|static
name|RelOptTableImpl
name|create
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
comment|//noinspection unchecked
specifier|final
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
name|expressionFunction
init|=
operator|(
name|Function
operator|)
name|Functions
operator|.
name|constant
argument_list|(
name|expression
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelOptTableImpl
argument_list|(
name|schema
argument_list|,
name|rowType
argument_list|,
name|names
argument_list|,
literal|null
argument_list|,
name|expressionFunction
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RelOptTableImpl
name|create
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
specifier|final
name|CalciteSchema
operator|.
name|TableEntry
name|tableEntry
parameter_list|,
name|Double
name|rowCount
parameter_list|)
block|{
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
name|expressionFunction
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|tableEntry
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|QueryableTable
condition|)
block|{
specifier|final
name|QueryableTable
name|queryableTable
init|=
operator|(
name|QueryableTable
operator|)
name|table
decl_stmt|;
name|expressionFunction
operator|=
operator|new
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|apply
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|queryableTable
operator|.
name|getExpression
argument_list|(
name|tableEntry
operator|.
name|schema
operator|.
name|plus
argument_list|()
argument_list|,
name|tableEntry
operator|.
name|name
argument_list|,
name|clazz
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
if|else if
condition|(
name|table
operator|instanceof
name|ScannableTable
operator|||
name|table
operator|instanceof
name|FilterableTable
operator|||
name|table
operator|instanceof
name|ProjectableFilterableTable
condition|)
block|{
name|expressionFunction
operator|=
operator|new
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|apply
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|Schemas
operator|.
name|tableExpression
argument_list|(
name|tableEntry
operator|.
name|schema
operator|.
name|plus
argument_list|()
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|tableEntry
operator|.
name|name
argument_list|,
name|table
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
name|expressionFunction
operator|=
operator|new
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|apply
parameter_list|(
name|Class
name|input
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
expr_stmt|;
block|}
return|return
operator|new
name|RelOptTableImpl
argument_list|(
name|schema
argument_list|,
name|rowType
argument_list|,
name|tableEntry
operator|.
name|path
argument_list|()
argument_list|,
name|table
argument_list|,
name|expressionFunction
argument_list|,
name|rowCount
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|RelOptTableImpl
name|create
parameter_list|(
name|RelOptSchema
name|schema
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|TranslatableTable
name|table
parameter_list|)
block|{
specifier|final
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
name|expressionFunction
init|=
operator|new
name|Function
argument_list|<
name|Class
argument_list|,
name|Expression
argument_list|>
argument_list|()
block|{
specifier|public
name|Expression
name|apply
parameter_list|(
name|Class
name|input
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
return|return
operator|new
name|RelOptTableImpl
argument_list|(
name|schema
argument_list|,
name|rowType
argument_list|,
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|,
name|table
argument_list|,
name|expressionFunction
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|this
argument_list|)
return|;
block|}
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|table
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|table
argument_list|)
return|;
block|}
if|if
condition|(
name|clazz
operator|==
name|CalciteSchema
operator|.
name|class
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|Schemas
operator|.
name|subSchema
argument_list|(
operator|(
operator|(
name|CalciteCatalogReader
operator|)
name|schema
operator|)
operator|.
name|rootSchema
argument_list|,
name|Util
operator|.
name|skipLast
argument_list|(
name|getQualifiedName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|expressionFunction
operator|.
name|apply
argument_list|(
name|clazz
argument_list|)
return|;
block|}
specifier|public
name|RelOptTable
name|extend
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedFields
parameter_list|)
block|{
if|if
condition|(
name|table
operator|instanceof
name|ExtensibleTable
condition|)
block|{
specifier|final
name|Table
name|extendedTable
init|=
operator|(
operator|(
name|ExtensibleTable
operator|)
name|table
operator|)
operator|.
name|extend
argument_list|(
name|extendedFields
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|extendedRowType
init|=
name|extendedTable
operator|.
name|getRowType
argument_list|(
name|schema
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelOptTableImpl
argument_list|(
name|schema
argument_list|,
name|extendedRowType
argument_list|,
name|names
argument_list|,
name|extendedTable
argument_list|,
name|expressionFunction
argument_list|,
name|rowCount
argument_list|)
return|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Cannot extend "
operator|+
name|table
argument_list|)
throw|;
comment|// TODO: user error
block|}
specifier|public
name|double
name|getRowCount
parameter_list|()
block|{
if|if
condition|(
name|rowCount
operator|!=
literal|null
condition|)
block|{
return|return
name|rowCount
return|;
block|}
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Double
name|rowCount
init|=
name|table
operator|.
name|getStatistic
argument_list|()
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|rowCount
operator|!=
literal|null
condition|)
block|{
return|return
name|rowCount
return|;
block|}
block|}
return|return
literal|100d
return|;
block|}
specifier|public
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
block|{
return|return
name|schema
return|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|table
operator|instanceof
name|TranslatableTable
condition|)
block|{
return|return
operator|(
operator|(
name|TranslatableTable
operator|)
name|table
operator|)
operator|.
name|toRel
argument_list|(
name|context
argument_list|,
name|this
argument_list|)
return|;
block|}
name|RelOptCluster
name|cluster
init|=
name|context
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|Class
name|elementType
init|=
name|deduceElementType
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|scan
init|=
operator|new
name|EnumerableTableScan
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|this
argument_list|,
name|elementType
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|FilterableTable
operator|||
name|table
operator|instanceof
name|ProjectableFilterableTable
condition|)
block|{
return|return
operator|new
name|EnumerableInterpreter
argument_list|(
name|cluster
argument_list|,
name|scan
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|scan
argument_list|,
literal|1d
argument_list|)
return|;
block|}
return|return
name|scan
return|;
block|}
specifier|private
name|Class
name|deduceElementType
parameter_list|()
block|{
if|if
condition|(
name|table
operator|instanceof
name|QueryableTable
condition|)
block|{
specifier|final
name|QueryableTable
name|queryableTable
init|=
operator|(
name|QueryableTable
operator|)
name|table
decl_stmt|;
specifier|final
name|Type
name|type
init|=
name|queryableTable
operator|.
name|getElementType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|instanceof
name|Class
condition|)
block|{
return|return
operator|(
name|Class
operator|)
name|type
return|;
block|}
else|else
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
block|}
if|else if
condition|(
name|table
operator|instanceof
name|ScannableTable
operator|||
name|table
operator|instanceof
name|FilterableTable
operator|||
name|table
operator|instanceof
name|ProjectableFilterableTable
condition|)
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
else|else
block|{
return|return
name|Object
operator|.
name|class
return|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
return|return
name|table
operator|.
name|getStatistic
argument_list|()
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
block|{
return|return
name|names
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
specifier|public
name|SqlAccessType
name|getAllowedAccess
parameter_list|()
block|{
return|return
name|SqlAccessType
operator|.
name|ALL
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptTableImpl.java
end_comment

end_unit

