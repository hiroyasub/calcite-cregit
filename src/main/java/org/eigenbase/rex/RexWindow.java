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
name|io
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

begin_comment
comment|/**  * Specification of the window of rows over which a {@link RexOver} windowed  * aggregate is evaluated.  *  *<p>Treat it as immutable!  *  * @author jhyde  * @version $Id$  * @since Dec 6, 2004  */
end_comment

begin_class
specifier|public
class|class
name|RexWindow
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|RexNode
index|[]
name|partitionKeys
decl_stmt|;
specifier|public
specifier|final
name|RexNode
index|[]
name|orderKeys
decl_stmt|;
specifier|private
specifier|final
name|SqlNode
name|lowerBound
decl_stmt|;
specifier|private
specifier|final
name|SqlNode
name|upperBound
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|physical
decl_stmt|;
specifier|private
specifier|final
name|String
name|digest
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a window.      *      *<p>If you need to create a window from outside this package, use {@link      * RexBuilder#makeOver}.      */
name|RexWindow
parameter_list|(
name|RexNode
index|[]
name|partitionKeys
parameter_list|,
name|RexNode
index|[]
name|orderKeys
parameter_list|,
name|SqlNode
name|lowerBound
parameter_list|,
name|SqlNode
name|upperBound
parameter_list|,
name|boolean
name|physical
parameter_list|)
block|{
assert|assert
name|partitionKeys
operator|!=
literal|null
assert|;
assert|assert
name|orderKeys
operator|!=
literal|null
assert|;
name|this
operator|.
name|partitionKeys
operator|=
name|partitionKeys
expr_stmt|;
name|this
operator|.
name|orderKeys
operator|=
name|orderKeys
expr_stmt|;
name|this
operator|.
name|lowerBound
operator|=
name|lowerBound
expr_stmt|;
name|this
operator|.
name|upperBound
operator|=
name|upperBound
expr_stmt|;
name|this
operator|.
name|physical
operator|=
name|physical
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|computeDigest
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|physical
condition|)
block|{
assert|assert
name|orderKeys
operator|.
name|length
operator|>
literal|0
operator|:
literal|"logical window requires sort key"
assert|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|digest
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|that
operator|instanceof
name|RexWindow
condition|)
block|{
name|RexWindow
name|window
init|=
operator|(
name|RexWindow
operator|)
name|that
decl_stmt|;
return|return
name|digest
operator|.
name|equals
argument_list|(
name|window
operator|.
name|digest
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
name|computeDigest
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|int
name|clauseCount
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|partitionKeys
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|clauseCount
operator|++
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|print
argument_list|(
literal|"PARTITION BY "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|partitionKeys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|partitionKey
init|=
name|partitionKeys
index|[
name|i
index|]
decl_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|partitionKey
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|orderKeys
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|clauseCount
operator|++
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|print
argument_list|(
literal|"ORDER BY "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|orderKeys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|orderKey
init|=
name|orderKeys
index|[
name|i
index|]
decl_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|orderKey
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lowerBound
operator|==
literal|null
condition|)
block|{
comment|// No ROWS or RANGE clause
block|}
if|else if
condition|(
name|upperBound
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|clauseCount
operator|++
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|physical
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|"ROWS "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pw
operator|.
name|print
argument_list|(
literal|"RANGE "
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|print
argument_list|(
name|lowerBound
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|clauseCount
operator|++
operator|>
literal|0
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|physical
condition|)
block|{
name|pw
operator|.
name|print
argument_list|(
literal|"ROWS BETWEEN "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pw
operator|.
name|print
argument_list|(
literal|"RANGE BETWEEN "
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|print
argument_list|(
name|lowerBound
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
literal|" AND "
argument_list|)
expr_stmt|;
name|pw
operator|.
name|print
argument_list|(
name|upperBound
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|getLowerBound
parameter_list|()
block|{
return|return
name|lowerBound
return|;
block|}
specifier|public
name|SqlNode
name|getUpperBound
parameter_list|()
block|{
return|return
name|upperBound
return|;
block|}
specifier|public
name|boolean
name|isRows
parameter_list|()
block|{
return|return
name|physical
return|;
block|}
specifier|public
name|SqlWindowOperator
operator|.
name|OffsetRange
name|getOffsetAndRange
parameter_list|()
block|{
return|return
name|SqlWindowOperator
operator|.
name|getOffsetAndRange
argument_list|(
name|getLowerBound
argument_list|()
argument_list|,
name|getUpperBound
argument_list|()
argument_list|,
name|isRows
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexWindow.java
end_comment

end_unit

