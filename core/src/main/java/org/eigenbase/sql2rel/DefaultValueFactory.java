begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql2rel
package|;
end_package

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
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * DefaultValueFactory supplies default values for INSERT, UPDATE, and NEW.  *  *<p>TODO jvs 26-Feb-2005: rename this to InitializerExpressionFactory, since  * it is in the process of being generalized to handle constructor invocations  * and eventually generated columns.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|DefaultValueFactory
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Whether a column is always generated. If a column is always generated,      * then non-generated values cannot be inserted into the column.      */
specifier|public
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
comment|/**      * Creates an expression which evaluates to the default value for a      * particular column.      *      * @param table the table containing the column      * @param iColumn the 0-based offset of the column in the table      *      * @return default value expression      */
specifier|public
name|RexNode
name|newColumnDefaultValue
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|)
function_decl|;
comment|/**      * Creates an expression which evaluates to the initializer expression for a      * particular attribute of a structured type.      *      * @param type the structured type      * @param constructor the constructor invoked to initialize the type      * @param iAttribute the 0-based offset of the attribute in the type      * @param constructorArgs arguments passed to the constructor invocation      *      * @return default value expression      */
specifier|public
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
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End DefaultValueFactory.java
end_comment

end_unit

