## Task 3 REST endpoints

### Get all trips

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:05:09 GMT
Content-Type: application/json
Content-Length: 285

[
{
"id": 1,
"category": "CITY",
"starttime": "10:00",
"name": "Adventure trip",
"endtime": "18:00",
"startposition": "Athens",
"price": 100.0,
"guide": null
},
{
"id": 2,
"category": "SNOW",
"starttime": "09:00",
"name": "Mountain trip",
"endtime": "17:00",
"startposition": "Thessaloniki",
"price": 150.0,
"guide": 1
}
]

### Get trip by id 2

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:25:06 GMT
Content-Type: application/json
Content-Length: 266

{
"trip": {
"id": 2,
"category": "SNOW",
"starttime": "09:00",
"name": "Mountain trip",
"endtime": "17:00",
"startposition": "Thessaloniki",
"price": 150.0,
"guide": 1
},
"guide": {
"id": 1,
"firstname": "John",
"lastname": "Doe",
"email": "john@doe",
"phone": "1234567890",
"yearsOfExperience": 5
}
}

### Get trip by id 0 || Exception

HTTP/1.1 404 Not Found
Date: Mon, 04 Nov 2024 10:06:07 GMT
Content-Type: application/json
Content-Length: 95

{
"status": 404,
"message": "Trip with id 0 not found",
"timestamp": "2024-11-04T11:06:07.351921500"
}

### Post new trio with json body

HTTP/1.1 201 Created
Date: Mon, 04 Nov 2024 10:06:55 GMT
Content-Type: application/json
Content-Length: 184

{
"id": 3,
"category": "SNOW",
"starttime": "2024-11-04T09:00:00",
"name": "Mountain Adventure",
"endtime": "2024-11-04T17:00:00",
"startposition": "Mountain Base Camp",
"price": 150.0,
"guide": null
}

### Put, Update trip with id 3

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:08:13 GMT
Content-Type: application/json
Content-Length: 183

{
"id": 3,
"category": "SNOW",
"starttime": "2024-11-04T09:00:00",
"name": "updated name !!!!",
"endtime": "2024-11-04T17:00:00",
"startposition": "Everrest Base Camp",
"price": 150.0,
"guide": null
}

### add guide to trip with id 3

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:09:14 GMT
Content-Type: application/json
Content-Length: 32

Successfully added guide to trip

### delete trip with id 3

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:10:03 GMT
Content-Type: application/json
Content-Length: 36

Successfully deleted trip with id: 3

### delete trip with id 0 || Exception

HTTP/1.1 404 Not Found
Date: Mon, 04 Nov 2024 10:28:01 GMT
Content-Type: application/json
Content-Length: 95

{
"status": 404,
"message": "Trip with id 0 not found",
"timestamp": "2024-11-04T11:28:01.037061700"
}

## 3.3.5

### Theoretical question: Why do we suggest a PUT method for adding a guide to a trip instead of a POST method?

We use the put method, because we are trying to add a guide to an already existing trip. Therefor we only need to update
that one value and not post a new trip entity.
Put also shows clear intention for other users or developers of the api
and its Idempotent meaning making the same put request multiple times will have the same effect as doing it once.
which is important for updating and adding resources consistently.

## Task 5

### Get trips by category "snow"

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 10:34:54 GMT
Content-Type: application/json
Content-Length: 332

[
{
"id": 2,
"category": "SNOW",
"starttime": "09:00",
"name": "Mountain trip",
"endtime": "17:00",
"startposition": "Thessaloniki",
"price": 150.0,
"guide": null
},
{
"id": 3,
"category": "SNOW",
"starttime": "2024-11-04T09:00:00",
"name": "Mountain Adventure",
"endtime": "2024-11-04T17:00:00",
"startposition": "Mountain Base Camp",
"price": 150.0,
"guide": null
}
]

## Task 6

### Updated get trip by id 1 with packing list from api

HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 11:18:31 GMT
Content-Type: application/json
Content-Encoding: gzip
Content-Length: 541

{
"trip": {
"id": 1,
"category": "CITY",
"starttime": "10:00",
"name": "Adventure trip",
"endtime": "18:00",
"startposition": "Athens",
"price": 100.0,
"guide": null
},
"packingList": {
"items": [
{
"name": "City Map",
"weightInGrams": 100,
"quantity": 1,
"description": "Detailed map of popular tourist spots.",
"category": "city",
"createdAt": "2024-10-30T17:44:58.547Z",
"updatedAt": "2024-10-30T17:44:58.547Z",
"buyingOptions": [
{
"shopName": "Map World",
"shopUrl": "https://shop5.com",
"price": 10.0
}
]
},
{
"name": "City Guidebook",
"weightInGrams": 150,
"quantity": 1,
"description": "Comprehensive guidebook for city travelers.",
"category": "city",
"createdAt": "2024-10-30T17:44:58.547Z",
"updatedAt": "2024-10-30T17:44:58.547Z",
"buyingOptions": [
{
"shopName": "Bookstore",
"shopUrl": "https://shop22.com",
"price": 20.0
}
]
},
{
"name": "Portable Phone Charger",
"weightInGrams": 150,
"quantity": 1,
"description": "Handy charger for long city explorations.",
"category": "city",
"createdAt": "2024-10-30T17:44:58.547Z",
"updatedAt": "2024-10-30T17:44:58.547Z",
"buyingOptions": [
{
"shopName": "Tech Gadgets",
"shopUrl": "https://shop5.com",
"price": 20.0
}
]
},
{
"name": "City Metro Card",
"weightInGrams": 10,
"quantity": 1,
"description": "Rechargeable metro card for city transportation.",
"category": "city",
"createdAt": "2024-10-30T17:44:58.547Z",
"updatedAt": "2024-10-30T17:44:58.547Z",
"buyingOptions": [
{
"shopName": "City Transit",
"shopUrl": "https://shop6.com",
"price": 15.0
}
]
},
{
"name": "Compact Umbrella",
"weightInGrams": 200,
"quantity": 1,
"description": "Small umbrella, perfect for unexpected showers.",
"category": "city",
"createdAt": "2024-10-30T17:44:58.547Z",
"updatedAt": "2024-10-30T17:44:58.547Z",
"buyingOptions": [
{
"shopName": "Rainy Days",
"shopUrl": "https://shop7.com",
"price": 12.0
}
]
}
]
}
}


### Get sum of weight for a given trip: trip 1
HTTP/1.1 200 OK
Date: Mon, 04 Nov 2024 11:30:09 GMT
Content-Type: application/json
Content-Length: 45

Total weight for trip with id 1 is: 610 grams


## Task 8 Security

### trying to post without being logged in: example

POST http://localhost:7070/api/trips

HTTP/1.1 401 Unauthorized
Date: Mon, 04 Nov 2024 12:29:55 GMT
Content-Type: text/plain
Content-Length: 25

You need to log in, dude!
