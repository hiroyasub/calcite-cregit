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
name|schema
package|;
end_package

begin_comment
comment|/**  * An interface to represent a version ID that can be used to create a  * read-consistent view of a Schema. This interface assumes a strict  * partial ordering contract that is:  *<ol>  *<li>irreflexive: !a.isBefore(a), which means a cannot happen before itself;  *<li>transitive: if a.isBefore(b) and b.isBefore(c) then a.isBefore(c);  *<li>antisymmetric: if a.isBefore(b) then !b.isBefore(a).  *</ol>  * Implementation classes of this interface must also override equals(Object),  * hashCode() and toString().  *  * @see Schema#snapshot(SchemaVersion)  */
end_comment

begin_interface
specifier|public
interface|interface
name|SchemaVersion
block|{
comment|/**    * Returns if this Version happens before the other Version.    * @param other the other Version object    */
name|boolean
name|isBefore
parameter_list|(
name|SchemaVersion
name|other
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SchemaVersion.java
end_comment

end_unit
