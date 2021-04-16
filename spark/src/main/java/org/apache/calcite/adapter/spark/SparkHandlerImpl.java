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
name|adapter
operator|.
name|spark
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableRules
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
name|config
operator|.
name|CalciteSystemProperty
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
name|linq4j
operator|.
name|tree
operator|.
name|ClassDeclaration
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
name|plan
operator|.
name|RelOptPlanner
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
name|plan
operator|.
name|RelOptRule
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
name|runtime
operator|.
name|ArrayBindable
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
name|Util
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
name|javac
operator|.
name|JaninoCompiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|spark
operator|.
name|SparkConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|spark
operator|.
name|api
operator|.
name|java
operator|.
name|JavaSparkContext
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
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Implementation of  * {@link org.apache.calcite.jdbc.CalcitePrepare.SparkHandler}. Gives the core  * Calcite engine access to rules that only exist in the Spark module.  */
end_comment

begin_class
specifier|public
class|class
name|SparkHandlerImpl
implements|implements
name|CalcitePrepare
operator|.
name|SparkHandler
block|{
specifier|private
specifier|final
name|HttpServer
name|classServer
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|classId
decl_stmt|;
specifier|private
specifier|final
name|SparkConf
name|sparkConf
init|=
operator|new
name|SparkConf
argument_list|()
operator|.
name|set
argument_list|(
literal|"spark.driver.bindAddress"
argument_list|,
literal|"localhost"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|JavaSparkContext
name|sparkContext
init|=
operator|new
name|JavaSparkContext
argument_list|(
literal|"local[1]"
argument_list|,
literal|"calcite"
argument_list|,
name|sparkConf
argument_list|)
decl_stmt|;
comment|/** Thread-safe holder. */
specifier|private
specifier|static
class|class
name|Holder
block|{
specifier|private
specifier|static
specifier|final
name|SparkHandlerImpl
name|INSTANCE
init|=
operator|new
name|SparkHandlerImpl
argument_list|()
decl_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|File
name|CLASS_DIR
init|=
operator|new
name|File
argument_list|(
literal|"build/sparkServer/classes"
argument_list|)
decl_stmt|;
comment|/** Creates a SparkHandlerImpl. */
specifier|private
name|SparkHandlerImpl
parameter_list|()
block|{
if|if
condition|(
operator|!
name|CLASS_DIR
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|!
name|CLASS_DIR
operator|.
name|mkdirs
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unable to create temporary folder "
operator|+
name|CLASS_DIR
argument_list|)
expr_stmt|;
block|}
name|classServer
operator|=
operator|new
name|HttpServer
argument_list|(
name|CLASS_DIR
argument_list|)
expr_stmt|;
comment|// Start the classServer and store its URI in a spark system property
comment|// (which will be passed to executors so that they can connect to it)
name|classServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"spark.repl.class.uri"
argument_list|,
name|classServer
operator|.
name|uri
argument_list|()
argument_list|)
expr_stmt|;
comment|// Generate a starting point for class names that is unlikely to clash with
comment|// previous classes. A better solution would be to clear the class directory
comment|// on startup.
specifier|final
name|Calendar
name|calendar
init|=
name|Util
operator|.
name|calendar
argument_list|()
decl_stmt|;
name|classId
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|*
literal|10000
operator|+
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|)
operator|*
literal|100
operator|+
name|calendar
operator|.
name|get
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a SparkHandlerImpl, initializing on first call. Calcite-core calls    * this via reflection. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
specifier|static
name|CalcitePrepare
operator|.
name|SparkHandler
name|instance
parameter_list|()
block|{
return|return
name|Holder
operator|.
name|INSTANCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|flattenTypes
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
block|{
name|RelNode
name|root2
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|rootRel
argument_list|,
name|rootRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|plus
argument_list|(
name|SparkRel
operator|.
name|CONVENTION
argument_list|)
operator|.
name|simplify
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|planner
operator|.
name|changeTraits
argument_list|(
name|root2
argument_list|,
name|rootRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|simplify
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerRules
parameter_list|(
name|RuleSetBuilder
name|builder
parameter_list|)
block|{
for|for
control|(
name|RelOptRule
name|rule
range|:
name|SparkRules
operator|.
name|rules
argument_list|()
control|)
block|{
name|builder
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|removeRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|sparkContext
parameter_list|()
block|{
return|return
name|sparkContext
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|ArrayBindable
name|compile
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|)
block|{
specifier|final
name|String
name|className
init|=
literal|"CalciteProgram"
operator|+
name|classId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
specifier|final
name|String
name|classFileName
init|=
name|className
operator|+
literal|".java"
decl_stmt|;
name|String
name|source
init|=
literal|"public class "
operator|+
name|className
operator|+
literal|"\n"
operator|+
literal|"    implements "
operator|+
name|ArrayBindable
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|", "
operator|+
name|Serializable
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" {\n"
operator|+
name|s
operator|+
literal|"\n"
operator|+
literal|"}\n"
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
condition|)
block|{
name|Util
operator|.
name|debugCode
argument_list|(
name|System
operator|.
name|out
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
name|JaninoCompiler
name|compiler
init|=
operator|new
name|JaninoCompiler
argument_list|()
decl_stmt|;
name|compiler
operator|.
name|getArgs
argument_list|()
operator|.
name|setDestdir
argument_list|(
name|CLASS_DIR
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|compiler
operator|.
name|getArgs
argument_list|()
operator|.
name|setSource
argument_list|(
name|source
argument_list|,
name|classFileName
argument_list|)
expr_stmt|;
name|compiler
operator|.
name|getArgs
argument_list|()
operator|.
name|setFullClassName
argument_list|(
name|className
argument_list|)
expr_stmt|;
name|compiler
operator|.
name|compile
argument_list|()
expr_stmt|;
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Class
argument_list|<
name|ArrayBindable
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|ArrayBindable
argument_list|>
operator|)
name|compiler
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
name|className
argument_list|)
decl_stmt|;
specifier|final
name|Constructor
argument_list|<
name|ArrayBindable
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
decl||
name|NoSuchMethodException
decl||
name|InvocationTargetException
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

end_unit

