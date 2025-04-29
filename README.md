# currency-converter-java-spring-boot
Currency Converter App
A Spring Boot application to convert currencies using the Currency Beacon API, with smart caching via database to reduce API calls. Built using Java, Spring Boot, and JPA with best software engineering practices (SOLID principles, clean architecture, DI, testing, and documentation).

 **Features**
1 Dropdowns for source and target currencies (loaded from IBAN Currency Codes)
2 Accepts whole numbers only as the amount to convert
3 Fetches real-time conversion rates from Currency Beacon API (if no record in DB within the last 1 hour)
4 Persists conversion rates in the database to minimize API calls
