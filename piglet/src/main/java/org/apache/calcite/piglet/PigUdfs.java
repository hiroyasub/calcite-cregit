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
name|piglet
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|builtin
operator|.
name|BigDecimalMax
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|builtin
operator|.
name|BigDecimalSum
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|pig
operator|.
name|data
operator|.
name|Tuple
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_comment
comment|/**  * Implementation methods.  *  *<p>Found by {@link PigUdfFinder} using reflection.  */
end_comment

begin_class
specifier|public
class|class
name|PigUdfs
block|{
specifier|private
name|PigUdfs
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|BigDecimal
name|bigdecimalsum
parameter_list|(
name|Tuple
name|input
parameter_list|)
throws|throws
name|IOException
block|{
comment|// "exec" method is declared in the parent class of
comment|// AlgebraicBigDecimalMathBase
return|return
operator|new
name|BigDecimalSum
argument_list|()
operator|.
name|exec
argument_list|(
name|input
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|BigDecimal
name|bigdecimalmax
parameter_list|(
name|Tuple
name|input
parameter_list|)
throws|throws
name|IOException
block|{
comment|// "exec" method is declared in the parent class of
comment|// AlgebraicBigDecimalMathBase
return|return
operator|new
name|BigDecimalMax
argument_list|()
operator|.
name|exec
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
end_class

end_unit

