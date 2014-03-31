begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|Schema
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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqConnection
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
name|prepare
operator|.
name|OptiqPrepareImpl
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
name|prepare
operator|.
name|PlannerImpl
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
name|server
operator|.
name|OptiqServerStatement
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
name|RelOptCluster
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
name|RelOptSchema
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
name|sql
operator|.
name|parser
operator|.
name|impl
operator|.
name|SqlParserImpl
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
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
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
comment|/**  * Tools for invoking Optiq functionality without initializing a container /  * server first.  */
end_comment

begin_class
specifier|public
class|class
name|Frameworks
block|{
specifier|private
name|Frameworks
parameter_list|()
block|{
block|}
comment|/**    * Creates an instance of {@code Planner}.    *    * @param lex The type of lexing the SqlParser should do.  Controls case rules    *     and quoted identifier syntax.    * @param schemaFactory Schema factory. Given a root schema, it creates and    *                      returns the schema that should be used to execute    *                      queries.    * @param operatorTable The instance of SqlOperatorTable that be should to    *     resolve Optiq operators.    * @param ruleSets An array of one or more rule sets used during the course of    *     query evaluation. The common use case is when there is a single rule    *     set and {@link net.hydromatic.optiq.tools.Planner#transform}    *     will only be called once. However, consumers may also register multiple    *     {@link net.hydromatic.optiq.tools.RuleSet}s and do multiple repetitions    *     of {@link Planner#transform} planning cycles using different indices.    *     The order of rule sets provided here determines the zero-based indices    *     of rule sets elsewhere in this class.    * @return The Planner object.    */
specifier|public
specifier|static
name|Planner
name|getPlanner
parameter_list|(
name|Lex
name|lex
parameter_list|,
name|Function1
argument_list|<
name|SchemaPlus
argument_list|,
name|Schema
argument_list|>
name|schemaFactory
parameter_list|,
name|SqlOperatorTable
name|operatorTable
parameter_list|,
name|RuleSet
modifier|...
name|ruleSets
parameter_list|)
block|{
return|return
name|getPlanner
argument_list|(
name|lex
argument_list|,
name|SqlParserImpl
operator|.
name|FACTORY
argument_list|,
name|schemaFactory
argument_list|,
name|operatorTable
argument_list|,
literal|null
argument_list|,
name|ruleSets
argument_list|)
return|;
block|}
comment|/**    * Creates an instance of {@code Planner}.    *    *<p>If {@code traitDefs} is specified, the planner first de-registers any    * existing {@link RelTraitDef}s, then registers the {@code RelTraitDef}s in    * this list.</p>    *    *<p>The order of {@code RelTraitDef}s in {@code traitDefs} matters if the    * planner is VolcanoPlanner. The planner calls {@link RelTraitDef#convert} in    * the order of this list. The most important trait comes first in the list,    * followed by the second most important one, etc.</p>    *    * @param lex The type of lexing the SqlParser should do.  Controls case rules    *     and quoted identifier syntax.    * @param parserFactory Parser factory creates and returns the SQL parser.    * @param schemaFactory Schema factory. Given a root schema, it creates and    *                      returns the schema that should be used to execute    *                      queries.    * @param operatorTable The instance of SqlOperatorTable that be should to    *     resolve Optiq operators.    * @param ruleSets An array of one or more rule sets used during the course of    *     query evaluation. The common use case is when there is a single rule    *     set and {@link net.hydromatic.optiq.tools.Planner#transform}    *     will only be called once. However, consumers may also register multiple    *     {@link net.hydromatic.optiq.tools.RuleSet}s and do multiple repetitions    *     of {@link Planner#transform} planning cycles using different indices.    *     The order of rule sets provided here determines the zero-based indices    *     of rule sets elsewhere in this class.    *  @param  traitDefs The list of RelTraitDef that would be registered with    *     planner, or null.    * @return The Planner object.    */
specifier|public
specifier|static
name|Planner
name|getPlanner
parameter_list|(
name|Lex
name|lex
parameter_list|,
name|SqlParserImplFactory
name|parserFactory
parameter_list|,
name|Function1
argument_list|<
name|SchemaPlus
argument_list|,
name|Schema
argument_list|>
name|schemaFactory
parameter_list|,
name|SqlOperatorTable
name|operatorTable
parameter_list|,
name|List
argument_list|<
name|RelTraitDef
argument_list|>
name|traitDefs
parameter_list|,
name|RuleSet
modifier|...
name|ruleSets
parameter_list|)
block|{
return|return
operator|new
name|PlannerImpl
argument_list|(
name|lex
argument_list|,
name|parserFactory
argument_list|,
name|schemaFactory
argument_list|,
name|operatorTable
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|ruleSets
argument_list|)
argument_list|,
name|traitDefs
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|traitDefs
argument_list|)
argument_list|)
return|;
block|}
comment|/** Piece of code to be run in a context where a planner is available. The    * planner is accessible from the {@code cluster} parameter, as are several    * other useful objects. */
specifier|public
interface|interface
name|PlannerAction
parameter_list|<
name|R
parameter_list|>
block|{
name|R
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|)
function_decl|;
block|}
comment|/** Piece of code to be run in a context where a planner and statement are    * available. The planner is accessible from the {@code cluster} parameter, as    * are several other useful objects. The connection and    * {@link net.hydromatic.optiq.DataContext} are accessible from the    * statement. */
specifier|public
interface|interface
name|PrepareAction
parameter_list|<
name|R
parameter_list|>
block|{
name|R
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|,
name|OptiqServerStatement
name|statement
parameter_list|)
function_decl|;
block|}
comment|/**    * Initializes a container then calls user-specified code with a planner.    *    * @param action Callback containing user-specified code    * @return Return value from action    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPlanner
parameter_list|(
specifier|final
name|PlannerAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
return|return
name|withPrepare
argument_list|(
operator|new
name|Frameworks
operator|.
name|PrepareAction
argument_list|<
name|R
argument_list|>
argument_list|()
block|{
specifier|public
name|R
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|,
name|OptiqServerStatement
name|statement
parameter_list|)
block|{
return|return
name|action
operator|.
name|apply
argument_list|(
name|cluster
argument_list|,
name|relOptSchema
argument_list|,
name|rootSchema
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
comment|/**    * Initializes a container then calls user-specified code with a planner    * and statement.    *    * @param action Callback containing user-specified code    * @return Return value from action    */
specifier|public
specifier|static
parameter_list|<
name|R
parameter_list|>
name|R
name|withPrepare
parameter_list|(
name|PrepareAction
argument_list|<
name|R
argument_list|>
name|action
parameter_list|)
block|{
try|try
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:optiq:"
argument_list|)
decl_stmt|;
name|OptiqConnection
name|optiqConnection
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|OptiqConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|OptiqServerStatement
name|statement
init|=
name|optiqConnection
operator|.
name|createStatement
argument_list|()
operator|.
name|unwrap
argument_list|(
name|OptiqServerStatement
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//noinspection deprecation
return|return
operator|new
name|OptiqPrepareImpl
argument_list|()
operator|.
name|perform
argument_list|(
name|statement
argument_list|,
name|action
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Frameworks.java
end_comment

end_unit

