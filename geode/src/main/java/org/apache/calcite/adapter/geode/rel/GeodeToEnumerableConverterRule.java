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
name|geode
operator|.
name|rel
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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableConvention
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

begin_comment
comment|/**  * Rule to convert a relational expression from  * {@link GeodeRel#CONVENTION} to {@link EnumerableConvention}.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeToEnumerableConverterRule
extends|extends
name|ConverterRule
block|{
specifier|public
specifier|static
specifier|final
name|ConverterRule
name|INSTANCE
init|=
operator|new
name|GeodeToEnumerableConverterRule
argument_list|()
decl_stmt|;
specifier|private
name|GeodeToEnumerableConverterRule
parameter_list|()
block|{
name|super
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|GeodeRel
operator|.
name|CONVENTION
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"GeodeToEnumerableConverterRule"
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
name|RelTraitSet
name|newTraitSet
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|getOutConvention
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeodeToEnumerableConverter
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newTraitSet
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
end_class

end_unit

