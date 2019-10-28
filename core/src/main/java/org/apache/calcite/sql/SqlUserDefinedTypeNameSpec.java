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
comment|/**  * A sql type name specification of user defined type.  *  *<p>Usually you should register the UDT into the {@link org.apache.calcite.jdbc.CalciteSchema}  * first before referencing it in the sql statement.  *  *<p>Internally we may new the {@code SqlUserDefinedTypeNameSpec} instance directly  * for some sql dialects during rel-to-sql conversion.  */
end_comment

begin_class
specifier|public
class|class
name|SqlUserDefinedTypeNameSpec
extends|extends
name|SqlTypeNameSpec
block|{
comment|/**    * Create a SqlUserDefinedTypeNameSpec instance.    *    * @param typeName Type name as SQL identifier    * @param pos The parser position    */
specifier|public
name|SqlUserDefinedTypeNameSpec
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|typeName
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlUserDefinedTypeNameSpec
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|name
argument_list|,
name|pos
argument_list|)
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|)
block|{
comment|// The type name is a compound identifier, that means it is a UDT,
comment|// use SqlValidator to deduce its type from the Schema.
return|return
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|getTypeName
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
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
block|{
specifier|final
name|String
name|name
init|=
name|getTypeName
argument_list|()
operator|.
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
comment|// We're generating a type for an alien system. For example,
comment|// UNSIGNED is a built-in type in MySQL.
comment|// (Need a more elegant way than '_' of flagging this.)
name|writer
operator|.
name|keyword
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getTypeName
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlTypeNameSpec
name|spec
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|spec
operator|instanceof
name|SqlUserDefinedTypeNameSpec
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|spec
argument_list|)
return|;
block|}
name|SqlUserDefinedTypeNameSpec
name|that
init|=
operator|(
name|SqlUserDefinedTypeNameSpec
operator|)
name|spec
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|getTypeName
argument_list|()
operator|.
name|equalsDeep
argument_list|(
name|that
operator|.
name|getTypeName
argument_list|()
argument_list|,
name|litmus
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|spec
argument_list|)
return|;
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlUserDefinedTypeNameSpec.java
end_comment

end_unit

