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
name|plan
operator|.
name|RelOptTable
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
name|schema
operator|.
name|ColumnStrategy
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
name|SqlFunction
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
name|function
operator|.
name|BiFunction
import|;
end_import

begin_comment
comment|/**  * InitializerExpressionFactory supplies default values for INSERT, UPDATE, and NEW.  */
end_comment

begin_interface
specifier|public
interface|interface
name|InitializerExpressionFactory
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Whether a column is always generated. If a column is always generated,    * then non-generated values cannot be inserted into the column.    *    * @see #generationStrategy(RelOptTable, int)    *    * @deprecated Use {@code c.generationStrategy(t, i) == VIRTUAL    * || c.generationStrategy(t, i) == STORED}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|boolean
name|isGeneratedAlways
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|)
function_decl|;
comment|/**    * Returns how a column is populated.    *    * @param table   the table containing the column    * @param iColumn the 0-based offset of the column in the table    *    * @return generation strategy, never null    *    * @see RelOptTable#getColumnStrategies()    */
name|ColumnStrategy
name|generationStrategy
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|)
function_decl|;
comment|/**    * Creates an expression which evaluates to the default value for a    * particular column.    *    *<p>If the default value comes from a un-validated {@link org.apache.calcite.sql.SqlNode},    * make sure to invoke {@link InitializerContext#validateExpression} first before you actually    * do the conversion with method {@link InitializerContext#convertExpression}.    *    * @param table   the table containing the column    * @param iColumn the 0-based offset of the column in the table    * @param context Context for creating the expression    *    * @return default value expression    */
name|RexNode
name|newColumnDefaultValue
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|,
name|InitializerContext
name|context
parameter_list|)
function_decl|;
comment|/**    * Creates a hook function to customize the relational expression right after the column    * expressions are converted. Usually the relational expression is a projection    * above a table scan.    *    * @return a hook function to transform the relational expression    * right after the column expression conversion to a customized one    *    * @see #newColumnDefaultValue(RelOptTable, int, InitializerContext)    */
annotation|@
name|Nullable
name|BiFunction
argument_list|<
name|InitializerContext
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|postExpressionConversionHook
parameter_list|()
function_decl|;
comment|/**    * Creates an expression which evaluates to the initializer expression for a    * particular attribute of a structured type.    *    * @param type            the structured type    * @param constructor     the constructor invoked to initialize the type    * @param iAttribute      the 0-based offset of the attribute in the type    * @param constructorArgs arguments passed to the constructor invocation    * @param context Context for creating the expression    *    * @return default value expression    */
name|RexNode
name|newAttributeInitializer
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlFunction
name|constructor
parameter_list|,
name|int
name|iAttribute
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|constructorArgs
parameter_list|,
name|InitializerContext
name|context
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

