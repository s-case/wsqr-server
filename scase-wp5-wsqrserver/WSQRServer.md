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
            
# Delete Service [/services/{service_name}/delete]
An endpoint for deleting a web service.

Deleting a new web service requires sending a DELETE request including the service name and
receiving a response containing the following attributes: 

- serviceName

## Annotating phrases [DELETE]

Using the WSQR server for deleting a service requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
        }
        
If the request is correct the server should return a Response 200 and the Web Service is deleted.

# Add Internal Measure [/services/[service_name}/internal/{measure_name}/add]
An endpoint for adding a new internal measure.

Adding a new internal measure requires sending a POST request including the service name and the measure name and
receiving a response containing the following attributes: 

- serviceName
- measureName
- measureKind
- measureValue

## Annotating phrases [POST]

Using the WSQR server for adding an internal measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
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
               "measure_name": "..."
            }

# Add External Measure [/services/[service_name}/external/{measure_name}/add]
An endpoint for adding a new external measure.

Adding a new external measure requires sending a POST request including the service name and the measure name and
receiving a response containing the following attributes: 

- serviceName
- measureName
- measureKind
- measureValue

## Annotating phrases [POST]

Using the WSQR server for adding an external measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
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
               "measure_name": "..."
            }

# Add Internal Validation Means [/services/{service_name}/internal/{measure_name}/{validation_means}/add]
An endpoint for adding a new internal validation means to a measure.

Adding a new internal validation means requires sending a POST request including the service name, the measure name and the validation means and receiving a response containing the following attributes: 

- serviceName
- measureName
- means

## Annotating phrases [POST]

Using the WSQR server for adding an internal validation means requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
            "validation_means": "..."
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
               "measure_name": "..."
               "validation_means": "..."
            }

# Add External Validation Means [/services/{service_name}/external/{measure_name}/{validation_means}/add]
An endpoint for adding a new ecternal validation means to a measure.

Adding a new external validation means requires sending a POST request including the service name, the measure name and the validation means and receiving a response containing the following attributes: 

- serviceName
- measureName
- means

## Annotating phrases [POST]

Using the WSQR server for adding an external validation means requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
            "validation_means": "..."
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
               "measure_name": "..."
               "validation_means": "..."
            }
            
# Update Internal Measure [/services/{service_name}/internal/{measure_name}/update]
An endpoint for updating an internal measure.

Updating an internal measure requires sending a POST request including the service name and the measure name and receiving a response containing the following attributes: 

- serviceName
- measureName

## Annotating phrases [POST]

Using the WSQR server for updating an internal measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
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
               "measure_name": "..."
               "value_kind": "..."
               "value": "..."
            }

# Update External Measure [/services/{service_name}/external/{measure_name}/update]
An endpoint for updating an external measure.

Updating an external measure requires sending a POST request including the service name and the measure name and receiving a response containing the following attributes: 

- serviceName
- measureName

## Annotating phrases [POST]

Using the WSQR server for updating an external measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
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
               "measure_name": "..."
               "value_kind": "..."
               "value": "..."
            }
            
# Update Internal Validation Means [/{service_name}/internal/{measure_name}/{validation_means}/update]
An endpoint for updating an internal validation means.

Updating an internal validation means requires sending a POST request including the service name, the measure name and the validation means and receiving a response containing the following attributes: 

- serviceName
- measureName
- validationMeans

## Annotating phrases [POST]

Using the WSQR server for updating an internal validation means requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
            "validation_means": "..."
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
               "measure_name": "..."
               "validation_means": "..."
            }  
            
# Update External Validation Means [/{service_name}/external/{measure_name}/{validation_means}/update]
An endpoint for updating an external validation means.

Updating an external validation means requires sending a POST request including the service name, the measure name and the validation means and receiving a response containing the following attributes: 

- serviceName
- measureName
- validationMeans

## Annotating phrases [POST]

Using the WSQR server for updating an external validation means requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
            "validation_means": "..."
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
               "measure_name": "..."
               "validation_means": "..."
            }
            
# Get Internal Measure by Position [/services/{service_name}/internal/{measure_position}/get]
An endpoint for getting an internal measure by position.

Getting a new internal measure requires sending a GET request including the service name and the measure position and receiving a response containing the following attributes: 

- serviceName
- measurePos

## Annotating phrases [POST]

Using the WSQR server for getting an internal measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_pos": "..."
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
               "measure_name": "..."
            }

# Get Internal Measure by Kind [/services/{service_name}/internal/{measure_name}/{value_kind}]
An endpoint for getting an internal measure by value kind.

Getting a new internal measure requires sending a GET request including the service name, the measure name and the value kind and receiving a response containing the following attributes: 

- serviceName
- measureName
- valueKind

## Annotating phrases [POST]

Using the WSQR server for getting an internal measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_value": "..."
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
               "measure_name": "..."
            }

# Get External Measure by Position [/services/{service_name}/external/{measure_position}/get]
An endpoint for getting an external measure by position.

Getting a new external measure requires sending a GET request including the service name and the measure position and receiving a response containing the following attributes: 

- serviceName
- measurePos

## Annotating phrases [POST]

Using the WSQR server for getting an external measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_pos": "..."
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
               "measure_name": "..."
            }  
            
# Get External Measure by Kind [/services/{service_name}/external/{measure_name}/{value_kind}]
An endpoint for getting an external measure by value kind.

Getting a new external measure requires sending a GET request including the service name, the measure name and the value kind and receiving a response containing the following attributes: 

- serviceName
- measureName
- valueKind

## Annotating phrases [POST]

Using the WSQR server for getting an external measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_value": "..."
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
               "measure_name": "..."
            }
            
# Get all Measures [/services/{service_name}/measures]
An endpoint for getting all measures of a web service.

Getting all measures requires sending a GET request including the service name and receiving a response containing the following attributes: 

- serviceName

## Annotating phrases [POST]

Using the WSQR server for getting all measures requires posting the following request:

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
               "measure_name": "..."
            }
            
# Delete Internal Measure [/{service_name}/internal/{measure_name}/{measure_kind}/delete]
An endpoint for deleting an internal measure.

Deleting a new internal measure requires sending a DELETE request including the service name, the measure name and the value kind
receiving a response containing the following attributes: 

- serviceName
- measure
- measureKind

## Annotating phrases [DELETE]

Using the WSQR server for deleting an internal measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
            "measure_kind": "..."
        }

If the request is correct the server should return a Response 200 and the measure is deleted.       

# Delete External Measure [/{service_name}/external/{measure_name}/{measure_kind}/delete]
An endpoint for deleting an external measure.

Deleting a new external measure requires sending a DELETE request including the service name, the measure name and the value kind
receiving a response containing the following attributes: 

- serviceName
- measure
- measureKind

## Annotating phrases [DELETE]

Using the WSQR server for deleting an external measure requires posting the following request:

+ Request (application/json)

        {
            "service_name": "..."
            "measure_name": "..."
            "measure_kind": "..."
        }
        
If the request is correct the server should return a Response 200 and the measure is deleted.
