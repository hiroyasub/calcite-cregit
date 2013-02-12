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
name|optiq
operator|.
name|runtime
package|;
end_package

begin_comment
comment|/**  * Utility methods called by generated code.  */
end_comment

begin_class
specifier|public
class|class
name|Utilities
block|{
specifier|public
specifier|static
name|boolean
name|equal
parameter_list|(
name|Object
name|o0
parameter_list|,
name|Object
name|o1
parameter_list|)
block|{
return|return
name|o0
operator|==
name|o1
operator|||
name|o0
operator|!=
literal|null
operator|&&
name|o0
operator|.
name|equals
argument_list|(
name|o1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|boolean
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
operator|(
name|v
condition|?
literal|2
else|:
literal|1
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|byte
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|char
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|short
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|int
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
name|v
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|long
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
operator|(
name|int
operator|)
operator|(
name|v
operator|^
operator|(
name|v
operator|>>>
literal|32
operator|)
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|float
name|v
parameter_list|)
block|{
return|return
name|hash
argument_list|(
name|h
argument_list|,
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|double
name|v
parameter_list|)
block|{
return|return
name|hash
argument_list|(
name|h
argument_list|,
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|,
name|Object
name|v
parameter_list|)
block|{
return|return
name|h
operator|*
literal|37
operator|+
operator|(
name|v
operator|==
literal|null
condition|?
literal|1
else|:
name|v
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|boolean
name|v0
parameter_list|,
name|boolean
name|v1
parameter_list|)
block|{
comment|// Same as Boolean.compare (introduced in JDK 1.7)
return|return
operator|(
name|v0
operator|==
name|v1
operator|)
condition|?
literal|0
else|:
operator|(
name|v0
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|byte
name|v0
parameter_list|,
name|byte
name|v1
parameter_list|)
block|{
comment|// Same as Byte.compare (introduced in JDK 1.7)
return|return
name|v0
operator|-
name|v1
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|char
name|v0
parameter_list|,
name|char
name|v1
parameter_list|)
block|{
comment|// Same as Character.compare (introduced in JDK 1.7)
return|return
name|v0
operator|-
name|v1
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|short
name|v0
parameter_list|,
name|short
name|v1
parameter_list|)
block|{
comment|// Same as Short.compare (introduced in JDK 1.7)
return|return
name|v0
operator|-
name|v1
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|int
name|v0
parameter_list|,
name|int
name|v1
parameter_list|)
block|{
comment|// Same as Integer.compare (introduced in JDK 1.7)
return|return
operator|(
name|v0
operator|<
name|v1
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|v0
operator|==
name|v1
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|long
name|v0
parameter_list|,
name|long
name|v1
parameter_list|)
block|{
comment|// Same as Long.compare (introduced in JDK 1.7)
return|return
operator|(
name|v0
operator|<
name|v1
operator|)
condition|?
operator|-
literal|1
else|:
operator|(
operator|(
name|v0
operator|==
name|v1
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|float
name|v0
parameter_list|,
name|float
name|v1
parameter_list|)
block|{
return|return
name|Float
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|double
name|v0
parameter_list|,
name|double
name|v1
parameter_list|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|v0
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compare
parameter_list|(
name|Comparable
name|v0
parameter_list|,
name|Comparable
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compareNullsFirst
parameter_list|(
name|Comparable
name|v0
parameter_list|,
name|Comparable
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|==
name|v1
condition|?
literal|0
else|:
name|v0
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|v1
operator|==
literal|null
condition|?
literal|1
else|:
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|compareNullsLast
parameter_list|(
name|Comparable
name|v0
parameter_list|,
name|Comparable
name|v1
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|v0
operator|==
name|v1
condition|?
literal|0
else|:
name|v0
operator|==
literal|null
condition|?
literal|1
else|:
name|v1
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|v0
operator|.
name|compareTo
argument_list|(
name|v1
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Utilities.java
end_comment

end_unit

