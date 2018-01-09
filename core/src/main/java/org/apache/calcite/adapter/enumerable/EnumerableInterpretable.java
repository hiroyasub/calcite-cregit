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
name|enumerable
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
name|DataContext
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
name|avatica
operator|.
name|Helper
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
name|interpreter
operator|.
name|Compiler
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
name|interpreter
operator|.
name|InterpretableConvention
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
name|interpreter
operator|.
name|InterpretableRel
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
name|interpreter
operator|.
name|Node
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
name|interpreter
operator|.
name|Row
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
name|interpreter
operator|.
name|Sink
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
name|AbstractEnumerable
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
name|Enumerable
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
name|Enumerator
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|ConventionTraitDef
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
name|RelOptCluster
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
name|prepare
operator|.
name|CalcitePrepareImpl
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
name|convert
operator|.
name|ConverterImpl
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
name|runtime
operator|.
name|Bindable
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
name|Hook
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
name|Typed
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
name|Utilities
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
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|CompileException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|CompilerFactoryFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|IClassBodyEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|ICompilerFactory
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
name|StringReader
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Relational expression that converts an enumerable input to interpretable  * calling convention.  *  * @see EnumerableConvention  * @see org.apache.calcite.interpreter.BindableConvention  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableInterpretable
extends|extends
name|ConverterImpl
implements|implements
name|InterpretableRel
block|{
specifier|protected
name|EnumerableInterpretable
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|InterpretableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableInterpretable
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|EnumerableInterpretable
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Node
name|implement
parameter_list|(
specifier|final
name|InterpreterImplementor
name|implementor
parameter_list|)
block|{
specifier|final
name|Bindable
name|bindable
init|=
name|toBindable
argument_list|(
name|implementor
operator|.
name|internalParameters
argument_list|,
name|implementor
operator|.
name|spark
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|getInput
argument_list|()
argument_list|,
name|EnumerableRel
operator|.
name|Prefer
operator|.
name|ARRAY
argument_list|)
decl_stmt|;
specifier|final
name|ArrayBindable
name|arrayBindable
init|=
name|box
argument_list|(
name|bindable
argument_list|)
decl_stmt|;
specifier|final
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
init|=
name|arrayBindable
operator|.
name|bind
argument_list|(
name|implementor
operator|.
name|dataContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableNode
argument_list|(
name|enumerable
argument_list|,
name|implementor
operator|.
name|compiler
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Bindable
name|toBindable
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|parameters
parameter_list|,
name|CalcitePrepare
operator|.
name|SparkHandler
name|spark
parameter_list|,
name|EnumerableRel
name|rel
parameter_list|,
name|EnumerableRel
operator|.
name|Prefer
name|prefer
parameter_list|)
block|{
name|EnumerableRelImplementor
name|relImplementor
init|=
operator|new
name|EnumerableRelImplementor
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|parameters
argument_list|)
decl_stmt|;
specifier|final
name|ClassDeclaration
name|expr
init|=
name|relImplementor
operator|.
name|implementRoot
argument_list|(
name|rel
argument_list|,
name|prefer
argument_list|)
decl_stmt|;
name|String
name|s
init|=
name|Expressions
operator|.
name|toString
argument_list|(
name|expr
operator|.
name|memberDeclarations
argument_list|,
literal|"\n"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
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
name|s
argument_list|)
expr_stmt|;
block|}
name|Hook
operator|.
name|JAVA_PLAN
operator|.
name|run
argument_list|(
name|s
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|spark
operator|!=
literal|null
operator|&&
name|spark
operator|.
name|enabled
argument_list|()
condition|)
block|{
return|return
name|spark
operator|.
name|compile
argument_list|(
name|expr
argument_list|,
name|s
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getBindable
argument_list|(
name|expr
argument_list|,
name|s
argument_list|,
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|Helper
operator|.
name|INSTANCE
operator|.
name|wrap
argument_list|(
literal|"Error while compiling generated Java code:\n"
operator|+
name|s
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|static
name|ArrayBindable
name|getArrayBindable
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|fieldCount
parameter_list|)
throws|throws
name|CompileException
throws|,
name|IOException
block|{
name|Bindable
name|bindable
init|=
name|getBindable
argument_list|(
name|expr
argument_list|,
name|s
argument_list|,
name|fieldCount
argument_list|)
decl_stmt|;
return|return
name|box
argument_list|(
name|bindable
argument_list|)
return|;
block|}
specifier|static
name|Bindable
name|getBindable
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|fieldCount
parameter_list|)
throws|throws
name|CompileException
throws|,
name|IOException
block|{
name|ICompilerFactory
name|compilerFactory
decl_stmt|;
try|try
block|{
name|compilerFactory
operator|=
name|CompilerFactoryFactory
operator|.
name|getDefaultCompilerFactory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to instantiate java compiler"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|IClassBodyEvaluator
name|cbe
init|=
name|compilerFactory
operator|.
name|newClassBodyEvaluator
argument_list|()
decl_stmt|;
name|cbe
operator|.
name|setClassName
argument_list|(
name|expr
operator|.
name|name
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setExtendedClass
argument_list|(
name|Utilities
operator|.
name|class
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setImplementedInterfaces
argument_list|(
name|fieldCount
operator|==
literal|1
condition|?
operator|new
name|Class
index|[]
block|{
name|Bindable
operator|.
name|class
block|,
name|Typed
operator|.
name|class
block|}
else|:
operator|new
name|Class
index|[]
block|{
name|ArrayBindable
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setParentClassLoader
argument_list|(
name|EnumerableInterpretable
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
comment|// Add line numbers to the generated janino class
name|cbe
operator|.
name|setDebuggingInformation
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|Bindable
operator|)
name|cbe
operator|.
name|createInstance
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
comment|/** Converts a bindable over scalar values into an array bindable, with each    * row as an array of 1 element. */
specifier|static
name|ArrayBindable
name|box
parameter_list|(
specifier|final
name|Bindable
name|bindable
parameter_list|)
block|{
if|if
condition|(
name|bindable
operator|instanceof
name|ArrayBindable
condition|)
block|{
return|return
operator|(
name|ArrayBindable
operator|)
name|bindable
return|;
block|}
return|return
operator|new
name|ArrayBindable
argument_list|()
block|{
specifier|public
name|Class
argument_list|<
name|Object
index|[]
argument_list|>
name|getElementType
parameter_list|()
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|bind
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
specifier|final
name|Enumerable
argument_list|<
name|?
argument_list|>
name|enumerable
init|=
name|bindable
operator|.
name|bind
argument_list|(
name|dataContext
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Enumerator
argument_list|<
name|?
argument_list|>
name|enumerator
init|=
name|enumerable
operator|.
name|enumerator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
block|{
name|enumerator
operator|.
name|current
argument_list|()
block|}
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|moveNext
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|enumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/** Interpreter node that reads from an {@link Enumerable}.    *    *<p>From the interpreter's perspective, it is a leaf node. */
specifier|private
specifier|static
class|class
name|EnumerableNode
implements|implements
name|Node
block|{
specifier|private
specifier|final
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
decl_stmt|;
specifier|private
specifier|final
name|Sink
name|sink
decl_stmt|;
name|EnumerableNode
parameter_list|(
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
parameter_list|,
name|Compiler
name|compiler
parameter_list|,
name|EnumerableInterpretable
name|rel
parameter_list|)
block|{
name|this
operator|.
name|enumerable
operator|=
name|enumerable
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|compiler
operator|.
name|sink
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
init|=
name|enumerable
operator|.
name|enumerator
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|Object
index|[]
name|values
init|=
name|enumerator
operator|.
name|current
argument_list|()
decl_stmt|;
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|of
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End EnumerableInterpretable.java
end_comment

end_unit

