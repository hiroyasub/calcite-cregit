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
name|runtime
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

begin_comment
comment|/**  * Utility methods called by generated code.  */
end_comment

begin_class
specifier|public
class|class
name|Utilities
block|{
comment|// Even though this is a utility class (all methods are static), we cannot
comment|// make the constructor private. Because Janino doesn't do static import,
comment|// generated code is placed in sub-classes.
specifier|protected
name|Utilities
parameter_list|()
block|{
block|}
comment|/** @deprecated Use {@link java.util.Objects#equals}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|boolean
name|equal
parameter_list|(
name|Object
name|o0
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
comment|// Same as java.lang.Objects.equals (JDK 1.7 and later)
comment|// and com.google.common.base.Objects.equal
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|o0
argument_list|,
name|o1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|Object
name|v
parameter_list|)
block|{
return|return
name|v
operator|==
literal|null
condition|?
literal|0
else|:
name|v
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** Computes the hash code of a {@code double} value. Equivalent to    * {@link Double}{@code .hashCode(double)}, but that method was only    * introduced in JDK 1.8.    *    * @param v Value    * @return Hash code    * @deprecated Use {@link Double#hashCode(double)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|double
name|v
parameter_list|)
block|{
return|return
name|Double
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
return|;
block|}
comment|/** Computes the hash code of a {@code float} value. Equivalent to    * {@link Float}{@code .hashCode(float)}, but that method was only    * introduced in JDK 1.8.    *    * @param v Value    * @return Hash code    * @deprecated Use {@link Float#hashCode(float)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|float
name|v
parameter_list|)
block|{
return|return
name|Float
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
return|;
block|}
comment|/** Computes the hash code of a {@code long} value. Equivalent to    * {@link Long}{@code .hashCode(long)}, but that method was only    * introduced in JDK 1.8.    *    * @param v Value    * @return Hash code    * @deprecated Use {@link Long#hashCode(long)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|long
name|v
parameter_list|)
block|{
return|return
name|Long
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
return|;
block|}
comment|/** Computes the hash code of a {@code boolean} value. Equivalent to    * {@link Boolean}{@code .hashCode(boolean)}, but that method was only    * introduced in JDK 1.8.    *    * @param v Value    * @return Hash code    * @deprecated Use {@link Boolean#hashCode(boolean)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|int
name|hashCode
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|boolean
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
name|hashCode
argument_list|(
name|v
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|byte
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|char
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|short
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|int
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|long
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
name|Long
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|float
name|v
parameter_list|)
block|{
return|return
name|hash
argument_list|(
name|h
argument_list|,
name|Float
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|double
name|v
parameter_list|)
block|{
return|return
name|hash
argument_list|(
name|h
argument_list|,
name|Double
operator|.
name|hashCode
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|Object
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|31
operator|+
operator|(
name|v
operator|==
literal|null
condition|?
literal|1
else|:
name|v
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|boolean
name|v0
parameter_list|,
name|boolean
name|v1
parameter_list|)
block|{
return|return
name|Boolean
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|byte
name|v0
parameter_list|,
name|byte
name|v1
parameter_list|)
block|{
return|return
name|Byte
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|char
name|v0
parameter_list|,
name|char
name|v1
parameter_list|)
block|{
return|return
name|Character
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|short
name|v0
parameter_list|,
name|short
name|v1
parameter_list|)
block|{
return|return
name|Short
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|int
name|v0
parameter_list|,
name|int
name|v1
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|long
name|v0
parameter_list|,
name|long
name|v1
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|float
name|v0
parameter_list|,
name|float
name|v1
parameter_list|)
block|{
return|return
name|Float
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|double
name|v0
parameter_list|,
name|double
name|v1
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|List
name|v0
parameter_list|,
name|List
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
specifier|final
name|Iterator
name|iterator0
init|=
name|v0
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
name|iterator1
init|=
name|v1
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|iterator0
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
operator|!
name|iterator1
operator|.
name|hasNext
argument_list|()
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|iterator1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
specifier|final
name|Object
name|o0
init|=
name|iterator0
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|o1
init|=
name|iterator1
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|c
init|=
name|compare_
argument_list|(
name|o0
argument_list|,
name|o1
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
block|}
block|}
specifier|private
specifier|static
name|int
name|compare_
parameter_list|(
name|Object
name|o0
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
if|if
condition|(
name|o0
operator|instanceof
name|Comparable
condition|)
block|{
return|return
name|compare
argument_list|(
operator|(
name|Comparable
operator|)
name|o0
argument_list|,
operator|(
name|Comparable
operator|)
name|o1
argument_list|)
return|;
block|}
return|return
name|compare
argument_list|(
operator|(
name|List
operator|)
name|o0
argument_list|,
operator|(
name|List
operator|)
name|o1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|Comparable
name|v0
parameter_list|,
name|Comparable
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compareNullsFirst
parameter_list|(
name|Comparable
name|v0
parameter_list|,
name|Comparable
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|==
name|v1
condition|?
literal|0
else|:
name|v0
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|v1
operator|==
literal|null
condition|?
literal|1
else|:
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compareNullsLast
parameter_list|(
name|Comparable
name|v0
parameter_list|,
name|Comparable
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|==
name|v1
condition|?
literal|0
else|:
name|v0
operator|==
literal|null
condition|?
literal|1
else|:
name|v1
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compareNullsLast
parameter_list|(
name|List
name|v0
parameter_list|,
name|List
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|==
name|v1
condition|?
literal|0
else|:
name|v0
operator|==
literal|null
condition|?
literal|1
else|:
name|v1
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|FlatLists
operator|.
name|ComparableListImpl
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
comment|/** Creates a pattern builder. */
specifier|public
specifier|static
name|Pattern
operator|.
name|PatternBuilder
name|patternBuilder
parameter_list|()
block|{
return|return
name|Pattern
operator|.
name|builder
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End Utilities.java
end_comment

end_unit

