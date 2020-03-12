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
name|SqlNode
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
name|SqlParser
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

begin_comment
comment|/** Define column strategies for the "VIRTUALCOLUMNS" table. */
end_comment

begin_class
specifier|public
class|class
name|VirtualColumnsExpressionFactory
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
literal|3
case|:
return|return
name|ColumnStrategy
operator|.
name|STORED
return|;
case|case
literal|4
case|:
return|return
name|ColumnStrategy
operator|.
name|VIRTUAL
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
if|if
condition|(
name|iColumn
operator|==
literal|4
condition|)
block|{
specifier|final
name|SqlNode
name|node
init|=
name|context
operator|.
name|parseExpression
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|,
literal|"A + 1"
argument_list|)
decl_stmt|;
comment|// Actually we should validate the node with physical schema,
comment|// here full table schema(includes the virtual columns) also works
comment|// because the expression "A + 1" does not reference any virtual column.
specifier|final
name|SqlNode
name|validated
init|=
name|context
operator|.
name|validateExpression
argument_list|(
name|table
operator|.
name|getRowType
argument_list|()
argument_list|,
name|node
argument_list|)
decl_stmt|;
return|return
name|context
operator|.
name|convertExpression
argument_list|(
name|validated
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|newColumnDefaultValue
argument_list|(
name|table
argument_list|,
name|iColumn
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

