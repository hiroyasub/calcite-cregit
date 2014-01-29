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
name|rex
package|;
end_package

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
name|SqlKind
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
comment|/**  * Local variable.  *  *<p>Identity is based upon type and index. We want multiple references to the  * same slot in the same context to be equal. A side effect is that references  * to slots in different contexts which happen to have the same index and type  * will be considered equal; this is not desired, but not too damaging, because  * of the immutability.  *  *<p>Variables are immutable.  */
end_comment

begin_class
specifier|public
class|class
name|RexLocalRef
extends|extends
name|RexSlot
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// array of common names, to reduce memory allocations
annotation|@
name|SuppressWarnings
argument_list|(
literal|"MismatchedQueryAndUpdateOfCollection"
argument_list|)
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|NAMES
init|=
operator|new
name|SelfPopulatingList
argument_list|(
literal|"$t"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a local variable.    *    * @param index Index of the field in the underlying rowtype    * @param type  Type of the column    * @pre type != null    * @pre index>= 0    */
specifier|public
name|RexLocalRef
parameter_list|(
name|int
name|index
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|createName
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|LOCAL_REF
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|RexLocalRef
condition|)
block|{
name|RexLocalRef
name|that
init|=
operator|(
name|RexLocalRef
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|type
operator|==
name|that
operator|.
name|type
operator|)
operator|&&
operator|(
name|this
operator|.
name|index
operator|==
name|that
operator|.
name|index
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Util
operator|.
name|hash
argument_list|(
name|type
operator|.
name|hashCode
argument_list|()
argument_list|,
name|index
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitLocalRef
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|createName
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|NAMES
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexLocalRef.java
end_comment

end_unit

