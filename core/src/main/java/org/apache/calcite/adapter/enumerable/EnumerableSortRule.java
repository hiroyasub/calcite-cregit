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
name|plan
operator|.
name|RelTraitSet
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
name|core
operator|.
name|Sort
import|;
end_import

begin_comment
comment|/**  * Rule to convert an {@link org.apache.calcite.rel.core.Sort} to an  * {@link EnumerableSort}.  */
end_comment

begin_class
class|class
name|EnumerableSortRule
extends|extends
name|ConverterRule
block|{
name|EnumerableSortRule
parameter_list|()
block|{
name|super
argument_list|(
name|Sort
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
literal|"EnumerableSortRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
operator|(
name|Sort
operator|)
name|rel
decl_stmt|;
if|if
condition|(
name|sort
operator|.
name|offset
operator|!=
literal|null
operator|||
name|sort
operator|.
name|fetch
operator|!=
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|sort
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
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|sort
operator|.
name|getInput
argument_list|()
decl_stmt|;
return|return
operator|new
name|EnumerableSort
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|input
argument_list|,
name|input
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
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableSortRule.java
end_comment

end_unit

