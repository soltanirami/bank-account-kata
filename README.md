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


To clarify the situation in the `README` of your project, you can explain the rationale for using the `provided` scope in the `pom.xml` and instruct users to handle the `gateway-starter` dependency on their own when using the `odihandler-model` library. Here's how you could phrase it:

---

# Project Overview

This project contains a library called **odihandler-model**, which includes functionalities from several dependencies. One of these dependencies is **gateway-starter**, which is also used by other libraries in the project. To avoid dependency conflicts or unwanted transitive dependencies, we've configured the `gateway-starter` dependency with a `provided` scope in the `pom.xml` of **odihandler-model**.

## Important Note on Dependency Management

The **odihandler-model** library depends on **gateway-starter**, but **gateway-starter** is not included transitively when you import **odihandler-model**. This is intentional to allow more flexibility for other libraries using **odihandler-model**, so that they can bring their own version of **gateway-starter**.

### Key points:
- **gateway-starter** is set with `provided` scope in **odihandler-model**.
- You will need to explicitly include **gateway-starter** in your own `pom.xml` or build configuration when using **odihandler-model** in your project.
- This approach ensures that other libraries can use their preferred version of **gateway-starter** without conflicts.

### Example: Adding Gateway-Starter to Your Project

If your project imports **odihandler-model**, make sure to also add your preferred version of **gateway-starter**:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>gateway-starter</artifactId>
    <version>1.2.3</version> <!-- Choose your desired version -->
</dependency>
```

By managing the **gateway-starter** dependency independently, you maintain full control over the version and configuration used across different parts of your project.

---

This should clarify how the dependency is handled and what users need to do when working with the `odihandler-model` library.