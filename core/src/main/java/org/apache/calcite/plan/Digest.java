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
name|plan
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
name|hint
operator|.
name|Hintable
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
name|type
operator|.
name|RelDataType
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
name|Pair
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
name|Collections
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
name|java
operator|.
name|util
operator|.
name|Objects
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
comment|/**  * A short description of relational expression's type, inputs, and  * other properties. The digest uniquely identifies the node; another node  * is equivalent if and only if it has the same value.  *  *<p>Row type is part of the digest for the rare occasion that similar  * expressions have different types, e.g. variants of  * {@code Project(child=rel#1, a=null)} where a is a null INTEGER or a  * null VARCHAR(10). Row type is represented as fieldTypes only, so {@code RelNode}  * that differ with field names only are treated equal.  * For instance, {@code Project(input=rel#1,empid=$0)} and {@code Project(input=rel#1,deptno=$0)}  * are equal.  *  *<p>Computed by {@code org.apache.calcite.rel.AbstractRelNode#computeDigest},  * assigned by {@link org.apache.calcite.rel.AbstractRelNode#onRegister},  * returned by {@link org.apache.calcite.rel.AbstractRelNode#getDigest()}.  */
end_comment

begin_class
specifier|public
class|class
name|Digest
implements|implements
name|Comparable
argument_list|<
name|Digest
argument_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|hashCode
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|items
decl_stmt|;
specifier|private
specifier|final
name|RelNode
name|rel
decl_stmt|;
comment|// Used for debugging, computed lazily.
specifier|private
name|String
name|digest
init|=
literal|null
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a digest with given rel and properties.    *    * @param rel   The rel    * @param items The properties, e.g. the inputs, the type, the traits and so on    */
specifier|private
name|Digest
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|items
parameter_list|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|items
operator|=
name|normalizeContents
argument_list|(
name|items
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|computeIdentity
argument_list|(
name|rel
argument_list|,
name|this
operator|.
name|items
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a digest with given rel, the digest is computed as simple,    * see {@link #simpleRelDigest(RelNode)}.    */
specifier|private
name|Digest
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|this
argument_list|(
name|rel
argument_list|,
name|simpleRelDigest
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a digest with given rel and string format digest. */
specifier|private
name|Digest
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
name|digest
parameter_list|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|items
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|digest
expr_stmt|;
name|this
operator|.
name|hashCode
operator|=
name|this
operator|.
name|digest
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the identity of this digest which is used to speedup hashCode and equals. */
specifier|private
specifier|static
name|int
name|computeIdentity
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|contents
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|collect
argument_list|(
name|rel
argument_list|,
name|contents
argument_list|,
literal|false
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Collects the items used for {@link #hashCode} and {@link #equals}.    *    *<p>Generally, the items used for hashCode and equals should be the same. The exception    * is the row type of the relational expression: the row type is needed because during    * planning, new equivalent rels may be produced with changed fields nullability    * (i.e. most of them comes from the rex simplify or constant reduction).    * This expects to be rare case, so the hashcode is computed without row type    * but when it conflicts, we compare with the row type involved(sans field names).    *    * @param rel      The rel to compute digest    * @param contents The rel properties should be considered in digest    * @param withType Whether to involve the row type    */
specifier|private
specifier|static
name|Object
index|[]
name|collect
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|contents
parameter_list|,
name|boolean
name|withType
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|hashCodeItems
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// The type name.
name|hashCodeItems
operator|.
name|add
argument_list|(
name|rel
operator|.
name|getRelTypeName
argument_list|()
argument_list|)
expr_stmt|;
comment|// The traits.
name|hashCodeItems
operator|.
name|addAll
argument_list|(
name|rel
operator|.
name|getTraitSet
argument_list|()
argument_list|)
expr_stmt|;
comment|// The hints.
if|if
condition|(
name|rel
operator|instanceof
name|Hintable
condition|)
block|{
name|hashCodeItems
operator|.
name|addAll
argument_list|(
operator|(
operator|(
name|Hintable
operator|)
name|rel
operator|)
operator|.
name|getHints
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|withType
condition|)
block|{
comment|// The row type sans field names.
name|RelDataType
name|relType
init|=
name|rel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
if|if
condition|(
name|relType
operator|.
name|isStruct
argument_list|()
condition|)
block|{
name|hashCodeItems
operator|.
name|addAll
argument_list|(
name|Pair
operator|.
name|right
argument_list|(
name|relType
operator|.
name|getFieldList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Make a decision here because
comment|// some downstream projects have custom rel type which has no explicit fields.
name|hashCodeItems
operator|.
name|add
argument_list|(
name|relType
argument_list|)
expr_stmt|;
block|}
block|}
comment|// The rel node contents(e.g. the inputs or exprs).
name|hashCodeItems
operator|.
name|addAll
argument_list|(
name|contents
argument_list|)
expr_stmt|;
return|return
name|hashCodeItems
operator|.
name|toArray
argument_list|()
return|;
block|}
comment|/** Normalizes the rel node properties, currently, just to replace the    * {@link RelNode} with a simple string format digest. **/
specifier|private
specifier|static
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|normalizeContents
parameter_list|(
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|items
parameter_list|)
block|{
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|normalized
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|item
range|:
name|items
control|)
block|{
if|if
condition|(
name|item
operator|.
name|right
operator|instanceof
name|RelNode
condition|)
block|{
name|RelNode
name|input
init|=
operator|(
name|RelNode
operator|)
name|item
operator|.
name|right
decl_stmt|;
name|normalized
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|item
operator|.
name|left
argument_list|,
name|simpleRelDigest
argument_list|(
name|input
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|normalized
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|normalized
return|;
block|}
comment|/**    * Returns a simple string format digest.    *    *<p>Currently, returns composition of class name and id.    *    * @param rel The rel    */
specifier|private
specifier|static
name|String
name|simpleRelDigest
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|rel
operator|.
name|getRelTypeName
argument_list|()
operator|+
literal|'#'
operator|+
name|rel
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
literal|null
operator|!=
name|digest
condition|)
block|{
return|return
name|digest
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|rel
operator|.
name|getRelTypeName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|rel
operator|.
name|getTraitSet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|trait
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|item
range|:
name|items
control|)
block|{
if|if
condition|(
name|j
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|item
operator|.
name|left
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|item
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|digest
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|digest
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|Nonnull
name|Digest
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|?
literal|0
else|:
name|this
operator|.
name|rel
operator|.
name|getId
argument_list|()
operator|-
name|other
operator|.
name|rel
operator|.
name|getId
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Digest
name|that
init|=
operator|(
name|Digest
operator|)
name|o
decl_stmt|;
return|return
name|hashCode
operator|==
name|that
operator|.
name|hashCode
operator|&&
name|deepEquals
argument_list|(
name|that
argument_list|)
return|;
block|}
comment|/**    * The method is used to resolve hash conflict, in current 6000+ tests, there are about 8    * tests with conflict, so we do not cache the hash code items in order to    * reduce mem consumption.    */
specifier|private
name|boolean
name|deepEquals
parameter_list|(
name|Digest
name|other
parameter_list|)
block|{
name|Object
index|[]
name|thisItems
init|=
name|collect
argument_list|(
name|this
operator|.
name|rel
argument_list|,
name|this
operator|.
name|items
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Object
index|[]
name|thatItems
init|=
name|collect
argument_list|(
name|other
operator|.
name|rel
argument_list|,
name|other
operator|.
name|items
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisItems
operator|.
name|length
operator|!=
name|thatItems
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|thisItems
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|thisItems
index|[
name|i
index|]
argument_list|,
name|thatItems
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|hashCode
return|;
block|}
comment|/**    * Creates a digest with given rel and properties.    */
specifier|public
specifier|static
name|Digest
name|create
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|contents
parameter_list|)
block|{
return|return
operator|new
name|Digest
argument_list|(
name|rel
argument_list|,
name|contents
argument_list|)
return|;
block|}
comment|/**    * Creates a digest with given rel.    */
specifier|public
specifier|static
name|Digest
name|create
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|Digest
argument_list|(
name|rel
argument_list|)
return|;
block|}
comment|/**    * Creates a digest with given rel and string format digest    */
specifier|public
specifier|static
name|Digest
name|create
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
name|digest
parameter_list|)
block|{
return|return
operator|new
name|Digest
argument_list|(
name|rel
argument_list|,
name|digest
argument_list|)
return|;
block|}
comment|/**    * Instantiates a digest with solid string format digest, this digest should only    * be used as a initial.    */
specifier|public
specifier|static
name|Digest
name|initial
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|Digest
argument_list|(
name|rel
argument_list|,
name|simpleRelDigest
argument_list|(
name|rel
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit
