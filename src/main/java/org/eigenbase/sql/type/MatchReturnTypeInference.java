begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Returns the first type that matches a set of given {@link SqlTypeName}s. If  * no match could be found, null is returned.  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|MatchReturnTypeInference
implements|implements
name|SqlReturnTypeInference
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|start
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|typeNames
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Returns the first type of typeName at or after position start (zero      * based).      *      * @see #MatchReturnTypeInference(int, SqlTypeName[])      */
specifier|public
name|MatchReturnTypeInference
parameter_list|(
name|int
name|start
parameter_list|,
name|SqlTypeName
modifier|...
name|typeName
parameter_list|)
block|{
name|this
argument_list|(
name|start
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|typeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the first type matching any type in typeNames at or after      * position start (zero based).      */
specifier|public
name|MatchReturnTypeInference
parameter_list|(
name|int
name|start
parameter_list|,
name|List
argument_list|<
name|SqlTypeName
argument_list|>
name|typeNames
parameter_list|)
block|{
assert|assert
name|start
operator|>=
literal|0
assert|;
assert|assert
literal|null
operator|!=
name|typeNames
assert|;
assert|assert
name|typeNames
operator|.
name|size
argument_list|()
operator|>
literal|0
assert|;
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|typeNames
operator|=
name|typeNames
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|opBinding
operator|.
name|getOperandCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataType
name|argType
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isOfSameTypeName
argument_list|(
name|typeNames
argument_list|,
name|argType
argument_list|)
condition|)
block|{
return|return
name|argType
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End MatchReturnTypeInference.java
end_comment

end_unit

