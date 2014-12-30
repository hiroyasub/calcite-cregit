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
name|interpreter
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
name|rel
operator|.
name|RelFieldCollation
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
name|rex
operator|.
name|RexLiteral
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
name|base
operator|.
name|Function
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
name|Iterables
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
name|Lists
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
name|Ordering
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Interpreter node that implements a  * {@link org.apache.calcite.rel.core.Sort}.  */
end_comment

begin_class
specifier|public
class|class
name|SortNode
extends|extends
name|AbstractSingleNode
argument_list|<
name|Sort
argument_list|>
block|{
specifier|public
name|SortNode
parameter_list|(
name|Interpreter
name|interpreter
parameter_list|,
name|Sort
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|interpreter
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|offset
init|=
name|rel
operator|.
name|offset
operator|==
literal|null
condition|?
literal|0
else|:
operator|(
operator|(
name|BigDecimal
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|rel
operator|.
name|offset
operator|)
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|fetch
init|=
name|rel
operator|.
name|fetch
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|BigDecimal
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|rel
operator|.
name|fetch
operator|)
operator|.
name|getValue
argument_list|()
operator|)
operator|.
name|intValue
argument_list|()
decl_stmt|;
comment|// In pure limit mode. No sort required.
name|Row
name|row
decl_stmt|;
name|loop
label|:
if|if
condition|(
name|rel
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|offset
condition|;
name|i
operator|++
control|)
block|{
name|row
operator|=
name|source
operator|.
name|receive
argument_list|()
expr_stmt|;
if|if
condition|(
name|row
operator|==
literal|null
condition|)
block|{
break|break
name|loop
break|;
block|}
block|}
if|if
condition|(
name|fetch
operator|>=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fetch
operator|&&
operator|(
name|row
operator|=
name|source
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
while|while
condition|(
operator|(
name|row
operator|=
name|source
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// Build a sorted collection.
specifier|final
name|List
argument_list|<
name|Row
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|row
operator|=
name|source
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|,
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|int
name|end
init|=
name|fetch
operator|<
literal|0
operator|||
name|offset
operator|+
name|fetch
operator|>
name|list
operator|.
name|size
argument_list|()
condition|?
name|list
operator|.
name|size
argument_list|()
else|:
name|offset
operator|+
name|fetch
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|offset
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|sink
operator|.
name|end
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Comparator
argument_list|<
name|Row
argument_list|>
name|comparator
parameter_list|()
block|{
if|if
condition|(
name|rel
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|comparator
argument_list|(
name|rel
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
return|return
name|Ordering
operator|.
name|compound
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|rel
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|RelFieldCollation
argument_list|,
name|Comparator
argument_list|<
name|?
super|super
name|Row
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Comparator
argument_list|<
name|?
super|super
name|Row
argument_list|>
name|apply
parameter_list|(
name|RelFieldCollation
name|input
parameter_list|)
block|{
return|return
name|comparator
argument_list|(
name|input
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|compare
parameter_list|(
name|Comparable
name|c1
parameter_list|,
name|Comparable
name|c2
parameter_list|,
name|int
name|nullComparison
parameter_list|)
block|{
if|if
condition|(
name|c1
operator|==
name|c2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|else if
condition|(
name|c1
operator|==
literal|null
condition|)
block|{
return|return
name|nullComparison
return|;
block|}
if|else if
condition|(
name|c2
operator|==
literal|null
condition|)
block|{
return|return
operator|-
name|nullComparison
return|;
block|}
else|else
block|{
comment|//noinspection unchecked
return|return
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
return|;
block|}
block|}
specifier|private
name|Comparator
argument_list|<
name|Row
argument_list|>
name|comparator
parameter_list|(
specifier|final
name|RelFieldCollation
name|fieldCollation
parameter_list|)
block|{
specifier|final
name|int
name|nullComparison
init|=
name|getNullComparison
argument_list|(
name|fieldCollation
operator|.
name|nullDirection
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|fieldCollation
operator|.
name|direction
condition|)
block|{
case|case
name|ASCENDING
case|:
return|return
operator|new
name|Comparator
argument_list|<
name|Row
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Row
name|o1
parameter_list|,
name|Row
name|o2
parameter_list|)
block|{
specifier|final
name|int
name|x
init|=
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
specifier|final
name|Comparable
name|c1
init|=
operator|(
name|Comparable
operator|)
name|o1
operator|.
name|getValues
argument_list|()
index|[
name|x
index|]
decl_stmt|;
specifier|final
name|Comparable
name|c2
init|=
operator|(
name|Comparable
operator|)
name|o2
operator|.
name|getValues
argument_list|()
index|[
name|x
index|]
decl_stmt|;
return|return
name|SortNode
operator|.
name|compare
argument_list|(
name|c1
argument_list|,
name|c2
argument_list|,
name|nullComparison
argument_list|)
return|;
block|}
block|}
return|;
default|default:
return|return
operator|new
name|Comparator
argument_list|<
name|Row
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|Row
name|o1
parameter_list|,
name|Row
name|o2
parameter_list|)
block|{
specifier|final
name|int
name|x
init|=
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
specifier|final
name|Comparable
name|c1
init|=
operator|(
name|Comparable
operator|)
name|o1
operator|.
name|getValues
argument_list|()
index|[
name|x
index|]
decl_stmt|;
specifier|final
name|Comparable
name|c2
init|=
operator|(
name|Comparable
operator|)
name|o2
operator|.
name|getValues
argument_list|()
index|[
name|x
index|]
decl_stmt|;
return|return
name|SortNode
operator|.
name|compare
argument_list|(
name|c2
argument_list|,
name|c1
argument_list|,
operator|-
name|nullComparison
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
specifier|private
name|int
name|getNullComparison
parameter_list|(
name|RelFieldCollation
operator|.
name|NullDirection
name|nullDirection
parameter_list|)
block|{
switch|switch
condition|(
name|nullDirection
condition|)
block|{
case|case
name|FIRST
case|:
return|return
operator|-
literal|1
return|;
case|case
name|UNSPECIFIED
case|:
case|case
name|LAST
case|:
return|return
literal|1
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|nullDirection
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SortNode.java
end_comment

end_unit

