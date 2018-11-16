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
name|enumerable
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
name|JavaTypeFactory
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
name|interpreter
operator|.
name|Row
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
name|function
operator|.
name|Function1
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
name|Blocks
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|MethodCallExpression
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
name|ParameterExpression
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
name|Primitive
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
name|Types
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
name|plan
operator|.
name|RelTraitSet
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
name|RelCollationTraitDef
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
name|core
operator|.
name|TableScan
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
name|StreamableTable
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
name|util
operator|.
name|BuiltInMethod
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
name|ArrayList
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.TableScan} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableTableScan
extends|extends
name|TableScan
implements|implements
name|EnumerableRel
block|{
specifier|private
specifier|final
name|Class
name|elementType
decl_stmt|;
comment|/** Creates an EnumerableTableScan.    *    *<p>Use {@link #create} unless you know what you are doing. */
specifier|public
name|EnumerableTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|Class
name|elementType
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|table
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|instanceof
name|EnumerableConvention
assert|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
block|}
comment|/** Creates an EnumerableTableScan. */
specifier|public
specifier|static
name|EnumerableTableScan
name|create
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
specifier|final
name|Table
name|table
init|=
name|relOptTable
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
decl_stmt|;
name|Class
name|elementType
init|=
name|EnumerableTableScan
operator|.
name|deduceElementType
argument_list|(
name|table
argument_list|)
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
block|{
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
return|return
name|table
operator|.
name|getStatistic
argument_list|()
operator|.
name|getCollations
argument_list|()
return|;
block|}
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableTableScan
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|relOptTable
argument_list|,
name|elementType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|EnumerableTableScan
operator|&&
name|table
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|EnumerableTableScan
operator|)
name|obj
operator|)
operator|.
name|table
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|table
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Returns whether EnumerableTableScan can generate code to handle a    * particular variant of the Table SPI. */
specifier|public
specifier|static
name|boolean
name|canHandle
parameter_list|(
name|Table
name|table
parameter_list|)
block|{
comment|// FilterableTable and ProjectableFilterableTable cannot be handled in
comment|// enumerable convention because they might reject filters and those filters
comment|// would need to be handled dynamically.
return|return
name|table
operator|instanceof
name|QueryableTable
operator|||
name|table
operator|instanceof
name|ScannableTable
return|;
block|}
specifier|public
specifier|static
name|Class
name|deduceElementType
parameter_list|(
name|Table
name|table
parameter_list|)
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
operator|||
name|table
operator|instanceof
name|StreamableTable
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
specifier|static
name|JavaRowFormat
name|deduceFormat
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
specifier|final
name|Class
name|elementType
init|=
name|deduceElementType
argument_list|(
name|table
operator|.
name|unwrap
argument_list|(
name|Table
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|elementType
operator|==
name|Object
index|[]
operator|.
name|class
condition|?
name|JavaRowFormat
operator|.
name|ARRAY
else|:
name|JavaRowFormat
operator|.
name|CUSTOM
return|;
block|}
specifier|private
name|Expression
name|getExpression
parameter_list|(
name|PhysType
name|physType
parameter_list|)
block|{
specifier|final
name|Expression
name|expression
init|=
name|table
operator|.
name|getExpression
argument_list|(
name|Queryable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|expression2
init|=
name|toEnumerable
argument_list|(
name|expression
argument_list|)
decl_stmt|;
assert|assert
name|Types
operator|.
name|isAssignableFrom
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
name|expression2
operator|.
name|getType
argument_list|()
argument_list|)
assert|;
return|return
name|toRows
argument_list|(
name|physType
argument_list|,
name|expression2
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|toEnumerable
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
specifier|final
name|Type
name|type
init|=
name|expression
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Types
operator|.
name|isArray
argument_list|(
name|type
argument_list|)
condition|)
block|{
if|if
condition|(
name|Types
operator|.
name|toClass
argument_list|(
name|type
argument_list|)
operator|.
name|getComponentType
argument_list|()
operator|.
name|isPrimitive
argument_list|()
condition|)
block|{
name|expression
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|AS_LIST
operator|.
name|method
argument_list|,
name|expression
argument_list|)
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|AS_ENUMERABLE
operator|.
name|method
argument_list|,
name|expression
argument_list|)
return|;
block|}
if|else if
condition|(
name|Types
operator|.
name|isAssignableFrom
argument_list|(
name|Iterable
operator|.
name|class
argument_list|,
name|type
argument_list|)
operator|&&
operator|!
name|Types
operator|.
name|isAssignableFrom
argument_list|(
name|Enumerable
operator|.
name|class
argument_list|,
name|type
argument_list|)
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|AS_ENUMERABLE2
operator|.
name|method
argument_list|,
name|expression
argument_list|)
return|;
block|}
if|else if
condition|(
name|Types
operator|.
name|isAssignableFrom
argument_list|(
name|Queryable
operator|.
name|class
argument_list|,
name|type
argument_list|)
condition|)
block|{
comment|// Queryable extends Enumerable, but it's too "clever", so we call
comment|// Queryable.asEnumerable so that operations such as take(int) will be
comment|// evaluated directly.
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|expression
argument_list|,
name|BuiltInMethod
operator|.
name|QUERYABLE_AS_ENUMERABLE
operator|.
name|method
argument_list|)
return|;
block|}
return|return
name|expression
return|;
block|}
specifier|private
name|Expression
name|toRows
parameter_list|(
name|PhysType
name|physType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|physType
operator|.
name|getFormat
argument_list|()
operator|==
name|JavaRowFormat
operator|.
name|SCALAR
operator|&&
name|Object
index|[]
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|elementType
argument_list|)
operator|&&
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|table
operator|.
name|unwrap
argument_list|(
name|ScannableTable
operator|.
name|class
argument_list|)
operator|!=
literal|null
operator|||
name|table
operator|.
name|unwrap
argument_list|(
name|FilterableTable
operator|.
name|class
argument_list|)
operator|!=
literal|null
operator|||
name|table
operator|.
name|unwrap
argument_list|(
name|ProjectableFilterableTable
operator|.
name|class
argument_list|)
operator|!=
literal|null
operator|)
condition|)
block|{
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|SLICE0
operator|.
name|method
argument_list|,
name|expression
argument_list|)
return|;
block|}
name|JavaRowFormat
name|oldFormat
init|=
name|format
argument_list|()
decl_stmt|;
if|if
condition|(
name|physType
operator|.
name|getFormat
argument_list|()
operator|==
name|oldFormat
operator|&&
operator|!
name|hasCollectionField
argument_list|(
name|rowType
argument_list|)
condition|)
block|{
return|return
name|expression
return|;
block|}
specifier|final
name|ParameterExpression
name|row_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|elementType
argument_list|,
literal|"row"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|fieldCount
init|=
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|expressionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|expressionList
operator|.
name|add
argument_list|(
name|fieldExpression
argument_list|(
name|row_
argument_list|,
name|i
argument_list|,
name|physType
argument_list|,
name|oldFormat
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|expression
argument_list|,
name|BuiltInMethod
operator|.
name|SELECT
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|physType
operator|.
name|record
argument_list|(
name|expressionList
argument_list|)
argument_list|,
name|row_
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|fieldExpression
parameter_list|(
name|ParameterExpression
name|row_
parameter_list|,
name|int
name|i
parameter_list|,
name|PhysType
name|physType
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
block|{
specifier|final
name|Expression
name|e
init|=
name|format
operator|.
name|field
argument_list|(
name|row_
argument_list|,
name|i
argument_list|,
literal|null
argument_list|,
name|physType
operator|.
name|getJavaFieldType
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|relFieldType
init|=
name|physType
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|relFieldType
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ARRAY
case|:
case|case
name|MULTISET
case|:
comment|// We can't represent a multiset or array as a List<Employee>, because
comment|// the consumer does not know the element type.
comment|// The standard element type is List.
comment|// We need to convert to a List<List>.
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|PhysType
name|elementPhysType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|typeFactory
argument_list|,
name|relFieldType
operator|.
name|getComponentType
argument_list|()
argument_list|,
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
decl_stmt|;
specifier|final
name|MethodCallExpression
name|e2
init|=
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|AS_ENUMERABLE2
operator|.
name|method
argument_list|,
name|e
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|e3
init|=
name|elementPhysType
operator|.
name|convertTo
argument_list|(
name|e2
argument_list|,
name|JavaRowFormat
operator|.
name|LIST
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|e3
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERABLE_TO_LIST
operator|.
name|method
argument_list|)
return|;
default|default:
return|return
name|e
return|;
block|}
block|}
specifier|private
name|JavaRowFormat
name|format
parameter_list|()
block|{
name|int
name|fieldCount
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldCount
operator|==
literal|0
condition|)
block|{
return|return
name|JavaRowFormat
operator|.
name|LIST
return|;
block|}
if|if
condition|(
name|Object
index|[]
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|elementType
argument_list|)
condition|)
block|{
return|return
name|fieldCount
operator|==
literal|1
condition|?
name|JavaRowFormat
operator|.
name|SCALAR
else|:
name|JavaRowFormat
operator|.
name|ARRAY
return|;
block|}
if|if
condition|(
name|Row
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|elementType
argument_list|)
condition|)
block|{
return|return
name|JavaRowFormat
operator|.
name|ROW
return|;
block|}
if|if
condition|(
name|fieldCount
operator|==
literal|1
operator|&&
operator|(
name|Object
operator|.
name|class
operator|==
name|elementType
operator|||
name|Primitive
operator|.
name|is
argument_list|(
name|elementType
argument_list|)
operator|||
name|Number
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|elementType
argument_list|)
operator|)
condition|)
block|{
return|return
name|JavaRowFormat
operator|.
name|SCALAR
return|;
block|}
return|return
name|JavaRowFormat
operator|.
name|CUSTOM
return|;
block|}
specifier|private
name|boolean
name|hasCollectionField
parameter_list|(
name|RelDataType
name|rowType
parameter_list|)
block|{
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|rowType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
switch|switch
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|ARRAY
case|:
case|case
name|MULTISET
case|:
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|EnumerableTableScan
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|table
argument_list|,
name|elementType
argument_list|)
return|;
block|}
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
comment|// Note that representation is ARRAY. This assumes that the table
comment|// returns a Object[] for each record. Actually a Table<T> can
comment|// return any type T. And, if it is a JdbcTable, we'd like to be
comment|// able to generate alternate accessors that return e.g. synthetic
comment|// records {T0 f0; T1 f1; ...} and don't box every primitive value.
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|format
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|expression
init|=
name|getExpression
argument_list|(
name|physType
argument_list|)
decl_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|Blocks
operator|.
name|toBlock
argument_list|(
name|expression
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableTableScan.java
end_comment

end_unit

