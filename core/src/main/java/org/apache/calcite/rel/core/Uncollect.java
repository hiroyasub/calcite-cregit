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
name|rel
operator|.
name|core
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
name|plan
operator|.
name|Convention
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
name|RelInput
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
name|RelWriter
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
name|SingleRel
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
name|sql
operator|.
name|SqlUnnestOperator
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
name|MapSqlType
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
comment|/**  * Relational expression that unnests its input's columns into a relation.  *  *<p>The input may have multiple columns, but each must be a multiset or  * array. If {@code withOrdinality}, the output contains an extra  * {@code ORDINALITY} column.  *  *<p>Like its inverse operation {@link Collect}, Uncollect is generally  * invoked in a nested loop, driven by  * {@link org.apache.calcite.rel.logical.LogicalCorrelate} or similar.  */
end_comment

begin_class
specifier|public
class|class
name|Uncollect
extends|extends
name|SingleRel
block|{
specifier|public
specifier|final
name|boolean
name|withOrdinality
decl_stmt|;
comment|// To alias the items in Uncollect list,
comment|// i.e., "UNNEST(a, b, c) as T(d, e, f)"
comment|// outputs as row type Record(d, e, f) where the field "d" has element type of "a",
comment|// field "e" has element type of "b"(Presto dialect).
comment|// Without the aliases, the expression "UNNEST(a)" outputs row type
comment|// same with element type of "a".
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|itemAliases
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|Uncollect
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an Uncollect.    *    *<p>Use {@link #create} unless you know what you're doing. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"method.invocation.invalid"
argument_list|)
specifier|public
name|Uncollect
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|boolean
name|withOrdinality
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|itemAliases
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|withOrdinality
operator|=
name|withOrdinality
expr_stmt|;
name|this
operator|.
name|itemAliases
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|itemAliases
argument_list|)
expr_stmt|;
assert|assert
name|deriveRowType
argument_list|()
operator|!=
literal|null
operator|:
literal|"invalid child rowtype"
assert|;
block|}
comment|/**    * Creates an Uncollect by parsing serialized output.    */
specifier|public
name|Uncollect
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|input
operator|.
name|getBoolean
argument_list|(
literal|"withOrdinality"
argument_list|,
literal|false
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an Uncollect.    *    *<p>Each field of the input relational expression must be an array or    * multiset.    *    * @param traitSet       Trait set    * @param input          Input relational expression    * @param withOrdinality Whether output should contain an ORDINALITY column    * @param itemAliases    Aliases for the operand items    */
specifier|public
specifier|static
name|Uncollect
name|create
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|boolean
name|withOrdinality
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|itemAliases
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
return|return
operator|new
name|Uncollect
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|withOrdinality
argument_list|,
name|itemAliases
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"withOrdinality"
argument_list|,
name|withOrdinality
argument_list|,
name|withOrdinality
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
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
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|Uncollect
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|withOrdinality
argument_list|,
name|itemAliases
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|deriveUncollectRowType
argument_list|(
name|input
argument_list|,
name|withOrdinality
argument_list|,
name|itemAliases
argument_list|)
return|;
block|}
comment|/**    * Returns the row type returned by applying the 'UNNEST' operation to a    * relational expression.    *    *<p>Each column in the relational expression must be a multiset of    * structs or an array. The return type is the combination of expanding    * element types from each column, plus an ORDINALITY column if {@code    * withOrdinality}. If {@code itemAliases} is not empty, the element types    * would not expand, each column element outputs as a whole (the return    * type has same column types as input type).    */
specifier|public
specifier|static
name|RelDataType
name|deriveUncollectRowType
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|boolean
name|withOrdinality
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|itemAliases
parameter_list|)
block|{
name|RelDataType
name|inputType
init|=
name|rel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
assert|assert
name|inputType
operator|.
name|isStruct
argument_list|()
operator|:
name|inputType
operator|+
literal|" is not a struct"
assert|;
name|boolean
name|requireAlias
init|=
operator|!
name|itemAliases
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|requireAlias
operator|||
name|itemAliases
operator|.
name|size
argument_list|()
operator|==
name|inputType
operator|.
name|getFieldCount
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|inputType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|ANY
condition|)
block|{
comment|// Component type is unknown to Uncollect, build a row type with input column name
comment|// and Any type.
return|return
name|builder
operator|.
name|add
argument_list|(
name|requireAlias
condition|?
name|itemAliases
operator|.
name|get
argument_list|(
literal|0
argument_list|)
else|:
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlTypeName
operator|.
name|ANY
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|instanceof
name|MapSqlType
condition|)
block|{
name|MapSqlType
name|mapType
init|=
operator|(
name|MapSqlType
operator|)
name|field
operator|.
name|getType
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|SqlUnnestOperator
operator|.
name|MAP_KEY_COLUMN_NAME
argument_list|,
name|mapType
operator|.
name|getKeyType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|SqlUnnestOperator
operator|.
name|MAP_VALUE_COLUMN_NAME
argument_list|,
name|mapType
operator|.
name|getValueType
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|RelDataType
name|ret
init|=
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
assert|assert
literal|null
operator|!=
name|ret
assert|;
if|if
condition|(
name|requireAlias
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|itemAliases
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|ret
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|builder
operator|.
name|addAll
argument_list|(
name|ret
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Element type is not a record, use the field name of the element directly
name|builder
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|withOrdinality
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|SqlUnnestOperator
operator|.
name|ORDINALITY_COLUMN_NAME
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
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

