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
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  *  A<code>SqlTableRef</code> is a node of a parse tree which represents  *  a table reference.  *  *<p>It can be attached with a sql hint statement, see {@link SqlHint} for details.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTableRef
extends|extends
name|SqlCall
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlIdentifier
name|tableName
decl_stmt|;
specifier|private
specifier|final
name|SqlNodeList
name|hints
decl_stmt|;
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|SqlOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"TABLE_REF"
argument_list|,
name|SqlKind
operator|.
name|TABLE_REF
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
annotation|@
name|Nullable
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
return|return
operator|new
name|SqlTableRef
argument_list|(
name|pos
argument_list|,
operator|(
name|SqlIdentifier
operator|)
name|requireNonNull
argument_list|(
name|operands
index|[
literal|0
index|]
argument_list|,
literal|"tableName"
argument_list|)
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|requireNonNull
argument_list|(
name|operands
index|[
literal|1
index|]
argument_list|,
literal|"hints"
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlTableRef
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlIdentifier
name|tableName
parameter_list|,
name|SqlNodeList
name|hints
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableName
operator|=
name|tableName
expr_stmt|;
name|this
operator|.
name|hints
operator|=
name|hints
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
name|ImmutableList
operator|.
name|of
argument_list|(
name|tableName
argument_list|,
name|hints
argument_list|)
return|;
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
name|tableName
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
if|if
condition|(
name|this
operator|.
name|hints
operator|!=
literal|null
operator|&&
name|this
operator|.
name|hints
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|getDialect
argument_list|()
operator|.
name|unparseTableScanHints
argument_list|(
name|writer
argument_list|,
name|this
operator|.
name|hints
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

