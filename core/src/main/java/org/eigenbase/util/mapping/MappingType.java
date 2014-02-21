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
name|util
operator|.
name|mapping
package|;
end_package

begin_comment
comment|/**  * Describes the type of a mapping, from the most general {@link #MULTI_FUNCTION}  * (every element in the source and target domain can participate in many  * mappings) to the most retricted {@link #BIJECTION} (every element in the  * source and target domain must be paired with precisely one element in the  * other domain).  *  *<p>Some common types:  *  *<ul>  *<li>A surjection is a mapping if every target has at least one source; also  * known as an 'onto' mapping.  *<li>A mapping is a partial function if every source has at most one target.  *<li>A mapping is a function if every source has precisely one target.  *<li>An injection is a mapping where a target has at most one source; also  * somewhat confusingly known as a 'one-to-one' mapping.  *<li>A bijection is a mapping which is both an injection and a surjection.  * Every source has precisely one target, and vice versa.  *</ul>  *  *<p>Once you know what type of mapping you want, call {@link  * Mappings#create(MappingType, int, int)} to create an efficient implementation  * of that mapping.  */
end_comment

begin_enum
specifier|public
enum|enum
name|MappingType
block|{
comment|//            ordinal source target function inverse
comment|//            ======= ====== ====== ======== =================
comment|//                  0      1      1 true     0 Bijection
name|BIJECTION
block|,
comment|//                  1<= 1      1 true     4 InverseSurjection
name|SURJECTION
block|,
comment|//                  2>= 1      1 true     8 InverseInjection
name|INJECTION
block|,
comment|//                  3    any      1 true     12 InverseFunction
name|FUNCTION
block|,
comment|/**    * An inverse surjection has a source for every target, and no source has    * more than one target.    */
comment|//                  4      1<= 1 partial  1 Surjection
name|INVERSE_SURJECTION
block|,
comment|/**    * A partial surjection has no more than one source for any target, and no    * more than one target for any source.    */
comment|//                  5<= 1<= 1 partial  5 PartialSurjection
name|PARTIAL_SURJECTION
block|,
comment|//                  6>= 1<= 1 partial  9 InversePartialInjection
name|PARTIAL_INJECTION
block|,
comment|//                  7    any<= 1 partial  13 InversePartialFunction
name|PARTIAL_FUNCTION
block|,
comment|//                  8      1>= 1 multi    2 Injection
name|INVERSE_INJECTION
block|,
comment|//                  9<= 1>= 1 multi    6 PartialInjection
name|INVERSE_PARTIAL_INJECTION
block|,
comment|//                 10>= 1>= 1 multi    10
name|TEN
block|,
comment|//                 11    any>= 1 multi    14
name|ELEVEN
block|,
comment|/**    * An inverse function has a source for every target, but a source might    * have 0, 1 or more targets.    *    *<p>Obeys the constaints {@link MappingType#isMandatorySource()}, {@link    * MappingType#isSingleSource()}.    *    *<p>Similar types:    *    *<ul>    *<li> {@link #INVERSE_SURJECTION} is stronger (a source may not have    * multiple targets);    *<li>{@link #INVERSE_PARTIAL_FUNCTION} is weaker (a target may have 0 or 1    * sources).    *</ul>    */
comment|//                 12      1    any multi    3 Function
name|INVERSE_FUNCTION
block|,
comment|//                 13<= 1    any multi    7 PartialFunction
name|INVERSE_PARTIAL_FUNCTION
block|,
comment|//                 14>= 1    any multi    11
name|FOURTEEN
block|,
comment|//                 15    any    any multi    15 MultiFunction
name|MULTI_FUNCTION
block|;
specifier|private
specifier|final
name|int
name|inverseOrdinal
decl_stmt|;
specifier|private
name|MappingType
parameter_list|()
block|{
name|this
operator|.
name|inverseOrdinal
operator|=
operator|(
operator|(
name|ordinal
argument_list|()
operator|&
literal|3
operator|)
operator|<<
literal|2
operator|)
operator||
operator|(
operator|(
name|ordinal
argument_list|()
operator|&
literal|12
operator|)
operator|>>
literal|2
operator|)
expr_stmt|;
block|}
specifier|public
name|MappingType
name|inverse
parameter_list|()
block|{
return|return
name|MappingType
operator|.
name|values
argument_list|()
index|[
name|this
operator|.
name|inverseOrdinal
index|]
return|;
block|}
comment|/**    * Returns whether this mapping type is (possibly a weaker form of) a given    * mapping type.    *    *<p>For example, a {@link #BIJECTION} is a {@link #FUNCTION}, but not    * every {link #Function} is a {@link #BIJECTION}.    */
specifier|public
name|boolean
name|isA
parameter_list|(
name|MappingType
name|mappingType
parameter_list|)
block|{
return|return
operator|(
name|ordinal
argument_list|()
operator|&
name|mappingType
operator|.
name|ordinal
argument_list|()
operator|)
operator|==
name|ordinal
argument_list|()
return|;
block|}
comment|/**    * A mapping is a total function if every source has precisely one target.    */
specifier|public
name|boolean
name|isFunction
parameter_list|()
block|{
return|return
operator|(
name|ordinal
argument_list|()
operator|&
operator|(
name|OPTIONAL_TARGET
operator||
name|MULTIPLE_TARGET
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**    * A mapping is a partial function if every source has at most one target.    */
specifier|public
name|boolean
name|isPartialFunction
parameter_list|()
block|{
return|return
operator|(
name|ordinal
argument_list|()
operator|&
name|MULTIPLE_TARGET
operator|)
operator|==
literal|0
return|;
block|}
comment|/**    * A mapping is a surjection if it is a function and every target has at    * least one source.    */
specifier|public
name|boolean
name|isSurjection
parameter_list|()
block|{
return|return
operator|(
name|ordinal
argument_list|()
operator|&
operator|(
name|OPTIONAL_TARGET
operator||
name|MULTIPLE_TARGET
operator||
name|OPTIONAL_SOURCE
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**    * A mapping is an injection if it is a function and no target has more than    * one source. (In other words, every source has precisely one target.)    */
specifier|public
name|boolean
name|isInjection
parameter_list|()
block|{
return|return
operator|(
name|ordinal
argument_list|()
operator|&
operator|(
name|OPTIONAL_TARGET
operator||
name|MULTIPLE_TARGET
operator||
name|MULTIPLE_SOURCE
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**    * A mapping is a bijection if it is a surjection and it is an injection.    * (In other words,    */
specifier|public
name|boolean
name|isBijection
parameter_list|()
block|{
return|return
operator|(
name|ordinal
argument_list|()
operator|&
operator|(
name|OPTIONAL_TARGET
operator||
name|MULTIPLE_TARGET
operator||
name|OPTIONAL_SOURCE
operator||
name|MULTIPLE_SOURCE
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**    * Constraint that every source has at least one target.    */
specifier|public
name|boolean
name|isMandatoryTarget
parameter_list|()
block|{
return|return
operator|!
operator|(
operator|(
name|ordinal
argument_list|()
operator|&
name|OPTIONAL_TARGET
operator|)
operator|==
name|OPTIONAL_TARGET
operator|)
return|;
block|}
comment|/**    * Constraint that every source has at most one target.    */
specifier|public
name|boolean
name|isSingleTarget
parameter_list|()
block|{
return|return
operator|!
operator|(
operator|(
name|ordinal
argument_list|()
operator|&
name|MULTIPLE_TARGET
operator|)
operator|==
name|MULTIPLE_TARGET
operator|)
return|;
block|}
comment|/**    * Constraint that every target has at least one source.    */
specifier|public
name|boolean
name|isMandatorySource
parameter_list|()
block|{
return|return
operator|!
operator|(
operator|(
name|ordinal
argument_list|()
operator|&
name|OPTIONAL_SOURCE
operator|)
operator|==
name|OPTIONAL_SOURCE
operator|)
return|;
block|}
comment|/**    * Constraint that every target has at most one source.    */
specifier|public
name|boolean
name|isSingleSource
parameter_list|()
block|{
return|return
operator|!
operator|(
operator|(
name|ordinal
argument_list|()
operator|&
name|MULTIPLE_SOURCE
operator|)
operator|==
name|MULTIPLE_SOURCE
operator|)
return|;
block|}
comment|/**    * Allow less than one source for a given target.    */
specifier|private
specifier|static
specifier|final
name|int
name|OPTIONAL_SOURCE
init|=
literal|1
decl_stmt|;
comment|/**    * Allow more than one source for a given target.    */
specifier|private
specifier|static
specifier|final
name|int
name|MULTIPLE_SOURCE
init|=
literal|2
decl_stmt|;
comment|/**    * Allow less than one target for a given source.    */
specifier|private
specifier|static
specifier|final
name|int
name|OPTIONAL_TARGET
init|=
literal|4
decl_stmt|;
comment|/**    * Allow more than one target for a given source.    */
specifier|private
specifier|static
specifier|final
name|int
name|MULTIPLE_TARGET
init|=
literal|8
decl_stmt|;
block|}
end_enum

begin_comment
comment|// End MappingType.java
end_comment

end_unit

