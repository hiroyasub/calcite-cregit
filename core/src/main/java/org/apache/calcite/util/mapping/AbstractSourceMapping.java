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
name|util
operator|.
name|mapping
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Simple implementation of  * {@link org.apache.calcite.util.mapping.Mappings.TargetMapping} where the  * number of sources and targets are specified as constructor parameters and you  * just need to implement one method,  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSourceMapping
extends|extends
name|Mappings
operator|.
name|AbstractMapping
implements|implements
name|Mapping
block|{
specifier|private
specifier|final
name|int
name|sourceCount
decl_stmt|;
specifier|private
specifier|final
name|int
name|targetCount
decl_stmt|;
specifier|public
name|AbstractSourceMapping
parameter_list|(
name|int
name|sourceCount
parameter_list|,
name|int
name|targetCount
parameter_list|)
block|{
name|this
operator|.
name|sourceCount
operator|=
name|sourceCount
expr_stmt|;
name|this
operator|.
name|targetCount
operator|=
name|targetCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSourceCount
parameter_list|()
block|{
return|return
name|sourceCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getTargetCount
parameter_list|()
block|{
return|return
name|targetCount
return|;
block|}
specifier|public
name|Mapping
name|inverse
parameter_list|()
block|{
return|return
name|Mappings
operator|.
name|invert
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|targetCount
return|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|MappingType
name|getMappingType
parameter_list|()
block|{
return|return
name|MappingType
operator|.
name|INVERSE_PARTIAL_FUNCTION
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|IntPair
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|IntPair
argument_list|>
argument_list|()
block|{
name|int
name|source
decl_stmt|;
name|int
name|target
init|=
operator|-
literal|1
decl_stmt|;
block|{
name|moveToNext
parameter_list|()
constructor_decl|;
block|}
specifier|private
name|void
name|moveToNext
parameter_list|()
block|{
while|while
condition|(
operator|++
name|target
operator|<
name|targetCount
condition|)
block|{
name|source
operator|=
name|getSourceOpt
argument_list|(
name|target
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|>=
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|target
operator|<
name|targetCount
return|;
block|}
specifier|public
name|IntPair
name|next
parameter_list|()
block|{
name|IntPair
name|p
init|=
operator|new
name|IntPair
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
decl_stmt|;
name|moveToNext
argument_list|()
expr_stmt|;
return|return
name|p
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"remove"
argument_list|)
throw|;
block|}
block|}
return|;
block|}
specifier|public
specifier|abstract
name|int
name|getSourceOpt
parameter_list|(
name|int
name|source
parameter_list|)
function_decl|;
block|}
end_class

begin_comment
comment|// End AbstractSourceMapping.java
end_comment

end_unit

