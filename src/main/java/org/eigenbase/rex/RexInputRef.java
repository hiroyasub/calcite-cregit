begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Variable which references a field of an input relational expression.  *  *<p>Fields of the input are 0-based. If there is more than one input, they are  * numbered consecutively. For example, if the inputs to a join are  *  *<ul>  *<li>Input #0: EMP(EMPNO, ENAME, DEPTNO) and</li>  *<li>Input #1: DEPT(DEPTNO AS DEPTNO2, DNAME)</li>  *</ul>  *  * then the fields are:  *  *<ul>  *<li>Field #0: EMPNO</li>  *<li>Field #1: ENAME</li>  *<li>Field #2: DEPTNO (from EMP)</li>  *<li>Field #3: DEPTNO2 (from DEPT)</li>  *<li>Field #4: DNAME</li>  *</ul>  *  * So<code>RexInputRef(3,Integer)</code> is the correct reference for the field  * DEPTNO2.</p>  *  * @author jhyde  * @version $Id$  * @since Nov 24, 2003  */
end_comment

begin_class
specifier|public
class|class
name|RexInputRef
extends|extends
name|RexSlot
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// array of common names, to reduce memory allocations
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|names
init|=
name|makeArray
argument_list|(
literal|32
argument_list|,
literal|"$"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an input variable.      *      * @param index Index of the field in the underlying rowtype      * @param type Type of the column      *      * @pre type != null      * @pre index>= 0      */
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
specifier|public
name|RexInputRef
name|clone
parameter_list|()
block|{
return|return
operator|new
name|RexInputRef
argument_list|(
name|index
argument_list|,
name|type
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
name|visitInputRef
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Creates a name for an input reference, of the form "$index". If the index      * is low, uses a cache of common names, to reduce gc.      */
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
operator|(
name|index
operator|<
name|names
operator|.
name|length
operator|)
condition|?
name|names
index|[
name|index
index|]
else|:
operator|(
literal|"$"
operator|+
name|index
operator|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexInputRef.java
end_comment

end_unit

