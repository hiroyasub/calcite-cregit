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
name|property
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Definition and accessor for a property.  *  *<p>For example:  *  *<blockquote><code>  *<pre>  * class MyProperties extends Properties {  *     public final IntegerProperty DebugLevel =  *         new IntegerProperty(this, "com.acme.debugLevel", 10);  * }  *  * MyProperties props = new MyProperties();  * System.out.println(props.DebugLevel.get()); // prints "10", the default  * props.DebugLevel.set(20);  * System.out.println(props.DebugLevel.get()); // prints "20"  *</pre>  *</code></blockquote>  *  * @author jhyde  * @version $Id$  * @since May 4, 2004  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Property
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|Properties
name|properties
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|String
name|defaultValue
decl_stmt|;
comment|/**      * List of triggers on this property. Access must be synchronized on this      * Property object.      */
specifier|private
specifier|final
name|TriggerList
name|triggerList
init|=
operator|new
name|TriggerList
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a Property and associates it with an underlying properties      * object.      *      * @param properties Properties object which holds values for this property.      * @param path Name by which this property is serialized to a properties      * file, for example "com.acme.trace.Verbosity".      * @param defaultValue Default value, null if there is no default.      */
specifier|protected
name|Property
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
if|if
condition|(
name|properties
operator|instanceof
name|TriggerableProperties
condition|)
block|{
operator|(
operator|(
name|TriggerableProperties
operator|)
name|properties
operator|)
operator|.
name|register
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the name of this property. Typically a dotted path such as      * "com.acme.foo.Bar".      *      * @return this property's name (typically a dotted path)      */
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|/**      * Returns the default value of this property. Derived classes (for example      * those with special rules) can override.      */
specifier|public
name|String
name|getDefaultValue
parameter_list|()
block|{
return|return
name|defaultValue
return|;
block|}
comment|/**      * Retrieves the value of a property, using a given default value, and      * optionally failing if there is no value.      */
specifier|protected
name|String
name|getInternal
parameter_list|(
name|String
name|defaultValue
parameter_list|,
name|boolean
name|required
parameter_list|)
block|{
name|String
name|value
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|path
argument_list|,
name|defaultValue
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
if|if
condition|(
name|defaultValue
operator|==
literal|null
condition|)
block|{
name|value
operator|=
name|getDefaultValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
block|}
if|if
condition|(
name|required
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Property "
operator|+
name|path
operator|+
literal|" must be set"
argument_list|)
throw|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * Adds a trigger to this property.      */
specifier|public
specifier|synchronized
name|void
name|addTrigger
parameter_list|(
name|Trigger
name|trigger
parameter_list|)
block|{
name|triggerList
operator|.
name|add
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a trigger from this property.      */
specifier|public
specifier|synchronized
name|void
name|removeTrigger
parameter_list|(
name|Trigger
name|trigger
parameter_list|)
block|{
name|triggerList
operator|.
name|remove
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called when a property's value has just changed.      *      *<p>If one of the triggers on the property throws a {@link      * org.eigenbase.util.property.Trigger.VetoRT} exception, this method passes      * it on.      *      * @param oldValue Previous value of the property      * @param value New value of the property      *      * @throws org.eigenbase.util.property.Trigger.VetoRT if one of the triggers      * threw a VetoRT      */
specifier|public
name|void
name|onChange
parameter_list|(
name|String
name|oldValue
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|TriggerableProperties
operator|.
name|equals
argument_list|(
name|oldValue
argument_list|,
name|value
argument_list|)
condition|)
block|{
return|return;
block|}
name|triggerList
operator|.
name|execute
argument_list|(
name|this
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets a property directly as a string.      *      * @return the previous value      */
specifier|public
name|String
name|setString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|properties
operator|.
name|setProperty
argument_list|(
name|path
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * Returns whether this property has a value assigned.      */
specifier|public
name|boolean
name|isSet
parameter_list|()
block|{
return|return
name|properties
operator|.
name|get
argument_list|(
name|path
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the value of this property as a string.      */
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|properties
operator|.
name|getProperty
argument_list|(
name|path
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
comment|/**      * Returns the boolean value of this property.      */
specifier|public
name|boolean
name|booleanValue
parameter_list|()
block|{
specifier|final
name|String
name|value
init|=
name|getInternal
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|toBoolean
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**      * Converts a string to a boolean.      *      *<p/>Note that {@link Boolean#parseBoolean(String)} is similar, but only      * exists from JDK 1.5 onwards, and only accepts 'true'.      *      * @return true if the string is "1" or "true" or "yes", ignoring case and      * any leading or trailing spaces      */
specifier|public
specifier|static
name|boolean
name|toBoolean
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
name|String
name|trimmedLowerValue
init|=
name|value
operator|.
name|toLowerCase
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
return|return
name|trimmedLowerValue
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|trimmedLowerValue
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
operator|||
name|trimmedLowerValue
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
return|;
block|}
comment|/**      * Returns the value of the property as a string, or null if the property is      * not set.      */
specifier|public
name|String
name|stringValue
parameter_list|()
block|{
return|return
name|getInternal
argument_list|(
literal|null
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**      * A trigger list a list of triggers associated with a given property.      *      *<p/>A trigger list is associated with a property key, and contains zero      * or more {@link Trigger} objects.      *      *<p/>Each {@link Trigger} is stored in a {@link WeakReference} so that      * when the Trigger is only reachable via weak references the Trigger will      * be be collected and the contents of the WeakReference will be set to      * null.      */
specifier|private
specifier|static
class|class
name|TriggerList
extends|extends
name|ArrayList
block|{
comment|/**          * Adds a Trigger, wrapping it in a WeakReference.          *          * @param trigger          */
name|void
name|add
parameter_list|(
specifier|final
name|Trigger
name|trigger
parameter_list|)
block|{
comment|// this is the object to add to list
name|Object
name|o
init|=
operator|(
name|trigger
operator|.
name|isPersistent
argument_list|()
operator|)
condition|?
name|trigger
else|:
operator|(
name|Object
operator|)
operator|new
name|WeakReference
comment|/*<Trigger>*/
argument_list|(
name|trigger
argument_list|)
decl_stmt|;
comment|// Add a Trigger in the correct group of phases in the list
for|for
control|(
name|ListIterator
comment|/*<Object>*/
name|it
init|=
name|listIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Trigger
name|t
init|=
name|convert
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|trigger
operator|.
name|phase
argument_list|()
operator|<
name|t
operator|.
name|phase
argument_list|()
condition|)
block|{
comment|// add it before
name|it
operator|.
name|hasPrevious
argument_list|()
expr_stmt|;
name|it
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return;
block|}
if|else if
condition|(
name|trigger
operator|.
name|phase
argument_list|()
operator|==
name|t
operator|.
name|phase
argument_list|()
condition|)
block|{
comment|// add it after
name|it
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|super
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
comment|/**          * Removes the given Trigger.          *          *<p/>In addition, removes any {@link WeakReference} that is empty.          *          * @param trigger          */
name|void
name|remove
parameter_list|(
specifier|final
name|Trigger
name|trigger
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Trigger
name|t
init|=
name|convert
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|t
operator|.
name|equals
argument_list|(
name|trigger
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**          * Executes every {@link Trigger} in this {@link TriggerList}, passing          * in the property key whose change was the casue.          *          *<p/>In addition, removes any {@link WeakReference} that is empty.          *          *<p>Synchronizes on {@code property} while modifying the trigger list.          *          * @param property The property whose change caused this property to          * fire          */
name|void
name|execute
parameter_list|(
name|Property
name|property
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|Trigger
operator|.
name|VetoRT
block|{
comment|// Make a copy so that if during the execution of a trigger a
comment|// Trigger is added or removed, we do not get a concurrent
comment|// modification exception. We do an explicit copy (rather than
comment|// a clone) so that we can remove any WeakReference whose
comment|// content has become null. Synchronize, per the locking strategy,
comment|// while the copy is being made.
name|List
comment|/*<Trigger>*/
name|l
init|=
operator|new
name|ArrayList
comment|/*<Trigger>*/
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|property
init|)
block|{
for|for
control|(
name|Iterator
comment|/*<Object>*/
name|it
init|=
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Trigger
name|t
init|=
name|convert
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|l
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|l
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Trigger
name|t
init|=
operator|(
name|Trigger
operator|)
name|l
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|t
operator|.
name|execute
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Converts a trigger or a weak reference to a trigger into a trigger.          * The result may be null.          */
specifier|private
name|Trigger
name|convert
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|WeakReference
condition|)
block|{
name|o
operator|=
operator|(
operator|(
name|WeakReference
operator|)
name|o
operator|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|Trigger
operator|)
name|o
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Property.java
end_comment

end_unit

