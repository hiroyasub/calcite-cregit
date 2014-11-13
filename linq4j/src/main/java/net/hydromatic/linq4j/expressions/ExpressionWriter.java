begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
package|;
end_package

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
name|*
import|;
end_import

begin_comment
comment|/**  * Converts an expression to Java code.  */
end_comment

begin_class
class|class
name|ExpressionWriter
block|{
specifier|static
specifier|final
name|Indent
name|INDENT
init|=
operator|new
name|Indent
argument_list|(
literal|20
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|int
name|level
decl_stmt|;
specifier|private
name|String
name|indent
init|=
literal|""
decl_stmt|;
specifier|private
name|boolean
name|indentPending
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|generics
decl_stmt|;
specifier|public
name|ExpressionWriter
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExpressionWriter
parameter_list|(
name|boolean
name|generics
parameter_list|)
block|{
name|this
operator|.
name|generics
operator|=
name|generics
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|Node
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|Expression
condition|)
block|{
name|Expression
name|expression1
init|=
operator|(
name|Expression
operator|)
name|expression
decl_stmt|;
name|expression1
operator|.
name|accept
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * If parentheses are required, writes this expression out with    * parentheses and returns true. If they are not required, does nothing    * and returns false.    */
specifier|public
name|boolean
name|requireParentheses
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
if|if
condition|(
name|lprec
operator|<
name|expression
operator|.
name|nodeType
operator|.
name|lprec
operator|&&
name|expression
operator|.
name|nodeType
operator|.
name|rprec
operator|>=
name|rprec
condition|)
block|{
return|return
literal|false
return|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**    * Increases the indentation level.    */
specifier|public
name|void
name|begin
parameter_list|()
block|{
name|indent
operator|=
name|INDENT
operator|.
name|get
argument_list|(
operator|++
name|level
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decreases the indentation level.    */
specifier|public
name|void
name|end
parameter_list|()
block|{
name|indent
operator|=
name|INDENT
operator|.
name|get
argument_list|(
operator|--
name|level
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExpressionWriter
name|newlineAndIndent
parameter_list|()
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|indentPending
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|indent
parameter_list|()
block|{
name|buf
operator|.
name|append
argument_list|(
name|indent
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|begin
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|begin
argument_list|()
expr_stmt|;
name|indentPending
operator|=
name|s
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|end
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|end
argument_list|()
expr_stmt|;
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|indentPending
operator|=
name|s
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|append
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|checkIndent
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|append
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|checkIndent
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|generics
condition|)
block|{
name|type
operator|=
name|Types
operator|.
name|stripGenerics
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|Types
operator|.
name|className
argument_list|(
name|type
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|append
parameter_list|(
name|AbstractNode
name|o
parameter_list|)
block|{
name|o
operator|.
name|accept0
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|append
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|checkIndent
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionWriter
name|append
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|checkIndent
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|void
name|checkIndent
parameter_list|()
block|{
if|if
condition|(
name|indentPending
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|indent
argument_list|)
expr_stmt|;
name|indentPending
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|public
name|StringBuilder
name|getBuf
parameter_list|()
block|{
name|checkIndent
argument_list|()
expr_stmt|;
return|return
name|buf
return|;
block|}
specifier|public
name|ExpressionWriter
name|list
parameter_list|(
name|String
name|begin
parameter_list|,
name|String
name|sep
parameter_list|,
name|String
name|end
parameter_list|,
name|Iterable
argument_list|<
name|?
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|?
argument_list|>
name|iterator
init|=
name|list
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|begin
argument_list|(
name|begin
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Object
name|o
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|Expression
condition|)
block|{
operator|(
operator|(
name|Expression
operator|)
name|o
operator|)
operator|.
name|accept
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|MemberDeclaration
condition|)
block|{
operator|(
operator|(
name|MemberDeclaration
operator|)
name|o
operator|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Type
condition|)
block|{
name|append
argument_list|(
operator|(
name|Type
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
break|break;
block|}
name|buf
operator|.
name|append
argument_list|(
name|sep
argument_list|)
expr_stmt|;
if|if
condition|(
name|sep
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|indentPending
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|end
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
name|begin
operator|.
name|endsWith
argument_list|(
literal|"\n"
argument_list|)
condition|)
block|{
name|begin
operator|=
name|begin
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|begin
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|begin
argument_list|)
operator|.
name|append
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|void
name|backUp
parameter_list|()
block|{
if|if
condition|(
name|buf
operator|.
name|lastIndexOf
argument_list|(
literal|"\n"
argument_list|)
operator|==
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|delete
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|buf
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|indentPending
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/** Helps generate strings of spaces, to indent text. */
specifier|private
specifier|static
class|class
name|Indent
extends|extends
name|ArrayList
argument_list|<
name|String
argument_list|>
block|{
specifier|public
name|Indent
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
name|ensureSize
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|String
name|of
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|ensureSize
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
return|return
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|private
name|void
name|ensureSize
parameter_list|(
name|int
name|targetSize
parameter_list|)
block|{
if|if
condition|(
name|targetSize
operator|<
name|size
argument_list|()
condition|)
block|{
return|return;
block|}
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
literal|2
operator|*
name|targetSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|chars
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
name|String
name|bigString
init|=
operator|new
name|String
argument_list|(
name|chars
argument_list|)
decl_stmt|;
name|clear
argument_list|()
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
name|targetSize
condition|;
name|i
operator|++
control|)
block|{
name|add
argument_list|(
name|bigString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End ExpressionWriter.java
end_comment

end_unit

