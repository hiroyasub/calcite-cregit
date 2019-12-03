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
name|validate
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
name|util
operator|.
name|CalciteValidatorException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|// NOTE:  This class gets compiled independently of everything else so that
end_comment

begin_comment
comment|// resource generation can use reflection.  That means it must have no
end_comment

begin_comment
comment|// dependencies on other Calcite code.
end_comment

begin_comment
comment|/**  * Exception thrown while validating a SQL statement.  *  *<p>Unlike {@link org.apache.calcite.runtime.CalciteException}, this is a  * checked exception, which reminds code authors to wrap it in another exception  * containing the line/column context.  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorException
extends|extends
name|Exception
implements|implements
name|CalciteValidatorException
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"org.apache.calcite.runtime.CalciteException"
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|831683113957131387L
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new SqlValidatorException object.    *    * @param message error message    * @param cause   underlying cause    */
specifier|public
name|SqlValidatorException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
comment|// TODO: see note in CalciteException constructor
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"SqlValidatorException"
argument_list|,
name|this
argument_list|)
expr_stmt|;
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
name|LOGGER
operator|.
name|error
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

