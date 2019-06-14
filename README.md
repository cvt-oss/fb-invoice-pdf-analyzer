This service parses and store Facebook invoice for campaings.

### Installation:

 - Build the source:
 ```
 $ mvn clean package
 ```
 - Start the application:
 ```
 docker-compose up --build -d
 ```

 The application is now accessible at `http://localhost:8080`

Usage:

- Post the Facebook invoice in PDF format (only Invoices in Czech language are supported)
```
http -f POST http://localhost:8080/api/pdf/invoice/process invoice@./invoice.pdf
```
The service returns an `id` in JSON format:
```json
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Fri, 14 Jun 2019 10:02:34 GMT
Transfer-Encoding: chunked

{
    "id": 1
}
```

- Retrieve the parsed invoice:
```
http http://localhost:8080/api/pdf/invoice/1
```

- The response is parsed invoice in a JSON format:
```json
HTTP/1.1 200
Content-Type: application/json;charset=UTF-8
Date: Fri, 14 Jun 2019 10:03:11 GMT
Transfer-Encoding: chunked

{
    "accountId": "sampleAccountId",
    "id": 1,
    "invoiceItems": [
        {
            "campaingName": "CampaingName1",
            "id": 2,
            "price": "1 000,86 Kč"
        },
        {
            "campaingName": "CampaingName2",
            "id": 3,
            "price": "200,97 Kč"
        }
    ],
    "originalFileName": "invoice.pdf",
    "paidOn": "19. 1. 2019 15:56",
    "referentialNumber": "sampleRefNumber",
    "totalPaid": "100,00 Kč (CZK)",
    "transactionId": "sampleTransactionId"
}
```

- If non existent invoice is being retrieved, 404 is returned:
```
$ http http://localhost:8080/api/pdf/invoice/2

HTTP/1.1 404
Content-Length: 0
Date: Fri, 14 Jun 2019 10:05:29 GMT
```
