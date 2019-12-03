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
name|jdbc
operator|.
name|CalcitePrepare
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
name|jdbc
operator|.
name|CalciteSchema
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
name|SqlDrop
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
name|SqlExecutableStatement
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
name|SqlUtil
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Parse tree for {@code DROP TABLE} statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlDropSchema
extends|extends
name|SqlDrop
implements|implements
name|SqlExecutableStatement
block|{
specifier|private
specifier|final
name|boolean
name|foreign
decl_stmt|;
specifier|private
specifier|final
name|SqlIdentifier
name|name
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
literal|"DROP SCHEMA"
argument_list|,
name|SqlKind
operator|.
name|DROP_TABLE
argument_list|)
decl_stmt|;
comment|/** Creates a SqlDropSchema. */
name|SqlDropSchema
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|foreign
parameter_list|,
name|boolean
name|ifExists
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|OPERATOR
argument_list|,
name|pos
argument_list|,
name|ifExists
argument_list|)
expr_stmt|;
name|this
operator|.
name|foreign
operator|=
name|foreign
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
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
name|SqlLiteral
operator|.
name|createBoolean
argument_list|(
name|foreign
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|name
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
literal|"DROP"
argument_list|)
expr_stmt|;
if|if
condition|(
name|foreign
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"FOREIGN"
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|keyword
argument_list|(
literal|"SCHEMA"
argument_list|)
expr_stmt|;
if|if
condition|(
name|ifExists
condition|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"IF EXISTS"
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
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|path
init|=
name|context
operator|.
name|getDefaultSchemaPath
argument_list|()
decl_stmt|;
name|CalciteSchema
name|schema
init|=
name|context
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|p
range|:
name|path
control|)
block|{
name|schema
operator|=
name|schema
operator|.
name|getSubSchema
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|final
name|boolean
name|existed
init|=
name|schema
operator|.
name|removeSubSchema
argument_list|(
name|name
operator|.
name|getSimple
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|existed
operator|&&
operator|!
name|ifExists
condition|)
block|{
throw|throw
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|name
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|RESOURCE
operator|.
name|schemaNotFound
argument_list|(
name|name
operator|.
name|getSimple
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

