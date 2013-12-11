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
name|rex
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
name|fun
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
comment|/**  * Utility class for various methods related to multisets.  *  * @author Wael Chatila  * @version $Id$  * @since Apr 4, 2005  */
end_comment

begin_class
specifier|public
class|class
name|RexMultisetUtil
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * A set defining all implementable multiset calls      */
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlOperator
argument_list|>
name|multisetOperators
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|SqlStdOperatorTable
operator|.
name|cardinalityFunc
argument_list|,
name|SqlStdOperatorTable
operator|.
name|castFunc
argument_list|,
name|SqlStdOperatorTable
operator|.
name|elementFunc
argument_list|,
name|SqlStdOperatorTable
operator|.
name|elementSlicefunc
argument_list|,
name|SqlStdOperatorTable
operator|.
name|multisetExceptAllOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|multisetExceptOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|multisetIntersectAllOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|multisetIntersectOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|multisetUnionAllOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|multisetUnionOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|isASetOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|memberOfOperator
argument_list|,
name|SqlStdOperatorTable
operator|.
name|submultisetOfOperator
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlStdOperatorTable
name|opTab
init|=
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns true if any expression in a program contains a mixing between      * multiset and non-multiset calls.      */
specifier|public
specifier|static
name|boolean
name|containsMixing
parameter_list|(
name|RexProgram
name|program
parameter_list|)
block|{
name|RexCallMultisetOperatorCounter
name|counter
init|=
operator|new
name|RexCallMultisetOperatorCounter
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|expr
range|:
name|program
operator|.
name|getExprList
argument_list|()
control|)
block|{
name|counter
operator|.
name|reset
argument_list|()
expr_stmt|;
name|expr
operator|.
name|accept
argument_list|(
name|counter
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|counter
operator|.
name|totalCount
operator|!=
name|counter
operator|.
name|multisetCount
operator|)
operator|&&
operator|(
literal|0
operator|!=
name|counter
operator|.
name|multisetCount
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns true if a node contains a mixing between multiset and      * non-multiset calls.      */
specifier|public
specifier|static
name|boolean
name|containsMixing
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
name|RexCallMultisetOperatorCounter
name|counter
init|=
operator|new
name|RexCallMultisetOperatorCounter
argument_list|()
decl_stmt|;
name|node
operator|.
name|accept
argument_list|(
name|counter
argument_list|)
expr_stmt|;
return|return
operator|(
name|counter
operator|.
name|totalCount
operator|!=
name|counter
operator|.
name|multisetCount
operator|)
operator|&&
operator|(
literal|0
operator|!=
name|counter
operator|.
name|multisetCount
operator|)
return|;
block|}
comment|/**      * Returns true if node contains a multiset operator, otherwise false. Use      * it with deep=false when checking if a RexCall is a multiset call.      *      * @param node Expression      * @param deep If true, returns whether expression contains a multiset. If      * false, returns whether expression<em>is</em> a multiset.      */
specifier|public
specifier|static
name|boolean
name|containsMultiset
parameter_list|(
specifier|final
name|RexNode
name|node
parameter_list|,
name|boolean
name|deep
parameter_list|)
block|{
return|return
literal|null
operator|!=
name|findFirstMultiset
argument_list|(
name|node
argument_list|,
name|deep
argument_list|)
return|;
block|}
comment|/**      * Returns whether a list of expressions contains a multiset.      */
specifier|public
specifier|static
name|boolean
name|containsMultiset
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|nodes
parameter_list|,
name|boolean
name|deep
parameter_list|)
block|{
for|for
control|(
name|RexNode
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|containsMultiset
argument_list|(
name|node
argument_list|,
name|deep
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns whether a program contains a multiset.      */
specifier|public
specifier|static
name|boolean
name|containsMultiset
parameter_list|(
name|RexProgram
name|program
parameter_list|)
block|{
return|return
name|containsMultiset
argument_list|(
name|program
operator|.
name|getExprList
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Returns true if call is call to<code>CAST</code> and the to/from cast      * types are of multiset types      */
specifier|public
specifier|static
name|boolean
name|isMultisetCast
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
operator|!
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|equals
argument_list|(
name|SqlStdOperatorTable
operator|.
name|castFunc
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|call
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|MULTISET
return|;
block|}
comment|/**      * Returns a reference to the first found multiset call or null if none was      * found      */
specifier|public
specifier|static
name|RexCall
name|findFirstMultiset
parameter_list|(
specifier|final
name|RexNode
name|node
parameter_list|,
name|boolean
name|deep
parameter_list|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|RexFieldAccess
condition|)
block|{
return|return
name|findFirstMultiset
argument_list|(
operator|(
operator|(
name|RexFieldAccess
operator|)
name|node
operator|)
operator|.
name|getReferenceExpr
argument_list|()
argument_list|,
name|deep
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|RexCall
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
name|RexCall
name|firstOne
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SqlOperator
name|op
range|:
name|multisetOperators
control|)
block|{
name|firstOne
operator|=
name|RexUtil
operator|.
name|findOperatorCall
argument_list|(
name|op
argument_list|,
name|call
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|firstOne
condition|)
block|{
if|if
condition|(
name|firstOne
operator|.
name|getOperator
argument_list|()
operator|.
name|equals
argument_list|(
name|SqlStdOperatorTable
operator|.
name|castFunc
argument_list|)
operator|&&
operator|!
name|isMultisetCast
argument_list|(
name|firstOne
argument_list|)
condition|)
block|{
name|firstOne
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|deep
operator|&&
operator|(
name|firstOne
operator|!=
name|call
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|firstOne
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * A RexShuttle that traverse all RexNode and counts total number of      * RexCalls traversed and number of multiset calls traversed.      *      *<p>totalCount>= multisetCount always holds true.      */
specifier|private
specifier|static
class|class
name|RexCallMultisetOperatorCounter
extends|extends
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
block|{
name|int
name|totalCount
init|=
literal|0
decl_stmt|;
name|int
name|multisetCount
init|=
literal|0
decl_stmt|;
name|RexCallMultisetOperatorCounter
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|void
name|reset
parameter_list|()
block|{
name|totalCount
operator|=
literal|0
expr_stmt|;
name|multisetCount
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|Void
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
operator|++
name|totalCount
expr_stmt|;
if|if
condition|(
name|multisetOperators
operator|.
name|contains
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|equals
argument_list|(
name|SqlStdOperatorTable
operator|.
name|castFunc
argument_list|)
operator|||
name|isMultisetCast
argument_list|(
name|call
argument_list|)
condition|)
block|{
operator|++
name|multisetCount
expr_stmt|;
block|}
block|}
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexMultisetUtil.java
end_comment

end_unit

