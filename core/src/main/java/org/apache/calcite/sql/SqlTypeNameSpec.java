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
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
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
name|SqlValidator
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
name|Litmus
import|;
end_import

begin_comment
comment|/**  * A<code>SqlTypeNameSpec</code> is a type name specification that allows user to  * customize sql node unparsing and data type deriving.  *  *<p>To customize sql node unparsing, override the method  * {@link #unparse(SqlWriter, int, int)}.  *  *<p>To customize data type deriving, override the method  * {@link #deriveType(SqlValidator)}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlTypeNameSpec
block|{
specifier|private
specifier|final
name|SqlIdentifier
name|typeName
decl_stmt|;
specifier|private
specifier|final
name|SqlParserPos
name|pos
decl_stmt|;
comment|/**    * Creates a {@code SqlTypeNameSpec}.    *    * @param name Name of the type    * @param pos  Parser position, must not be null    */
specifier|public
name|SqlTypeNameSpec
parameter_list|(
name|SqlIdentifier
name|name
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
operator|.
name|typeName
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
comment|/**    * Derive type from this SqlTypeNameSpec.    *    * @param validator The sql validator    * @return the {@code RelDataType} instance, throws exception if we could not    *         deduce the type    */
specifier|public
specifier|abstract
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|)
function_decl|;
comment|/** Writes a SQL representation of this spec to a writer. */
specifier|public
specifier|abstract
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
function_decl|;
comment|/** Returns whether this spec is structurally equivalent to another spec. */
specifier|public
specifier|abstract
name|boolean
name|equalsDeep
parameter_list|(
name|SqlTypeNameSpec
name|spec
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
function_decl|;
specifier|public
name|SqlParserPos
name|getParserPos
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
specifier|public
name|SqlIdentifier
name|getTypeName
parameter_list|()
block|{
return|return
name|typeName
return|;
block|}
block|}
end_class

end_unit

