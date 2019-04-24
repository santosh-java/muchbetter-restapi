1. How long did you spend on the coding test? What would you add to your solution if you spent more time on it? If you didn't spend much time on the coding test then use this as an opportunity to explain what you would add.
- I spent around 16 Hours to complete the assignment in which:
  - 6 hours has gone in understanding the requirements, frameworks to be used 
  - 2 for the design of DB and API
  - 5 hours for coding
  - 3 hours for documentation
- If spent more time, then I would plan to implement the scalable DB design techniques discussed in the README.md DB Design section. And also HATEOAS wherever applicable

2. What was the most useful feature that was added to Java 8? Please include a snippet of code that shows how you've used it.
   - Lambdas and functional programming was the new feature that was added in Java 8 which I found very useful. [Application.java](../src/com/muchbetter/codetest/Application.java) contains Java8 Lambda method referencing and function implementations in place. 
   
3. What is your favourite framework/library/package that you love but couldn't use in the task? What do you like about it so much?
   - Both Ratpack and Redission are two new frameworks I had to learn and use in this task. I would prefer using Jersey as I am well used to it. But Ratpack is an asynchronous/lightweight framework that uses Promises heavily which is a very good learning experience for me during this task.
   
4. What great new thing you learnt about in the past year and what are you looking forward to learning more about over the next year?
   - I learnt/learning about distributed architecture for ReST APIs
   - Would like to learn more on highly scalable distributed systems and managing multiple micro-services integration
   
5. How would you track down a performance issue in production? Have you ever had to do this? Can you add anything to your implementation to help with this?
   - For tracking any performance issues, the first line of defence is our logging which should provide a little bit of insight into what is happening. I would like to add the total time taken by each of the APIs logged so that we will have a basic idea on where is the issue occurring. 
   - We can implement health check APIs which would track the JVM health and DB health of our application. The collected data can be eventually used for creating dashboards for monitoring the application health. We can also use visualization tools like Grafana.
   - Have the thread and memory dumps of the running JVM and analyze the dumps for any long waiting threads by using some memory analyzer tools like Eclipse Memory Analyser. We can find potential trouble makers by this.
   - Next is to understand the DB performance and check the performance using tools like RDBTools (in case of Redis DB) where we can have comprehensive information on the Memory analysis.
   - I did not get a chance to do this kind of analysis but was part of the team that had done the performance analysis and helped me to understand how we do this kind of stuff.
   
6. How would you improve the APIs that you just used?
   - By using a SQL Database instead of NoSQL Redis as our application deals with transactions of money and it needs ACID compliance 
   - By using OAuth2 for authorization 
   - By adding HATEOAS wherever needed to make the APIs more intuitive
   - By implementing pagination for /transactions API
7. Please describe yourself in JSON format.
   - {  
   "name":"Santosh Giri Govind Marthi",
   "age":"36",
   "educational qualification":"Master's Degree",
   "interests":"exploring anything new either technology/art",
   "life":"happily married with two kids"
}

8. What is the meaning of life?
   - Ups and Downs, highs and lows... all through these life has to flow... only then you will grow... and your soul will glow...
