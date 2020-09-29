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
name|sql
operator|.
name|advise
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
name|adapter
operator|.
name|enumerable
operator|.
name|CallImplementor
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
name|NullPolicy
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
name|Linq4j
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
name|Expression
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
name|linq4j
operator|.
name|tree
operator|.
name|Types
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
name|ImplementableFunction
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
name|TableFunction
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
name|impl
operator|.
name|ReflectiveFunctionBase
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
name|sql
operator|.
name|validate
operator|.
name|SqlMoniker
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
name|BuiltInMethod
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
name|Iterables
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
name|Type
import|;
end_import

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
name|Collections
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
comment|/**  * Table function that returns completion hints for a given SQL statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAdvisorGetHintsFunction
implements|implements
name|TableFunction
implements|,
name|ImplementableFunction
block|{
specifier|private
specifier|static
specifier|final
name|Expression
name|ADVISOR
init|=
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|DataContext
operator|.
name|ROOT
argument_list|,
name|BuiltInMethod
operator|.
name|DATA_CONTEXT_GET
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|DataContext
operator|.
name|Variable
operator|.
name|SQL_ADVISOR
operator|.
name|camelName
argument_list|)
argument_list|)
argument_list|,
name|SqlAdvisor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Method
name|GET_COMPLETION_HINTS
init|=
name|Types
operator|.
name|lookupMethod
argument_list|(
name|SqlAdvisorGetHintsFunction
operator|.
name|class
argument_list|,
literal|"getCompletionHints"
argument_list|,
name|SqlAdvisor
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CallImplementor
name|IMPLEMENTOR
init|=
name|RexImpTable
operator|.
name|createImplementor
argument_list|(
parameter_list|(
name|translator
parameter_list|,
name|call
parameter_list|,
name|operands
parameter_list|)
lambda|->
name|Expressions
operator|.
name|call
argument_list|(
name|GET_COMPLETION_HINTS
argument_list|,
name|Iterables
operator|.
name|concat
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|ADVISOR
argument_list|)
argument_list|,
name|operands
argument_list|)
argument_list|)
argument_list|,
name|NullPolicy
operator|.
name|ANY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|PARAMETERS
init|=
name|ReflectiveFunctionBase
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"sql"
argument_list|)
operator|.
name|add
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"pos"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|CallImplementor
name|getImplementor
parameter_list|()
block|{
return|return
name|IMPLEMENTOR
return|;
block|}
annotation|@
name|Override
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
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|SqlAdvisorHint
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|SqlAdvisorHint
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|PARAMETERS
return|;
block|}
comment|/**    * Returns completion hints for a given SQL statement.    *    *<p>Typically this is called from generated code    * (via {@link SqlAdvisorGetHintsFunction#IMPLEMENTOR}).    *    * @param advisor Advisor to produce completion hints    * @param sql     SQL to complete    * @param pos     Cursor position in SQL    * @return the table that contains completion hints for a given SQL statement    */
specifier|public
specifier|static
name|Enumerable
argument_list|<
name|SqlAdvisorHint
argument_list|>
name|getCompletionHints
parameter_list|(
specifier|final
name|SqlAdvisor
name|advisor
parameter_list|,
specifier|final
name|String
name|sql
parameter_list|,
specifier|final
name|int
name|pos
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|replaced
init|=
block|{
literal|null
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|hints
init|=
name|advisor
operator|.
name|getCompletionHints
argument_list|(
name|sql
argument_list|,
name|pos
argument_list|,
name|replaced
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlAdvisorHint
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|hints
operator|.
name|size
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
operator|new
name|SqlAdvisorHint
argument_list|(
name|replaced
index|[
literal|0
index|]
argument_list|,
literal|null
argument_list|,
literal|"MATCH"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlMoniker
name|hint
range|:
name|hints
control|)
block|{
name|res
operator|.
name|add
argument_list|(
operator|new
name|SqlAdvisorHint
argument_list|(
name|hint
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|res
argument_list|)
operator|.
name|asQueryable
argument_list|()
return|;
block|}
block|}
end_class

end_unit

