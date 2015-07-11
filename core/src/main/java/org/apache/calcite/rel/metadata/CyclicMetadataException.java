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
package|;
end_package

begin_comment
comment|/**  * Exception that indicates that a cycle has been detected while  * computing metadata.  */
end_comment

begin_class
specifier|public
class|class
name|CyclicMetadataException
extends|extends
name|RuntimeException
block|{
comment|/** Singleton instance. Since this exception is thrown for signaling purposes,    * rather than on an actual error, re-using a singleton instance saves the    * effort of constructing an exception instance. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ThrowableInstanceNeverThrown"
argument_list|)
specifier|public
specifier|static
specifier|final
name|CyclicMetadataException
name|INSTANCE
init|=
operator|new
name|CyclicMetadataException
argument_list|()
decl_stmt|;
comment|/** Creates a CyclicMetadataException. */
specifier|private
name|CyclicMetadataException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CyclicMetadataException.java
end_comment

end_unit

