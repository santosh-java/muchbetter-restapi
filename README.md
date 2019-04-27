# ratpack-restapi
ReST API implementation using Ratpack asynchronous API.

The submitted code contains ReST API implementation for the scenario described in [MuchBetter Interveiwer](https://github.com/shanmuha/interviewer/)

*The structuring of the classes is as follows:*
1. com.muchbetter.codetest.Application is the main class that bootstraps the netty server of Ratpack framework
2. All the API data model classes are in com.muchbetter.codetest.datamodel package
3. All the DB data model classes are in com.muchbetter.codetest.datamodel.db package
4. All the exception classes are in com.muchbetter.codetest.exception package
5. All the request handler classes are in com.muchbetter.codetest.handlers package
6. All other utilities and services classes are in com.muchbetter.codetest.utils and com.muchbetter.codetest.service respectively.

As per the requirement of the API, the technology stack used is as follows:
1. [Ratpack](https://ratpack.io/) as Java Server side framework which is lightweight netty based.
2. [Redis](https://redis.io/) is used for the storage server of the data.
3. [slf4j/log4j12](https://www.slf4j.org/manual.html) was used for API logging
4. [Redisson](https://github.com/redisson) client API framework for interacting with the Redis data server

For authentication purposes, as of now, I had used Base64 encode/decode to generate the token. And also did not include the changes
needed for token expiry, refresh token, etc. But for more robust token handling I would prefer to use JWT framework which is widely used
for OAuth2.

**Design:**

**_DB Design:_**
  As per the requirement, I decided to have two main Entities in the system.
  1. *User* '[UUID:userId, double:balance, Stirng:currency, List<Transaction>:userTransactions]'
  2. *Transaction* '[UUID:transactionId, Date:transactionDate,   String:transactionDescription, double:transactionAmount, 
  String:transactionCurrency, TransactionType:transactionType]'
  
  *And following details are stored in the Redis DB as Maps:*
  1. Token information as Map with Token as key and User.userId as value [StoreName: **TOKENS_STORE**]
  2. User information as Map with User.userId as key and User as value [StoreName: **USERS_STORE**]
  3. User transaction details as Map with User.userId as key and List<Transactions> as value [StoreName: **USER_TRANSACTION_STORE**]
  
  >Plan was to store only Transaction ID details in User.userTransactions and the actual transaction details in a separate 
  map so that the User data map will not bloat over time as the transactions increase.
  
  How will this DB design scale?
  - For the coding test purpose, I used only one instance of the Redis DB. But in reality, we will have to have more than one instance of DB servers running. To accommodate the multiple DB servers, we can make use of mod(%) based approach of storing the data specific to a User in the DB servers.  
  - For example, if we have configured 3 DB servers (DB0, DB1, DB2) for storage, then we can segregate all the Users into three groups say based on the hashCode value of their userId. Say there is a User with 123456 hash value his/her userId, then the DB instance for the user can be calculated as 123456 % 3 = 0, where 123456 is UserId hashcode and 3 is the number of DB servers we have. In this case, the user data shall be stored in DB0. Likewise, we can make sure that any request coming for a particular user can be routed to the same DB.  
  - If in case the number of DB servers have to be increased after some time, then at that point in time the new DB for existing users has to be calculated and the data migration has to be done. 
  - To avoid this scenario, we can make use of range based approach where we will have a pre-determined range of users for each DB instance.
  - That way if the number of Users is increasing, we can add new DB servers and have the new Users data stored in the new DB servers.
>**Since we are dealing with transactions involving money, I think Redis is not an ideal DB. Instead, we should use SQL database like Oracle as it is consistent with Writes and Reads**

**_API Design:_**
- Each API endpoint has a separate Handler instance which handles the incoming requests.
- Except /login API, all other APIs have been chained with AuthHandler so that before the actual API is hit, we will perform 
Authentication of the passed in auth token. If the auth succeeds, then only further processing of the request happens.

1. **_/login_** : As described, a POST request to this API will create a new User with default balance. To be consistent with the user balance, I 
added an initial Transaction for that default balance and have it updated all accorss. On Successful execution of the API, we will 
return the newly created User token as JSON format
- _Success Status_: 
  1. **201 CREATED** (As we are creating a new User every time this endpoint is hit with POST request)
- _Failure Statuses_: 
  1. **401 Unauthorized** if the provided token is invalid 
  2. **405 Method not allowed** for any HTTP method other than POST is invoked
  3. **500 Internal Server Error** for any exceptions during DB interactions or any other exceptions. 

2. **_/balance_** : GET requests to this API are handled and after successful authentication of the user, we will fetch the User data and return
the balance information in the form of JSON with balance and currency details
- _Success Status_:
  1. **200 OK** if the request is successfully handled
- _Failure Statuses_:
  1. **401 Unauthorized** if the provided token is invalid 
  2. **405 Method not allowed** for any HTTP method other than POST is invoked
  3. **500 Internal Server Error** for any exceptions during DB interactions or any other exceptions. 
 
3. **_/transactions_** : GET requests to this API are handled and after successful authentication of the user, we will fetch all the transactions
of the User from USER_TRANSACTION_STORE and returned to the client in the form of JSON.
- _Success Status_:
  1. **200 OK** if the request is successfully handled
- _Failure Statuses_: 
  1. **401 Unauthorized** if the provided token is invalid 
  2. **405 Method not allowed** for any HTTP method other than POST is invoked
  3. **500 Internal Server Error** for any exceptions during DB interactions or any other exceptions.  

4. **_/spend_** : POST requests to this API are handled and after successful authentication of the user by checking in TOKEN_STORE, we will
perform the following operations:
  - Fetch the User information from USERS_STORE
  - Fetch the Transactions list from USER_TRANSACTION_STORE
  - Fetch the transaction details from the request body 
  - Check if the current transaction amount can be deducted from the user balance or not and if so we will update both User balance
 and the Transactions list. The details are updated in the corresponding data stores.
  - If the balance will become invalid if we apply the transaction we will reject the transaction with an appropriate message.
 - _Success Status_:
  1. **200 OK** if the request is successfully handled
 - _Faioure Statuses_:
  1. **400 Bad Request** if the transaction will make the balance invalid after applying
  2. **401 Unauthorized** if the provided token is invalid 
  3. **405 Method not allowed** for any HTTP method other than GET is invoked
  4. **500 Internal Server Error** for any exceptions during DB interactions or any other exceptions. 

**Further improvements planned:**
1. For all the APIs, I would like to implement HAETOAS wherever necessary so that it will be intuitive for the clients to use
the API. 
2. Implement pagination for /transactions API.
3. Need to pass in the DB details and other configuration details as configurable parameters to the application so that it would be robust. As of now, DB details are hardcoded.

>**_Included a runnable JAR file which can be used to start the Ratpack server with all the four ReST endpoints started._**

> **- To run the jar please use: _java -jar MuchBetterCodeTest_BasicFunctionality.jar_**

> **- Redis DB server has to be installed for the application to run successfully at _localhost_ and default port _6379_**

> **- To find out the address at which the server has started, check the console logs or the log file (_muchbetter_api.log_) generated containing the entry similar to _"Ratpack started (development) for http://localhost:60531"_**
