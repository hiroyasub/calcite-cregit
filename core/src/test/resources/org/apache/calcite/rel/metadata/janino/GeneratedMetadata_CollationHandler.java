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
operator|.
name|janino
package|;
end_package

begin_class
specifier|public
specifier|final
class|class
name|GeneratedMetadata_CollationHandler
implements|implements
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|BuiltInMetadata
operator|.
name|Collation
operator|.
name|Handler
block|{
specifier|private
specifier|final
name|Object
name|methodKey0
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|janino
operator|.
name|DescriptiveCacheKey
argument_list|(
literal|"ImmutableList Handler.collations()"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|RelMdCollation
name|provider0
decl_stmt|;
specifier|public
name|GeneratedMetadata_CollationHandler
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|RelMdCollation
name|provider0
parameter_list|)
block|{
name|this
operator|.
name|provider0
operator|=
name|provider0
expr_stmt|;
block|}
specifier|public
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|MetadataDef
name|getDef
parameter_list|()
block|{
return|return
name|provider0
operator|.
name|getDef
argument_list|()
return|;
block|}
specifier|public
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
name|collations
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
name|r
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
while|while
condition|(
name|r
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|DelegatingMetadataRel
condition|)
block|{
name|r
operator|=
operator|(
operator|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|DelegatingMetadataRel
operator|)
name|r
operator|)
operator|.
name|getMetadataDelegateRel
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Object
name|key
decl_stmt|;
name|key
operator|=
name|methodKey0
expr_stmt|;
specifier|final
name|Object
name|v
init|=
name|mq
operator|.
name|map
operator|.
name|get
argument_list|(
name|r
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|v
operator|==
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|NullSentinel
operator|.
name|ACTIVE
condition|)
block|{
throw|throw
operator|new
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|CyclicMetadataException
argument_list|()
throw|;
block|}
if|if
condition|(
name|v
operator|==
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|NullSentinel
operator|.
name|INSTANCE
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|(
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
operator|)
name|v
return|;
block|}
name|mq
operator|.
name|map
operator|.
name|put
argument_list|(
name|r
argument_list|,
name|key
argument_list|,
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|NullSentinel
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
name|x
init|=
name|collations_
argument_list|(
name|r
argument_list|,
name|mq
argument_list|)
decl_stmt|;
name|mq
operator|.
name|map
operator|.
name|put
argument_list|(
name|r
argument_list|,
name|key
argument_list|,
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|NullSentinel
operator|.
name|mask
argument_list|(
name|x
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|x
return|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Exception
name|e
parameter_list|)
block|{
name|mq
operator|.
name|map
operator|.
name|row
argument_list|(
name|r
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
name|collations_
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
name|r
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
if|if
condition|(
name|r
operator|instanceof
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
name|EnumerableCorrelate
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
name|EnumerableCorrelate
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
name|EnumerableHashJoin
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
name|EnumerableHashJoin
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
name|EnumerableMergeJoin
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
name|EnumerableMergeJoin
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
name|EnumerableMergeUnion
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
name|EnumerableMergeUnion
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
name|EnumerableNestedLoopJoin
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
name|EnumerableNestedLoopJoin
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcToEnumerableConverter
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcToEnumerableConverter
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
name|SortExchange
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
name|SortExchange
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
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
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
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
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
if|else if
condition|(
name|r
operator|instanceof
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
condition|)
block|{
return|return
name|provider0
operator|.
name|collations
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
operator|)
name|r
argument_list|,
name|mq
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
argument_list|(
literal|"No handler for method [public abstract com.google.common.collect.ImmutableList org.apache.calcite.rel.metadata.BuiltInMetadata$Collation$Handler.collations(org.apache.calcite.rel.RelNode,org.apache.calcite.rel.metadata.RelMetadataQuery)] applied to argument of type ["
operator|+
name|r
operator|.
name|getClass
argument_list|()
operator|+
literal|"]; we recommend you create a catch-all (RelNode) handler"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

