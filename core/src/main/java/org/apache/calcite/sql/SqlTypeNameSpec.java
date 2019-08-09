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
name|parser
operator|.
name|SqlParserPos
import|;
end_import

begin_comment
comment|/**  * A<code>SqlTypeNameSpec</code> is a type name that allows user to  * customize sql node unparsing and data type deriving.  *  *<p>To customize sql node unparsing, override the method {@link #unparse(SqlWriter, int, int)}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlTypeNameSpec
extends|extends
name|SqlIdentifier
block|{
comment|/**    * Creates a {@code SqlTypeNameSpec}.    *    * @param name Name of the type.    * @param pos  Parser position, must not be null.    */
name|SqlTypeNameSpec
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|RelDataType
name|deriveType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End SqlTypeNameSpec.java
end_comment

end_unit

