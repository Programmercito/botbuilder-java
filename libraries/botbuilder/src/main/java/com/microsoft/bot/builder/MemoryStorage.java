package com.microsoft.bot.builder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.CompletableFuture;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A storage layer that uses an in-memory dictionary.
*/
public class MemoryStorage implements IStorage
{
	private static final ObjectMapper _mapper = new ObjectMapper();
    private HashMap<String, JsonNode> _memory;
	private final Object _syncroot = new Object();
	private int _eTag = 0;

	/** 
	 Initializes a new instance of the <see cref="MemoryStorage"/> class.
	 
	 @param dictionary A pre-existing dictionary to use; or null to use a new one.
	*/

	public MemoryStorage()
	{
		this(null);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: public MemoryStorage(Dictionary<string, JObject> dictionary = null)
	public MemoryStorage(HashMap<String, JsonNode> dictionary)
	{
	    _mapper.enableDefaultTyping();
		_memory = (dictionary != null) ? dictionary : new HashMap<String, JsonNode>();
	}

	/** 
	 Deletes storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to delete.

	 @return A task that represents the work queued to execute.
	 {@link ReadAsync(string[] )}
	 {@link WriteAsync(IDictionary{string, object} )}
	*/
	public final void DeleteAsync(String[] keys)
	{
		synchronized (_syncroot)
		{
			for (String key : keys)
			{
				_memory.remove(key);
			}
		}
	}

	/** 
	 Reads storage items from storage.
	 
	 @param keys keys of the <see cref="IStoreItem"/> objects to read.

	 @return A task that represents the work queued to execute.
	 If the activities are successfully sent, the task result contains
	 the items read, indexed by key.
	 {@link DeleteAsync(string[] )}
	 {@link WriteAsync(IDictionary{string, object} )}
	*/
	public final Map<String, JsonNode> ReadAsync(String[] keys)
	{
		HashMap<String, Object> storeItems = new HashMap<String, Object>(keys.length);
		synchronized (_syncroot)
		{
			for (String key : keys)
			{cd
				TValue state;
				if (_memory.containsKey(key) ? (state = _memory.get(key)) == state : false)
				{
					if (state != null)
					{
						storeItems.put(key, state.<Object>ToObject(StateJsonSerializer));
					}
				}
			}
		}

		return storeItems;
	}

	/** 
	 Writes storage items to storage.
	 
	 @param changes The items to write, indexed by key.

	 @return A task that represents the work queued to execute.
	 {@link DeleteAsync(string[] )}
	 {@link ReadAsync(string[] )}
	*/
	public final void WriteAsync(Map<String, Object> changes)
	{
		synchronized (_syncroot)
		{
			for (Map.Entry<String, Object> change : changes.entrySet())
			{
//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var newValue = change.getValue();

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var oldStateETag = null;

				TValue oldState;
				if (_memory.containsKey(change.getKey()) ? (oldState = _memory.get(change.getKey())) == oldState : false)
				{
					Object etag;
//C# TO JAVA CONVERTER TODO TASK: The following method call contained an unresolved 'out' keyword - these cannot be converted using the 'OutObject' helper class unless the method is within the code being modified:
					if (oldState.TryGetValue("eTag", out etag))
					{
						oldStateETag = etag.<String>Value();
					}
				}

//C# TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java unless the Java 10 inferred typing option is selected:
				var newState = JObject.FromObject(newValue, StateJsonSerializer);

				// Set ETag if applicable
//C# TO JAVA CONVERTER TODO TASK: Java has no equivalent to C# pattern variables in 'is' expressions:
//ORIGINAL LINE: if (newValue is IStoreItem newStoreItem)
				if (newValue instanceof IStoreItem newStoreItem)
				{
					if (oldStateETag != null && !newStoreItem.ETag.equals("*") && newStoreItem.ETag != oldStateETag)
					{
						throw new RuntimeException(String.format("Etag conflict.\r\n\r\nOriginal: %1$s\r\nCurrent: %2$s", newStoreItem.ETag, oldStateETag));
					}

					newState["eTag"] = (_eTag++).toString();
				}

				_memory.put(change.getKey(), newState);
			}
		}

		return Task.CompletedTask;
	}
}