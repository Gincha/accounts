Service uses in memory DB that is refreshed on each service restart.

ClientA has accounts A (balance 15.12 EUR) and C (balance 135 EUR)
ClientB has account B (balance 0.01 EUR)

### Available API`s

* Get all accounts for client (GET http://localhost:8080/accounts/{clientId})
  * curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8080/accounts/clientA


* Get all transactions for accounts (GET http://localhost:8080/accounts/{accountId}/transactions) (optional offset and limit parameters can be set to manage page results) 
  * curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8080/accounts/B/transactions
  * curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://localhost:8080/accounts/B/transactions?offset=0&limit=3


* Transfer funds between two accounts (POST http://localhost:8080/accounts/transfer)
  * curl --header "Content-Type: application/json" \
    --request POST \
    --data '{"fromAccount":"A","toAccount":"B","currency":"EUR","amount":10.0}' \
    http://localhost:8080/accounts/transfer

## Database
DB is accessible on http://localhost:8080/h2-console with username "user"