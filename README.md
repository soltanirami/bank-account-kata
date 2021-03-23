# Bank account kata
https://gist.github.com/abachar/d20bdcd07dac589feef8ef21b487648c

In order to develop My BankAccount kata i choosed to use :
- Java 8
- Spring boot && spring web
- Swagger 
- H2 Database
- Lombok
- Guava

MyBankAccountApp is based on Event Sourcing and CQRS (Command Query Responsibility Segregation).

The application publishes three APIs :
- /my-account/deposit/ ===> to make a deposit
- /my-account/withdrawl/ ===> to make a withdrawl
- /my-account/transactions/ ===> to list all my transactions

All the APIs are testable with Swagger UI via this link http://localhost:8080/swagger-ui.html# after running the app.
