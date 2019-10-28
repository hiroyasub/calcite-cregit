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
name|rel
operator|.
name|metadata
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
name|hep
operator|.
name|HepRelVertex
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
name|volcano
operator|.
name|RelSubset
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
name|core
operator|.
name|Aggregate
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
name|Calc
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
name|Correlate
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
name|Exchange
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
name|Filter
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
name|Intersect
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
name|Join
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
name|Match
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
name|Minus
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
name|Project
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
name|Sample
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
name|TableModify
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
name|TableScan
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
name|Union
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
name|Values
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
name|Window
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
name|BuiltInMethod
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
name|Util
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
name|ArrayListMultimap
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
name|Multimap
import|;
end_import

begin_comment
comment|/**  * RelMdNodeTypeCount supplies a default implementation of  * {@link RelMetadataQuery#getNodeTypes} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdNodeTypes
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|NodeTypes
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|RelMetadataProvider
name|SOURCE
init|=
name|ReflectiveRelMetadataProvider
operator|.
name|reflectiveSource
argument_list|(
name|BuiltInMethod
operator|.
name|NODE_TYPES
operator|.
name|method
argument_list|,
operator|new
name|RelMdNodeTypes
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|NodeTypes
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|NodeTypes
operator|.
name|DEF
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.NodeTypes#getNodeTypes()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#getNodeTypes(RelNode)    */
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|RelNode
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|HepRelVertex
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getNodeTypes
argument_list|(
name|rel
operator|.
name|getCurrentRel
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|RelSubset
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getNodeTypes
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|rel
operator|.
name|getBest
argument_list|()
argument_list|,
name|rel
operator|.
name|getOriginal
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Union
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Union
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Intersect
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Intersect
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Minus
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Minus
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Filter
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Calc
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Calc
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Project
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Sort
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Join
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Aggregate
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|TableScan
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Values
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Values
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|TableModify
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|TableModify
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Exchange
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Exchange
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Sample
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Sample
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Correlate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Correlate
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Window
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Window
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|Match
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|getNodeTypes
argument_list|(
name|rel
argument_list|,
name|Match
operator|.
name|class
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|getNodeTypes
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|c
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|nodeTypeCount
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|partialNodeTypeCount
init|=
name|mq
operator|.
name|getNodeTypes
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|partialNodeTypeCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|nodeTypeCount
operator|.
name|putAll
argument_list|(
name|partialNodeTypeCount
argument_list|)
expr_stmt|;
block|}
name|nodeTypeCount
operator|.
name|put
argument_list|(
name|c
argument_list|,
name|rel
argument_list|)
expr_stmt|;
return|return
name|nodeTypeCount
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdNodeTypes.java
end_comment

end_unit

