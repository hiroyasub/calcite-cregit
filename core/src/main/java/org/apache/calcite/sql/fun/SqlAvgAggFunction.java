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
name|fun
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
name|sql
operator|.
name|SqlAggFunction
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
name|SqlFunctionCategory
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
name|SqlKind
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
name|type
operator|.
name|OperandTypes
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
name|type
operator|.
name|ReturnTypes
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
name|Optionality
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

begin_comment
comment|/**  *<code>Avg</code> is an aggregator which returns the average of the values  * which go into it. It has precisely one argument of numeric type  * (<code>int</code>,<code>long</code>,<code>float</code>,<code>  * double</code>), and the result is the same type.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAvgAggFunction
extends|extends
name|SqlAggFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlAvgAggFunction.    */
specifier|public
name|SqlAvgAggFunction
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|this
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|,
name|kind
argument_list|)
expr_stmt|;
block|}
name|SqlAvgAggFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|kind
argument_list|,
name|ReturnTypes
operator|.
name|AVG_AGG_FUNCTION
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Optionality
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|SqlKind
operator|.
name|AVG_AGG_FUNCTIONS
operator|.
name|contains
argument_list|(
name|kind
argument_list|)
argument_list|,
literal|"unsupported sql kind"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SqlAvgAggFunction
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|Subtype
name|subtype
parameter_list|)
block|{
name|this
argument_list|(
name|SqlKind
operator|.
name|valueOf
argument_list|(
name|subtype
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the specific function, e.g. AVG or STDDEV_POP.    *    * @return Subtype    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|Subtype
name|getSubtype
parameter_list|()
block|{
return|return
name|Subtype
operator|.
name|valueOf
argument_list|(
name|kind
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
comment|/** Sub-type of aggregate function. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
enum|enum
name|Subtype
block|{
name|AVG
block|,
name|STDDEV_POP
block|,
name|STDDEV_SAMP
block|,
name|VAR_POP
block|,
name|VAR_SAMP
block|}
block|}
end_class

begin_comment
comment|// End SqlAvgAggFunction.java
end_comment

end_unit

