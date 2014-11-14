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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|function
operator|.
name|Functions
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

begin_comment
comment|/**  * Binary search for the implementation of  * RANGE BETWEEN XXX PRECEDING/FOLLOWING clause.  *  */
end_comment

begin_class
specifier|public
class|class
name|BinarySearch
block|{
comment|// Even though this is a utility class (all methods are static), we cannot
comment|// make the constructor private. Because Janino doesn't do static import,
comment|// generated code is placed in sub-classes.
specifier|protected
name|BinarySearch
parameter_list|()
block|{
block|}
comment|/**    * Performs binary search of the lower bound in the given array.    * It is assumed that the array is sorted.    * The method is guaranteed to return the minimal index of the element that is    * greater or equal to the given key.    * @param a array that holds the values    * @param key element to look for    * @param comparator comparator that compares keys    * @param<T> the type of elements in array    * @return minimal index of the element that is    *   greater or equal to the given key. Returns -1 when all elements exceed    *   the given key or the array is empty. Returns {@code a.length} when all    *   elements are less than the given key.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
name|lowerBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|T
name|key
parameter_list|,
name|Comparator
argument_list|<
name|T
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|lowerBound
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
operator|-
literal|1
argument_list|,
name|Functions
operator|.
expr|<
name|T
operator|>
name|identitySelector
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Performs binary search of the upper bound in the given array.    * It is assumed that the array is sorted.    * The method is guaranteed to return the maximal index of the element that is    * less or equal to the given key.    * @param a array that holds the values    * @param key element to look for    * @param comparator comparator that compares keys    * @param<T> the type of elements in array    * @return maximal index of the element that is    *   less or equal to the given key. Returns -1 when all elements are less    *   than the given key or the array is empty. Returns {@code a.length} when    *   all elements exceed the given key.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
name|upperBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|T
name|key
parameter_list|,
name|Comparator
argument_list|<
name|T
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|upperBound
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
operator|-
literal|1
argument_list|,
name|Functions
operator|.
expr|<
name|T
operator|>
name|identitySelector
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Performs binary search of the lower bound in the given array.    * The elements in the array are transformed before the comparison.    * It is assumed that the array is sorted.    * The method is guaranteed to return the minimal index of the element that is    * greater or equal to the given key.    * @param a array that holds the values    * @param key element to look for    * @param keySelector function that transforms array contents to the type    *                    of the key    * @param comparator comparator that compares keys    * @param<T> the type of elements in array    * @param<K> the type of lookup key    * @return minimal index of the element that is    *   greater or equal to the given key. Returns -1 when all elements exceed    *   the given key or the array is empty. Returns {@code a.length} when all    *   elements are less than the given key.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|K
parameter_list|>
name|int
name|lowerBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|K
name|key
parameter_list|,
name|Function1
argument_list|<
name|T
argument_list|,
name|K
argument_list|>
name|keySelector
parameter_list|,
name|Comparator
argument_list|<
name|K
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|lowerBound
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
operator|-
literal|1
argument_list|,
name|keySelector
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Performs binary search of the upper bound in the given array.    * The elements in the array are transformed before the comparison.    * It is assumed that the array is sorted.    * The method is guaranteed to return the maximal index of the element that is    * less or equal to the given key.    * @param a array that holds the values    * @param key element to look for    * @param keySelector function that transforms array contents to the type    *                    of the key    * @param comparator comparator that compares keys    * @param<T> the type of elements in array    * @param<K> the type of lookup key    * @return maximal index of the element that is    *   less or equal to the given key. Returns -1 when all elements are less    *   than the given key or the array is empty. Returns {@code a.length} when    *   all elements exceed the given key.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|K
parameter_list|>
name|int
name|upperBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|K
name|key
parameter_list|,
name|Function1
argument_list|<
name|T
argument_list|,
name|K
argument_list|>
name|keySelector
parameter_list|,
name|Comparator
argument_list|<
name|K
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|upperBound
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
literal|0
argument_list|,
name|a
operator|.
name|length
operator|-
literal|1
argument_list|,
name|keySelector
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Performs binary search of the lower bound in the given section of array.    * It is assumed that the array is sorted.    * The method is guaranteed to return the minimal index of the element that is    * greater or equal to the given key.    * @param a array that holds the values    * @param key element to look for    * @param imin the minimal index (inclusive) to look for    * @param imax the maximum index (inclusive) to look for    * @param comparator comparator that compares keys    * @param<T> the type of elements in array    * @return minimal index of the element that is    *   greater or equal to the given key. Returns -1 when all elements exceed    *   the given key or the array is empty. Returns {@code a.length} when all    *   elements are less than the given key.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
name|lowerBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|T
name|key
parameter_list|,
name|int
name|imin
parameter_list|,
name|int
name|imax
parameter_list|,
name|Comparator
argument_list|<
name|T
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|lowerBound
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
name|imin
argument_list|,
name|imax
argument_list|,
name|Functions
operator|.
expr|<
name|T
operator|>
name|identitySelector
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Performs binary search of the upper bound in the given array.    * It is assumed that the array is sorted.    * The method is guaranteed to return the maximal index of the element that is    * less or equal to the given key.    * @param a array that holds the values    * @param key element to look for    * @param imin the minimal index (inclusive) to look for    * @param imax the maximum index (inclusive) to look for    * @param comparator comparator that compares keys    * @param<T> the type of elements in array    * @return maximal index of the element that is    *   less or equal to the given key. Returns -1 when all elements are less    *   than the given key or the array is empty. Returns {@code a.length} when    *   all elements exceed the given key.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|int
name|upperBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|T
name|key
parameter_list|,
name|int
name|imin
parameter_list|,
name|int
name|imax
parameter_list|,
name|Comparator
argument_list|<
name|T
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|upperBound
argument_list|(
name|a
argument_list|,
name|key
argument_list|,
name|imin
argument_list|,
name|imax
argument_list|,
name|Functions
operator|.
expr|<
name|T
operator|>
name|identitySelector
argument_list|()
argument_list|,
name|comparator
argument_list|)
return|;
block|}
comment|/**    * Taken from http://en.wikipedia.org/wiki/Binary_search_algorithm    * #Deferred_detection_of_equality    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|K
parameter_list|>
name|int
name|lowerBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|K
name|key
parameter_list|,
name|int
name|imin
parameter_list|,
name|int
name|imax
parameter_list|,
name|Function1
argument_list|<
name|T
argument_list|,
name|K
argument_list|>
name|keySelector
parameter_list|,
name|Comparator
argument_list|<
name|K
argument_list|>
name|comparator
parameter_list|)
block|{
comment|// continually narrow search until just one element remains
while|while
condition|(
name|imin
operator|<
name|imax
condition|)
block|{
comment|// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=5045582
name|int
name|imid
init|=
operator|(
name|imin
operator|+
name|imax
operator|)
operator|>>>
literal|1
decl_stmt|;
comment|// code must guarantee the interval is reduced at each iteration
assert|assert
name|imid
operator|<
name|imax
operator|:
literal|"search interval should be reduced min="
operator|+
name|imin
operator|+
literal|", mid="
operator|+
name|imid
operator|+
literal|", max="
operator|+
name|imax
assert|;
comment|// note: 0<= imin< imax implies imid will always be less than imax
comment|// reduce the search
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|keySelector
operator|.
name|apply
argument_list|(
name|a
index|[
name|imid
index|]
argument_list|)
argument_list|,
name|key
argument_list|)
operator|<
literal|0
condition|)
block|{
comment|// change min index to search upper subarray
name|imin
operator|=
name|imid
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|imax
operator|=
name|imid
expr_stmt|;
block|}
block|}
comment|// At exit of while:
comment|//   if a[] is empty, then imax< imin
comment|//   otherwise imax == imin
comment|// deferred test for equality
if|if
condition|(
name|imax
operator|!=
name|imin
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|keySelector
operator|.
name|apply
argument_list|(
name|a
index|[
name|imin
index|]
argument_list|)
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
comment|// Detected exact match, just return it
return|return
name|imin
return|;
block|}
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// We were asked the key that is greater that all the values in array
return|return
name|imin
operator|+
literal|1
return|;
block|}
comment|// If imin != 0 we return imin since a[imin-1]< key< a[imin]
comment|// If imin == 0 we return -1 since the resulting window might be empty
comment|// For instance, range between 100 preceding and 99 preceding
comment|// Use if-else to ensure code coverage is reported for each return
if|if
condition|(
name|imin
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
name|imin
return|;
block|}
block|}
comment|/**    * Taken from http://en.wikipedia.org/wiki/Binary_search_algorithm    * #Deferred_detection_of_equality    * Adapted to find upper bound.    */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|,
name|K
parameter_list|>
name|int
name|upperBound
parameter_list|(
name|T
index|[]
name|a
parameter_list|,
name|K
name|key
parameter_list|,
name|int
name|imin
parameter_list|,
name|int
name|imax
parameter_list|,
name|Function1
argument_list|<
name|T
argument_list|,
name|K
argument_list|>
name|keySelector
parameter_list|,
name|Comparator
argument_list|<
name|K
argument_list|>
name|comparator
parameter_list|)
block|{
name|int
name|initialMax
init|=
name|imax
decl_stmt|;
comment|// continually narrow search until just one element remains
while|while
condition|(
name|imin
operator|<
name|imax
condition|)
block|{
name|int
name|imid
init|=
operator|(
name|imin
operator|+
name|imax
operator|+
literal|1
operator|)
operator|>>>
literal|1
decl_stmt|;
comment|// code must guarantee the interval is reduced at each iteration
assert|assert
name|imid
operator|>
name|imin
operator|:
literal|"search interval should be reduced min="
operator|+
name|imin
operator|+
literal|", mid="
operator|+
name|imid
operator|+
literal|", max="
operator|+
name|imax
assert|;
comment|// note: 0<= imin< imax implies imid will always be less than imax
comment|// reduce the search
if|if
condition|(
name|comparator
operator|.
name|compare
argument_list|(
name|keySelector
operator|.
name|apply
argument_list|(
name|a
index|[
name|imid
index|]
argument_list|)
argument_list|,
name|key
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// change max index to search lower subarray
name|imax
operator|=
name|imid
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|imin
operator|=
name|imid
expr_stmt|;
block|}
block|}
comment|// At exit of while:
comment|//   if a[] is empty, then imax< imin
comment|//   otherwise imax == imin
comment|// deferred test for equality
if|if
condition|(
name|imax
operator|!=
name|imin
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|cmp
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|keySelector
operator|.
name|apply
argument_list|(
name|a
index|[
name|imin
index|]
argument_list|)
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
comment|// Detected exact match, just return it
return|return
name|imin
return|;
block|}
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
comment|// We were asked the key that is less than all the values in array
return|return
name|imin
operator|-
literal|1
return|;
block|}
comment|// If imin != initialMax we return imin since a[imin-1]< key< a[imin]
comment|// If imin == initialMax we return initialMax+11 since
comment|// the resulting window might be empty
comment|// For instance, range between 99 following and 100 following
comment|// Use if-else to ensure code coverage is reported for each return
if|if
condition|(
name|imin
operator|==
name|initialMax
condition|)
block|{
return|return
name|initialMax
operator|+
literal|1
return|;
block|}
else|else
block|{
return|return
name|imin
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End BinarySearch.java
end_comment

end_unit

