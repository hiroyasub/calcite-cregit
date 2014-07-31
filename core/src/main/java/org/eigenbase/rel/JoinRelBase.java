begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|FlatLists
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  *<code>JoinRelBase</code> is an abstract base class for implementations of  * {@link JoinRel}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JoinRelBase
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RexNode
name|condition
decl_stmt|;
specifier|protected
name|RelNode
name|left
decl_stmt|;
specifier|protected
name|RelNode
name|right
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|variablesStopped
decl_stmt|;
comment|/**    * Values must be of enumeration {@link JoinRelType}, except that {@link    * JoinRelType#RIGHT} is disallowed.    */
specifier|protected
name|JoinRelType
name|joinType
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a JoinRelBase.    *    * @param cluster          Cluster    * @param traits           Traits    * @param left             Left input    * @param right            Right input    * @param condition        Join condition    * @param joinType         Join type    * @param variablesStopped Set of names of variables which are set by the    *                         LHS and used by the RHS and are not available to    *                         nodes above this JoinRel in the tree    */
specifier|protected
name|JoinRelBase
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|)
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|variablesStopped
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|variablesStopped
argument_list|)
expr_stmt|;
assert|assert
name|joinType
operator|!=
literal|null
assert|;
assert|assert
name|condition
operator|!=
literal|null
assert|;
name|this
operator|.
name|joinType
operator|=
name|joinType
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|condition
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|getCondition
parameter_list|()
block|{
return|return
name|condition
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
block|{
return|return
name|FlatLists
operator|.
name|of
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
specifier|public
name|JoinRelType
name|getJoinType
parameter_list|()
block|{
return|return
name|joinType
return|;
block|}
specifier|public
name|RelNode
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|RelNode
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
comment|// TODO: enable
specifier|public
name|boolean
name|isValid_
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
operator|!
name|super
operator|.
name|isValid
argument_list|(
name|fail
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|!=
name|getSystemFieldList
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|+
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"field count mismatch"
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|condition
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|!=
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"condition must be boolean: "
operator|+
name|condition
operator|.
name|getType
argument_list|()
assert|;
return|return
literal|false
return|;
block|}
comment|// The input to the condition is a row type consisting of system
comment|// fields, left fields, and right fields. Very similar to the
comment|// output row type, except that fields have not yet been made due
comment|// due to outer joins.
name|RexChecker
name|checker
init|=
operator|new
name|RexChecker
argument_list|(
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|getSystemFieldList
argument_list|()
argument_list|)
operator|.
name|addAll
argument_list|(
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
operator|.
name|addAll
argument_list|(
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|fail
argument_list|)
decl_stmt|;
name|condition
operator|.
name|accept
argument_list|(
name|checker
argument_list|)
expr_stmt|;
if|if
condition|(
name|checker
operator|.
name|getFailureCount
argument_list|()
operator|>
literal|0
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|checker
operator|.
name|getFailureCount
argument_list|()
operator|+
literal|" failures in condition "
operator|+
name|condition
assert|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// implement RelNode
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// REVIEW jvs 9-Apr-2006:  Just for now...
name|double
name|rowCount
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|rowCount
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|double
name|estimateJoinedRows
parameter_list|(
name|JoinRelBase
name|joinRel
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
name|double
name|product
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|joinRel
operator|.
name|getLeft
argument_list|()
argument_list|)
operator|*
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|joinRel
operator|.
name|getRight
argument_list|()
argument_list|)
decl_stmt|;
comment|// TODO:  correlation factor
return|return
name|product
operator|*
name|RelMetadataQuery
operator|.
name|getSelectivity
argument_list|(
name|joinRel
argument_list|,
name|condition
argument_list|)
return|;
block|}
comment|// implement RelNode
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|estimateJoinedRows
argument_list|(
name|this
argument_list|,
name|condition
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesStopped
parameter_list|()
block|{
return|return
name|variablesStopped
return|;
block|}
specifier|public
name|void
name|childrenAccept
parameter_list|(
name|RelVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|left
argument_list|,
literal|0
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|right
argument_list|,
literal|1
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
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
name|input
argument_list|(
literal|"left"
argument_list|,
name|left
argument_list|)
operator|.
name|input
argument_list|(
literal|"right"
argument_list|,
name|right
argument_list|)
operator|.
name|item
argument_list|(
literal|"condition"
argument_list|,
name|condition
argument_list|)
operator|.
name|item
argument_list|(
literal|"joinType"
argument_list|,
name|joinType
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"systemFields"
argument_list|,
name|getSystemFieldList
argument_list|()
argument_list|,
operator|!
name|getSystemFieldList
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|replaceInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|RelNode
name|p
parameter_list|)
block|{
switch|switch
condition|(
name|ordinalInParent
condition|)
block|{
case|case
literal|0
case|:
name|this
operator|.
name|left
operator|=
name|p
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|this
operator|.
name|right
operator|=
name|p
expr_stmt|;
break|break;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|()
throw|;
block|}
block|}
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|deriveJoinRowType
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
argument_list|,
name|joinType
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
literal|null
argument_list|,
name|getSystemFieldList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns whether this JoinRel has already spawned a    * {@link org.eigenbase.rel.rules.SemiJoinRel} via    * {@link org.eigenbase.rel.rules.AddRedundantSemiJoinRule}.    *    *<p>The base implementation returns false.</p>    *    * @return whether this join has already spawned a semi join    */
specifier|public
name|boolean
name|isSemiJoinDone
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Returns a list of system fields that will be prefixed to    * output row type.    *    * @return list of system fields    */
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getSystemFieldList
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
comment|/**    * Derives the type of a join relational expression.    *    * @param leftType        Row type of left input to join    * @param rightType       Row type of right input to join    * @param joinType        Type of join    * @param typeFactory     Type factory    * @param fieldNameList   List of names of fields; if null, field names are    *                        inherited and made unique    * @param systemFieldList List of system fields that will be prefixed to    *                        output row type; typically empty but must not be    *                        null    * @return join type    */
specifier|public
specifier|static
name|RelDataType
name|deriveJoinRowType
parameter_list|(
name|RelDataType
name|leftType
parameter_list|,
name|RelDataType
name|rightType
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|systemFieldList
parameter_list|)
block|{
assert|assert
name|systemFieldList
operator|!=
literal|null
assert|;
switch|switch
condition|(
name|joinType
condition|)
block|{
case|case
name|LEFT
case|:
name|rightType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|rightType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|RIGHT
case|:
name|leftType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|leftType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
case|case
name|FULL
case|:
name|leftType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|leftType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|rightType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|rightType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
return|return
name|createJoinType
argument_list|(
name|typeFactory
argument_list|,
name|leftType
argument_list|,
name|rightType
argument_list|,
name|fieldNameList
argument_list|,
name|systemFieldList
argument_list|)
return|;
block|}
comment|/**    * Returns the type the row which results when two relations are joined.    *    *<p>The resulting row type consists of    * the system fields (if any), followed by    * the fields of the left type, followed by    * the fields of the right type. The field name list, if present, overrides    * the original names of the fields.    *    * @param typeFactory     Type factory    * @param leftType        Type of left input to join    * @param rightType       Type of right input to join    * @param fieldNameList   If not null, overrides the original names of the    *                        fields    * @param systemFieldList List of system fields that will be prefixed to    *                        output row type; typically empty but must not be    *                        null    * @return type of row which results when two relations are joined    */
specifier|public
specifier|static
name|RelDataType
name|createJoinType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|leftType
parameter_list|,
name|RelDataType
name|rightType
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNameList
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|systemFieldList
parameter_list|)
block|{
assert|assert
operator|(
name|fieldNameList
operator|==
literal|null
operator|)
operator|||
operator|(
name|fieldNameList
operator|.
name|size
argument_list|()
operator|==
operator|(
name|systemFieldList
operator|.
name|size
argument_list|()
operator|+
name|leftType
operator|.
name|getFieldCount
argument_list|()
operator|+
name|rightType
operator|.
name|getFieldCount
argument_list|()
operator|)
operator|)
assert|;
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|typeList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
comment|// use a hashset to keep track of the field names; this is needed
comment|// to ensure that the contains() call to check for name uniqueness
comment|// runs in constant time; otherwise, if the number of fields is large,
comment|// doing a contains() on a list can be expensive
name|HashSet
argument_list|<
name|String
argument_list|>
name|uniqueNameList
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|systemFieldList
argument_list|,
name|typeList
argument_list|,
name|nameList
argument_list|,
name|uniqueNameList
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|leftType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|typeList
argument_list|,
name|nameList
argument_list|,
name|uniqueNameList
argument_list|)
expr_stmt|;
if|if
condition|(
name|rightType
operator|!=
literal|null
condition|)
block|{
name|addFields
argument_list|(
name|rightType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|typeList
argument_list|,
name|nameList
argument_list|,
name|uniqueNameList
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldNameList
operator|!=
literal|null
condition|)
block|{
assert|assert
name|fieldNameList
operator|.
name|size
argument_list|()
operator|==
name|nameList
operator|.
name|size
argument_list|()
assert|;
name|nameList
operator|=
name|fieldNameList
expr_stmt|;
block|}
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|typeList
argument_list|,
name|nameList
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|addFields
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|typeList
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|nameList
parameter_list|,
name|HashSet
argument_list|<
name|String
argument_list|>
name|uniqueNameList
parameter_list|)
block|{
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fieldList
control|)
block|{
name|String
name|name
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
comment|// Ensure that name is unique from all previous field names
if|if
condition|(
name|uniqueNameList
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
name|nameBase
init|=
name|name
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
condition|;
name|j
operator|++
control|)
block|{
name|name
operator|=
name|nameBase
operator|+
name|j
expr_stmt|;
if|if
condition|(
operator|!
name|uniqueNameList
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
name|nameList
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|uniqueNameList
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|typeList
operator|.
name|add
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|JoinRelBase
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
assert|assert
name|inputs
operator|.
name|size
argument_list|()
operator|==
literal|2
assert|;
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|getCondition
argument_list|()
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|joinType
argument_list|,
name|isSemiJoinDone
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a copy of this join, overriding condition, system fields and    * inputs.    *    *<p>General contract as {@link org.eigenbase.rel.RelNode#copy}.    *    * @param conditionExpr Condition    * @param left          Left input    * @param right         Right input    * @param joinType      Join type    * @param semiJoinDone  Whether this join has been translated to a    *                      semi-join    * @return Copy of this join    */
specifier|public
specifier|abstract
name|JoinRelBase
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RexNode
name|conditionExpr
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End JoinRelBase.java
end_comment

end_unit

