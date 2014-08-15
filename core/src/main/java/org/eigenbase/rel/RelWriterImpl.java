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
name|io
operator|.
name|PrintWriter
import|;
end_import

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
name|linq4j
operator|.
name|Ord
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
name|Spacer
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

begin_comment
comment|/**  * Implementation of {@link org.eigenbase.rel.RelWriter}.  */
end_comment

begin_class
specifier|public
class|class
name|RelWriterImpl
implements|implements
name|RelWriter
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|PrintWriter
name|pw
decl_stmt|;
specifier|private
specifier|final
name|SqlExplainLevel
name|detailLevel
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|withIdPrefix
decl_stmt|;
specifier|protected
specifier|final
name|Spacer
name|spacer
init|=
operator|new
name|Spacer
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelWriterImpl
parameter_list|(
name|PrintWriter
name|pw
parameter_list|)
block|{
name|this
argument_list|(
name|pw
argument_list|,
name|SqlExplainLevel
operator|.
name|EXPPLAN_ATTRIBUTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelWriterImpl
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|SqlExplainLevel
name|detailLevel
parameter_list|,
name|boolean
name|withIdPrefix
parameter_list|)
block|{
name|this
operator|.
name|pw
operator|=
name|pw
expr_stmt|;
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
expr_stmt|;
name|this
operator|.
name|withIdPrefix
operator|=
name|withIdPrefix
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|void
name|explain_
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|rel
operator|.
name|getInputs
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|RelMetadataQuery
operator|.
name|isVisibleInExplain
argument_list|(
name|rel
argument_list|,
name|detailLevel
argument_list|)
condition|)
block|{
comment|// render children in place of this, at same level
name|explainInputs
argument_list|(
name|inputs
argument_list|)
expr_stmt|;
return|return;
block|}
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|spacer
operator|.
name|spaces
argument_list|(
name|s
argument_list|)
expr_stmt|;
if|if
condition|(
name|withIdPrefix
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
name|rel
operator|.
name|getId
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
name|rel
operator|.
name|getRelTypeName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|detailLevel
operator|!=
name|SqlExplainLevel
operator|.
name|NO_ATTRIBUTES
condition|)
block|{
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|.
name|right
operator|instanceof
name|RelNode
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|j
operator|++
operator|==
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|s
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|s
operator|.
name|append
argument_list|(
name|value
operator|.
name|left
argument_list|)
operator|.
name|append
argument_list|(
literal|"=["
argument_list|)
operator|.
name|append
argument_list|(
name|value
operator|.
name|right
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|j
operator|>
literal|0
condition|)
block|{
name|s
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|detailLevel
condition|)
block|{
case|case
name|ALL_ATTRIBUTES
case|:
name|s
operator|.
name|append
argument_list|(
literal|": rowcount = "
argument_list|)
operator|.
name|append
argument_list|(
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|", cumulative cost = "
argument_list|)
operator|.
name|append
argument_list|(
name|RelMetadataQuery
operator|.
name|getCumulativeCost
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|detailLevel
condition|)
block|{
case|case
name|NON_COST_ATTRIBUTES
case|:
case|case
name|ALL_ATTRIBUTES
case|:
if|if
condition|(
operator|!
name|withIdPrefix
condition|)
block|{
comment|// If we didn't print the rel id at the start of the line, print
comment|// it at the end.
name|s
operator|.
name|append
argument_list|(
literal|", id = "
argument_list|)
operator|.
name|append
argument_list|(
name|rel
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
name|pw
operator|.
name|println
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|spacer
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|explainInputs
argument_list|(
name|inputs
argument_list|)
expr_stmt|;
name|spacer
operator|.
name|subtract
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|explainInputs
parameter_list|(
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|input
operator|.
name|explain
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|void
name|explain
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|valueList
parameter_list|)
block|{
name|explain_
argument_list|(
name|rel
argument_list|,
name|valueList
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlExplainLevel
name|getDetailLevel
parameter_list|()
block|{
return|return
name|detailLevel
return|;
block|}
specifier|public
name|RelWriter
name|input
parameter_list|(
name|String
name|term
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|term
argument_list|,
operator|(
name|Object
operator|)
name|input
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|RelWriter
name|item
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|term
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|RelWriter
name|itemIf
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|,
name|boolean
name|condition
parameter_list|)
block|{
if|if
condition|(
name|condition
condition|)
block|{
name|item
argument_list|(
name|term
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|RelWriter
name|done
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|values
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|left
operator|.
name|equals
argument_list|(
literal|"subset"
argument_list|)
condition|)
block|{
operator|++
name|i
expr_stmt|;
block|}
for|for
control|(
name|RelNode
name|input
range|:
name|node
operator|.
name|getInputs
argument_list|()
control|)
block|{
assert|assert
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|right
operator|==
name|input
assert|;
operator|++
name|i
expr_stmt|;
block|}
for|for
control|(
name|RexNode
name|expr
range|:
name|node
operator|.
name|getChildExps
argument_list|()
control|)
block|{
assert|assert
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|right
operator|==
name|expr
assert|;
operator|++
name|i
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|valuesCopy
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
name|explain_
argument_list|(
name|node
argument_list|,
name|valuesCopy
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|boolean
name|nest
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Converts the collected terms and values to a string. Does not write to    * the parent writer.    */
specifier|public
name|String
name|simple
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"("
argument_list|)
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|values
argument_list|)
control|)
block|{
if|if
condition|(
name|ord
operator|.
name|i
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|ord
operator|.
name|e
operator|.
name|left
argument_list|)
operator|.
name|append
argument_list|(
literal|"=["
argument_list|)
operator|.
name|append
argument_list|(
name|ord
operator|.
name|e
operator|.
name|right
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelWriterImpl.java
end_comment

end_unit

