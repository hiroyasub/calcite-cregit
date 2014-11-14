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
name|linq4j
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  * Supports a simple iteration over a collection.  *  *<p>Analogous to LINQ's System.Collections.Enumerator. Unlike LINQ, if the  * underlying collection has been modified it is only optional that an  * implementation of the Enumerator interface detects it and throws a  * {@link java.util.ConcurrentModificationException}.</p>  *  * @param<T> Element type  */
end_comment

begin_interface
specifier|public
interface|interface
name|Enumerator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Closeable
block|{
comment|/**    * Gets the current element in the collection.    *    *<p>After an enumerator is created or after the {@link #reset} method is    * called, the {@link #moveNext} method must be called to advance the    * enumerator to the first element of the collection before reading the    * value of the {@code current} property; otherwise, {@code current} is    * undefined.</p>    *    *<p>This method also throws {@link java.util.NoSuchElementException} if    * the last call to {@code moveNext} returned {@code false}, which indicates    * the end of the collection.</p>    *    *<p>This method does not move the position of the enumerator, and    * consecutive calls to {@code current} return the same object until either    * {@code moveNext} or {@code reset} is called.</p>    *    *<p>An enumerator remains valid as long as the collection remains    * unchanged. If changes are made to the collection, such as adding,    * modifying, or deleting elements, the enumerator is irrecoverably    * invalidated. The next call to {@code moveNext} or {@code reset} may,    * at the discretion of the implementation, throw a    * {@link java.util.ConcurrentModificationException}. If the collection is    * modified between {@code moveNext} and {@code current}, {@code current}    * returns the element that it is set to, even if the enumerator is already    * invalidated.    *    * @return Current element    * @throws java.util.ConcurrentModificationException    *          if collection has    *          been modified    * @throws java.util.NoSuchElementException    *          if {@code moveToNext} has not    *          been called, has not been called since the most recent call to    *          {@code reset}, or returned false    */
name|T
name|current
parameter_list|()
function_decl|;
comment|/**    * Advances the enumerator to the next element of the collection.    *    *<p>After an enumerator is created or after the {@code reset} method is    * called, an enumerator is positioned before the first element of the    * collection, and the first call to the {@code moveNext} method moves the    * enumerator over the first element of the collection.</p>    *    *<p>If {@code moveNext} passes the end of the collection, the enumerator    * is positioned after the last element in the collection and    * {@code moveNext} returns {@code false}. When the enumerator is at this    * position, subsequent calls to {@code moveNext} also return {@code false}    * until {@code #reset} is called.</p>    *    *<p>An enumerator remains valid as long as the collection remains    * unchanged. If changes are made to the collection, such as adding,    * modifying, or deleting elements, the enumerator is irrecoverably    * invalidated. The next call to {@code moveNext} or {@link #reset} may,    * at the discretion of the implementation, throw a    * {@link java.util.ConcurrentModificationException}.</p>    *    * @return {@code true} if the enumerator was successfully advanced to the    *         next element; {@code false} if the enumerator has passed the end of    *         the collection    */
name|boolean
name|moveNext
parameter_list|()
function_decl|;
comment|/**    * Sets the enumerator to its initial position, which is before the first    * element in the collection.    *    *<p>An enumerator remains valid as long as the collection remains    * unchanged. If changes are made to the collection, such as adding,    * modifying, or deleting elements, the enumerator is irrecoverably    * invalidated. The next call to {@link #moveNext} or {@code reset} may,    * at the discretion of the implementation, throw a    * {@link java.util.ConcurrentModificationException}.</p>    *    *<p>This method is optional; it may throw    * {@link UnsupportedOperationException}.</p>    *    *<h3>Notes to Implementers</h3>    *    *<p>All calls to Reset must result in the same state for the enumerator.    * The preferred implementation is to move the enumerator to the beginning    * of the collection, before the first element. This invalidates the    * enumerator if the collection has been modified since the enumerator was    * created, which is consistent with {@link #moveNext()} and    * {@link #current()}.</p>    */
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Closes this enumerable and releases resources.    *    *<p>This method is idempotent. Calling it multiple times has the same effect    * as calling it once.</p>    */
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Enumerator.java
end_comment

end_unit

