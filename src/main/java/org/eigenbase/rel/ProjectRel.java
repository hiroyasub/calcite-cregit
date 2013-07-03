begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>ProjectRel</code> is a relational expression which computes a set of  * 'select expressions' from its input relational expression.  *  *<p>The result is usually 'boxed' as a record with one named field for each  * column; if there is precisely one expression, the result may be 'unboxed',  * and consist of the raw value type.</p>  *  * @version $Id$  * @author jhyde  * @since March, 2004  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ProjectRel
extends|extends
name|ProjectRelBase
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a ProjectRel with no sort keys.      *      * @param cluster Cluster this relational expression belongs to      * @param child input relational expression      * @param exps set of expressions for the input columns      * @param fieldNames aliases of the expressions      * @param flags values as in {@link ProjectRelBase.Flags}      */
specifier|public
name|ProjectRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|child
argument_list|,
name|exps
argument_list|,
name|RexUtil
operator|.
name|createStructType
argument_list|(
name|cluster
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|exps
argument_list|,
name|fieldNames
argument_list|)
argument_list|,
name|flags
argument_list|,
name|Collections
operator|.
expr|<
name|RelCollation
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a ProjectRel.      *      * @param cluster Cluster this relational expression belongs to      * @param child input relational expression      * @param exps List of expressions for the input columns      * @param rowType output row type      * @param flags values as in {@link ProjectRelBase.Flags}      * @param collationList List of sort keys      */
specifier|public
name|ProjectRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|int
name|flags
parameter_list|,
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|collationList
operator|.
name|isEmpty
argument_list|()
condition|?
name|RelCollationImpl
operator|.
name|EMPTY
else|:
name|collationList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|child
argument_list|,
name|exps
argument_list|,
name|rowType
argument_list|,
name|flags
argument_list|,
name|collationList
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|ProjectRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|getProjectExpList
argument_list|()
argument_list|,
name|rowType
argument_list|,
name|getFlags
argument_list|()
argument_list|,
name|collationList
argument_list|)
return|;
block|}
comment|/**      * Returns a permutation, if this projection is merely a permutation of its      * input fields, otherwise null.      */
specifier|public
name|Permutation
name|getPermutation
parameter_list|()
block|{
specifier|final
name|int
name|fieldCount
init|=
name|rowType
operator|.
name|getFields
argument_list|()
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|fieldCount
operator|!=
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFields
argument_list|()
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Permutation
name|permutation
init|=
operator|new
name|Permutation
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
operator|++
name|i
control|)
block|{
if|if
condition|(
name|exps
index|[
name|i
index|]
operator|instanceof
name|RexInputRef
condition|)
block|{
name|permutation
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|(
operator|(
name|RexInputRef
operator|)
name|exps
index|[
name|i
index|]
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|permutation
return|;
block|}
block|}
end_class

begin_comment
comment|// End ProjectRel.java
end_comment

end_unit

