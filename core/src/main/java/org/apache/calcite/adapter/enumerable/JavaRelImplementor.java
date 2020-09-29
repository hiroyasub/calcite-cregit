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
name|plan
operator|.
name|RelImplementor
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
name|rex
operator|.
name|RexBuilder
import|;
end_import

begin_comment
comment|/**  * Abstract base class for implementations of {@link RelImplementor}  * that generate java code.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JavaRelImplementor
implements|implements
name|RelImplementor
block|{
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|protected
name|JavaRelImplementor
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
assert|assert
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
operator|instanceof
name|JavaTypeFactory
operator|:
literal|"Type factory of rexBuilder should be a JavaTypeFactory"
assert|;
block|}
specifier|public
name|RexBuilder
name|getRexBuilder
parameter_list|()
block|{
return|return
name|rexBuilder
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
operator|(
name|JavaTypeFactory
operator|)
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
return|;
block|}
comment|/**    * Returns the expression used to access    * {@link org.apache.calcite.DataContext}.    *    * @return expression used to access {@link org.apache.calcite.DataContext}.    */
specifier|public
name|ParameterExpression
name|getRootExpression
parameter_list|()
block|{
return|return
name|DataContext
operator|.
name|ROOT
return|;
block|}
block|}
end_class

end_unit

