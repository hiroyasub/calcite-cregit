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
name|tools
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
name|plan
operator|.
name|RelTraitSet
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
name|rel
operator|.
name|RelNode
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
name|rel
operator|.
name|RelRoot
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|SourceStringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_comment
comment|/**  * A fa&ccedil;ade that covers Calcite's query planning process: parse SQL,  * validate the parse tree, convert the parse tree to a relational expression,  * and optimize the relational expression.  *  *<p>Planner is NOT thread safe. However, it can be reused for  * different queries. The consumer of this interface is responsible for calling  * reset() after each use of Planner that corresponds to a different  * query.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Planner
extends|extends
name|AutoCloseable
block|{
comment|/**    * Parses and validates a SQL statement.    *    * @param sql The SQL statement to parse.    * @return The root node of the SQL parse tree.    * @throws org.apache.calcite.sql.parser.SqlParseException on parse error    */
specifier|default
name|SqlNode
name|parse
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
return|return
name|parse
argument_list|(
operator|new
name|SourceStringReader
argument_list|(
name|sql
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Parses and validates a SQL statement.    *    * @param source A reader which will provide the SQL statement to parse.    *    * @return The root node of the SQL parse tree.    * @throws org.apache.calcite.sql.parser.SqlParseException on parse error    */
name|SqlNode
name|parse
parameter_list|(
name|Reader
name|source
parameter_list|)
throws|throws
name|SqlParseException
function_decl|;
comment|/**    * Validates a SQL statement.    *    * @param sqlNode Root node of the SQL parse tree.    * @return Validated node    * @throws ValidationException if not valid    */
name|SqlNode
name|validate
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|)
throws|throws
name|ValidationException
function_decl|;
comment|/**    * Validates a SQL statement.    *    * @param sqlNode Root node of the SQL parse tree.    * @return Validated node and its validated type.    * @throws ValidationException if not valid    */
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|RelDataType
argument_list|>
name|validateAndGetType
parameter_list|(
name|SqlNode
name|sqlNode
parameter_list|)
throws|throws
name|ValidationException
function_decl|;
comment|/**    * Returns a record type that contains the name and type of each parameter.    * Returns a record type with no fields if there are no parameters.    *    * @return Record type    */
name|RelDataType
name|getParameterRowType
parameter_list|()
function_decl|;
comment|/**    * Converts a SQL parse tree into a tree of relational expressions.    *    *<p>You must call {@link #validate(org.apache.calcite.sql.SqlNode)} first.    *    * @param sql The root node of the SQL parse tree.    * @return The root node of the newly generated RelNode tree.    * @throws org.apache.calcite.tools.RelConversionException if the node    * cannot be converted or has not been validated    */
name|RelRoot
name|rel
parameter_list|(
name|SqlNode
name|sql
parameter_list|)
throws|throws
name|RelConversionException
function_decl|;
comment|// CHECKSTYLE: IGNORE 1
comment|/** @deprecated Use {@link #rel}. */
annotation|@
name|Deprecated
comment|// to removed before 2.0
name|RelNode
name|convert
parameter_list|(
name|SqlNode
name|sql
parameter_list|)
throws|throws
name|RelConversionException
function_decl|;
comment|/** Returns the type factory. */
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Converts one relational expression tree into another relational expression    * based on a particular rule set and requires set of traits.    *    * @param ruleSetIndex The RuleSet to use for conversion purposes.  Note that    *                     this is zero-indexed and is based on the list and order    *                     of RuleSets provided in the construction of this    *                     Planner.    * @param requiredOutputTraits The set of RelTraits required of the root node    *                             at the termination of the planning cycle.    * @param rel The root of the RelNode tree to convert.    * @return The root of the new RelNode tree.    * @throws org.apache.calcite.tools.RelConversionException on conversion    *     error    */
name|RelNode
name|transform
parameter_list|(
name|int
name|ruleSetIndex
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|RelNode
name|rel
parameter_list|)
throws|throws
name|RelConversionException
function_decl|;
comment|/**    * Resets this {@code Planner} to be used with a new query. This    * should be called between each new query.    */
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Releases all internal resources utilized while this {@code Planner}    * exists.  Once called, this Planner object is no longer valid.    */
annotation|@
name|Override
name|void
name|close
parameter_list|()
function_decl|;
name|RelTraitSet
name|getEmptyTraitSet
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

