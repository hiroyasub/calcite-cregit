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
name|interpreter
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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Utilities relating to {@link org.apache.calcite.interpreter.Interpreter}  * and {@link org.apache.calcite.interpreter.InterpretableConvention}.  */
end_comment

begin_class
specifier|public
class|class
name|Interpreters
block|{
specifier|private
name|Interpreters
parameter_list|()
block|{
block|}
comment|/** Creates a {@link org.apache.calcite.runtime.Bindable} that interprets a    * given relational expression. */
specifier|public
specifier|static
name|ArrayBindable
name|bindable
parameter_list|(
specifier|final
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
name|rel
operator|instanceof
name|ArrayBindable
condition|)
block|{
comment|// E.g. if rel instanceof BindableRel
return|return
operator|(
name|ArrayBindable
operator|)
name|rel
return|;
block|}
return|return
operator|new
name|ArrayBindable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|bind
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
return|return
operator|new
name|Interpreter
argument_list|(
name|dataContext
argument_list|,
name|rel
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|}
return|;
block|}
block|}
end_class

end_unit

