package Microsoft.Bot.Builder;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.


/** 
 A method that can participate in send activity events for the current turn.
 
 @param turnContext The context object for the turn.
 @param activities The activities to send.
 @param next The delegate to call to continue event processing.
 @return A task that represents the work queued to execute.
 A handler calls the <paramref name="next"/> delegate to pass control to
 the next registered handler. If a handler doesn’t call the next delegate,
 the adapter does not call any of the subsequent handlers and does not send the
 <paramref name="activities"/>.
 <p>If the activities are successfully sent, the <paramref name="next"/> delegate returns
 an array of <see cref="ResourceResponse"/> objects containing the IDs that
 the receiving channel assigned to the activities. Use this array as the return value of this handler.</p>
 
 {@link BotAdapter}
 {@link UpdateActivityHandler}
 {@link DeleteActivityHandler}
 {@link ITurnContext.OnSendActivities(SendActivitiesHandler)}
*/
@FunctionalInterface
public interface SendActivitiesHandler
{
	Task<ResourceResponse[]> invoke(ITurnContext turnContext, java.util.ArrayList<Activity> activities, Func<Task<ResourceResponse[]>> next);
}