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
name|adapter
operator|.
name|enumerable
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
name|linq4j
operator|.
name|function
operator|.
name|Experimental
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
name|plan
operator|.
name|Convention
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
name|convert
operator|.
name|ConverterRule
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
name|logical
operator|.
name|LogicalTableSpool
import|;
end_import

begin_comment
comment|/**  * Rule to convert a {@link LogicalTableSpool} into an {@link EnumerableTableSpool}.  *  *<p>NOTE: The current API is experimental and subject to change without notice.</p>  */
end_comment

begin_class
annotation|@
name|Experimental
specifier|public
class|class
name|EnumerableTableSpoolRule
extends|extends
name|ConverterRule
block|{
name|EnumerableTableSpoolRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalTableSpool
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableTableSpoolRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|LogicalTableSpool
name|spool
init|=
operator|(
name|LogicalTableSpool
operator|)
name|rel
decl_stmt|;
return|return
name|EnumerableTableSpool
operator|.
name|create
argument_list|(
name|convert
argument_list|(
name|spool
operator|.
name|getInput
argument_list|()
argument_list|,
name|spool
operator|.
name|getInput
argument_list|()
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|,
name|spool
operator|.
name|readType
argument_list|,
name|spool
operator|.
name|writeType
argument_list|,
name|spool
operator|.
name|getTableName
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableTableSpoolRule.java
end_comment

end_unit

