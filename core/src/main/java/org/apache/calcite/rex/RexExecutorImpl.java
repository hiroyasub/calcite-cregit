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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumUtils
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
name|RexToLixTranslator
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
name|RexToLixTranslator
operator|.
name|InputGetter
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
name|java
operator|.
name|JavaTypeFactory
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
name|JavaTypeFactoryImpl
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
name|BlockBuilder
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
name|IndexExpression
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
name|MethodCallExpression
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
name|MethodDeclaration
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
name|ParameterExpression
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
name|sql
operator|.
name|validate
operator|.
name|SqlConformance
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
name|SqlConformanceEnum
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
name|List
import|;
end_import

begin_comment
comment|/** * Evaluates a {@link RexNode} expression. */
end_comment

begin_class
specifier|public
class|class
name|RexExecutorImpl
implements|implements
name|RexExecutor
block|{
specifier|private
specifier|final
name|DataContext
name|dataContext
decl_stmt|;
specifier|public
name|RexExecutorImpl
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
specifier|private
name|String
name|compile
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
name|RexToLixTranslator
operator|.
name|InputGetter
name|getter
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|emptyRowType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|compile
argument_list|(
name|rexBuilder
argument_list|,
name|constExps
argument_list|,
name|getter
argument_list|,
name|emptyRowType
argument_list|)
return|;
block|}
specifier|private
name|String
name|compile
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
name|RexToLixTranslator
operator|.
name|InputGetter
name|getter
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|RexProgramBuilder
name|programBuilder
init|=
operator|new
name|RexProgramBuilder
argument_list|(
name|rowType
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|constExps
control|)
block|{
name|programBuilder
operator|.
name|addProject
argument_list|(
name|node
argument_list|,
literal|"c"
operator|+
name|programBuilder
operator|.
name|getProjectList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|JavaTypeFactoryImpl
name|javaTypeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|getTypeSystem
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|BlockBuilder
name|blockBuilder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|root0_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"root0"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|root_
init|=
name|DataContext
operator|.
name|ROOT
decl_stmt|;
name|blockBuilder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|root_
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|root0_
argument_list|,
name|DataContext
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|programBuilder
operator|.
name|getProgram
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
name|RexToLixTranslator
operator|.
name|translateProjects
argument_list|(
name|program
argument_list|,
name|javaTypeFactory
argument_list|,
name|conformance
argument_list|,
name|blockBuilder
argument_list|,
literal|null
argument_list|,
name|root_
argument_list|,
name|getter
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|blockBuilder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|expressions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MethodDeclaration
name|methodDecl
init|=
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|BuiltInMethod
operator|.
name|FUNCTION1_APPLY
operator|.
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|root0_
argument_list|)
argument_list|,
name|blockBuilder
operator|.
name|toBlock
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|code
init|=
name|Expressions
operator|.
name|toString
argument_list|(
name|methodDecl
argument_list|)
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
name|code
argument_list|)
expr_stmt|;
block|}
return|return
name|code
return|;
block|}
comment|/**    * Creates an {@link RexExecutable} that allows to apply the    * generated code during query processing (filter, projection).    *    * @param rexBuilder Rex builder    * @param exps Expressions    * @param rowType describes the structure of the input row.    */
specifier|public
name|RexExecutable
name|getExecutable
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
specifier|final
name|JavaTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|getTypeSystem
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|InputGetter
name|getter
init|=
operator|new
name|DataContextInputGetter
argument_list|(
name|rowType
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|String
name|code
init|=
name|compile
argument_list|(
name|rexBuilder
argument_list|,
name|exps
argument_list|,
name|getter
argument_list|,
name|rowType
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexExecutable
argument_list|(
name|code
argument_list|,
literal|"generated Rex code"
argument_list|)
return|;
block|}
comment|/**    * Do constant reduction using generated code.    */
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
specifier|final
name|String
name|code
init|=
name|compile
argument_list|(
name|rexBuilder
argument_list|,
name|constExps
argument_list|,
parameter_list|(
name|list
parameter_list|,
name|index
parameter_list|,
name|storageType
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
argument_list|)
decl_stmt|;
specifier|final
name|RexExecutable
name|executable
init|=
operator|new
name|RexExecutable
argument_list|(
name|code
argument_list|,
name|constExps
argument_list|)
decl_stmt|;
name|executable
operator|.
name|setDataContext
argument_list|(
name|dataContext
argument_list|)
expr_stmt|;
name|executable
operator|.
name|reduce
argument_list|(
name|rexBuilder
argument_list|,
name|constExps
argument_list|,
name|reducedValues
argument_list|)
expr_stmt|;
block|}
comment|/**    * Implementation of    * {@link org.apache.calcite.adapter.enumerable.RexToLixTranslator.InputGetter}    * that reads the values of input fields by calling    *<code>{@link org.apache.calcite.DataContext#get}("inputRecord")</code>.    */
specifier|private
specifier|static
class|class
name|DataContextInputGetter
implements|implements
name|InputGetter
block|{
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
name|DataContextInputGetter
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
specifier|public
name|Expression
name|field
parameter_list|(
name|BlockBuilder
name|list
parameter_list|,
name|int
name|index
parameter_list|,
name|Type
name|storageType
parameter_list|)
block|{
name|MethodCallExpression
name|recFromCtx
init|=
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
literal|"inputRecord"
argument_list|)
argument_list|)
decl_stmt|;
name|Expression
name|recFromCtxCasted
init|=
name|EnumUtils
operator|.
name|convert
argument_list|(
name|recFromCtx
argument_list|,
name|Object
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
name|IndexExpression
name|recordAccess
init|=
name|Expressions
operator|.
name|arrayIndex
argument_list|(
name|recFromCtxCasted
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|storageType
operator|==
literal|null
condition|)
block|{
specifier|final
name|RelDataType
name|fieldType
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|storageType
operator|=
operator|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
operator|)
operator|.
name|getJavaClass
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
block|}
return|return
name|EnumUtils
operator|.
name|convert
argument_list|(
name|recordAccess
argument_list|,
name|storageType
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

