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
name|config
package|;
end_package

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
name|MoreObjects
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
name|ImmutableSet
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Function
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
name|IntPredicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * A Calcite specific system property that is used to configure various aspects of the framework.  *  *<p>Calcite system properties must always be in the "calcite" root namespace.</p>  *  * @param<T> the type of the property value  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CalciteSystemProperty
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Holds all system properties related with the Calcite.    *    *<p>Deprecated<code>"saffron.properties"</code> (in namespaces"saffron" and "net.sf.saffron")    * are also kept here but under "calcite" namespace.</p>    */
specifier|private
specifier|static
specifier|final
name|Properties
name|PROPERTIES
init|=
name|loadProperties
argument_list|()
decl_stmt|;
comment|/**    * Whether to run Calcite in debug mode.    *    *<p>When debug mode is activated significantly more information is gathered and printed to    * STDOUT. It is most commonly used to print and identify problems in generated java code. Debug    * mode is also used to perform more verifications at runtime, which are not performed during    * normal execution.</p>    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|DEBUG
init|=
name|booleanProperty
argument_list|(
literal|"calcite.debug"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Whether to exploit join commutative property.    */
comment|// TODO review zabetak:
comment|// Does the property control join commutativity or rather join associativity? The property is
comment|// associated with {@link org.apache.calcite.rel.rules.JoinAssociateRule} and not with
comment|// {@link org.apache.calcite.rel.rules.JoinCommuteRule}.
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|COMMUTE
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.join.commute"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/** Whether to enable the collation trait in the default planner configuration.    *    *<p>Some extra optimizations are possible if enabled, but queries should    * work either way. At some point this will become a preference, or we will    * run multiple phases: first disabled, then enabled. */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENABLE_COLLATION_TRAIT
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.collation.trait"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/** Whether the enumerable convention is enabled in the default planner configuration. */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENABLE_ENUMERABLE
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.enumerable"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/** Whether the EnumerableTableScan should support ARRAY fields. */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENUMERABLE_ENABLE_TABLESCAN_ARRAY
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.enumerable.tablescan.array"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/** Whether the EnumerableTableScan should support MAP fields. */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENUMERABLE_ENABLE_TABLESCAN_MAP
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.enumerable.tablescan.map"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/** Whether the EnumerableTableScan should support MULTISET fields. */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENUMERABLE_ENABLE_TABLESCAN_MULTISET
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.enumerable.tablescan.multiset"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/** Whether streaming is enabled in the default planner configuration. */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENABLE_STREAM
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.stream"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether RexNode digest should be normalized (e.g. call operands ordered).    *<p>Normalization helps to treat $0=$1 and $1=$0 expressions equal, thus it saves efforts    * on planning.</p> */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ENABLE_REX_DIGEST_NORMALIZE
init|=
name|booleanProperty
argument_list|(
literal|"calcite.enable.rexnode.digest.normalize"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    *  Whether to follow the SQL standard strictly.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|STRICT
init|=
name|booleanProperty
argument_list|(
literal|"calcite.strict.sql"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Whether to include a GraphViz representation when dumping the state of the Volcano planner.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|DUMP_GRAPHVIZ
init|=
name|booleanProperty
argument_list|(
literal|"calcite.volcano.dump.graphviz"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to include<code>RelSet</code> information when dumping the state of the Volcano    * planner.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|DUMP_SETS
init|=
name|booleanProperty
argument_list|(
literal|"calcite.volcano.dump.sets"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to enable top-down optimization. This config can be overridden    * by {@link CalciteConnectionProperty#TOPDOWN_OPT}.    *    *<p>Note: Enabling top-down optimization will automatically disable    * the use of AbstractConverter and related rules.</p>    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TOPDOWN_OPT
init|=
name|booleanProperty
argument_list|(
literal|"calcite.planner.topdown.opt"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Whether to run integration tests.    */
comment|// TODO review zabetak:
comment|// The property is used in only one place and it is associated with mongodb. Should we drop this
comment|// property and just use TEST_MONGODB?
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|INTEGRATION_TEST
init|=
name|booleanProperty
argument_list|(
literal|"calcite.integrationTest"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Which database to use for tests that require a JDBC data source.    *    *<p>The property can take one of the following values:    *    *<ul>    *<li>HSQLDB (default)</li>    *<li>H2</li>    *<li>MYSQL</li>    *<li>ORACLE</li>    *<li>POSTGRESQL</li>    *</ul>    *    *<p>If the specified value is not included in the previous list, the default    * is used.    *    *<p>We recommend that casual users use hsqldb, and frequent Calcite    * developers use MySQL. The test suite runs faster against the MySQL database    * (mainly because of the 0.1 second versus 6 seconds startup time). You have    * to populate MySQL manually with the foodmart data set, otherwise there will    * be test failures.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|TEST_DB
init|=
name|stringProperty
argument_list|(
literal|"calcite.test.db"
argument_list|,
literal|"HSQLDB"
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"HSQLDB"
argument_list|,
literal|"H2"
argument_list|,
literal|"MYSQL"
argument_list|,
literal|"ORACLE"
argument_list|,
literal|"POSTGRESQL"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Path to the dataset file that should used for integration tests.    *    *<p>If a path is not set, then one of the following values will be used:    *    *<ul>    *<li>../calcite-test-dataset</li>    *<li>../../calcite-test-dataset</li>    *<li>.</li>    *</ul>    * The first valid path that exists in the filesystem will be chosen.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|TEST_DATASET_PATH
init|=
operator|new
name|CalciteSystemProperty
argument_list|<>
argument_list|(
literal|"calcite.test.dataset"
argument_list|,
name|v
lambda|->
block|{
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
return|return
name|v
return|;
block|}
specifier|final
name|String
index|[]
name|dirs
init|=
block|{
literal|"../calcite-test-dataset"
operator|,
literal|"../../calcite-test-dataset"
block|}
empty_stmt|;
for|for
control|(
name|String
name|s
range|:
name|dirs
control|)
block|{
if|if
condition|(
operator|new
name|File
argument_list|(
name|s
argument_list|)
operator|.
name|exists
argument_list|()
operator|&&
operator|new
name|File
argument_list|(
name|s
argument_list|,
literal|"vm"
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
return|return
literal|"."
return|;
block|}
argument_list|)
decl_stmt|;
comment|/**    * Whether to run MongoDB tests.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_MONGODB
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.mongodb"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to run Splunk tests.    *    *<p>Disabled by default, because we do not expect Splunk to be installed    * and populated with the data set necessary for testing.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_SPLUNK
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.splunk"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Whether to run Druid tests.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_DRUID
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.druid"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|/**    * Whether to run Cassandra tests.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_CASSANDRA
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.cassandra"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to run InnoDB tests.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_INNODB
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.innodb"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to run Redis tests.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_REDIS
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.redis"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * Whether to use Docker containers (https://www.testcontainers.org/) in tests.    *    * If the property is set to<code>true</code>, affected tests will attempt to start Docker    * containers; when Docker is not available tests fallback to other execution modes and if it's    * not possible they are skipped entirely.    *    * If the property is set to<code>false</code>, Docker containers are not used at all and    * affected tests either fallback to other execution modes or skipped entirely.    *    * Users can override the default behavior to force non-Dockerized execution even when Docker    * is installed on the machine; this can be useful for replicating an issue that appears only in    * non-docker test mode or for running tests both with and without containers in CI.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|TEST_WITH_DOCKER_CONTAINER
init|=
name|booleanProperty
argument_list|(
literal|"calcite.test.docker"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * A list of ids designating the queries    * (from query.json in new.hydromatic:foodmart-queries:0.4.1)    * that should be run as part of FoodmartTest.    */
comment|// TODO review zabetak:
comment|// The name of the property is not appropriate. A better alternative would be
comment|// calcite.test.foodmart.queries.ids. Moreover, I am not in favor of using system properties for
comment|// parameterized tests.
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
annotation|@
name|Nullable
name|String
argument_list|>
name|TEST_FOODMART_QUERY_IDS
init|=
operator|new
name|CalciteSystemProperty
argument_list|<>
argument_list|(
literal|"calcite.ids"
argument_list|,
name|Function
operator|.
expr|<@
name|Nullable
name|String
operator|>
name|identity
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Whether the optimizer will consider adding converters of infinite cost in    * order to convert a relational expression from one calling convention to    * another.    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|ALLOW_INFINITE_COST_CONVERTERS
init|=
name|booleanProperty
argument_list|(
literal|"calcite.opt.allowInfiniteCostConverters"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**    * The name of the default character set.    *    *<p>It is used by {@link org.apache.calcite.sql.validate.SqlValidator}.    */
comment|// TODO review zabetak:
comment|// What happens if a wrong value is specified?
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|DEFAULT_CHARSET
init|=
name|stringProperty
argument_list|(
literal|"calcite.default.charset"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
comment|/**    * The name of the default national character set.    *    *<p>It is used with the N'string' construct in    * {@link org.apache.calcite.sql.SqlLiteral#SqlLiteral}    * and may be different from the {@link #DEFAULT_CHARSET}.    */
comment|// TODO review zabetak:
comment|// What happens if a wrong value is specified?
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|DEFAULT_NATIONAL_CHARSET
init|=
name|stringProperty
argument_list|(
literal|"calcite.default.nationalcharset"
argument_list|,
literal|"ISO-8859-1"
argument_list|)
decl_stmt|;
comment|/**    * The name of the default collation.    *    *<p>It is used in {@link org.apache.calcite.sql.SqlCollation} and    * {@link org.apache.calcite.sql.SqlLiteral#SqlLiteral}.    */
comment|// TODO review zabetak:
comment|// What happens if a wrong value is specified?
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|DEFAULT_COLLATION
init|=
name|stringProperty
argument_list|(
literal|"calcite.default.collation.name"
argument_list|,
literal|"ISO-8859-1$en_US"
argument_list|)
decl_stmt|;
comment|/**    * The strength of the default collation.    * Allowed values (as defined in {@link java.text.Collator}) are: primary, secondary,    * tertiary, identical.    *    *<p>It is used in {@link org.apache.calcite.sql.SqlCollation} and    * {@link org.apache.calcite.sql.SqlLiteral#SqlLiteral}.</p>    */
comment|// TODO review zabetak:
comment|// What happens if a wrong value is specified?
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|DEFAULT_COLLATION_STRENGTH
init|=
name|stringProperty
argument_list|(
literal|"calcite.default.collation.strength"
argument_list|,
literal|"primary"
argument_list|)
decl_stmt|;
comment|/**    * The maximum size of the cache of metadata handlers.    *    *<p>A typical value is the number of queries being concurrently prepared multiplied by the    * number of types of metadata.</p>    *    *<p>If the value is less than 0, there is no limit.</p>    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Integer
argument_list|>
name|METADATA_HANDLER_CACHE_MAXIMUM_SIZE
init|=
name|intProperty
argument_list|(
literal|"calcite.metadata.handler.cache.maximum.size"
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
comment|/**    * The maximum size of the cache used for storing Bindable objects, instantiated via    * dynamically generated Java classes.    *    *<p>The default value is 0.</p>    *    *<p>The property can take any value between [0, {@link Integer#MAX_VALUE}] inclusive. If the    * value is not valid (or not specified) then the default value is used.</p>    *    *<p>The cached objects may be quite big so it is suggested to use a rather small cache size    * (e.g., 1000). For the most common use cases a number close to 1000 should be enough to    * alleviate the performance penalty of compiling and loading classes.</p>    *    *<p>Setting this property to 0 disables the cache.</p>    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Integer
argument_list|>
name|BINDABLE_CACHE_MAX_SIZE
init|=
name|intProperty
argument_list|(
literal|"calcite.bindable.cache.maxSize"
argument_list|,
literal|0
argument_list|,
name|v
lambda|->
name|v
operator|>=
literal|0
operator|&&
name|v
operator|<=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|/**    * The concurrency level of the cache used for storing Bindable objects, instantiated via    * dynamically generated Java classes.    *    *<p>The default value is 1.</p>    *    *<p>The property can take any value between [1, {@link Integer#MAX_VALUE}] inclusive. If the    * value is not valid (or not specified) then the default value is used.</p>    *    *<p>This property has no effect if the cache is disabled (i.e., {@link #BINDABLE_CACHE_MAX_SIZE}    * set to 0.</p>    */
specifier|public
specifier|static
specifier|final
name|CalciteSystemProperty
argument_list|<
name|Integer
argument_list|>
name|BINDABLE_CACHE_CONCURRENCY_LEVEL
init|=
name|intProperty
argument_list|(
literal|"calcite.bindable.cache.concurrencyLevel"
argument_list|,
literal|1
argument_list|,
name|v
lambda|->
name|v
operator|>=
literal|1
operator|&&
name|v
operator|<=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|CalciteSystemProperty
argument_list|<
name|Boolean
argument_list|>
name|booleanProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
comment|// Note that "" -> true (convenient for command-lines flags like '-Dflag')
return|return
operator|new
name|CalciteSystemProperty
argument_list|<>
argument_list|(
name|key
argument_list|,
name|v
lambda|->
name|v
operator|==
literal|null
condition|?
name|defaultValue
else|:
literal|""
operator|.
name|equals
argument_list|(
name|v
argument_list|)
operator|||
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|CalciteSystemProperty
argument_list|<
name|Integer
argument_list|>
name|intProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
return|return
name|intProperty
argument_list|(
name|key
argument_list|,
name|defaultValue
argument_list|,
name|v
lambda|->
literal|true
argument_list|)
return|;
block|}
comment|/**    * Returns the value of the system property with the specified name as {@code    * int}. If any of the conditions below hold, returns the    *<code>defaultValue</code>:    *    *<ol>    *<li>the property is not defined;    *<li>the property value cannot be transformed to an int;    *<li>the property value does not satisfy the checker.    *</ol>    */
specifier|private
specifier|static
name|CalciteSystemProperty
argument_list|<
name|Integer
argument_list|>
name|intProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|defaultValue
parameter_list|,
name|IntPredicate
name|valueChecker
parameter_list|)
block|{
return|return
operator|new
name|CalciteSystemProperty
argument_list|<>
argument_list|(
name|key
argument_list|,
name|v
lambda|->
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
name|int
name|intVal
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|v
argument_list|)
decl_stmt|;
return|return
name|valueChecker
operator|.
name|test
argument_list|(
name|intVal
argument_list|)
condition|?
name|intVal
else|:
name|defaultValue
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
return|return
name|defaultValue
return|;
block|}
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|stringProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
return|return
operator|new
name|CalciteSystemProperty
argument_list|<>
argument_list|(
name|key
argument_list|,
name|v
lambda|->
name|v
operator|==
literal|null
condition|?
name|defaultValue
else|:
name|v
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|CalciteSystemProperty
argument_list|<
name|String
argument_list|>
name|stringProperty
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allowedValues
parameter_list|)
block|{
return|return
operator|new
name|CalciteSystemProperty
argument_list|<>
argument_list|(
name|key
argument_list|,
name|v
lambda|->
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
name|String
name|normalizedValue
init|=
name|v
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
return|return
name|allowedValues
operator|.
name|contains
argument_list|(
name|normalizedValue
argument_list|)
condition|?
name|normalizedValue
else|:
name|defaultValue
return|;
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Properties
name|loadProperties
parameter_list|()
block|{
name|Properties
name|saffronProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|ClassLoader
name|classLoader
init|=
name|MoreObjects
operator|.
name|firstNonNull
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
argument_list|,
name|CalciteSystemProperty
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
decl_stmt|;
comment|// Read properties from the file "saffron.properties", if it exists in classpath
try|try
init|(
name|InputStream
name|stream
init|=
name|classLoader
operator|.
name|getResourceAsStream
argument_list|(
literal|"saffron.properties"
argument_list|)
init|)
block|{
if|if
condition|(
name|stream
operator|!=
literal|null
condition|)
block|{
name|saffronProperties
operator|.
name|load
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while reading from saffron.properties file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
literal|"java.security.AccessControlException"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
comment|// Merge system and saffron properties, mapping deprecated saffron
comment|// namespaces to calcite
specifier|final
name|Properties
name|allProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|Stream
operator|.
name|concat
argument_list|(
name|saffronProperties
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|,
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
argument_list|)
operator|.
name|forEach
argument_list|(
name|prop
lambda|->
block|{
name|String
name|deprecatedKey
init|=
operator|(
name|String
operator|)
name|prop
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|newKey
init|=
name|deprecatedKey
operator|.
name|replace
argument_list|(
literal|"net.sf.saffron."
argument_list|,
literal|"calcite."
argument_list|)
operator|.
name|replace
argument_list|(
literal|"saffron."
argument_list|,
literal|"calcite."
argument_list|)
decl_stmt|;
if|if
condition|(
name|newKey
operator|.
name|startsWith
argument_list|(
literal|"calcite."
argument_list|)
condition|)
block|{
name|allProperties
operator|.
name|setProperty
argument_list|(
name|newKey
argument_list|,
operator|(
name|String
operator|)
name|prop
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|allProperties
return|;
block|}
specifier|private
specifier|final
name|T
name|value
decl_stmt|;
specifier|private
name|CalciteSystemProperty
argument_list|(
name|String
name|key
argument_list|,
name|Function
operator|<
condition|?
name|super
expr|@
name|Nullable
name|String
argument_list|,
operator|?
expr|extends
name|T
operator|>
name|valueParser
argument_list|)
block|{
name|this
operator|.
name|value
operator|=
name|valueParser
operator|.
name|apply
argument_list|(
name|PROPERTIES
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
block|;   }
comment|/**    * Returns the value of this property.    *    * @return the value of this property or<code>null</code> if a default value has not been    * defined for this property.    */
specifier|public
name|T
name|value
argument_list|()
block|{
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

