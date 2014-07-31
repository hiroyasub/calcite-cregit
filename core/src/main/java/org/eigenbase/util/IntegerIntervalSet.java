begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_comment
comment|/**  * A set of non-negative integers defined by a sequence of points, intervals,  * and exclusions.  */
end_comment

begin_class
specifier|public
class|class
name|IntegerIntervalSet
extends|extends
name|AbstractSet
argument_list|<
name|Integer
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|s
decl_stmt|;
specifier|private
name|IntegerIntervalSet
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|visit
parameter_list|(
name|String
name|s
parameter_list|,
name|Handler
name|handler
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|split
init|=
name|s
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s1
range|:
name|split
control|)
block|{
if|if
condition|(
name|s1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|boolean
name|exclude
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|s1
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|s1
operator|=
name|s1
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|exclude
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|String
index|[]
name|split1
init|=
name|s1
operator|.
name|split
argument_list|(
literal|"-"
argument_list|)
decl_stmt|;
if|if
condition|(
name|split1
operator|.
name|length
operator|==
literal|1
condition|)
block|{
specifier|final
name|int
name|n
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|split1
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|handler
operator|.
name|range
argument_list|(
name|n
argument_list|,
name|n
argument_list|,
name|exclude
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|n0
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|split1
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
specifier|final
name|int
name|n1
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|split1
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|handler
operator|.
name|range
argument_list|(
name|n0
argument_list|,
name|n1
argument_list|,
name|exclude
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Parses a range of integers expressed as a string. The string can contain    * non-negative integers separated by commas, ranges (represented by a    * hyphen between two integers), and exclusions (represented by a preceding    * hyphen). For example, "1,2,3-20,-7,-10-15,12".    *    *<p>Inclusions and exclusions are performed in the order that they are    * seen. For example, "1-10,-2-9,3-7,-4-6"</p> does contain 3, because it is    * included by "1-10", excluded by "-2-9" and last included by "3-7". But it    * does not include 4.    *    * @param s Range set    */
specifier|public
specifier|static
name|Set
argument_list|<
name|Integer
argument_list|>
name|of
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|IntegerIntervalSet
argument_list|(
name|s
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"NullableProblems"
argument_list|)
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumeratorIterator
argument_list|(
name|enumerator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|e
init|=
name|enumerator
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|moveNext
argument_list|()
condition|)
block|{
operator|++
name|n
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
specifier|private
name|Enumerator
argument_list|<
name|Integer
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|int
index|[]
name|bounds
init|=
block|{
name|Integer
operator|.
name|MAX_VALUE
block|,
name|Integer
operator|.
name|MIN_VALUE
block|}
decl_stmt|;
name|visit
argument_list|(
name|s
argument_list|,
operator|new
name|Handler
argument_list|()
block|{
specifier|public
name|void
name|range
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|exclude
parameter_list|)
block|{
if|if
condition|(
operator|!
name|exclude
condition|)
block|{
name|bounds
index|[
literal|0
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bounds
index|[
literal|0
index|]
argument_list|,
name|start
argument_list|)
expr_stmt|;
name|bounds
index|[
literal|1
index|]
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bounds
index|[
literal|1
index|]
argument_list|,
name|end
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
name|bounds
index|[
literal|0
index|]
operator|-
literal|1
decl_stmt|;
specifier|public
name|Integer
name|current
parameter_list|()
block|{
return|return
name|i
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
operator|++
name|i
operator|>
name|bounds
index|[
literal|1
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|i
operator|=
name|bounds
index|[
literal|0
index|]
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no resources
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|Number
operator|&&
name|contains
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
specifier|final
name|int
name|n
parameter_list|)
block|{
specifier|final
name|boolean
index|[]
name|bs
init|=
block|{
literal|false
block|}
decl_stmt|;
name|visit
argument_list|(
name|s
argument_list|,
operator|new
name|Handler
argument_list|()
block|{
specifier|public
name|void
name|range
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|exclude
parameter_list|)
block|{
if|if
condition|(
name|start
operator|<=
name|n
operator|&&
name|n
operator|<=
name|end
condition|)
block|{
name|bs
index|[
literal|0
index|]
operator|=
operator|!
name|exclude
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|bs
index|[
literal|0
index|]
return|;
block|}
specifier|private
interface|interface
name|Handler
block|{
name|void
name|range
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|,
name|boolean
name|exclude
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End IntegerIntervalSet.java
end_comment

end_unit

