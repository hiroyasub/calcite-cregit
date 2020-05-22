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
name|babel
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
name|ddl
operator|.
name|SqlCreateTable
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

begin_comment
comment|/**  * Parse tree for {@code CREATE TABLE} statement, with extensions for particular  * SQL dialects supported by Babel.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBabelCreateTable
extends|extends
name|SqlCreateTable
block|{
specifier|private
specifier|final
name|TableCollectionType
name|tableCollectionType
decl_stmt|;
comment|// CHECKSTYLE: IGNORE 2; can't use 'volatile' because it is a Java keyword
comment|// but checkstyle does not like trailing '_'.
specifier|private
specifier|final
name|boolean
name|volatile_
decl_stmt|;
comment|/** Creates a SqlBabelCreateTable. */
specifier|public
name|SqlBabelCreateTable
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|replace
parameter_list|,
name|TableCollectionType
name|tableCollectionType
parameter_list|,
name|boolean
name|volatile_
parameter_list|,
name|boolean
name|ifNotExists
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|,
name|SqlNodeList
name|columnList
parameter_list|,
name|SqlNode
name|query
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|,
name|replace
argument_list|,
name|ifNotExists
argument_list|,
name|name
argument_list|,
name|columnList
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|tableCollectionType
operator|=
name|tableCollectionType
expr_stmt|;
name|this
operator|.
name|volatile_
operator|=
name|volatile_
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
literal|"CREATE"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|tableCollectionType
condition|)
block|{
case|case
name|SET
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"SET"
argument_list|)
expr_stmt|;
break|break;
case|case
name|MULTISET
case|:
name|writer
operator|.
name|keyword
argument_list|(
literal|"MULTISET"
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
if|if
condition|(
name|volatile_
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"VOLATILE"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|keyword
argument_list|(
literal|"TABLE"
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
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
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
block|}
end_class

end_unit
