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
name|jdbc
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
name|config
operator|.
name|CalciteConnectionConfigImpl
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
name|prepare
operator|.
name|CalciteCatalogReader
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
name|SqlConformanceEnum
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
name|SqlValidatorImpl
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
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * A SqlValidator with schema and type factory of the given  * {@link org.apache.calcite.jdbc.CalcitePrepare.Context}.  *  *<p>This class is only used to derive data type for DDL sql node.  * Usually we deduce query sql node data type(i.e. the {@code SqlSelect})  * during the validation phrase. DDL nodes don't have validation,  * they can be executed directly through  * {@link org.apache.calcite.sql.SqlExecutableStatement#execute(CalcitePrepare.Context)}.  * During the execution, {@link org.apache.calcite.sql.SqlDataTypeSpec} uses  * this validator to derive its type.  */
end_comment

begin_class
specifier|public
class|class
name|ContextSqlValidator
extends|extends
name|SqlValidatorImpl
block|{
comment|/**    * Create a {@code ContextSqlValidator}.    * @param context Prepare context.    * @param mutable Whether to get the mutable schema.    */
specifier|public
name|ContextSqlValidator
parameter_list|(
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|,
name|boolean
name|mutable
parameter_list|)
block|{
name|super
argument_list|(
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
argument_list|,
name|getCatalogReader
argument_list|(
name|context
argument_list|,
name|mutable
argument_list|)
argument_list|,
name|context
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|CalciteCatalogReader
name|getCatalogReader
parameter_list|(
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|,
name|boolean
name|mutable
parameter_list|)
block|{
return|return
operator|new
name|CalciteCatalogReader
argument_list|(
name|mutable
condition|?
name|context
operator|.
name|getMutableRootSchema
argument_list|()
else|:
name|context
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|context
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
operator|new
name|CalciteConnectionConfigImpl
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ContextSqlValidator.java
end_comment

end_unit
