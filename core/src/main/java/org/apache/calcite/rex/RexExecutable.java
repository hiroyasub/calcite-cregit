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
name|rex
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|Pair
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
name|janino
operator|.
name|ClassBodyEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|janino
operator|.
name|Scanner
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
name|Serializable
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
name|Arrays
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
comment|/**  * Result of compiling code generated from a {@link RexNode} expression.  */
end_comment

begin_class
specifier|public
class|class
name|RexExecutable
block|{
specifier|private
specifier|static
specifier|final
name|String
name|GENERATED_CLASS_NAME
init|=
literal|"Reducer"
decl_stmt|;
specifier|private
specifier|final
name|Function1
argument_list|<
name|DataContext
argument_list|,
name|Object
index|[]
argument_list|>
name|compiledFunction
decl_stmt|;
specifier|private
specifier|final
name|String
name|code
decl_stmt|;
specifier|private
name|DataContext
name|dataContext
decl_stmt|;
specifier|public
name|RexExecutable
parameter_list|(
name|String
name|code
parameter_list|,
name|Object
name|reason
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
name|this
operator|.
name|compiledFunction
operator|=
name|compile
argument_list|(
name|code
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Function1
argument_list|<
name|DataContext
argument_list|,
name|Object
index|[]
argument_list|>
name|compile
parameter_list|(
name|String
name|code
parameter_list|,
name|Object
name|reason
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ClassBodyEvaluator
name|cbe
init|=
operator|new
name|ClassBodyEvaluator
argument_list|()
decl_stmt|;
name|cbe
operator|.
name|setClassName
argument_list|(
name|GENERATED_CLASS_NAME
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
operator|new
name|Class
index|[]
block|{
name|Function1
operator|.
name|class
block|,
name|Serializable
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setParentClassLoader
argument_list|(
name|RexExecutable
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|cook
argument_list|(
operator|new
name|Scanner
argument_list|(
literal|null
argument_list|,
operator|new
name|StringReader
argument_list|(
name|code
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Class
name|c
init|=
name|cbe
operator|.
name|getClazz
argument_list|()
decl_stmt|;
comment|//noinspection unchecked
specifier|final
name|Constructor
argument_list|<
name|Function1
argument_list|<
name|DataContext
argument_list|,
name|Object
index|[]
argument_list|>
argument_list|>
name|constructor
init|=
name|c
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
name|CompileException
decl||
name|IOException
decl||
name|InstantiationException
decl||
name|IllegalAccessException
decl||
name|InvocationTargetException
decl||
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"While compiling "
operator|+
name|reason
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setDataContext
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
name|this
operator|.
name|dataContext
operator|=
name|dataContext
expr_stmt|;
block|}
specifier|public
name|void
name|reduce
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|constExps
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducedValues
parameter_list|)
block|{
name|Object
index|[]
name|values
decl_stmt|;
try|try
block|{
name|values
operator|=
name|compiledFunction
operator|.
name|apply
argument_list|(
name|dataContext
argument_list|)
expr_stmt|;
assert|assert
name|values
operator|.
name|length
operator|==
name|constExps
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|valueList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|Object
argument_list|>
name|value
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|constExps
argument_list|,
name|valueList
argument_list|)
control|)
block|{
name|reducedValues
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|value
operator|.
name|right
argument_list|,
name|value
operator|.
name|left
operator|.
name|getType
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// One or more of the expressions failed.
comment|// Don't reduce any of the expressions.
name|reducedValues
operator|.
name|addAll
argument_list|(
name|constExps
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|Object
index|[
name|constExps
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
block|}
name|Hook
operator|.
name|EXPRESSION_REDUCER
operator|.
name|run
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|code
argument_list|,
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Function1
argument_list|<
name|DataContext
argument_list|,
name|Object
index|[]
argument_list|>
name|getFunction
parameter_list|()
block|{
return|return
name|compiledFunction
return|;
block|}
specifier|public
name|Object
index|[]
name|execute
parameter_list|()
block|{
return|return
name|compiledFunction
operator|.
name|apply
argument_list|(
name|dataContext
argument_list|)
return|;
block|}
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|code
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexExecutable.java
end_comment

end_unit

