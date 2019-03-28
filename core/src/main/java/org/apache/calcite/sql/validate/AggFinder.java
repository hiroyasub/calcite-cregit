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
name|sql
operator|.
name|validate
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
name|sql
operator|.
name|SqlCall
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|SqlOperatorTable
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
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/** Visitor that looks for an aggregate function inside a tree of  * {@link SqlNode} objects and throws {@link Util.FoundOne} when it finds  * one. */
end_comment

begin_class
class|class
name|AggFinder
extends|extends
name|AggVisitor
block|{
comment|/**    * Creates an AggFinder.    *    * @param opTab Operator table    * @param over Whether to find windowed function calls {@code agg(x) OVER    *             windowSpec}    * @param aggregate Whether to find non-windowed aggregate calls    * @param group Whether to find group functions (e.g. {@code TUMBLE})    * @param delegate Finder to which to delegate when processing the arguments    * @param nameMatcher Whether to match the agg function case-sensitively    */
name|AggFinder
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|boolean
name|over
parameter_list|,
name|boolean
name|aggregate
parameter_list|,
name|boolean
name|group
parameter_list|,
name|AggFinder
name|delegate
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|over
argument_list|,
name|aggregate
argument_list|,
name|group
argument_list|,
name|delegate
argument_list|,
name|nameMatcher
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Finds an aggregate.    *    * @param node Parse tree to search    * @return First aggregate function in parse tree, or null if not found    */
specifier|public
name|SqlCall
name|findAgg
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
try|try
block|{
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|(
name|SqlCall
operator|)
name|e
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
specifier|public
name|SqlCall
name|findAgg
parameter_list|(
name|List
argument_list|<
name|SqlNode
argument_list|>
name|nodes
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|SqlNode
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|(
name|SqlCall
operator|)
name|e
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
specifier|protected
name|Void
name|found
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|call
argument_list|)
throw|;
block|}
comment|/** Creates a copy of this finder that has the same parameters as this,    * then returns the list of all aggregates found. */
name|Iterable
argument_list|<
name|SqlCall
argument_list|>
name|findAll
parameter_list|(
name|Iterable
argument_list|<
name|SqlNode
argument_list|>
name|nodes
parameter_list|)
block|{
specifier|final
name|AggIterable
name|aggIterable
init|=
operator|new
name|AggIterable
argument_list|(
name|opTab
argument_list|,
name|over
argument_list|,
name|aggregate
argument_list|,
name|group
argument_list|,
name|delegate
argument_list|,
name|nameMatcher
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|accept
argument_list|(
name|aggIterable
argument_list|)
expr_stmt|;
block|}
return|return
name|aggIterable
operator|.
name|calls
return|;
block|}
comment|/** Iterates over all aggregates. */
specifier|static
class|class
name|AggIterable
extends|extends
name|AggVisitor
implements|implements
name|Iterable
argument_list|<
name|SqlCall
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|SqlCall
argument_list|>
name|calls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|AggIterable
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|boolean
name|over
parameter_list|,
name|boolean
name|aggregate
parameter_list|,
name|boolean
name|group
parameter_list|,
name|AggFinder
name|delegate
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|over
argument_list|,
name|aggregate
argument_list|,
name|group
argument_list|,
name|delegate
argument_list|,
name|nameMatcher
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Void
name|found
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
name|calls
operator|.
name|add
argument_list|(
name|call
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Iterator
argument_list|<
name|SqlCall
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|calls
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AggFinder.java
end_comment

end_unit

