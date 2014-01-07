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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|logging
operator|.
name|Logger
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
name|RelImplementor
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
name|RelDataTypeFactory
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
name|RexBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|trace
operator|.
name|EigenbaseTrace
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
name|Util
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link RelImplementor}.  */
end_comment

begin_class
specifier|public
class|class
name|RelImplementorImpl
implements|implements
name|RelImplementor
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|tracer
init|=
name|EigenbaseTrace
operator|.
name|getRelImplementorTracer
argument_list|()
decl_stmt|;
comment|/**    * Maps a {@link String} to the {@link RelImplementorImpl.Frame} whose    * {@link Frame#rel}.correlVariable == correlName.    */
specifier|protected
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Frame
argument_list|>
name|mapCorrel2Frame
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Frame
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * Maps a {@link org.eigenbase.rel.RelNode} to the unique frame whose    * {@link RelImplementorImpl.Frame#rel} is    * that relational expression.    */
specifier|protected
specifier|final
name|Map
argument_list|<
name|RelNode
argument_list|,
name|Frame
argument_list|>
name|mapRel2Frame
init|=
operator|new
name|HashMap
argument_list|<
name|RelNode
argument_list|,
name|Frame
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|public
name|RelImplementorImpl
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
block|}
specifier|public
name|RexBuilder
name|getRexBuilder
parameter_list|()
block|{
return|return
name|rexBuilder
return|;
block|}
specifier|public
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|Object
name|visitChild
parameter_list|(
name|RelNode
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
assert|assert
name|child
operator|==
name|parent
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
assert|;
block|}
name|createFrame
argument_list|(
name|parent
argument_list|,
name|ordinal
argument_list|,
name|child
argument_list|)
expr_stmt|;
return|return
name|visitChildInternal
argument_list|(
name|child
argument_list|,
name|ordinal
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|protected
name|void
name|createFrame
parameter_list|(
name|RelNode
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
name|Frame
name|frame
init|=
operator|new
name|Frame
argument_list|()
decl_stmt|;
name|frame
operator|.
name|rel
operator|=
name|child
expr_stmt|;
name|frame
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|frame
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|mapRel2Frame
operator|.
name|put
argument_list|(
name|child
argument_list|,
name|frame
argument_list|)
expr_stmt|;
name|String
name|correl
init|=
name|child
operator|.
name|getCorrelVariable
argument_list|()
decl_stmt|;
if|if
condition|(
name|correl
operator|!=
literal|null
condition|)
block|{
comment|// Record that this frame is responsible for setting this
comment|// variable. But if another frame is already doing the job --
comment|// this frame's parent, which belongs to the same set -- don't
comment|// override it.
if|if
condition|(
name|mapCorrel2Frame
operator|.
name|get
argument_list|(
name|correl
argument_list|)
operator|==
literal|null
condition|)
block|{
name|mapCorrel2Frame
operator|.
name|put
argument_list|(
name|correl
argument_list|,
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Object
name|visitChildInternal
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|Object
name|arg
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|protected
name|RelNode
name|findInputRel
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|findInputRel
argument_list|(
name|rel
argument_list|,
name|offset
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
return|;
block|}
specifier|private
name|RelNode
name|findInputRel
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
index|[]
name|offsets
parameter_list|)
block|{
if|if
condition|(
name|rel
operator|instanceof
name|JoinRel
condition|)
block|{
comment|// no variable here -- go deeper
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelNode
name|result
init|=
name|findInputRel
argument_list|(
name|inputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|offset
argument_list|,
name|offsets
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
block|}
if|else if
condition|(
name|offset
operator|==
name|offsets
index|[
literal|0
index|]
condition|)
block|{
return|return
name|rel
return|;
block|}
else|else
block|{
name|offsets
index|[
literal|0
index|]
operator|++
expr_stmt|;
block|}
return|return
literal|null
return|;
comment|// not found
block|}
comment|/**    * Returns a list of the relational expressions which are ancestors of the    * current one.    *    * @pre // rel must be on the implementation stack    */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getAncestorRels
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|ancestorList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
name|Frame
name|frame
init|=
name|mapRel2Frame
operator|.
name|get
argument_list|(
name|rel
argument_list|)
decl_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|frame
operator|!=
literal|null
argument_list|,
literal|"rel must be on the current implementation stack"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|ancestorList
operator|.
name|add
argument_list|(
name|frame
operator|.
name|rel
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|parentRel
init|=
name|frame
operator|.
name|parent
decl_stmt|;
if|if
condition|(
name|parentRel
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|frame
operator|=
name|mapRel2Frame
operator|.
name|get
argument_list|(
name|parentRel
argument_list|)
expr_stmt|;
name|Util
operator|.
name|permAssert
argument_list|(
name|frame
operator|!=
literal|null
argument_list|,
literal|"ancestor rel must have frame"
argument_list|)
expr_stmt|;
block|}
return|return
name|ancestorList
return|;
block|}
specifier|protected
specifier|static
class|class
name|Frame
block|{
comment|/**      *<code>rel</code>'s parent      */
specifier|public
name|RelNode
name|parent
decl_stmt|;
comment|/**      * relation which is being implemented in this frame      */
specifier|public
name|RelNode
name|rel
decl_stmt|;
comment|/**      * ordinal of<code>rel</code> within<code>parent</code>      */
specifier|public
name|int
name|ordinal
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelImplementorImpl.java
end_comment

end_unit

