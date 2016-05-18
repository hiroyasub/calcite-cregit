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
name|interpreter
operator|.
name|Row
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
name|Enumerable
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
name|Function1
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
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Utilities for processing {@link org.apache.calcite.linq4j.Enumerable}  * collections.  *  *<p>This class is a place to put things not yet added to linq4j.  * Methods are subject to removal without notice.  */
end_comment

begin_class
specifier|public
class|class
name|Enumerables
block|{
specifier|private
specifier|static
specifier|final
name|Function1
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|SLICE
init|=
operator|new
name|Function1
argument_list|<
name|Object
index|[]
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|Object
index|[]
name|a0
parameter_list|)
block|{
return|return
name|a0
index|[
literal|0
index|]
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Function1
argument_list|<
name|Object
index|[]
argument_list|,
name|Row
argument_list|>
name|ARRAY_TO_ROW
init|=
operator|new
name|Function1
argument_list|<
name|Object
index|[]
argument_list|,
name|Row
argument_list|>
argument_list|()
block|{
specifier|public
name|Row
name|apply
parameter_list|(
name|Object
index|[]
name|a0
parameter_list|)
block|{
return|return
name|Row
operator|.
name|asCopy
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|Enumerables
parameter_list|()
block|{
block|}
comment|/** Converts an enumerable over singleton arrays into the enumerable of their    * first elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Enumerable
argument_list|<
name|E
argument_list|>
name|slice0
parameter_list|(
name|Enumerable
argument_list|<
name|E
index|[]
argument_list|>
name|enumerable
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|enumerable
operator|.
name|select
argument_list|(
operator|(
name|Function1
argument_list|<
name|E
index|[]
argument_list|,
name|E
argument_list|>
operator|)
name|SLICE
argument_list|)
return|;
block|}
comment|/** Converts an {@link Enumerable} over object arrays into an    * {@link Enumerable} over {@link Row} objects. */
specifier|public
specifier|static
name|Enumerable
argument_list|<
name|Row
argument_list|>
name|toRow
parameter_list|(
specifier|final
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
parameter_list|)
block|{
return|return
name|enumerable
operator|.
name|select
argument_list|(
name|ARRAY_TO_ROW
argument_list|)
return|;
block|}
comment|/** Converts a supplier of an {@link Enumerable} over object arrays into a    * supplier of an {@link Enumerable} over {@link Row} objects. */
specifier|public
specifier|static
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Row
argument_list|>
argument_list|>
name|toRow
parameter_list|(
specifier|final
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
name|supplier
parameter_list|)
block|{
return|return
operator|new
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Row
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|Row
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|toRow
argument_list|(
name|supplier
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End Enumerables.java
end_comment

end_unit

