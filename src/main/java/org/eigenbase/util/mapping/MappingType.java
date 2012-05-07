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
comment|/**  * Describes the type of a mapping, from the most general {@link #MultiFunction}  * (every element in the source and target domain can participate in many  * mappings) to the most retricted {@link #Bijection} (every element in the  * source and target domain must be paired with precisely one element in the  * other domain).  *  *<p>Some common types:  *  *<ul>  *<li>A surjection is a mapping if every target has at least one source; also  * known as an 'onto' mapping.  *<li>A mapping is a partial function if every source has at most one target.  *<li>A mapping is a function if every source has precisely one target.  *<li>An injection is a mapping where a target has at most one source; also  * somewhat confusingly known as a 'one-to-one' mapping.  *<li>A bijection is a mapping which is both an injection and a surjection.  * Every source has precisely one target, and vice versa.  *</ul>  *  *<p>Once you know what type of mapping you want, call {@link  * Mappings#create(MappingType, int, int)} to create an efficient implementation  * of that mapping.  *  * @author jhyde  * @version $Id$  * @since Mar 24, 2006  */
end_comment

begin_enum
specifier|public
enum|enum
name|MappingType
block|{
comment|//            ordinal source target function inverse
comment|//            ======= ====== ====== ======== =================
comment|//                  0      1      1 true     0 Bijection
name|Bijection
block|,
comment|//                  1<= 1      1 true     4 InverseSurjection
name|Surjection
block|,
comment|//                  2>= 1      1 true     8 InverseInjection
name|Injection
block|,
comment|//                  3    any      1 true     12 InverseFunction
name|Function
block|,
comment|/**      * An inverse surjection has a source for every target, and no source has      * more than one target.      */
comment|//                  4      1<= 1 partial  1 Surjection
name|InverseSurjection
block|,
comment|/**      * A partial surjection has no more than one source for any target, and no      * more than one target for any source.      */
comment|//                  5<= 1<= 1 partial  5 PartialSurjection
name|PartialSurjection
block|,
comment|//                  6>= 1<= 1 partial  9 InversePartialInjection
name|PartialInjection
block|,
comment|//                  7    any<= 1 partial  13 InversePartialFunction
name|PartialFunction
block|,
comment|//                  8      1>= 1 multi    2 Injection
name|InverseInjection
block|,
comment|//                  9<= 1>= 1 multi    6 PartialInjection
name|InversePartialInjection
block|,
comment|//                 10>= 1>= 1 multi    10
name|Ten
block|,
comment|//                 11    any>= 1 multi    14
name|Eleven
block|,
comment|/**      * An inverse function has a source for every target, but a source might      * have 0, 1 or more targets.      *      *<p>Obeys the constaints {@link MappingType#isMandatorySource()}, {@link      * MappingType#isSingleSource()}.      *      *<p>Similar types:      *      *<ul>      *<li> {@link #InverseSurjection} is stronger (a source may not have      * multiple targets);      *<li>{@link #InversePartialFunction} is weaker (a target may have 0 or 1      * sources).      *</ul>      */
comment|//                 12      1    any multi    3 Function
name|InverseFunction
block|,
comment|//                 13<= 1    any multi    7 PartialFunction
name|InversePartialFunction
block|,
comment|//                 14>= 1    any multi    11
name|Fourteen
block|,
comment|//                 15    any    any multi    15 MultiFunction
name|MultiFunction
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
comment|/**      * Returns whether this mapping type is (possibly a weaker form of) a given      * mapping type.      *      *<p>For example, a {@link #Bijection} is a {@link #Function}, but not      * every {link #Function} is a {@link #Bijection}.      */
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
comment|/**      * A mapping is a total function if every source has precisely one target.      */
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
name|OptionalTarget
operator||
name|MultipleTarget
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**      * A mapping is a partial function if every source has at most one target.      */
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
name|MultipleTarget
operator|)
operator|==
literal|0
return|;
block|}
comment|/**      * A mapping is a surjection if it is a function and every target has at      * least one source.      */
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
name|OptionalTarget
operator||
name|MultipleTarget
operator||
name|OptionalSource
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**      * A mapping is an injection if it is a function and no target has more than      * one source. (In other words, every source has precisely one target.)      */
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
name|OptionalTarget
operator||
name|MultipleTarget
operator||
name|MultipleSource
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**      * A mapping is a bijection if it is a surjection and it is an injection.      * (In other words,      */
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
name|OptionalTarget
operator||
name|MultipleTarget
operator||
name|OptionalSource
operator||
name|MultipleSource
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**      * Constraint that every source has at least one target.      */
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
name|OptionalTarget
operator|)
operator|==
name|OptionalTarget
operator|)
return|;
block|}
comment|/**      * Constraint that every source has at most one target.      */
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
name|MultipleTarget
operator|)
operator|==
name|MultipleTarget
operator|)
return|;
block|}
comment|/**      * Constraint that every target has at least one source.      */
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
name|OptionalSource
operator|)
operator|==
name|OptionalSource
operator|)
return|;
block|}
comment|/**      * Constraint that every target has at most one source.      */
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
name|MultipleSource
operator|)
operator|==
name|MultipleSource
operator|)
return|;
block|}
comment|/**      * Allow less than one source for a given target.      */
specifier|private
specifier|static
specifier|final
name|int
name|OptionalSource
init|=
literal|1
decl_stmt|;
comment|/**      * Allow more than one source for a given target.      */
specifier|private
specifier|static
specifier|final
name|int
name|MultipleSource
init|=
literal|2
decl_stmt|;
comment|/**      * Allow less than one target for a given source.      */
specifier|private
specifier|static
specifier|final
name|int
name|OptionalTarget
init|=
literal|4
decl_stmt|;
comment|/**      * Allow more than one target for a given source.      */
specifier|private
specifier|static
specifier|final
name|int
name|MultipleTarget
init|=
literal|8
decl_stmt|;
block|}
end_enum

begin_comment
comment|// End MappingType.java
end_comment

end_unit

