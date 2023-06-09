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
name|sql2rel
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
name|RelDataType
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexNode
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
name|parser
operator|.
name|SqlParseException
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
name|SqlParser
import|;
end_import

begin_comment
comment|/**  * Provides context for {@link InitializerExpressionFactory} methods.  */
end_comment

begin_interface
specifier|public
interface|interface
name|InitializerContext
block|{
name|RexBuilder
name|getRexBuilder
parameter_list|()
function_decl|;
comment|/**    * Parses a column computation expression for a table.    *    *<p>Usually this expression is declared in the {@code CREATE TABLE}    * statement, e.g.    *    *<blockquote><pre>{@code    *   create table t(    *     a int not null,    *     b varchar(5) as (my_udf(a)) virtual,    *     c int not null as (a + 1));    * }</pre></blockquote>    *    *<p>You can use the string format expression "my_udf(a)" and "a + 1"    * as the initializer expression of column b and c.    *    *<p>Calcite doesn't really need this now because the DDL nodes    * can be executed directly from {@code SqlNode}s, but we still provide the way    * to initialize from a SQL-like string, because a string can be used to persist easily and    * the column expressions are important part of the table metadata.    *    * @param config parse config    * @param expr   the SQL-style column expression    * @return a {@code SqlNode} instance    */
specifier|default
name|SqlNode
name|parseExpression
parameter_list|(
name|SqlParser
operator|.
name|Config
name|config
parameter_list|,
name|String
name|expr
parameter_list|)
block|{
name|SqlParser
name|parser
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|expr
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|parser
operator|.
name|parseExpression
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to parse expression "
operator|+
name|expr
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Validate the expression with a base table row type. The expression may reference the fields    * of the row type defines.    *    * @param rowType the table row type    * @param expr    the expression    * @return a validated {@code SqlNode}, usually it transforms    * from a {@code SqlUnresolvedFunction} to a resolved one    */
name|SqlNode
name|validateExpression
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|SqlNode
name|expr
parameter_list|)
function_decl|;
comment|/**    * Converts a {@code SqlNode} to {@code RexNode}.    *    *<p>Caution that the {@code SqlNode} must be validated,    * you can use {@link #validateExpression} to validate if the {@code SqlNode}    * is un-validated.    *    * @param expr the expression of sql node to convert    * @return a converted {@code RexNode} instance    */
name|RexNode
name|convertExpression
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

