begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|lambda
operator|.
name|streams
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

begin_comment
comment|/**  * Stream.  *  *<p>Based on {@code java.util.streams.Stream}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Stream
parameter_list|<
name|T
parameter_list|>
extends|extends
name|SequentialStreamOps
argument_list|<
name|T
argument_list|>
extends|,
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|int
name|STATE_UNIQUE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|STATE_SORTED
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|STATE_SIZED
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|STATE_MASK
init|=
literal|0x7
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|STATE_UNKNOWN_MASK_V1
init|=
operator|~
operator|(
name|STATE_UNIQUE
operator||
name|STATE_SORTED
operator||
name|STATE_SIZED
operator|)
decl_stmt|;
block|}
end_interface

begin_comment
comment|// End Stream.java
end_comment

end_unit

