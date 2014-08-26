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
name|impl
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
name|Expression
import|;
end_import

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
name|Expressions
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
name|*
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
name|RelDataType
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
name|RelDataTypeFactory
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
name|RexCall
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Modifier
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
name|Type
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

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link net.hydromatic.optiq.TableFunction} based on a  * method. */
end_comment

begin_class
specifier|public
class|class
name|TableFunctionImpl
extends|extends
name|ReflectiveFunctionBase
implements|implements
name|TableFunction
implements|,
name|ImplementableFunction
block|{
specifier|private
specifier|final
name|CallImplementor
name|implementor
decl_stmt|;
comment|/** Private constructor; use {@link #create}. */
specifier|private
name|TableFunctionImpl
parameter_list|(
name|Method
name|method
parameter_list|,
name|CallImplementor
name|implementor
parameter_list|)
block|{
name|super
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|this
operator|.
name|implementor
operator|=
name|implementor
expr_stmt|;
block|}
comment|/** Creates a {@link TableFunctionImpl} from a class, looking for an "eval"    * method. Returns null if there is no such method. */
specifier|public
specifier|static
name|TableFunction
name|create
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
block|{
specifier|final
name|Method
name|method
init|=
name|findMethod
argument_list|(
name|clazz
argument_list|,
literal|"eval"
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|create
argument_list|(
name|method
argument_list|)
return|;
block|}
comment|/** Creates a {@link TableFunctionImpl} from a method. */
specifier|public
specifier|static
name|TableFunction
name|create
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|Class
name|clazz
init|=
name|method
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|classHasPublicZeroArgsConstructor
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
throw|throw
name|RESOURCE
operator|.
name|requireDefaultConstructor
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|ex
argument_list|()
throw|;
block|}
block|}
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|returnType
init|=
name|method
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|QueryableTable
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|returnType
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|CallImplementor
name|implementor
init|=
name|createImplementor
argument_list|(
name|method
argument_list|)
decl_stmt|;
return|return
operator|new
name|TableFunctionImpl
argument_list|(
name|method
argument_list|,
name|implementor
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
return|return
name|apply
argument_list|(
name|arguments
argument_list|)
operator|.
name|getRowType
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
return|return
name|apply
argument_list|(
name|arguments
argument_list|)
operator|.
name|getElementType
argument_list|()
return|;
block|}
specifier|public
name|CallImplementor
name|getImplementor
parameter_list|()
block|{
return|return
name|implementor
return|;
block|}
specifier|private
specifier|static
name|CallImplementor
name|createImplementor
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|)
block|{
return|return
name|RexImpTable
operator|.
name|createImplementor
argument_list|(
operator|new
name|ReflectiveCallNotNullImplementor
argument_list|(
name|method
argument_list|)
block|{
specifier|public
name|Expression
name|implement
parameter_list|(
name|RexToLixTranslator
name|translator
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|translatedOperands
parameter_list|)
block|{
name|Expression
name|expr
init|=
name|super
operator|.
name|implement
argument_list|(
name|translator
argument_list|,
name|call
argument_list|,
name|translatedOperands
argument_list|)
decl_stmt|;
name|Expression
name|queryable
init|=
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|convert_
argument_list|(
name|expr
argument_list|,
name|QueryableTable
operator|.
name|class
argument_list|)
argument_list|,
name|BuiltinMethod
operator|.
name|QUERYABLE_TABLE_AS_QUERYABLE
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|DataContext
operator|.
name|ROOT
argument_list|,
name|BuiltinMethod
operator|.
name|DATA_CONTEXT_GET_QUERY_PROVIDER
operator|.
name|method
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|,
name|SchemaPlus
operator|.
name|class
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|expr
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|queryable
argument_list|,
name|BuiltinMethod
operator|.
name|QUERYABLE_AS_ENUMERABLE
operator|.
name|method
argument_list|)
expr_stmt|;
return|return
name|expr
return|;
block|}
block|}
argument_list|,
name|NullPolicy
operator|.
name|ANY
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|QueryableTable
name|apply
parameter_list|(
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
block|{
try|try
block|{
name|Object
name|o
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|o
operator|=
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
comment|//noinspection unchecked
specifier|final
name|Object
name|table
init|=
name|method
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
name|arguments
operator|.
name|toArray
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|QueryableTable
operator|)
name|table
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|RESOURCE
operator|.
name|illegalArgumentForTableFunctionCall
argument_list|(
name|method
operator|.
name|toString
argument_list|()
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
argument_list|,
name|arguments
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|ex
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
catch|catch
parameter_list|(
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
block|}
block|}
end_class

begin_comment
comment|// End TableFunctionImpl.java
end_comment

end_unit

