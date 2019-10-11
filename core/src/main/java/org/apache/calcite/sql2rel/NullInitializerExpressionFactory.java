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
name|SqlFunction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link InitializerExpressionFactory} that always supplies NULL.  */
end_comment

begin_class
specifier|public
class|class
name|NullInitializerExpressionFactory
implements|implements
name|InitializerExpressionFactory
block|{
specifier|public
specifier|static
specifier|final
name|InitializerExpressionFactory
name|INSTANCE
init|=
operator|new
name|NullInitializerExpressionFactory
argument_list|()
decl_stmt|;
specifier|public
name|NullInitializerExpressionFactory
parameter_list|()
block|{
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|boolean
name|isGeneratedAlways
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
name|generationStrategy
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|)
condition|)
block|{
case|case
name|VIRTUAL
case|:
case|case
name|STORED
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
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
return|return
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|iColumn
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|?
name|ColumnStrategy
operator|.
name|NULLABLE
else|:
name|ColumnStrategy
operator|.
name|NOT_NULLABLE
return|;
block|}
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
name|RelDataType
name|fieldType
init|=
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|iColumn
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|context
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeNullLiteral
argument_list|(
name|fieldType
argument_list|)
return|;
block|}
specifier|public
name|BiFunction
argument_list|<
name|InitializerContext
argument_list|,
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|postExpressionConversionHook
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|RexNode
name|newAttributeInitializer
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlFunction
name|constructor
parameter_list|,
name|int
name|iAttribute
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|constructorArgs
parameter_list|,
name|InitializerContext
name|context
parameter_list|)
block|{
specifier|final
name|RelDataType
name|fieldType
init|=
name|type
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|iAttribute
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
return|return
name|context
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeNullLiteral
argument_list|(
name|fieldType
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End NullInitializerExpressionFactory.java
end_comment

end_unit

