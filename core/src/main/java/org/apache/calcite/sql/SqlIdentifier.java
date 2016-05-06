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
name|sql
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
name|rel
operator|.
name|type
operator|.
name|DynamicRecordType
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
name|parser
operator|.
name|SqlParserPos
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
name|util
operator|.
name|SqlVisitor
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
name|validate
operator|.
name|SqlMonotonicity
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
name|validate
operator|.
name|SqlQualified
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
name|validate
operator|.
name|SqlValidator
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
name|validate
operator|.
name|SqlValidatorScope
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
name|Litmus
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
name|Util
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
name|base
operator|.
name|Function
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
name|Lists
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
comment|/**  * A<code>SqlIdentifier</code> is an identifier, possibly compound.  */
end_comment

begin_class
specifier|public
class|class
name|SqlIdentifier
extends|extends
name|SqlNode
block|{
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|STAR_TO_EMPTY
init|=
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|?
literal|""
else|:
name|s
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|EMPTY_TO_STAR
init|=
operator|new
name|Function
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|?
literal|"*"
else|:
name|s
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|?
literal|"\"*\""
else|:
name|s
return|;
block|}
block|}
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Array of the components of this compound identifier.    *    *<p>The empty string represents the wildcard "*",    * to distinguish it from a real "*" (presumably specified using quotes).    *    *<p>It's convenient to have this member public, and it's convenient to    * have this member not-final, but it's a shame it's public and not-final.    * If you assign to this member, please use    * {@link #setNames(java.util.List, java.util.List)}.    * And yes, we'd like to make identifiers immutable one day.    */
specifier|public
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|names
decl_stmt|;
comment|/**    * This identifier's collation (if any).    */
specifier|final
name|SqlCollation
name|collation
decl_stmt|;
comment|/**    * A list of the positions of the components of compound identifiers.    */
specifier|protected
name|ImmutableList
argument_list|<
name|SqlParserPos
argument_list|>
name|componentPositions
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a compound identifier, for example<code>foo.bar</code>.    *    * @param names Parts of the identifier, length&ge; 1    */
specifier|public
name|SqlIdentifier
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlCollation
name|collation
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|List
argument_list|<
name|SqlParserPos
argument_list|>
name|componentPositions
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|names
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|this
operator|.
name|collation
operator|=
name|collation
expr_stmt|;
name|this
operator|.
name|componentPositions
operator|=
name|componentPositions
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|componentPositions
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
assert|assert
name|name
operator|!=
literal|null
assert|;
block|}
block|}
specifier|public
name|SqlIdentifier
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|names
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a simple identifier, for example<code>foo</code>, with a    * collation.    */
specifier|public
name|SqlIdentifier
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlCollation
name|collation
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|name
argument_list|)
argument_list|,
name|collation
argument_list|,
name|pos
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a simple identifier, for example<code>foo</code>.    */
specifier|public
name|SqlIdentifier
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|name
argument_list|)
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an identifier that is a singleton wildcard star. */
specifier|public
specifier|static
name|SqlIdentifier
name|star
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
name|star
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
literal|""
argument_list|)
argument_list|,
name|pos
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|pos
argument_list|)
argument_list|)
return|;
block|}
comment|/** Creates an identifier that ends in a wildcard star. */
specifier|public
specifier|static
name|SqlIdentifier
name|star
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|List
argument_list|<
name|SqlParserPos
argument_list|>
name|componentPositions
parameter_list|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|names
argument_list|,
name|STAR_TO_EMPTY
argument_list|)
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|,
name|componentPositions
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|IDENTIFIER
return|;
block|}
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|names
argument_list|,
name|collation
argument_list|,
name|pos
argument_list|,
name|componentPositions
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Util
operator|.
name|sepList
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|names
argument_list|,
name|EMPTY_TO_STAR
argument_list|)
argument_list|,
literal|"."
argument_list|)
return|;
block|}
comment|/**    * Modifies the components of this identifier and their positions.    *    * @param names Names of components    * @param poses Positions of components    */
specifier|public
name|void
name|setNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|List
argument_list|<
name|SqlParserPos
argument_list|>
name|poses
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|names
argument_list|)
expr_stmt|;
name|this
operator|.
name|componentPositions
operator|=
name|poses
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|poses
argument_list|)
expr_stmt|;
block|}
comment|/** Returns an identifier that is the same as this except one modified name.    * Does not modify this identifier. */
specifier|public
name|SqlIdentifier
name|setName
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|String
index|[]
name|nameArray
init|=
name|names
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|names
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|nameArray
index|[
name|i
index|]
operator|=
name|name
expr_stmt|;
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|nameArray
argument_list|)
argument_list|,
name|collation
argument_list|,
name|pos
argument_list|,
name|componentPositions
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
comment|/** Returns an identifier that is the same as this except with a component    * added at a given position. Does not modify this identifier. */
specifier|public
name|SqlIdentifier
name|add
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|name
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names2
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|names2
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|name
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|SqlParserPos
argument_list|>
name|pos2
decl_stmt|;
if|if
condition|(
name|componentPositions
operator|==
literal|null
condition|)
block|{
name|pos2
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|pos2
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|componentPositions
argument_list|)
expr_stmt|;
name|pos2
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|names2
argument_list|,
name|collation
argument_list|,
name|pos
argument_list|,
name|pos2
argument_list|)
return|;
block|}
comment|/**    * Returns the position of the<code>i</code>th component of a compound    * identifier, or the position of the whole identifier if that information    * is not present.    *    * @param i Ordinal of component.    * @return Position of i'th component    */
specifier|public
name|SqlParserPos
name|getComponentParserPosition
parameter_list|(
name|int
name|i
parameter_list|)
block|{
assert|assert
operator|(
name|i
operator|>=
literal|0
operator|)
operator|&&
operator|(
name|i
operator|<
name|names
operator|.
name|size
argument_list|()
operator|)
assert|;
return|return
operator|(
name|componentPositions
operator|==
literal|null
operator|)
condition|?
name|getParserPosition
argument_list|()
else|:
name|componentPositions
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/**    * Copies names and components from another identifier. Does not modify the    * cross-component parser position.    *    * @param other identifier from which to copy    */
specifier|public
name|void
name|assignNamesFrom
parameter_list|(
name|SqlIdentifier
name|other
parameter_list|)
block|{
name|setNames
argument_list|(
name|other
operator|.
name|names
argument_list|,
name|other
operator|.
name|componentPositions
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates an identifier which contains only the<code>ordinal</code>th    * component of this compound identifier. It will have the correct    * {@link SqlParserPos}, provided that detailed position information is    * available.    */
specifier|public
name|SqlIdentifier
name|getComponent
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|getComponent
argument_list|(
name|ordinal
argument_list|,
name|ordinal
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|public
name|SqlIdentifier
name|getComponent
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|to
parameter_list|)
block|{
specifier|final
name|SqlParserPos
name|pos
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|SqlParserPos
argument_list|>
name|pos2
decl_stmt|;
if|if
condition|(
name|componentPositions
operator|==
literal|null
condition|)
block|{
name|pos2
operator|=
literal|null
expr_stmt|;
name|pos
operator|=
name|this
operator|.
name|pos
expr_stmt|;
block|}
else|else
block|{
name|pos2
operator|=
name|componentPositions
operator|.
name|subList
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
expr_stmt|;
name|pos
operator|=
name|SqlParserPos
operator|.
name|sum
argument_list|(
name|pos2
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|names
operator|.
name|subList
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|,
name|collation
argument_list|,
name|pos
argument_list|,
name|pos2
argument_list|)
return|;
block|}
comment|/**    * Creates an identifier that consists of this identifier plus a name segment.    * Does not modify this identifier.    */
specifier|public
name|SqlIdentifier
name|plus
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|names
init|=
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|names
argument_list|)
operator|.
name|add
argument_list|(
name|name
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|SqlParserPos
argument_list|>
name|componentPositions
decl_stmt|;
specifier|final
name|SqlParserPos
name|pos2
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|componentPositions
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|SqlParserPos
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|componentPositions
operator|=
name|builder
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|componentPositions
argument_list|)
operator|.
name|add
argument_list|(
name|pos
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|pos2
operator|=
name|SqlParserPos
operator|.
name|sum
argument_list|(
name|builder
operator|.
name|add
argument_list|(
name|this
operator|.
name|pos
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|componentPositions
operator|=
literal|null
expr_stmt|;
name|pos2
operator|=
name|pos
expr_stmt|;
block|}
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|names
argument_list|,
name|collation
argument_list|,
name|pos2
argument_list|,
name|componentPositions
argument_list|)
return|;
block|}
comment|/** Creates an identifier that consists of all but the last {@code n}    * name segments of this one. */
specifier|public
name|SqlIdentifier
name|skipLast
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|getComponent
argument_list|(
literal|0
argument_list|,
name|names
operator|.
name|size
argument_list|()
operator|-
name|n
argument_list|)
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|IDENTIFIER
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|identifier
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|null
operator|!=
name|collation
condition|)
block|{
name|collation
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validateIdentifier
argument_list|(
name|this
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|validateExpr
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
comment|// First check for builtin functions which don't have parentheses,
comment|// like "LOCALTIME".
name|SqlCall
name|call
init|=
name|SqlUtil
operator|.
name|makeCall
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
name|validator
operator|.
name|validateCall
argument_list|(
name|call
argument_list|,
name|scope
argument_list|)
expr_stmt|;
return|return;
block|}
name|validator
operator|.
name|validateIdentifier
argument_list|(
name|this
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|SqlIdentifier
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
return|;
block|}
name|SqlIdentifier
name|that
init|=
operator|(
name|SqlIdentifier
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|names
operator|.
name|size
argument_list|()
operator|!=
name|that
operator|.
name|names
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
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
name|names
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|that
operator|.
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
return|;
block|}
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|SqlCollation
name|getCollation
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
specifier|public
name|String
name|getSimple
parameter_list|()
block|{
assert|assert
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Returns whether this identifier is a star, such as "*" or "foo.bar.*".    */
specifier|public
name|boolean
name|isStar
parameter_list|()
block|{
return|return
name|Util
operator|.
name|last
argument_list|(
name|names
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/**    * Returns whether this is a simple identifier. "FOO" is simple; "*",    * "FOO.*" and "FOO.BAR" are not.    */
specifier|public
name|boolean
name|isSimple
parameter_list|()
block|{
return|return
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|!
name|isStar
argument_list|()
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
comment|// for "star" column, whether it's static or dynamic return not_monotonic directly.
if|if
condition|(
name|Util
operator|.
name|last
argument_list|(
name|names
argument_list|)
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|||
name|DynamicRecordType
operator|.
name|isDynamicStarColName
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|names
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
comment|// First check for builtin functions which don't have parentheses,
comment|// like "LOCALTIME".
specifier|final
name|SqlValidator
name|validator
init|=
name|scope
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|SqlCall
name|call
init|=
name|SqlUtil
operator|.
name|makeCall
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
return|return
name|call
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
return|;
block|}
specifier|final
name|SqlQualified
name|qualified
init|=
name|scope
operator|.
name|fullyQualify
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|final
name|SqlIdentifier
name|fqId
init|=
name|qualified
operator|.
name|identifier
decl_stmt|;
return|return
name|qualified
operator|.
name|namespace
operator|.
name|resolve
argument_list|()
operator|.
name|getMonotonicity
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|fqId
operator|.
name|names
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlIdentifier.java
end_comment

end_unit

