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
name|RexLiteral
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
name|SqlIntervalQualifier
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
name|SqlLiteral
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

begin_comment
comment|/**  * Converts expressions from {@link SqlNode} to {@link RexNode}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlNodeToRexConverter
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts a {@link SqlCall} to a {@link RexNode} expression.    */
name|RexNode
name|convertCall
parameter_list|(
name|SqlRexContext
name|cx
parameter_list|,
name|SqlCall
name|call
parameter_list|)
function_decl|;
comment|/**    * Converts a {@link SqlLiteral SQL literal} to a    * {@link RexLiteral REX literal}.    *    *<p>The result is {@link RexNode}, not {@link RexLiteral} because if the    * literal is NULL (or the boolean Unknown value), we make a<code>CAST(NULL    * AS type)</code> expression.    */
name|RexNode
name|convertLiteral
parameter_list|(
name|SqlRexContext
name|cx
parameter_list|,
name|SqlLiteral
name|literal
parameter_list|)
function_decl|;
comment|/**    * Converts a {@link SqlIntervalQualifier SQL Interval Qualifier} to a    * {@link RexLiteral REX literal}.    */
name|RexLiteral
name|convertInterval
parameter_list|(
name|SqlRexContext
name|cx
parameter_list|,
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlNodeToRexConverter.java
end_comment

end_unit

