begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Serializable wrapper around a {@link Charset}.  *  *<p>It serializes itself by writing out the name of the character set, for  * example "ISO-8859-1". On the other side, it deserializes itself by looking  * for a charset with the same name.  *  *<p>A SerializableCharset is immutable.  */
end_comment

begin_class
specifier|public
class|class
name|SerializableCharset
implements|implements
name|Serializable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|Charset
name|charset
decl_stmt|;
specifier|private
name|String
name|charsetName
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SerializableCharset. External users should call {@link    * #forCharset(Charset)}.    *    * @param charset Character set; must not be null    */
specifier|private
name|SerializableCharset
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
assert|assert
name|charset
operator|!=
literal|null
assert|;
name|this
operator|.
name|charset
operator|=
name|charset
expr_stmt|;
name|this
operator|.
name|charsetName
operator|=
name|charset
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Per {@link Serializable}.    */
specifier|private
name|void
name|writeObject
parameter_list|(
name|ObjectOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeObject
argument_list|(
name|charset
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Per {@link Serializable}.    */
specifier|private
name|void
name|readObject
parameter_list|(
name|ObjectInputStream
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|charsetName
operator|=
operator|(
name|String
operator|)
name|in
operator|.
name|readObject
argument_list|()
expr_stmt|;
name|charset
operator|=
name|Charset
operator|.
name|availableCharsets
argument_list|()
operator|.
name|get
argument_list|(
name|this
operator|.
name|charsetName
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the wrapped {@link Charset}.    *    * @return the wrapped Charset    */
specifier|public
name|Charset
name|getCharset
parameter_list|()
block|{
return|return
name|charset
return|;
block|}
comment|/**    * Returns a SerializableCharset wrapping the given Charset, or null if the    * {@code charset} is null.    *    * @param charset Character set to wrap, or null    * @return Wrapped charset    */
specifier|public
specifier|static
name|SerializableCharset
name|forCharset
parameter_list|(
name|Charset
name|charset
parameter_list|)
block|{
if|if
condition|(
name|charset
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SerializableCharset
argument_list|(
name|charset
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SerializableCharset.java
end_comment

end_unit

