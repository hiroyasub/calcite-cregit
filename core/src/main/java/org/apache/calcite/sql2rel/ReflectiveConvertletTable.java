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
name|sql2rel
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
name|rex
operator|.
name|RexNode
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
name|SqlCall
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
name|SqlNode
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
name|SqlOperator
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
name|parser
operator|.
name|SqlParserPos
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
name|util
operator|.
name|HashMap
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
comment|/**  * Implementation of {@link SqlRexConvertletTable} which uses reflection to call  * any method of the form<code>public RexNode convertXxx(ConvertletContext,  * SqlNode)</code> or<code>public RexNode convertXxx(ConvertletContext,  * SqlOperator, SqlCall)</code>.  */
end_comment

begin_class
specifier|public
class|class
name|ReflectiveConvertletTable
implements|implements
name|SqlRexConvertletTable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ReflectiveConvertletTable
parameter_list|()
block|{
for|for
control|(
specifier|final
name|Method
name|method
range|:
name|getClass
argument_list|()
operator|.
name|getMethods
argument_list|()
control|)
block|{
name|registerNodeTypeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|registerOpTypeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Registers method if it: a. is public, and b. is named "convertXxx", and    * c. has a return type of "RexNode" or a subtype d. has a 2 parameters with    * types ConvertletContext and SqlNode (or a subtype) respectively.    */
specifier|private
name|void
name|registerNodeTypeMethod
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
name|isPublic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"convert"
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|RexNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|Class
index|[]
name|parameterTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|parameterTypes
index|[
literal|0
index|]
operator|!=
name|SqlRexContext
operator|.
name|class
condition|)
block|{
return|return;
block|}
specifier|final
name|Class
name|parameterType
init|=
name|parameterTypes
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|SqlNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|parameterType
argument_list|)
condition|)
block|{
return|return;
block|}
name|map
operator|.
name|put
argument_list|(
name|parameterType
argument_list|,
operator|(
name|SqlRexConvertlet
operator|)
parameter_list|(
name|cx
parameter_list|,
name|call
parameter_list|)
lambda|->
block|{
try|try
block|{
return|return
operator|(
name|RexNode
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|ReflectiveConvertletTable
operator|.
name|this
argument_list|,
name|cx
argument_list|,
name|call
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while converting "
operator|+
name|call
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Registers method if it: a. is public, and b. is named "convertXxx", and    * c. has a return type of "RexNode" or a subtype d. has a 3 parameters with    * types: ConvertletContext; SqlOperator (or a subtype), SqlCall (or a    * subtype).    */
specifier|private
name|void
name|registerOpTypeMethod
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
name|isPublic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|method
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"convert"
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|RexNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|Class
index|[]
name|parameterTypes
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterTypes
operator|.
name|length
operator|!=
literal|3
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|parameterTypes
index|[
literal|0
index|]
operator|!=
name|SqlRexContext
operator|.
name|class
condition|)
block|{
return|return;
block|}
specifier|final
name|Class
name|opClass
init|=
name|parameterTypes
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|SqlOperator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|opClass
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|Class
name|parameterType
init|=
name|parameterTypes
index|[
literal|2
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|SqlCall
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|parameterType
argument_list|)
condition|)
block|{
return|return;
block|}
name|map
operator|.
name|put
argument_list|(
name|opClass
argument_list|,
operator|(
name|SqlRexConvertlet
operator|)
parameter_list|(
name|cx
parameter_list|,
name|call
parameter_list|)
lambda|->
block|{
try|try
block|{
return|return
operator|(
name|RexNode
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|ReflectiveConvertletTable
operator|.
name|this
argument_list|,
name|cx
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
name|call
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"while converting "
operator|+
name|call
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlRexConvertlet
name|get
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
name|SqlRexConvertlet
name|convertlet
decl_stmt|;
specifier|final
name|SqlOperator
name|op
init|=
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
comment|// Is there a convertlet for this operator
comment|// (e.g. SqlStdOperatorTable.plusOperator)?
name|convertlet
operator|=
operator|(
name|SqlRexConvertlet
operator|)
name|map
operator|.
name|get
argument_list|(
name|op
argument_list|)
expr_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
return|;
block|}
comment|// Is there a convertlet for this class of operator
comment|// (e.g. SqlBinaryOperator)?
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|op
operator|.
name|getClass
argument_list|()
decl_stmt|;
while|while
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|convertlet
operator|=
operator|(
name|SqlRexConvertlet
operator|)
name|map
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
return|;
block|}
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
comment|// Is there a convertlet for this class of expression
comment|// (e.g. SqlCall)?
name|clazz
operator|=
name|call
operator|.
name|getClass
argument_list|()
expr_stmt|;
while|while
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
name|convertlet
operator|=
operator|(
name|SqlRexConvertlet
operator|)
name|map
operator|.
name|get
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
if|if
condition|(
name|convertlet
operator|!=
literal|null
condition|)
block|{
return|return
name|convertlet
return|;
block|}
name|clazz
operator|=
name|clazz
operator|.
name|getSuperclass
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Registers a convertlet for a given operator instance    *    * @param op         Operator instance, say    * {@link org.apache.calcite.sql.fun.SqlStdOperatorTable#MINUS}    * @param convertlet Convertlet    */
specifier|protected
name|void
name|registerOp
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|SqlRexConvertlet
name|convertlet
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|op
argument_list|,
name|convertlet
argument_list|)
expr_stmt|;
block|}
comment|/**    * Registers that one operator is an alias for another.    *    * @param alias  Operator which is alias    * @param target Operator to translate calls to    */
specifier|protected
name|void
name|addAlias
parameter_list|(
specifier|final
name|SqlOperator
name|alias
parameter_list|,
specifier|final
name|SqlOperator
name|target
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|alias
argument_list|,
operator|(
name|SqlRexConvertlet
operator|)
parameter_list|(
name|cx
parameter_list|,
name|call
parameter_list|)
lambda|->
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|alias
argument_list|,
literal|"call to wrong operator"
argument_list|)
expr_stmt|;
specifier|final
name|SqlCall
name|newCall
init|=
name|target
operator|.
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|call
operator|.
name|getOperandList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|cx
operator|.
name|convertExpression
argument_list|(
name|newCall
argument_list|)
return|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ReflectiveConvertletTable.java
end_comment

end_unit

