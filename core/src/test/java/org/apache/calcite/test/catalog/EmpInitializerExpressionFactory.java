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
name|test
operator|.
name|catalog
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
name|plan
operator|.
name|RelOptTable
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
name|schema
operator|.
name|ColumnStrategy
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
name|SqlTypeName
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
name|sql2rel
operator|.
name|InitializerContext
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
name|sql2rel
operator|.
name|NullInitializerExpressionFactory
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
comment|/** Default values for the "EMPDEFAULTS" table. */
end_comment

begin_class
class|class
name|EmpInitializerExpressionFactory
extends|extends
name|NullInitializerExpressionFactory
block|{
annotation|@
name|Override
specifier|public
name|ColumnStrategy
name|generationStrategy
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|)
block|{
switch|switch
condition|(
name|iColumn
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|5
case|:
return|return
name|ColumnStrategy
operator|.
name|DEFAULT
return|;
default|default:
return|return
name|super
operator|.
name|generationStrategy
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|newColumnDefaultValue
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|int
name|iColumn
parameter_list|,
name|InitializerContext
name|context
parameter_list|)
block|{
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|context
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|iColumn
condition|)
block|{
case|case
literal|0
case|:
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|123
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|"Bob"
argument_list|)
return|;
case|case
literal|5
case|:
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
literal|555
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
return|;
default|default:
return|return
name|rexBuilder
operator|.
name|constantNull
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End EmpInitializerExpressionFactory.java
end_comment

end_unit
