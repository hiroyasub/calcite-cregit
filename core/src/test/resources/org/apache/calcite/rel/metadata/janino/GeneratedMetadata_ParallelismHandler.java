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
name|GeneratedMetadata_ParallelismHandler
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
name|Parallelism
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
literal|"Boolean Handler.isPhaseTransition()"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Object
name|methodKey1
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
literal|"Integer Handler.splitCount()"
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
name|RelMdParallelism
name|provider1
decl_stmt|;
specifier|public
name|GeneratedMetadata_ParallelismHandler
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
name|RelMdParallelism
name|provider1
parameter_list|)
block|{
name|this
operator|.
name|provider1
operator|=
name|provider1
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
name|provider1
operator|.
name|getDef
argument_list|()
return|;
block|}
specifier|public
name|java
operator|.
name|lang
operator|.
name|Boolean
name|isPhaseTransition
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
name|java
operator|.
name|lang
operator|.
name|Boolean
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
name|java
operator|.
name|lang
operator|.
name|Boolean
name|x
init|=
name|isPhaseTransition_
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
name|java
operator|.
name|lang
operator|.
name|Boolean
name|isPhaseTransition_
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
name|rel
operator|.
name|core
operator|.
name|Exchange
condition|)
block|{
return|return
name|provider1
operator|.
name|isPhaseTransition
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
name|Exchange
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
name|provider1
operator|.
name|isPhaseTransition
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
name|provider1
operator|.
name|isPhaseTransition
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
name|RelNode
condition|)
block|{
return|return
name|provider1
operator|.
name|isPhaseTransition
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
literal|"No handler for method [public abstract java.lang.Boolean org.apache.calcite.rel.metadata.BuiltInMetadata$Parallelism$Handler.isPhaseTransition(org.apache.calcite.rel.RelNode,org.apache.calcite.rel.metadata.RelMetadataQuery)] applied to argument of type ["
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
specifier|public
name|java
operator|.
name|lang
operator|.
name|Integer
name|splitCount
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
name|methodKey1
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
name|java
operator|.
name|lang
operator|.
name|Integer
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
name|java
operator|.
name|lang
operator|.
name|Integer
name|x
init|=
name|splitCount_
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
name|java
operator|.
name|lang
operator|.
name|Integer
name|splitCount_
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
name|rel
operator|.
name|RelNode
condition|)
block|{
return|return
name|provider1
operator|.
name|splitCount
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
literal|"No handler for method [public abstract java.lang.Integer org.apache.calcite.rel.metadata.BuiltInMetadata$Parallelism$Handler.splitCount(org.apache.calcite.rel.RelNode,org.apache.calcite.rel.metadata.RelMetadataQuery)] applied to argument of type ["
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

