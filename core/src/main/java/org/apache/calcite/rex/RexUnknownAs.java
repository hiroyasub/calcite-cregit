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
name|rex
package|;
end_package

begin_comment
comment|/** Policy for whether a simplified expression may instead return another  * value.  *  *<p>In particular, it deals with converting three-valued logic (TRUE, FALSE,  * UNKNOWN) to two-valued logic (TRUE, FALSE) for callers that treat the UNKNOWN  * value the same as TRUE or FALSE.  *  *<p>Sometimes the three-valued version of the expression is simpler (has a  * smaller expression tree) than the two-valued version. In these cases,  * favor simplicity over reduction to two-valued logic.  *  * @see RexSimplify */
end_comment

begin_enum
specifier|public
enum|enum
name|RexUnknownAs
block|{
comment|/** Policy that indicates that the expression is being used in a context    * Where an UNKNOWN value is treated in the same way as FALSE. Therefore, when    * simplifying the expression, it is acceptable for the simplified expression    * to evaluate to FALSE in some situations where the original expression would    * evaluate to UNKNOWN.    *    *<p>SQL predicates ({@code WHERE}, {@code ON}, {@code HAVING} and    * {@code FILTER (WHERE)} clauses, a {@code WHEN} clause of a {@code CASE}    * expression, and in {@code CHECK} constraints) all treat UNKNOWN as FALSE.    *    *<p>If the simplified expression never returns UNKNOWN, the simplifier    * should make this clear to the caller, if possible, by marking its type as    * {@code BOOLEAN NOT NULL}. */
name|FALSE
block|,
comment|/** Policy that indicates that the expression is being used in a context    * Where an UNKNOWN value is treated in the same way as TRUE. Therefore, when    * simplifying the expression, it is acceptable for the simplified expression    * to evaluate to TRUE in some situations where the original expression would    * evaluate to UNKNOWN.    *    *<p>This does not occur commonly in SQL. However, it occurs internally    * during simplification. For example, "{@code WHERE NOT expression}"    * evaluates "{@code NOT expression}" in a context that treats UNKNOWN as    * FALSE; it is useful to consider that "{@code expression}" is evaluated in a    * context that treats UNKNOWN as TRUE.    *    *<p>If the simplified expression never returns UNKNOWN, the simplifier    * should make this clear to the caller, if possible, by marking its type as    * {@code BOOLEAN NOT NULL}. */
name|TRUE
block|,
comment|/** Policy that indicates that the expression is being used in a context    * Where an UNKNOWN value is treated as is. This occurs:    *    *<ul>    *<li>In any expression whose type is not {@code BOOLEAN}    *<li>In {@code BOOLEAN} expressions that are {@code NOT NULL}    *<li>In {@code BOOLEAN} expressions where {@code UNKNOWN} should be    *       returned as is, for example in a {@code SELECT} clause, or within an    *       expression such as an operand to {@code AND}, {@code OR} or    *       {@code NOT}    *</ul>    *    *<p>If you are unsure, use UNKNOWN. It is the safest option. */
name|UNKNOWN
block|;
comment|/** Returns {@link #FALSE} if {@code unknownAsFalse} is true,    * {@link #UNKNOWN} otherwise. */
specifier|public
specifier|static
name|RexUnknownAs
name|falseIf
parameter_list|(
name|boolean
name|unknownAsFalse
parameter_list|)
block|{
return|return
name|unknownAsFalse
condition|?
name|FALSE
else|:
name|UNKNOWN
return|;
block|}
specifier|public
name|boolean
name|toBoolean
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|FALSE
case|:
return|return
literal|false
return|;
case|case
name|TRUE
case|:
return|return
literal|true
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown"
argument_list|)
throw|;
block|}
block|}
specifier|public
name|RexUnknownAs
name|negate
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|TRUE
case|:
return|return
name|FALSE
return|;
case|case
name|FALSE
case|:
return|return
name|TRUE
return|;
default|default:
return|return
name|UNKNOWN
return|;
block|}
block|}
comment|/** Combines this with another {@code RexUnknownAs} in the same way as the    * three-valued logic of OR.    *    *<p>For example, {@code TRUE or FALSE} returns {@code TRUE};    * {@code FALSE or UNKNOWN} returns {@code UNKNOWN}. */
specifier|public
name|RexUnknownAs
name|or
parameter_list|(
name|RexUnknownAs
name|other
parameter_list|)
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|TRUE
case|:
return|return
name|this
return|;
case|case
name|UNKNOWN
case|:
return|return
name|other
operator|==
name|TRUE
condition|?
name|other
else|:
name|this
return|;
case|case
name|FALSE
case|:
default|default:
return|return
name|other
return|;
block|}
block|}
block|}
end_enum

end_unit

