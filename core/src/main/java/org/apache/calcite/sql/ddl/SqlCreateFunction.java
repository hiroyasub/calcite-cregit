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
name|SqlLiteral
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
name|Pair
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
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * Parse tree for {@code CREATE FUNCTION} statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlCreateFunction
extends|extends
name|SqlCreate
block|{
specifier|private
specifier|final
name|SqlIdentifier
name|name
decl_stmt|;
specifier|private
specifier|final
name|SqlNode
name|className
decl_stmt|;
specifier|private
specifier|final
name|SqlNodeList
name|usingList
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlSpecialOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"CREATE FUNCTION"
argument_list|,
name|SqlKind
operator|.
name|CREATE_FUNCTION
argument_list|)
decl_stmt|;
comment|/** Creates a SqlCreateFunction. */
specifier|public
name|SqlCreateFunction
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
name|SqlNode
name|className
parameter_list|,
name|SqlNodeList
name|usingList
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
argument_list|,
literal|"name"
argument_list|)
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
name|this
operator|.
name|usingList
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|usingList
argument_list|,
literal|"usingList"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|usingList
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|0
argument_list|)
expr_stmt|;
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
name|getReplace
argument_list|()
condition|?
literal|"CREATE OR REPLACE"
else|:
literal|"CREATE"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"FUNCTION"
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
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
name|className
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
name|usingList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"USING"
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
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|SIMPLE
argument_list|)
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|SqlLiteral
argument_list|,
name|SqlLiteral
argument_list|>
name|using
range|:
name|pairs
argument_list|()
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|using
operator|.
name|left
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
comment|// FILE, URL or ARCHIVE
name|using
operator|.
name|right
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
comment|// e.g. 'file:foo/bar.jar'
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlLiteral
argument_list|,
name|SqlLiteral
argument_list|>
argument_list|>
name|pairs
parameter_list|()
block|{
return|return
name|Util
operator|.
name|pairs
argument_list|(
operator|(
name|List
operator|)
name|usingList
argument_list|)
return|;
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
name|Arrays
operator|.
name|asList
argument_list|(
name|name
argument_list|,
name|className
argument_list|,
name|usingList
argument_list|)
return|;
block|}
block|}
end_class

end_unit

