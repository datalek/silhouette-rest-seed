Silhouette REST Seed
=================================

Example project for Play Framework that use [Silhouette](https://github.com/mohiva/play-silhouette) for authentication and authorization, expose rest api for signup, signin and social authentication.

## Basic usage

### Sign-up

```bash
curl -X POST http://localhost:9000/auth/signup -H 'Content-Type: application/json' -d '{"firstName": "Alessandro", "lastName": "Random", "identifier": "merle@test.it", "password": "ohmygodthispasswordisverystrong!"}' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< X-Auth-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC...

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC...",
  "expiresOn": "2015-02-20T10:35:42.813+01:00"
}
```

### Sign-in

_Not necessary just after the sign-up because you already have a valid token._

```bash
curl -X POST http://localhost:9000/auth/signin/credentials -H 'Content-Type: application/json' -d '{"identifier": "merle@test.it", "password": "ohmygodthispasswordisverystrong!"}' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< X-Auth-Token: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC...

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC...",
  "expiresOn": "2015-02-20T10:35:42.813+01:00"
}
```

### Check if a request is authenticated

```bash
curl http://localhost:9000 -H 'X-Auth-Token:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC...' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8

{
	"id":"0711b0ea-0935-4697-9b0d-6a1fa7233166",
	"loginInfo":{
		"providerID":"credentials",
		"providerKey":"merle@test.it"
	},
	"email":"merle@test.it",
	"info":{
		"firstName":"Alessandro",
		"lastName":"Random",
		"fullName":"Alessandro Random"
	},
	"roles":["user"]
}
```

### Secured Acton with autorization

_The token must belong to a user with Admin role_

```bash
curl http://localhost:9000/onlygodoruser -H 'X-Auth-Token:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVC...' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8

{"result":"Oh yess GOD"}
```

## Features

* Sign Up
* Sign In (Credentials)
* Authorization
* Dependency Injection with Cake Pattern
* Publishing Events
* Avatar service
* Mail service

## Documentation

Consultate the [Silhouette documentation](http://docs.silhouette.mohiva.com/) for more information. If you need help with the integration of Silhouette into your project, don't hesitate and ask questions in our [mailing list](https://groups.google.com/forum/#!forum/play-silhouette) or on [Stack Overflow](http://stackoverflow.com/questions/tagged/playframework).

## Next Features

* Link logged user with one or more social profile (already done, but not tested yet!)
* Custom avatar service

# License

The code is licensed under [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0). 
