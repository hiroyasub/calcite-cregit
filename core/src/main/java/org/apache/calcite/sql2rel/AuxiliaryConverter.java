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
name|RexBuilder
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
name|RexCall
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
name|SqlFunction
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
name|fun
operator|.
name|SqlStdOperatorTable
import|;
end_import

begin_comment
comment|/** Converts an expression for a group window function (e.g. TUMBLE)  * into an expression for an auxiliary group function (e.g. TUMBLE_START).  *  * @see SqlStdOperatorTable#TUMBLE  */
end_comment

begin_interface
specifier|public
interface|interface
name|AuxiliaryConverter
block|{
comment|/** Converts an expression.    *    * @param rexBuilder Rex  builder    * @param groupCall Call to the group function, e.g. "TUMBLE($2, 36000)"    * @param e Expression holding result of the group function, e.g. "$0"    *    * @return Expression for auxiliary function, e.g. "$0 + 36000" converts    * the result of TUMBLE to the result of TUMBLE_END    */
name|RexNode
name|convert
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexNode
name|groupCall
parameter_list|,
name|RexNode
name|e
parameter_list|)
function_decl|;
comment|/** Simple implementation of {@link AuxiliaryConverter}. */
class|class
name|Impl
implements|implements
name|AuxiliaryConverter
block|{
specifier|private
specifier|final
name|SqlFunction
name|f
decl_stmt|;
specifier|public
name|Impl
parameter_list|(
name|SqlFunction
name|f
parameter_list|)
block|{
name|this
operator|.
name|f
operator|=
name|f
expr_stmt|;
block|}
specifier|public
name|RexNode
name|convert
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexNode
name|groupCall
parameter_list|,
name|RexNode
name|e
parameter_list|)
block|{
switch|switch
condition|(
name|f
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|TUMBLE_START
case|:
case|case
name|HOP_START
case|:
case|case
name|SESSION_START
case|:
case|case
name|SESSION_END
case|:
return|return
name|e
return|;
case|case
name|TUMBLE_END
case|:
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|e
argument_list|,
operator|(
operator|(
name|RexCall
operator|)
name|groupCall
operator|)
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
case|case
name|HOP_END
case|:
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|e
argument_list|,
operator|(
operator|(
name|RexCall
operator|)
name|groupCall
operator|)
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown: "
operator|+
name|f
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_interface

begin_comment
comment|// End AuxiliaryConverter.java
end_comment

end_unit

