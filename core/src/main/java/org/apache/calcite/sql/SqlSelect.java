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
name|ImmutableNullableList
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
name|Objects
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * A<code>SqlSelect</code> is a node of a parse tree which represents a select  * statement. It warrants its own node type just because we have a lot of  * methods to put somewhere.  */
end_comment

begin_class
specifier|public
class|class
name|SqlSelect
extends|extends
name|SqlCall
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// constants representing operand positions
specifier|public
specifier|static
specifier|final
name|int
name|FROM_OPERAND
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|WHERE_OPERAND
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|HAVING_OPERAND
init|=
literal|5
decl_stmt|;
name|SqlNodeList
name|keywordList
decl_stmt|;
name|SqlNodeList
name|selectList
decl_stmt|;
name|SqlNode
name|from
decl_stmt|;
name|SqlNode
name|where
decl_stmt|;
name|SqlNodeList
name|groupBy
decl_stmt|;
name|SqlNode
name|having
decl_stmt|;
name|SqlNodeList
name|windowDecls
decl_stmt|;
name|SqlNodeList
name|orderBy
decl_stmt|;
name|SqlNode
name|offset
decl_stmt|;
name|SqlNode
name|fetch
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlSelect
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNodeList
name|keywordList
parameter_list|,
name|SqlNodeList
name|selectList
parameter_list|,
name|SqlNode
name|from
parameter_list|,
name|SqlNode
name|where
parameter_list|,
name|SqlNodeList
name|groupBy
parameter_list|,
name|SqlNode
name|having
parameter_list|,
name|SqlNodeList
name|windowDecls
parameter_list|,
name|SqlNodeList
name|orderBy
parameter_list|,
name|SqlNode
name|offset
parameter_list|,
name|SqlNode
name|fetch
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|keywordList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|keywordList
operator|!=
literal|null
condition|?
name|keywordList
else|:
operator|new
name|SqlNodeList
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|selectList
operator|=
name|selectList
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
name|this
operator|.
name|where
operator|=
name|where
expr_stmt|;
name|this
operator|.
name|groupBy
operator|=
name|groupBy
expr_stmt|;
name|this
operator|.
name|having
operator|=
name|having
expr_stmt|;
name|this
operator|.
name|windowDecls
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|windowDecls
operator|!=
literal|null
condition|?
name|windowDecls
else|:
operator|new
name|SqlNodeList
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|orderBy
operator|=
name|orderBy
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|fetch
operator|=
name|fetch
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|SqlSelectOperator
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|SELECT
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|ImmutableNullableList
operator|.
name|of
argument_list|(
name|keywordList
argument_list|,
name|selectList
argument_list|,
name|from
argument_list|,
name|where
argument_list|,
name|groupBy
argument_list|,
name|having
argument_list|,
name|windowDecls
argument_list|,
name|orderBy
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|keywordList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
operator|(
name|SqlNodeList
operator|)
name|operand
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|selectList
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|from
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|where
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|groupBy
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|having
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|6
case|:
name|windowDecls
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
operator|(
name|SqlNodeList
operator|)
name|operand
argument_list|)
expr_stmt|;
break|break;
case|case
literal|7
case|:
name|orderBy
operator|=
operator|(
name|SqlNodeList
operator|)
name|operand
expr_stmt|;
break|break;
case|case
literal|8
case|:
name|offset
operator|=
name|operand
expr_stmt|;
break|break;
case|case
literal|9
case|:
name|fetch
operator|=
name|operand
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|i
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|final
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
name|getModifierNode
argument_list|(
name|SqlSelectKeyword
operator|.
name|DISTINCT
argument_list|)
operator|!=
literal|null
return|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getModifierNode
parameter_list|(
name|SqlSelectKeyword
name|modifier
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|keyword
range|:
name|keywordList
control|)
block|{
name|SqlSelectKeyword
name|keyword2
init|=
operator|(
operator|(
name|SqlLiteral
operator|)
name|keyword
operator|)
operator|.
name|symbolValue
argument_list|(
name|SqlSelectKeyword
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyword2
operator|==
name|modifier
condition|)
block|{
return|return
name|keyword
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getFrom
parameter_list|()
block|{
return|return
name|from
return|;
block|}
specifier|public
name|void
name|setFrom
parameter_list|(
name|SqlNode
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
block|}
specifier|public
specifier|final
name|SqlNodeList
name|getGroup
parameter_list|()
block|{
return|return
name|groupBy
return|;
block|}
specifier|public
name|void
name|setGroupBy
parameter_list|(
name|SqlNodeList
name|groupBy
parameter_list|)
block|{
name|this
operator|.
name|groupBy
operator|=
name|groupBy
expr_stmt|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getHaving
parameter_list|()
block|{
return|return
name|having
return|;
block|}
specifier|public
name|void
name|setHaving
parameter_list|(
name|SqlNode
name|having
parameter_list|)
block|{
name|this
operator|.
name|having
operator|=
name|having
expr_stmt|;
block|}
specifier|public
specifier|final
name|SqlNodeList
name|getSelectList
parameter_list|()
block|{
return|return
name|selectList
return|;
block|}
specifier|public
name|void
name|setSelectList
parameter_list|(
name|SqlNodeList
name|selectList
parameter_list|)
block|{
name|this
operator|.
name|selectList
operator|=
name|selectList
expr_stmt|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getWhere
parameter_list|()
block|{
return|return
name|where
return|;
block|}
specifier|public
name|void
name|setWhere
parameter_list|(
name|SqlNode
name|whereClause
parameter_list|)
block|{
name|this
operator|.
name|where
operator|=
name|whereClause
expr_stmt|;
block|}
annotation|@
name|Nonnull
specifier|public
specifier|final
name|SqlNodeList
name|getWindowList
parameter_list|()
block|{
return|return
name|windowDecls
return|;
block|}
specifier|public
specifier|final
name|SqlNodeList
name|getOrderList
parameter_list|()
block|{
return|return
name|orderBy
return|;
block|}
specifier|public
name|void
name|setOrderBy
parameter_list|(
name|SqlNodeList
name|orderBy
parameter_list|)
block|{
name|this
operator|.
name|orderBy
operator|=
name|orderBy
expr_stmt|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|public
name|void
name|setOffset
parameter_list|(
name|SqlNode
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|public
specifier|final
name|SqlNode
name|getFetch
parameter_list|()
block|{
return|return
name|fetch
return|;
block|}
specifier|public
name|void
name|setFetch
parameter_list|(
name|SqlNode
name|fetch
parameter_list|)
block|{
name|this
operator|.
name|fetch
operator|=
name|fetch
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
name|validateQuery
argument_list|(
name|this
argument_list|,
name|scope
argument_list|,
name|validator
operator|.
name|getUnknownType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Override SqlCall, to introduce a sub-query frame.
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|writer
operator|.
name|inQuery
argument_list|()
condition|)
block|{
comment|// If this SELECT is the topmost item in a sub-query, introduce a new
comment|// frame. (The topmost item in the sub-query might be a UNION or
comment|// ORDER. In this case, we don't need a wrapper frame.)
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
name|SUB_QUERY
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|hasOrderBy
parameter_list|()
block|{
return|return
name|orderBy
operator|!=
literal|null
operator|&&
name|orderBy
operator|.
name|size
argument_list|()
operator|!=
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasWhere
parameter_list|()
block|{
return|return
name|where
operator|!=
literal|null
return|;
block|}
specifier|public
name|boolean
name|isKeywordPresent
parameter_list|(
name|SqlSelectKeyword
name|targetKeyWord
parameter_list|)
block|{
return|return
name|getModifierNode
argument_list|(
name|targetKeyWord
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlSelect.java
end_comment

end_unit

