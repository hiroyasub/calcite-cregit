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
name|SqlCall
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
name|SqlCollation
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
name|SqlDataTypeSpec
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Parse tree for SqlAttributeDefinition,  * which is part of a {@link SqlCreateType}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAttributeDefinition
extends|extends
name|SqlCall
block|{
specifier|private
specifier|static
specifier|final
name|SqlSpecialOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"ATTRIBUTE_DEF"
argument_list|,
name|SqlKind
operator|.
name|ATTRIBUTE_DEF
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|SqlIdentifier
name|name
decl_stmt|;
specifier|public
specifier|final
name|SqlDataTypeSpec
name|dataType
decl_stmt|;
specifier|final
name|SqlNode
name|expression
decl_stmt|;
specifier|final
name|SqlCollation
name|collation
decl_stmt|;
comment|/** Creates a SqlAttributeDefinition; use {@link SqlDdlNodes#attribute}. */
name|SqlAttributeDefinition
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
name|SqlDataTypeSpec
name|dataType
parameter_list|,
name|SqlNode
name|expression
parameter_list|,
name|SqlCollation
name|collation
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|dataType
operator|=
name|dataType
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|collation
operator|=
name|collation
expr_stmt|;
block|}
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
name|name
argument_list|,
name|dataType
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
name|name
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
name|dataType
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
if|if
condition|(
name|collation
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"COLLATE"
argument_list|)
expr_stmt|;
name|collation
operator|.
name|unparse
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dataType
operator|.
name|getNullable
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|dataType
operator|.
name|getNullable
argument_list|()
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"NOT NULL"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expression
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"DEFAULT"
argument_list|)
expr_stmt|;
name|exp
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO: refactor this to a util class to share with SqlColumnDeclaration
specifier|private
name|void
name|exp
parameter_list|(
name|SqlWriter
name|writer
parameter_list|)
block|{
if|if
condition|(
name|writer
operator|.
name|isAlwaysUseParentheses
argument_list|()
condition|)
block|{
name|expression
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
else|else
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|expression
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
name|writer
operator|.
name|sep
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

