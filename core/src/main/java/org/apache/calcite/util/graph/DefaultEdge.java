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
name|graph
package|;
end_package

begin_comment
comment|/**  * Default implementation of Edge.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultEdge
block|{
specifier|public
specifier|final
name|Object
name|source
decl_stmt|;
specifier|public
specifier|final
name|Object
name|target
decl_stmt|;
specifier|public
name|DefaultEdge
parameter_list|(
name|Object
name|source
parameter_list|,
name|Object
name|target
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|source
operator|.
name|hashCode
argument_list|()
operator|*
literal|31
operator|+
name|target
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|DefaultEdge
operator|&&
operator|(
operator|(
name|DefaultEdge
operator|)
name|obj
operator|)
operator|.
name|source
operator|.
name|equals
argument_list|(
name|source
argument_list|)
operator|&&
operator|(
operator|(
name|DefaultEdge
operator|)
name|obj
operator|)
operator|.
name|target
operator|.
name|equals
argument_list|(
name|target
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|DirectedGraph
operator|.
name|EdgeFactory
argument_list|<
name|V
argument_list|,
name|DefaultEdge
argument_list|>
name|factory
parameter_list|()
block|{
return|return
name|DefaultEdge
operator|::
operator|new
return|;
block|}
block|}
end_class

begin_comment
comment|// End DefaultEdge.java
end_comment

end_unit

