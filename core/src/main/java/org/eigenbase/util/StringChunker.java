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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
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
name|logging
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility class for breaking huge strings into smaller&quot;chunks&quot; for  * applications that require it (such as fitting into transmission buffers).  * The class contains methods that allow the user to specify the maximum chunk  * length, or by default uses a maximum chunk length of 32,000 characters.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|StringChunker
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_CHUNK_SIZE
init|=
literal|32000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"org.eigenbase.util.StringChunker"
argument_list|)
decl_stmt|;
comment|/**    * Breaks a large string into&quot;chunks&quot; of no more than a specified    * length for easier transmission or storage. The array of resulting chunks    * are kept in order so they can be reassembled easily.    *    * @param input  String to break into chunks    * @param maxLen Maximum size (in characters) of a chunk    * @return String array containing the chunks of the input string, in order    * If the input string is empty, the return is a single-element array    * containing an empty string.    * @throws NullPointerException if the input string is null    */
specifier|public
specifier|static
name|String
index|[]
name|slice
parameter_list|(
name|String
name|input
parameter_list|,
name|int
name|maxLen
parameter_list|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|input
argument_list|)
condition|)
block|{
comment|// handle easy case of empty input string
return|return
operator|new
name|String
index|[]
block|{
name|input
block|}
return|;
block|}
name|String
index|[]
name|results
init|=
operator|new
name|String
index|[
name|countChunks
argument_list|(
name|input
operator|.
name|length
argument_list|()
argument_list|,
name|maxLen
argument_list|)
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
name|int
name|rowNum
init|=
literal|0
decl_stmt|;
name|String
name|temp
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|input
operator|.
name|length
argument_list|()
condition|)
block|{
name|temp
operator|=
name|input
operator|.
name|substring
argument_list|(
name|offset
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|offset
operator|+
name|maxLen
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|results
index|[
name|rowNum
index|]
operator|=
name|temp
expr_stmt|;
name|offset
operator|+=
name|maxLen
expr_stmt|;
name|rowNum
operator|++
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
comment|/**    * Breaks a large string into&quot;chunks&quot; of up to 32,000 characters    * for easier transmission or storage. The array of resulting chunks are    * kept in order so they can be reassembled easily.    *    * @param input String to break into chunks    * @return String array containing the chunks of the input string, in order    * If the input string is empty, the return is a single-element array    * containing an empty string.    * @throws NullPointerException if the input string is null    */
specifier|public
specifier|static
name|String
index|[]
name|slice
parameter_list|(
name|String
name|input
parameter_list|)
block|{
return|return
name|slice
argument_list|(
name|input
argument_list|,
name|DEFAULT_CHUNK_SIZE
argument_list|)
return|;
block|}
comment|/**    * Calculates the number of&quot;chunks&quot; created when dividing a    * corpus of a certain size into chunks of a given size.    *    * @param wholeSize int representing the size of the corpus    * @param chunkSize int representing the maximum size of each chunk    * @return int representing the total number of chunks    */
specifier|public
specifier|static
specifier|final
name|int
name|countChunks
parameter_list|(
name|int
name|wholeSize
parameter_list|,
name|int
name|chunkSize
parameter_list|)
block|{
if|if
condition|(
name|wholeSize
operator|==
literal|0
condition|)
block|{
name|wholeSize
operator|=
literal|1
expr_stmt|;
block|}
name|int
name|result
init|=
name|wholeSize
operator|/
name|chunkSize
decl_stmt|;
if|if
condition|(
name|wholeSize
operator|%
name|chunkSize
operator|!=
literal|0
condition|)
block|{
name|result
operator|++
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Writes a chunked string into a PreparedStatement sink. Each chunk is    * written with a sequential index in its row, so code reading the chunks    * can reconstruct the original string by ordering the chunks on read with    * an ORDER BY clause.<P>    * The prepared statement must contain two columns: the first being an    * INTEGER to hold the index value, and the second a VARCHAR of sufficient    * size to hold the largest chunk value.    *    * @param chunks   Array of String objects representing the chunks of a larger    *                 original String    * @param ps       PreparedStatement that will write each indexed chunk    * @param startIdx int representing the initial chunk index, which will be    *                 incremented for subsequent chunks    * @return the number of chunks written    * @throws SQLException if the input data does not match the prepared    *                      statement or if the statement execution fails    */
specifier|public
specifier|static
name|int
name|writeChunks
parameter_list|(
name|String
index|[]
name|chunks
parameter_list|,
name|PreparedStatement
name|ps
parameter_list|,
name|int
name|startIdx
parameter_list|)
throws|throws
name|SQLException
block|{
name|int
name|idx
init|=
name|startIdx
decl_stmt|;
for|for
control|(
name|String
name|chunk
range|:
name|chunks
control|)
block|{
name|ps
operator|.
name|setInt
argument_list|(
literal|1
argument_list|,
name|idx
argument_list|)
expr_stmt|;
name|ps
operator|.
name|setString
argument_list|(
literal|2
argument_list|,
name|chunk
argument_list|)
expr_stmt|;
name|ps
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
name|idx
operator|++
expr_stmt|;
block|}
return|return
name|idx
operator|-
name|startIdx
return|;
block|}
comment|/**    * Writes a chunked string into a PreparedStatement sink. Each chunk is    * written with a sequential index in its row, so code reading the chunks    * can reconstruct the original string by ordering the chunks on read with    * an ORDER BY clause. The chunk index starts at zero.<P>    * The prepared statement must contain two columns: the first being an    * INTEGER to hold the index value, and the second a VARCHAR of sufficient    * size to hold the largest chunk value.    *    * @param chunks Array of String objects representing the chunks of a larger    *               original String    * @param ps     PreparedStatement that will write each indexed chunk    * @return the number of chunks written    * @throws SQLException if the input data does not match the prepared    *                      statement or if the statement execution fails    */
specifier|public
specifier|static
name|int
name|writeChunks
parameter_list|(
name|String
index|[]
name|chunks
parameter_list|,
name|PreparedStatement
name|ps
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|writeChunks
argument_list|(
name|chunks
argument_list|,
name|ps
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Reads a set of string&quot;chunks&quot; from a ResultSet and    * concatenates them in the order read to produce a large, single string.    * One column from each row is designated as the one containing the string    * chunks.    * This is the complement to the<code>writeChunks()</code> methods.    *    * @param rs          ResultSet to read chunks from    * @param columnIndex index indicating which column in the result set to    *                    use as the chunk for each row    * @return String containing all concatenated chunks    * @throws SQLException if there is a database access problem or if the    *                      designated column is not a string    */
specifier|public
specifier|static
name|String
name|readChunks
parameter_list|(
name|ResultSet
name|rs
parameter_list|,
name|int
name|columnIndex
parameter_list|)
throws|throws
name|SQLException
block|{
name|int
name|chunkCount
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|DEFAULT_CHUNK_SIZE
argument_list|)
decl_stmt|;
name|String
name|chunk
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|rs
operator|.
name|next
argument_list|()
condition|)
block|{
name|chunk
operator|=
name|rs
operator|.
name|getString
argument_list|(
name|columnIndex
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
name|chunkCount
operator|++
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Reads a set of strings&quot;chunks&quot; from a ResultSet and    * concatenates them in the order read to produce a large, single string.    * The chunks are taken from the first column of each row in the result    * set.    *    * @param rs ResultSet to read chunks from    * @return String containing all concatenated chunks    * @throws SQLException if there is a database access problem or if the    *                      designated column is not a string    */
specifier|public
specifier|static
name|String
name|readChunks
parameter_list|(
name|ResultSet
name|rs
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|readChunks
argument_list|(
name|rs
argument_list|,
literal|1
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End StringChunker.java
end_comment

end_unit

