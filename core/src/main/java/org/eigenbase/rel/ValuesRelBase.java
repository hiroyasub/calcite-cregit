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
name|*
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
name|Pair
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Functions
import|;
end_import

begin_comment
comment|/**  *<code>ValuesRelBase</code> is an abstract base class for implementations of  * {@link ValuesRel}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ValuesRelBase
extends|extends
name|AbstractRelNode
block|{
comment|/**    * Lambda that helps render tuples as strings.    */
specifier|private
specifier|static
specifier|final
name|Function1
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|,
name|Object
argument_list|>
name|F
init|=
operator|new
name|Function1
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
parameter_list|)
block|{
name|String
name|s
init|=
name|tuple
operator|.
name|toString
argument_list|()
decl_stmt|;
assert|assert
name|s
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
assert|;
assert|assert
name|s
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
assert|;
return|return
literal|"{ "
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
literal|" }"
return|;
block|}
block|}
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new ValuesRelBase. Note that tuples passed in become owned by    * this rel (without a deep copy), so caller must not modify them after this    * call, otherwise bad things will happen.    *    * @param cluster .    * @param rowType row type for tuples produced by this rel    * @param tuples  2-dimensional array of tuple values to be produced; outer    *                list contains tuples; each inner list is one tuple; all    *                tuples must be of same length, conforming to rowType    */
specifier|protected
name|ValuesRelBase
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|,
name|RelTraitSet
name|traits
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
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|tuples
operator|=
name|tuples
expr_stmt|;
assert|assert
name|assertRowType
argument_list|()
assert|;
block|}
comment|/**    * Creates a ValuesRelBase by parsing serialized output.    */
specifier|public
name|ValuesRelBase
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
name|getRowType
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|input
operator|.
name|getTuples
argument_list|(
literal|"tuples"
argument_list|)
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|getTuples
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getTuples
argument_list|(
literal|"tuples"
argument_list|)
return|;
block|}
comment|/**    * @return rows of literals represented by this rel    */
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|getTuples
parameter_list|()
block|{
return|return
name|tuples
return|;
block|}
comment|/**    * @return true if all tuples match rowType; otherwise, assert on mismatch    */
specifier|private
name|boolean
name|assertRowType
parameter_list|()
block|{
for|for
control|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
range|:
name|tuples
control|)
block|{
assert|assert
name|tuple
operator|.
name|size
argument_list|()
operator|==
name|rowType
operator|.
name|getFieldCount
argument_list|()
assert|;
for|for
control|(
name|Pair
argument_list|<
name|RexLiteral
argument_list|,
name|RelDataTypeField
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|tuple
argument_list|,
name|rowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
control|)
block|{
name|RexLiteral
name|literal
init|=
name|pair
operator|.
name|left
decl_stmt|;
name|RelDataType
name|fieldType
init|=
name|pair
operator|.
name|right
operator|.
name|getType
argument_list|()
decl_stmt|;
comment|// TODO jvs 19-Feb-2006: strengthen this a bit.  For example,
comment|// overflow, rounding, and padding/truncation must already have
comment|// been dealt with.
if|if
condition|(
operator|!
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|literal
argument_list|)
condition|)
block|{
assert|assert
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|fieldType
argument_list|,
name|literal
operator|.
name|getType
argument_list|()
argument_list|)
operator|:
literal|"to "
operator|+
name|fieldType
operator|+
literal|" from "
operator|+
name|literal
assert|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|// implement RelNode
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|rowType
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
name|double
name|dRows
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
comment|// Assume CPU is negligible since values are precomputed.
name|double
name|dCpu
init|=
literal|1
decl_stmt|;
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
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
name|tuples
operator|.
name|size
argument_list|()
return|;
block|}
comment|// implement RelNode
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
comment|// A little adapter just to get the tuples to come out
comment|// with curly brackets instead of square brackets.  Plus
comment|// more whitespace for readability.
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
comment|// For rel digest, include the row type since a rendered
comment|// literal may leave the type ambiguous (e.g. "null").
operator|.
name|itemIf
argument_list|(
literal|"type"
argument_list|,
name|rowType
argument_list|,
name|pw
operator|.
name|getDetailLevel
argument_list|()
operator|==
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"type"
argument_list|,
name|rowType
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|pw
operator|.
name|nest
argument_list|()
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"tuples"
argument_list|,
name|Functions
operator|.
name|adapt
argument_list|(
name|tuples
argument_list|,
name|F
argument_list|)
argument_list|,
operator|!
name|pw
operator|.
name|nest
argument_list|()
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"tuples"
argument_list|,
name|tuples
argument_list|,
name|pw
operator|.
name|nest
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ValuesRelBase.java
end_comment

end_unit

