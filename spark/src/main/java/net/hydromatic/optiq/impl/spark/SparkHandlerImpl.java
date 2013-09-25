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
name|impl
operator|.
name|spark
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
name|expressions
operator|.
name|ClassDeclaration
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
name|OptiqPrepare
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
name|rules
operator|.
name|java
operator|.
name|JavaRules
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
name|runtime
operator|.
name|Bindable
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
name|runtime
operator|.
name|Typed
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
name|org
operator|.
name|eigenbase
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
name|eigenbase
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
name|relopt
operator|.
name|volcano
operator|.
name|VolcanoPlanner
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
name|FileWriter
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
comment|/**  * Implementation of {@link OptiqPrepare.SparkHandler}. Gives the core Optiq  * engine access to rules that only exist in the Spark module.  */
end_comment

begin_class
specifier|public
class|class
name|SparkHandlerImpl
implements|implements
name|OptiqPrepare
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
name|JavaSparkContext
name|sparkContext
init|=
operator|new
name|JavaSparkContext
argument_list|(
literal|"local[1]"
argument_list|,
literal|"optiq"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|SparkHandlerImpl
name|INSTANCE
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|File
name|SRC_DIR
init|=
operator|new
name|File
argument_list|(
literal|"/tmp"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|File
name|CLASS_DIR
init|=
operator|new
name|File
argument_list|(
literal|"core/target/classes"
argument_list|)
decl_stmt|;
comment|/** Creates a SparkHandlerImpl. */
specifier|private
name|SparkHandlerImpl
parameter_list|()
block|{
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
name|Calendar
operator|.
name|getInstance
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
comment|/** Creates a SparkHandlerImpl, initializing on first call. Optiq-core calls    * this via reflection. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
specifier|static
specifier|final
name|OptiqPrepare
operator|.
name|SparkHandler
name|INSTANCE
parameter_list|()
block|{
if|if
condition|(
name|INSTANCE
operator|==
literal|null
condition|)
block|{
name|INSTANCE
operator|=
operator|new
name|SparkHandlerImpl
argument_list|()
expr_stmt|;
block|}
return|return
name|INSTANCE
return|;
block|}
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
argument_list|)
return|;
block|}
specifier|public
name|void
name|registerRules
parameter_list|(
name|VolcanoPlanner
name|planner
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
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
name|planner
operator|.
name|removeRule
argument_list|(
name|JavaRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|sparkContext
parameter_list|()
block|{
return|return
name|sparkContext
return|;
block|}
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|Bindable
name|compile
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|)
block|{
try|try
block|{
name|String
name|className
init|=
literal|"OptiqProgram"
operator|+
name|classId
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|SRC_DIR
argument_list|,
name|className
operator|+
literal|".java"
argument_list|)
decl_stmt|;
name|FileWriter
name|fileWriter
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|,
literal|false
argument_list|)
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
name|Bindable
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|", "
operator|+
name|Typed
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"======================"
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|write
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|fileWriter
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|file
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
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|Object
name|o
init|=
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
return|return
operator|(
name|Bindable
operator|)
name|o
return|;
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
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
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
catch|catch
parameter_list|(
name|InstantiationException
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
catch|catch
parameter_list|(
name|IllegalAccessException
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
comment|// End SparkHandlerImpl.java
end_comment

end_unit

