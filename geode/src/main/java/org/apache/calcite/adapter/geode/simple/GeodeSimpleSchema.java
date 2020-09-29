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
name|simple
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
name|geode
operator|.
name|util
operator|.
name|GeodeUtils
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
name|impl
operator|.
name|AbstractSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|Region
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|client
operator|.
name|ClientCache
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
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
name|util
operator|.
name|GeodeUtils
operator|.
name|autodetectRelTypeFromRegion
import|;
end_import

begin_comment
comment|/**  * Geode Simple Schema.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeSimpleSchema
extends|extends
name|AbstractSchema
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|String
name|locatorHost
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|int
name|locatorPort
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|String
index|[]
name|regionNames
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|String
name|pdxAutoSerializerPackageExp
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|ClientCache
name|clientCache
decl_stmt|;
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|tableMap
decl_stmt|;
specifier|public
name|GeodeSimpleSchema
parameter_list|(
name|String
name|locatorHost
parameter_list|,
name|int
name|locatorPort
parameter_list|,
name|String
index|[]
name|regionNames
parameter_list|,
name|String
name|pdxAutoSerializerPackageExp
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|locatorHost
operator|=
name|locatorHost
expr_stmt|;
name|this
operator|.
name|locatorPort
operator|=
name|locatorPort
expr_stmt|;
name|this
operator|.
name|regionNames
operator|=
name|regionNames
expr_stmt|;
name|this
operator|.
name|pdxAutoSerializerPackageExp
operator|=
name|pdxAutoSerializerPackageExp
expr_stmt|;
name|this
operator|.
name|clientCache
operator|=
name|GeodeUtils
operator|.
name|createClientCache
argument_list|(
name|locatorHost
argument_list|,
name|locatorPort
argument_list|,
name|pdxAutoSerializerPackageExp
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
if|if
condition|(
name|tableMap
operator|==
literal|null
condition|)
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|regionName
range|:
name|regionNames
control|)
block|{
name|Region
name|region
init|=
name|GeodeUtils
operator|.
name|createRegion
argument_list|(
name|clientCache
argument_list|,
name|regionName
argument_list|)
decl_stmt|;
name|Table
name|table
init|=
operator|new
name|GeodeSimpleScannableTable
argument_list|(
name|regionName
argument_list|,
name|autodetectRelTypeFromRegion
argument_list|(
name|region
argument_list|)
argument_list|,
name|clientCache
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|regionName
argument_list|,
name|table
argument_list|)
expr_stmt|;
block|}
name|tableMap
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
name|tableMap
return|;
block|}
block|}
end_class

end_unit

