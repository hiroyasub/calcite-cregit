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
name|model
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Element that describes a star schema and provides a framework for defining,  * recognizing, and recommending materialized views at various levels of  * aggregation.  *  *<p>Occurs within {@link JsonSchema#lattices}.  *  * @see JsonRoot Description of schema elements  */
end_comment

begin_class
specifier|public
class|class
name|JsonLattice
block|{
comment|/** The name of this lattice.    *    *<p>Required.    */
specifier|public
name|String
name|name
decl_stmt|;
comment|/** SQL query that defines the lattice.    *    *<p>Must be a string or a list of strings (which are concatenated into a    * multi-line SQL string, separated by newlines).    *    *<p>The structure of the SQL statement, and in particular the order of    * items in the FROM clause, defines the fact table, dimension tables, and    * join paths for this lattice.    */
specifier|public
name|Object
name|sql
decl_stmt|;
comment|/** Whether to materialize tiles on demand as queries are executed.    *    *<p>Optional; default is true.    */
specifier|public
name|boolean
name|auto
init|=
literal|true
decl_stmt|;
comment|/** Whether to use an optimization algorithm to suggest and populate an    * initial set of tiles.    *    *<p>Optional; default is false.    */
specifier|public
name|boolean
name|algorithm
init|=
literal|false
decl_stmt|;
comment|/** Maximum time (in milliseconds) to run the algorithm.    *    *<p>Optional; default is -1, meaning no timeout.    *    *<p>When the timeout is reached, Calcite uses the best result that has    * been obtained so far.    */
specifier|public
name|long
name|algorithmMaxMillis
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Estimated number of rows.    *    *<p>If null, Calcite will a query to find the real value. */
specifier|public
name|Double
name|rowCountEstimate
decl_stmt|;
comment|/** Name of a class that provides estimates of the number of distinct values    * in each column.    *    *<p>The class must implement the    * {@link org.apache.calcite.materialize.LatticeStatisticProvider} interface.    *    *<p>Or, you can use a class name plus a static field, for example    * "org.apache.calcite.materialize.Lattices#CACHING_SQL_STATISTIC_PROVIDER".    *    *<p>If not set, Calcite will generate and execute a SQL query to find the    * real value, and cache the results. */
specifier|public
name|String
name|statisticProvider
decl_stmt|;
comment|/** List of materialized aggregates to create up front. */
specifier|public
specifier|final
name|List
argument_list|<
name|JsonTile
argument_list|>
name|tiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** List of measures that a tile should have by default.    *    *<p>A tile can define its own measures, including measures not in this list.    *    *<p>Optional. The default list is just "count(*)".    */
specifier|public
name|List
argument_list|<
name|JsonMeasure
argument_list|>
name|defaultMeasures
decl_stmt|;
specifier|public
name|void
name|accept
parameter_list|(
name|ModelHandler
name|handler
parameter_list|)
block|{
name|handler
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"JsonLattice(name="
operator|+
name|name
operator|+
literal|", sql="
operator|+
name|getSql
argument_list|()
operator|+
literal|")"
return|;
block|}
comment|/** Returns the SQL query as a string, concatenating a list of lines if    * necessary. */
specifier|public
name|String
name|getSql
parameter_list|()
block|{
return|return
name|toString
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|/** Converts a string or a list of strings to a string. The list notation    * is a convenient way of writing long multi-line strings in JSON. */
specifier|static
name|String
name|toString
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
literal|null
condition|?
literal|null
else|:
name|o
operator|instanceof
name|String
condition|?
operator|(
name|String
operator|)
name|o
else|:
name|concatenate
argument_list|(
operator|(
name|List
operator|)
name|o
argument_list|)
return|;
block|}
comment|/** Converts a list of strings into a multi-line string. */
specifier|private
specifier|static
name|String
name|concatenate
parameter_list|(
name|List
name|list
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|String
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"each element of a string list must be a string; found: "
operator|+
name|o
argument_list|)
throw|;
block|}
name|buf
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|o
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|visitChildren
parameter_list|(
name|ModelHandler
name|modelHandler
parameter_list|)
block|{
for|for
control|(
name|JsonMeasure
name|jsonMeasure
range|:
name|defaultMeasures
control|)
block|{
name|jsonMeasure
operator|.
name|accept
argument_list|(
name|modelHandler
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|JsonTile
name|jsonTile
range|:
name|tiles
control|)
block|{
name|jsonTile
operator|.
name|accept
argument_list|(
name|modelHandler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JsonLattice.java
end_comment

end_unit

