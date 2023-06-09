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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Ord
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
name|linq4j
operator|.
name|tree
operator|.
name|Primitive
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
name|metadata
operator|.
name|CyclicMetadataException
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
name|metadata
operator|.
name|DelegatingMetadataRel
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
name|metadata
operator|.
name|NullSentinel
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|RexNode
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
name|runtime
operator|.
name|FlatLists
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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|rel
operator|.
name|metadata
operator|.
name|janino
operator|.
name|CodeGeneratorUtil
operator|.
name|argList
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
name|rel
operator|.
name|metadata
operator|.
name|janino
operator|.
name|CodeGeneratorUtil
operator|.
name|paramList
import|;
end_import

begin_comment
comment|/**  * Generates caching code for janino backed metadata.  */
end_comment

begin_class
class|class
name|CacheGeneratorUtil
block|{
specifier|private
name|CacheGeneratorUtil
parameter_list|()
block|{
block|}
specifier|static
name|void
name|cacheProperties
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|selectStrategy
argument_list|(
name|method
argument_list|)
operator|.
name|cacheProperties
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
block|}
specifier|static
name|void
name|cachedMethod
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|String
name|delRelClass
init|=
name|DelegatingMetadataRel
operator|.
name|class
operator|.
name|getName
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"  public "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"(\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      "
argument_list|)
operator|.
name|append
argument_list|(
name|RelNode
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" r,\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      "
argument_list|)
operator|.
name|append
argument_list|(
name|RelMetadataQuery
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" mq"
argument_list|)
expr_stmt|;
name|paramList
argument_list|(
name|buff
argument_list|,
name|method
argument_list|)
operator|.
name|append
argument_list|(
literal|") {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    while (r instanceof "
argument_list|)
operator|.
name|append
argument_list|(
name|delRelClass
argument_list|)
operator|.
name|append
argument_list|(
literal|") {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      r = (("
argument_list|)
operator|.
name|append
argument_list|(
name|delRelClass
argument_list|)
operator|.
name|append
argument_list|(
literal|") r).getMetadataDelegateRel();\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    final Object key;\n"
argument_list|)
expr_stmt|;
name|selectStrategy
argument_list|(
name|method
argument_list|)
operator|.
name|cacheKeyBlock
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"    final Object v = mq.map.get(r, key);\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    if (v != null) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      if (v == "
argument_list|)
operator|.
name|append
argument_list|(
name|NullSentinel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".ACTIVE) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"        throw new "
argument_list|)
operator|.
name|append
argument_list|(
name|CyclicMetadataException
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"();\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      if (v == "
argument_list|)
operator|.
name|append
argument_list|(
name|NullSentinel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".INSTANCE) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"        return null;\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      return ("
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|") v;\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    mq.map.put(r, key,"
argument_list|)
operator|.
name|append
argument_list|(
name|NullSentinel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".ACTIVE);\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    try {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      final "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" x = "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"_(r, mq"
argument_list|)
expr_stmt|;
name|argList
argument_list|(
name|buff
argument_list|,
name|method
argument_list|)
operator|.
name|append
argument_list|(
literal|");\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      mq.map.put(r, key, "
argument_list|)
operator|.
name|append
argument_list|(
name|NullSentinel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".mask(x));\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      return x;\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    } catch ("
argument_list|)
operator|.
name|append
argument_list|(
name|Exception
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" e) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      mq.map.row(r).clear();\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      throw e;\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  }\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|appendKeyName
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"methodKey"
argument_list|)
operator|.
name|append
argument_list|(
name|methodIndex
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|appendKeyName
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|int
name|methodIndex
parameter_list|,
name|String
name|arg
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"methodKey"
argument_list|)
operator|.
name|append
argument_list|(
name|methodIndex
argument_list|)
operator|.
name|append
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|CacheKeyStrategy
name|selectStrategy
parameter_list|(
name|Method
name|method
parameter_list|)
block|{
switch|switch
condition|(
name|method
operator|.
name|getParameterCount
argument_list|()
condition|)
block|{
case|case
literal|2
case|:
return|return
name|CacheKeyStrategy
operator|.
name|NO_ARG
return|;
case|case
literal|3
case|:
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|2
index|]
decl_stmt|;
if|if
condition|(
name|clazz
operator|.
name|equals
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|CacheKeyStrategy
operator|.
name|BOOLEAN_ARG
return|;
block|}
if|else if
condition|(
name|Enum
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
return|return
name|CacheKeyStrategy
operator|.
name|ENUM_ARG
return|;
block|}
if|else if
condition|(
name|clazz
operator|.
name|equals
argument_list|(
name|int
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|CacheKeyStrategy
operator|.
name|INT_ARG
return|;
block|}
else|else
block|{
return|return
name|CacheKeyStrategy
operator|.
name|DEFAULT
return|;
block|}
default|default:
return|return
name|CacheKeyStrategy
operator|.
name|DEFAULT
return|;
block|}
block|}
specifier|private
specifier|static
name|StringBuilder
name|newDescriptiveCacheKey
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|String
name|arg
parameter_list|)
block|{
return|return
name|buff
operator|.
name|append
argument_list|(
literal|"      new "
argument_list|)
operator|.
name|append
argument_list|(
name|DescriptiveCacheKey
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"(\""
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
operator|.
name|append
argument_list|(
name|arg
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
operator|.
name|append
argument_list|(
literal|"\");\n"
argument_list|)
return|;
block|}
comment|/**    * Generates a set of properties that are to be used by a fragment of code to    * efficiently create metadata keys.    */
specifier|private
enum|enum
name|CacheKeyStrategy
block|{
comment|/**      * Generates an immutable method key, then during each call instantiates a new list to all      * the arguments.      *      * Example:      *<code>      *     private final Object method_key_0 =      *        new org.apache.calcite.rel.metadata.janino.DescriptiveCacheKey("...");      *      *  ...      *      *   public java.lang.Double getDistinctRowCount(      *       org.apache.calcite.rel.RelNode r,      *       org.apache.calcite.rel.metadata.RelMetadataQuery mq,      *       org.apache.calcite.util.ImmutableBitSet a2,      *       org.apache.calcite.rex.RexNode a3) {      *     final Object key;      *     key = org.apache.calcite.runtime.FlatLists.of(method_key_0, org.apache.calcite.rel      * .metadata.NullSentinel.mask(a2), a3);      *     final Object v = mq.map.get(r, key);      *     if (v != null) {      *      ...      *</code>      */
name|DEFAULT
block|{
annotation|@
name|Override
name|void
name|cacheProperties
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|String
name|args
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Class
operator|::
name|getSimpleName
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|", "
argument_list|)
argument_list|)
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"  private final Object "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
expr_stmt|;
name|newDescriptiveCacheKey
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|cacheKeyBlock
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"    key = "
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|method
operator|.
name|getParameterCount
argument_list|()
operator|<
literal|6
condition|?
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|FlatLists
operator|.
name|class
else|:
name|ImmutableList
operator|.
name|class
operator|)
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".of("
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|safeArgList
argument_list|(
name|buff
argument_list|,
name|method
argument_list|)
operator|.
name|append
argument_list|(
literal|");\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Returns e.g. ", ignoreNulls". */
specifier|private
name|StringBuilder
name|safeArgList
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|)
block|{
comment|//We ignore the first 2 arguments since they are included other ways.
for|for
control|(
name|Ord
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|t
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|method
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
operator|.
name|subList
argument_list|(
literal|2
argument_list|,
name|method
operator|.
name|getParameterCount
argument_list|()
argument_list|)
control|)
block|{
if|if
condition|(
name|Primitive
operator|.
name|is
argument_list|(
name|t
operator|.
name|e
argument_list|)
operator|||
name|RexNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|t
operator|.
name|e
argument_list|)
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", a"
argument_list|)
operator|.
name|append
argument_list|(
name|t
operator|.
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|NullSentinel
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".mask(a"
argument_list|)
operator|.
name|append
argument_list|(
name|t
operator|.
name|i
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buff
return|;
block|}
block|}
block|,
comment|/**      * Generates an immutable key that is reused across all calls.      *      * Example:      *<code>      *     private final Object method_key_0 =      *       new org.apache.calcite.rel.metadata.janino.DescriptiveCacheKey("...");      *      *   ...      *      *   public java.lang.Double getPercentageOriginalRows(      *       org.apache.calcite.rel.RelNode r,      *       org.apache.calcite.rel.metadata.RelMetadataQuery mq) {      *     final Object key;      *     key = method_key_0;      *     final Object v = mq.map.get(r, key);      *</code>      */
name|NO_ARG
block|{
annotation|@
name|Override
name|void
name|cacheProperties
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"  private final Object "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
expr_stmt|;
name|newDescriptiveCacheKey
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|cacheKeyBlock
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"    key = "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|";\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**      * Generates immutable cache keys for metadata calls with single enum argument.      *      * Example:      *<code>      *   private final Object method_key_0Null =      *       new org.apache.calcite.rel.metadata.janino.DescriptiveCacheKey(      *         "Boolean isVisibleInExplain(null)");      *   private final Object[] method_key_0 =      *       org.apache.calcite.rel.metadata.janino.CacheUtil.generateEnum(      *         "Boolean isVisibleInExplain", org.apache.calcite.sql.SqlExplainLevel.values());      *      *   ...      *      *   public java.lang.Boolean isVisibleInExplain(      *       org.apache.calcite.rel.RelNode r,      *       org.apache.calcite.rel.metadata.RelMetadataQuery mq,      *       org.apache.calcite.sql.SqlExplainLevel a2) {      *     final Object key;      *     if (a2 == null) {      *       key = method_key_0Null;      *     } else {      *       key = method_key_0[a2.ordinal()];      *     }      *</code>      */
name|ENUM_ARG
block|{
annotation|@
name|Override
name|void
name|cacheKeyBlock
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"    if (a2 == null) {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      key = "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"Null"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|";\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    } else {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      key = "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"[a2.ordinal()];\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|cacheProperties
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
assert|assert
name|method
operator|.
name|getParameterCount
argument_list|()
operator|==
literal|3
assert|;
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|2
index|]
decl_stmt|;
assert|assert
name|Enum
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
assert|;
name|buff
operator|.
name|append
argument_list|(
literal|"  private final Object "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"Null"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
expr_stmt|;
name|newDescriptiveCacheKey
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
literal|"null"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  private final Object[] "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      "
argument_list|)
operator|.
name|append
argument_list|(
name|CacheUtil
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".generateEnum(\""
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\", "
argument_list|)
operator|.
name|append
argument_list|(
name|clazz
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".values());\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**      * Generates 2 immutable keys for functions that only take a single boolean arg.      *      * Example:      *<code>      *  private final Object method_key_0True =      *       new org.apache.calcite.rel.metadata.janino.DescriptiveCacheKey("...");      *   private final Object method_key_0False =      *       new org.apache.calcite.rel.metadata.janino.DescriptiveCacheKey("...");      *      *   ...      *      *   public java.util.Set getUniqueKeys(      *       org.apache.calcite.rel.RelNode r,      *       org.apache.calcite.rel.metadata.RelMetadataQuery mq,      *       boolean a2) {      *     final Object key;      *     key = a2 ? method_key_0True : method_key_0False;      *     final Object v = mq.map.get(r, key);      *     ...      *</code>      */
name|BOOLEAN_ARG
block|{
annotation|@
name|Override
name|void
name|cacheKeyBlock
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"    key = a2 ? "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"True"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" : "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"False"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|";\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|cacheProperties
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
assert|assert
name|method
operator|.
name|getParameterCount
argument_list|()
operator|==
literal|3
assert|;
assert|assert
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
assert|;
name|buff
operator|.
name|append
argument_list|(
literal|"  private final Object "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"True"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
expr_stmt|;
name|newDescriptiveCacheKey
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
literal|"true"
argument_list|)
operator|.
name|append
argument_list|(
literal|"  private final Object "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"False"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
expr_stmt|;
name|newDescriptiveCacheKey
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**      * Uses a flyweight for fixed range, otherwise instantiates a new list with the arguement in it.      *      * Example:      *<code>      *   private final Object method_key_0 =      *         new org.apache.calcite.rel.metadata.janino.DescriptiveCacheKey("...");      *   private final Object[] method_key_0FlyWeight =      *       org.apache.calcite.rel.metadata.janino.CacheUtil.generateRange(      *         "java.util.Set getColumnOrigins", -256, 256);      *      *   ...      *      *   public java.util.Set getColumnOrigins(      *       org.apache.calcite.rel.RelNode r,      *       org.apache.calcite.rel.metadata.RelMetadataQuery mq,      *       int a2) {      *     final Object key;      *     if (a2&gt;= -256&& a2&lt; 256) {      *       key = method_key_0FlyWeight[a2 + 256];      *     } else {      *       key = org.apache.calcite.runtime.FlatLists.of(method_key_0, a2);      *     }      *</code>      */
name|INT_ARG
block|{
specifier|private
specifier|final
name|int
name|min
init|=
operator|-
literal|256
decl_stmt|;
specifier|private
specifier|final
name|int
name|max
init|=
literal|256
decl_stmt|;
annotation|@
name|Override
name|void
name|cacheKeyBlock
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
assert|assert
name|method
operator|.
name|getParameterCount
argument_list|()
operator|==
literal|3
assert|;
assert|assert
name|method
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|2
index|]
operator|==
name|int
operator|.
name|class
assert|;
name|buff
operator|.
name|append
argument_list|(
literal|"    if (a2>= "
argument_list|)
operator|.
name|append
argument_list|(
name|min
argument_list|)
operator|.
name|append
argument_list|(
literal|"&& a2< "
argument_list|)
operator|.
name|append
argument_list|(
name|max
argument_list|)
operator|.
name|append
argument_list|(
literal|") {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      key = "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"FlyWeight"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"[a2 + "
argument_list|)
operator|.
name|append
argument_list|(
operator|-
name|min
argument_list|)
operator|.
name|append
argument_list|(
literal|"];\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    } else {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      key = "
argument_list|)
operator|.
name|append
argument_list|(
name|FlatLists
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".of("
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", a2);\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"    }\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|cacheProperties
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|Method
name|method
parameter_list|,
name|int
name|methodIndex
parameter_list|)
block|{
name|DEFAULT
operator|.
name|cacheProperties
argument_list|(
name|buff
argument_list|,
name|method
argument_list|,
name|methodIndex
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"  private final Object[] "
argument_list|)
expr_stmt|;
name|appendKeyName
argument_list|(
name|buff
argument_list|,
name|methodIndex
argument_list|,
literal|"FlyWeight"
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" =\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"      "
argument_list|)
operator|.
name|append
argument_list|(
name|CacheUtil
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|".generateRange(\""
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\", "
argument_list|)
operator|.
name|append
argument_list|(
name|min
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|max
argument_list|)
operator|.
name|append
argument_list|(
literal|");\n"
argument_list|)
expr_stmt|;
block|}
block|}
block|;
specifier|abstract
name|void
name|cacheKeyBlock
argument_list|(
name|StringBuilder
name|buff
argument_list|,
name|Method
name|method
argument_list|,
name|int
name|methodIndex
argument_list|)
decl_stmt|;
specifier|abstract
name|void
name|cacheProperties
argument_list|(
name|StringBuilder
name|buff
argument_list|,
name|Method
name|method
argument_list|,
name|int
name|methodIndex
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

