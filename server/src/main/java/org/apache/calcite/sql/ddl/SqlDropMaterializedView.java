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
name|ddl
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
name|jdbc
operator|.
name|CalcitePrepare
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
name|jdbc
operator|.
name|CalciteSchema
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
name|materialize
operator|.
name|MaterializationKey
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
name|materialize
operator|.
name|MaterializationService
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
name|Table
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
name|Wrapper
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
name|SqlIdentifier
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
name|SqlOperator
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
name|SqlSpecialOperator
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
name|util
operator|.
name|Pair
import|;
end_import

begin_comment
comment|/**  * Parse tree for {@code DROP MATERIALIZED VIEW} statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlDropMaterializedView
extends|extends
name|SqlDropObject
block|{
specifier|private
specifier|static
specifier|final
name|SqlOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"DROP MATERIALIZED VIEW"
argument_list|,
name|SqlKind
operator|.
name|DROP_MATERIALIZED_VIEW
argument_list|)
decl_stmt|;
comment|/** Creates a SqlDropMaterializedView. */
name|SqlDropMaterializedView
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|ifExists
parameter_list|,
name|SqlIdentifier
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|OPERATOR
argument_list|,
name|pos
argument_list|,
name|ifExists
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|)
block|{
specifier|final
name|Pair
argument_list|<
name|CalciteSchema
argument_list|,
name|String
argument_list|>
name|pair
init|=
name|SqlDdlNodes
operator|.
name|schema
argument_list|(
name|context
argument_list|,
literal|true
argument_list|,
name|name
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|pair
operator|.
name|left
operator|.
name|plus
argument_list|()
operator|.
name|getTable
argument_list|(
name|pair
operator|.
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
comment|// Materialized view exists.
name|super
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|Wrapper
condition|)
block|{
specifier|final
name|MaterializationKey
name|materializationKey
init|=
operator|(
operator|(
name|Wrapper
operator|)
name|table
operator|)
operator|.
name|unwrap
argument_list|(
name|MaterializationKey
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|materializationKey
operator|!=
literal|null
condition|)
block|{
name|MaterializationService
operator|.
name|instance
argument_list|()
operator|.
name|removeMaterialization
argument_list|(
name|materializationKey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlDropMaterializedView.java
end_comment

end_unit
