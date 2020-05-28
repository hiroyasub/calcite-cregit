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
operator|.
name|ddl
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
name|SqlCreate
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
name|SqlIdentifier
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
name|SqlKind
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
name|SqlNode
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
name|SqlNodeList
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
name|SqlOperator
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
name|SqlSpecialOperator
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
name|SqlWriter
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
name|util
operator|.
name|ImmutableNullableList
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Parse tree for {@code CREATE MATERIALIZED VIEW} statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCreateMaterializedView
extends|extends
name|SqlCreate
block|{
specifier|public
specifier|final
name|SqlIdentifier
name|name
decl_stmt|;
specifier|public
specifier|final
annotation|@
name|Nullable
name|SqlNodeList
name|columnList
decl_stmt|;
specifier|public
specifier|final
name|SqlNode
name|query
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"CREATE MATERIALIZED VIEW"
argument_list|,
name|SqlKind
operator|.
name|CREATE_MATERIALIZED_VIEW
argument_list|)
decl_stmt|;
comment|/** Creates a SqlCreateView. */
name|SqlCreateMaterializedView
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|replace
parameter_list|,
name|boolean
name|ifNotExists
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
annotation|@
name|Nullable
name|SqlNodeList
name|columnList
parameter_list|,
name|SqlNode
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|OPERATOR
argument_list|,
name|pos
argument_list|,
name|replace
argument_list|,
name|ifNotExists
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
comment|// may be null
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"nullness"
argument_list|)
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
name|name
argument_list|,
name|columnList
argument_list|,
name|query
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
name|writer
operator|.
name|keyword
argument_list|(
literal|"CREATE"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"MATERIALIZED VIEW"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ifNotExists
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"IF NOT EXISTS"
argument_list|)
expr_stmt|;
block|}
name|name
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
name|columnList
operator|!=
literal|null
condition|)
block|{
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
for|for
control|(
name|SqlNode
name|c
range|:
name|columnList
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|c
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
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
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
name|query
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

