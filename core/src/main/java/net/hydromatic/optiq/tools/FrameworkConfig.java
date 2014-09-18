begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|config
operator|.
name|Lex
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
name|Context
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
name|RelOptCostFactory
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
name|RelTraitDef
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
name|RelDataTypeSystem
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
name|SqlOperatorTable
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
name|parser
operator|.
name|SqlParserImplFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql2rel
operator|.
name|SqlRexConvertletTable
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

begin_comment
comment|/**  * Interface that describes how to configure planning sessions generated  * using the Frameworks tools.  *  * @see Frameworks#newConfigBuilder()  */
end_comment

begin_interface
specifier|public
interface|interface
name|FrameworkConfig
block|{
comment|/**    * The type of lexical analysis the SqlParser should do.  Controls case rules    * and quoted identifier syntax.    */
name|Lex
name|getLex
parameter_list|()
function_decl|;
comment|/**    * Provides the parser factory that creates the SqlParser used in parsing    * queries.    */
name|SqlParserImplFactory
name|getParserFactory
parameter_list|()
function_decl|;
comment|/**    * Returns the default schema that should be checked before looking at the    * root schema.  Returns null to only consult the root schema.    */
name|SchemaPlus
name|getDefaultSchema
parameter_list|()
function_decl|;
comment|/**    * Returns a list of one or more programs used during the course of query    * evaluation.    *    *<p>The common use case is when there is a single program    * created using {@link Programs#of(RuleSet)}    * and {@link net.hydromatic.optiq.tools.Planner#transform}    * will only be called once.    *    *<p>However, consumers may also create programs    * not based on rule sets, register multiple programs,    * and do multiple repetitions    * of {@link Planner#transform} planning cycles using different indices.    *    *<p>The order of programs provided here determines the zero-based indices    * of programs elsewhere in this class.    */
name|ImmutableList
argument_list|<
name|Program
argument_list|>
name|getPrograms
parameter_list|()
function_decl|;
comment|/**    * Returns operator table that should be used to    * resolve functions and operators during query validation.    */
name|SqlOperatorTable
name|getOperatorTable
parameter_list|()
function_decl|;
comment|/**    * Returns the cost factory that should be used when creating the planner.    * If null, use the default cost factory for that planner.    */
name|RelOptCostFactory
name|getCostFactory
parameter_list|()
function_decl|;
comment|/**    * Returns a list of trait definitions.    *    *<p>If the list is not null, the planner first de-registers any    * existing {@link RelTraitDef}s, then registers the {@code RelTraitDef}s in    * this list.</p>    *    *<p>The order of {@code RelTraitDef}s in the list matters if the    * planner is VolcanoPlanner. The planner calls {@link RelTraitDef#convert} in    * the order of this list. The most important trait comes first in the list,    * followed by the second most important one, etc.</p>    */
name|ImmutableList
argument_list|<
name|RelTraitDef
argument_list|>
name|getTraitDefs
parameter_list|()
function_decl|;
comment|/**    * Returns the convertlet table that should be used when converting from SQL    * to row expressions    */
name|SqlRexConvertletTable
name|getConvertletTable
parameter_list|()
function_decl|;
comment|/**    * Returns the PlannerContext that should be made available during planning by    * calling {@link org.eigenbase.relopt.RelOptPlanner#getContext()}.    */
name|Context
name|getContext
parameter_list|()
function_decl|;
comment|/**    * Returns the type system.    */
name|RelDataTypeSystem
name|getTypeSystem
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End FrameworkConfig.java
end_comment

end_unit

