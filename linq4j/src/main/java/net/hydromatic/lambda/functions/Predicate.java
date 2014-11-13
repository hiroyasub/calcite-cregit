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
name|functions
package|;
end_package

begin_comment
comment|/**  * Predicate.  *  *<p>Based on {@code java.util.functions.Predicate}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Predicate
parameter_list|<
name|T
parameter_list|>
block|{
name|boolean
name|test
parameter_list|(
name|T
name|t
parameter_list|)
function_decl|;
name|Predicate
argument_list|<
name|T
argument_list|>
name|and
parameter_list|(
name|Predicate
argument_list|<
name|?
super|super
name|T
argument_list|>
name|p
parameter_list|)
function_decl|;
comment|// default:
comment|// return Predicates.and(this, p);
name|Predicate
argument_list|<
name|T
argument_list|>
name|negate
parameter_list|()
function_decl|;
comment|// default:
comment|// return Predicates.negate(this);
name|Predicate
argument_list|<
name|T
argument_list|>
name|or
parameter_list|(
name|Predicate
argument_list|<
name|?
super|super
name|T
argument_list|>
name|p
parameter_list|)
function_decl|;
comment|// default:
comment|// return Predicates.or(this, p);
name|Predicate
argument_list|<
name|T
argument_list|>
name|xor
parameter_list|(
name|Predicate
argument_list|<
name|?
super|super
name|T
argument_list|>
name|p
parameter_list|)
function_decl|;
comment|// default:
comment|// return Predicates.xor(this, p);
block|}
end_interface

begin_comment
comment|// End Predicate.java
end_comment

end_unit

