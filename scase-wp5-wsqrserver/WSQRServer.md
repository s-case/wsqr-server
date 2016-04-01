FORMAT: 1A

# WSQR Server API
This is the API of the NLP Server created as part of the EU-funded project [S-CASE](http://www.scasefp7.eu/).
This page serves as usage instructions for the RESTful WSQR web service.

## Media Types
This API uses the JSON media-type for all possible actions. Requests and responses must all have JSON format.

## Error States
The common HTTP Response Status Codes are used.

# WSQR Server API Root [/]
This is the WSQR Server API entry point. It contains a description of the service and links to the main resources.

## Retrieve the Entry Point [GET]

+ Response 200 (application/json)
    + Headers

            {
               "transfer-encoding": "chunked",
               "date": "...",
               "content-type": "application/json",
               "server": "..."
            }

    + Body

            {
                "module": "WSQR Server",
                "description": "WSQR Server of the EU-funded project S-CASE. See http://www.scasefp7.eu/",
                "_links": {
                    "services": "http://localhost:8022/services"
                }
            }


# Add Service [/services/{service_name}/add]
An endpoint for adding new web services.

Adding a new web service requires sending a POST request including the service name and
receiving a response containing the following attributes: 

- serviceName

## Annotating phrases [POST]

Using the WSQR server for adding a service requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
        }

If the request is correct the server should return a Response 200

+ Response 200 (application/json)
   + Headers
   
            {
               "transfer-encoding": "chunked",
               "date": "...",
               "content-type": "application/json",
               "server": "..."
            }

   + Body

            {
               "serviceName": "..."
            }

If the input format is not correct (e.g. the request is not in json), then the server returns a Response 400

+ Response 400 (text/html)
   + Headers

            {
               "content-length": "...",
               "content-language": "en",
               "server": "...",
               "connection": "close",
               "date": "...",
               "content-type": "text/html;charset=utf-8"
            }

   + Body

            (HTML formatted error)
            HTTP Status 400 - Bad Request
            The request sent by the client was syntactically incorrect.

Finally, if the format of the input is correct, yet the input itself is erroneous (e.g. a misspelled json key),
then the server returns a Response 422

+ Response 422 (text/plain)
   + Headers

            {
               "transfer-encoding": "chunked",
               "date": "...",
               "content-type": "text/plain",
               "server": "..."
            }

   + Body

            JSONObject["..."] not found.
