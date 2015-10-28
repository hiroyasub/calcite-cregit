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
name|schema
operator|.
name|impl
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
name|AggImplementor
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
name|adapter
operator|.
name|enumerable
operator|.
name|RexImpTable
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
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeFactory
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
name|schema
operator|.
name|AggregateFunction
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
name|schema
operator|.
name|FunctionParameter
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
name|schema
operator|.
name|ImplementableAggFunction
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
name|ReflectUtil
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
name|base
operator|.
name|Preconditions
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link AggregateFunction} via user-defined class.  * The class should implement {@code A init()}, {@code A add(A, V)}, and  * {@code R result(A)} methods.  * All the methods should be either static or instance.  * Bonus point: when using non-static implementation, the aggregate object is  * reused through the calculation, thus it can have aggregation-related state.  */
end_comment

begin_class
specifier|public
class|class
name|AggregateFunctionImpl
implements|implements
name|AggregateFunction
implements|,
name|ImplementableAggFunction
block|{
specifier|public
specifier|final
name|boolean
name|isStatic
decl_stmt|;
specifier|public
specifier|final
name|Method
name|initMethod
decl_stmt|;
specifier|public
specifier|final
name|Method
name|addMethod
decl_stmt|;
specifier|public
specifier|final
name|Method
name|mergeMethod
decl_stmt|;
specifier|public
specifier|final
name|Method
name|resultMethod
decl_stmt|;
comment|// may be null
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|valueTypes
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|parameters
decl_stmt|;
specifier|public
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|accumulatorType
decl_stmt|;
specifier|public
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|resultType
decl_stmt|;
specifier|public
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|declaringClass
decl_stmt|;
comment|/** Private constructor; use {@link #create}. */
specifier|private
name|AggregateFunctionImpl
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|declaringClass
parameter_list|,
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|params
parameter_list|,
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|valueTypes
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|accumulatorType
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|resultType
parameter_list|,
name|Method
name|initMethod
parameter_list|,
name|Method
name|addMethod
parameter_list|,
name|Method
name|mergeMethod
parameter_list|,
name|Method
name|resultMethod
parameter_list|)
block|{
name|this
operator|.
name|declaringClass
operator|=
name|declaringClass
expr_stmt|;
name|this
operator|.
name|valueTypes
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|valueTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|params
expr_stmt|;
name|this
operator|.
name|accumulatorType
operator|=
name|accumulatorType
expr_stmt|;
name|this
operator|.
name|resultType
operator|=
name|resultType
expr_stmt|;
name|this
operator|.
name|initMethod
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|initMethod
argument_list|)
expr_stmt|;
name|this
operator|.
name|addMethod
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|addMethod
argument_list|)
expr_stmt|;
name|this
operator|.
name|mergeMethod
operator|=
name|mergeMethod
expr_stmt|;
name|this
operator|.
name|resultMethod
operator|=
name|resultMethod
expr_stmt|;
name|this
operator|.
name|isStatic
operator|=
name|Modifier
operator|.
name|isStatic
argument_list|(
name|initMethod
operator|.
name|getModifiers
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|resultMethod
operator|!=
literal|null
operator|||
name|accumulatorType
operator|==
name|resultType
assert|;
block|}
comment|/** Creates an aggregate function, or returns null. */
specifier|public
specifier|static
name|AggregateFunctionImpl
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
name|initMethod
init|=
name|ReflectiveFunctionBase
operator|.
name|findMethod
argument_list|(
name|clazz
argument_list|,
literal|"init"
argument_list|)
decl_stmt|;
specifier|final
name|Method
name|addMethod
init|=
name|ReflectiveFunctionBase
operator|.
name|findMethod
argument_list|(
name|clazz
argument_list|,
literal|"add"
argument_list|)
decl_stmt|;
specifier|final
name|Method
name|mergeMethod
init|=
literal|null
decl_stmt|;
comment|// TODO:
specifier|final
name|Method
name|resultMethod
init|=
name|ReflectiveFunctionBase
operator|.
name|findMethod
argument_list|(
name|clazz
argument_list|,
literal|"result"
argument_list|)
decl_stmt|;
if|if
condition|(
name|initMethod
operator|!=
literal|null
operator|&&
name|addMethod
operator|!=
literal|null
condition|)
block|{
comment|// A is return type of init by definition
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|accumulatorType
init|=
name|initMethod
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
comment|// R is return type of result by definition
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|resultType
init|=
name|resultMethod
operator|!=
literal|null
condition|?
name|resultMethod
operator|.
name|getReturnType
argument_list|()
else|:
name|accumulatorType
decl_stmt|;
comment|// V is remaining args of add by definition
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|addParamTypes
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|addMethod
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|addParamTypes
operator|.
name|isEmpty
argument_list|()
operator|||
name|addParamTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|!=
name|accumulatorType
condition|)
block|{
throw|throw
name|RESOURCE
operator|.
name|firstParameterOfAdd
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
specifier|final
name|ReflectiveFunctionBase
operator|.
name|ParameterListBuilder
name|params
init|=
name|ReflectiveFunctionBase
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|valueTypes
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|addParamTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|type
init|=
name|addParamTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|ReflectUtil
operator|.
name|getParameterName
argument_list|(
name|addMethod
argument_list|,
name|i
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|optional
init|=
name|ReflectUtil
operator|.
name|isParameterOptional
argument_list|(
name|addMethod
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|type
argument_list|,
name|name
argument_list|,
name|optional
argument_list|)
expr_stmt|;
name|valueTypes
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|// A init()
comment|// A add(A, V)
comment|// A merge(A, A)
comment|// R result(A)
comment|// TODO: check add returns A
comment|// TODO: check merge returns A
comment|// TODO: check merge args are (A, A)
comment|// TODO: check result args are (A)
return|return
operator|new
name|AggregateFunctionImpl
argument_list|(
name|clazz
argument_list|,
name|params
operator|.
name|build
argument_list|()
argument_list|,
name|valueTypes
operator|.
name|build
argument_list|()
argument_list|,
name|accumulatorType
argument_list|,
name|resultType
argument_list|,
name|initMethod
argument_list|,
name|addMethod
argument_list|,
name|mergeMethod
argument_list|,
name|resultMethod
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
specifier|public
name|RelDataType
name|getReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|resultType
argument_list|)
return|;
block|}
specifier|public
name|AggImplementor
name|getImplementor
parameter_list|(
name|boolean
name|windowContext
parameter_list|)
block|{
return|return
operator|new
name|RexImpTable
operator|.
name|UserDefinedAggReflectiveImplementor
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateFunctionImpl.java
end_comment

end_unit

