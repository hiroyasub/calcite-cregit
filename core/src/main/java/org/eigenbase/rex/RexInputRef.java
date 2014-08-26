begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Pair
import|;
end_import

begin_comment
comment|/**  * Variable which references a field of an input relational expression.  *  *<p>Fields of the input are 0-based. If there is more than one input, they are  * numbered consecutively. For example, if the inputs to a join are</p>  *  *<ul>  *<li>Input #0: EMP(EMPNO, ENAME, DEPTNO) and</li>  *<li>Input #1: DEPT(DEPTNO AS DEPTNO2, DNAME)</li>  *</ul>  *  *<p>then the fields are:</p>  *  *<ul>  *<li>Field #0: EMPNO</li>  *<li>Field #1: ENAME</li>  *<li>Field #2: DEPTNO (from EMP)</li>  *<li>Field #3: DEPTNO2 (from DEPT)</li>  *<li>Field #4: DNAME</li>  *</ul>  *  *<p>So<code>RexInputRef(3, Integer)</code> is the correct reference for the  * field DEPTNO2.</p>  */
end_comment

begin_class
specifier|public
class|class
name|RexInputRef
extends|extends
name|RexSlot
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// list of common names, to reduce memory allocations
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
literal|"$"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an input variable.    *    * @param index Index of the field in the underlying row-type    * @param type  Type of the column    */
specifier|public
name|RexInputRef
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
comment|/**    * Creates a reference to a given field in a row type.    */
specifier|public
specifier|static
name|RexInputRef
name|of
parameter_list|(
name|int
name|index
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|index
argument_list|,
name|rowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a reference to a given field in a list of fields.    */
specifier|public
specifier|static
name|RexInputRef
name|of
parameter_list|(
name|int
name|index
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
return|return
operator|new
name|RexInputRef
argument_list|(
name|index
argument_list|,
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a reference to a given field in a list of fields.    */
specifier|public
specifier|static
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
name|of2
parameter_list|(
name|int
name|index
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
specifier|final
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|RexNode
operator|)
operator|new
name|RexInputRef
argument_list|(
name|index
argument_list|,
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
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
name|INPUT_REF
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
name|visitInputRef
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Creates a name for an input reference, of the form "$index". If the index    * is low, uses a cache of common names, to reduce gc.    */
specifier|public
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
comment|// End RexInputRef.java
end_comment

end_unit

