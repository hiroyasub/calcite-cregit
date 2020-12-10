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
name|util
operator|.
name|SqlBasicVisitor
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
name|util
operator|.
name|ImmutableNullableList
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * Parse tree node that represents UNPIVOT applied to a table reference  * (or sub-query).  *  *<p>Syntax:  *<blockquote><pre>{@code  * SELECT *  * FROM query  * UNPIVOT [ { INCLUDE | EXCLUDE } NULLS ] (  *   columns FOR columns IN ( columns [ AS values ], ...))  *  * where:  *  * columns: column  *        | '(' column, ... ')'  * values:  value  *        | '(' value, ... ')'  * }</pre></blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|SqlUnpivot
extends|extends
name|SqlCall
block|{
specifier|public
name|SqlNode
name|query
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|includeNulls
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|measureList
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|axisList
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|inList
decl_stmt|;
specifier|static
specifier|final
name|Operator
name|OPERATOR
init|=
operator|new
name|Operator
argument_list|(
name|SqlKind
operator|.
name|UNPIVOT
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlUnpivot
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
name|query
parameter_list|,
name|boolean
name|includeNulls
parameter_list|,
name|SqlNodeList
name|measureList
parameter_list|,
name|SqlNodeList
name|axisList
parameter_list|,
name|SqlNodeList
name|inList
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|includeNulls
operator|=
name|includeNulls
expr_stmt|;
name|this
operator|.
name|measureList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|measureList
argument_list|)
expr_stmt|;
name|this
operator|.
name|axisList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|axisList
argument_list|)
expr_stmt|;
name|this
operator|.
name|inList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|inList
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
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
name|query
argument_list|,
name|measureList
argument_list|,
name|axisList
argument_list|,
name|inList
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"nullness"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
name|operand
parameter_list|)
block|{
comment|// Only 'query' is mutable. (It is required for validation.)
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|query
operator|=
name|operand
expr_stmt|;
break|break;
default|default:
name|super
operator|.
name|setOperand
argument_list|(
name|i
argument_list|,
name|operand
argument_list|)
expr_stmt|;
block|}
block|}
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
name|query
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"UNPIVOT"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|includeNulls
condition|?
literal|"INCLUDE NULLS"
else|:
literal|"EXCLUDE NULLS"
argument_list|)
expr_stmt|;
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
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
comment|// force parentheses if there is more than one foo
specifier|final
name|int
name|leftPrec1
init|=
name|measureList
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|measureList
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"FOR"
argument_list|)
expr_stmt|;
comment|// force parentheses if there is more than one axis
specifier|final
name|int
name|leftPrec2
init|=
name|axisList
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|axisList
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec2
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"IN"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|list
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|PARENTHESES
argument_list|,
name|SqlWriter
operator|.
name|COMMA
argument_list|,
name|SqlPivot
operator|.
name|stripList
argument_list|(
name|inList
argument_list|)
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
comment|/** Returns the measure list as SqlIdentifiers. */
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
specifier|public
name|void
name|forEachMeasure
parameter_list|(
name|Consumer
argument_list|<
name|SqlIdentifier
argument_list|>
name|consumer
parameter_list|)
block|{
operator|(
operator|(
name|List
argument_list|<
name|SqlIdentifier
argument_list|>
operator|)
operator|(
name|List
operator|)
name|measureList
operator|)
operator|.
name|forEach
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
block|}
comment|/** Returns contents of the IN clause {@code (nodeList, valueList)} pairs.    * {@code valueList} is null if the entry has no {@code AS} clause. */
specifier|public
name|void
name|forEachNameValues
parameter_list|(
name|BiConsumer
argument_list|<
name|SqlNodeList
argument_list|,
annotation|@
name|Nullable
name|SqlNodeList
argument_list|>
name|consumer
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|node
range|:
name|inList
control|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|AS
case|:
specifier|final
name|SqlCall
name|call
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
assert|assert
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
assert|;
specifier|final
name|SqlNodeList
name|nodeList
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlNodeList
name|valueList
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|nodeList
argument_list|,
name|valueList
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|SqlNodeList
name|nodeList2
init|=
operator|(
name|SqlNodeList
operator|)
name|node
decl_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|nodeList2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Returns the set of columns that are referenced in the {@code FOR}    * clause. All columns that are not used will be part of the returned row. */
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|usedColumnNames
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|columnNames
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlVisitor
argument_list|<
name|Void
argument_list|>
name|nameCollector
init|=
operator|new
name|SqlBasicVisitor
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
name|columnNames
operator|.
name|add
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|id
operator|.
name|names
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|visit
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|forEachNameValues
argument_list|(
parameter_list|(
name|aliasList
parameter_list|,
name|valueList
parameter_list|)
lambda|->
name|aliasList
operator|.
name|accept
argument_list|(
name|nameCollector
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|columnNames
return|;
block|}
comment|/** Computes an alias. In the query fragment    *<blockquote>    *   {@code UNPIVOT ... FOR ... IN ((c1, c2) AS 'c1_c2', (c3, c4))}    *</blockquote>    * note that {@code (c3, c4)} has no {@code AS}. The computed alias is    * 'C3_C4'. */
specifier|public
specifier|static
name|String
name|aliasValue
parameter_list|(
name|SqlNodeList
name|aliasList
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|aliasList
operator|.
name|forEach
argument_list|(
name|alias
lambda|->
block|{
if|if
condition|(
name|b
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|'_'
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
name|Util
operator|.
name|last
argument_list|(
operator|(
operator|(
name|SqlIdentifier
operator|)
name|alias
operator|)
operator|.
name|names
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Unpivot operator. */
specifier|static
class|class
name|Operator
extends|extends
name|SqlSpecialOperator
block|{
name|Operator
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
