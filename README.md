# API Documentation Rules

Custom rules for REST api documentation.

## Installing / Getting started

Copy the plugin jar file to the directory extensions/plugins inside your Sonar
Qube instalation, restart the server and activate the rules in some quality
profile.

## Developing

```shell
git clone https://github.com/tst-labs/api-documentation-rules.git
cd api-documentation-rules/
mvn clean install -Pqulice,coverage
```

### Deploying / Publishing

In case there's some step you have to take that publishes this project to a
server, this is the right time to state it.

```shell
packagemanager deploy awesome-project -s server.com -u username -p password
```

And again you'd need to tell what the previous code actually does.

## Features

What's all the bells and whistles this project can perform?
* What's the main functionality
* You can also do another thing
* If you get really randy, you can even do this

## Contributing

If you'd like to contribute, please fork the repository and use a feature
branch. Pull requests are warmly welcome.

Before submitting your pull request, please don't forget to run

```shell
mvn clean install -Pqulice,coverage
```

If there's anything else the developer needs to know (e.g. the code style
guide), you should link it here. If there's a lot of things to take into
consideration, it is common to separate this section to its own file called
`CONTRIBUTING.md` (or similar). If so, you should say that it exists here.

## Licensing

The code in this project is licensed under MIT license, except for some files
that we modified from Sonar Qube samples, which are under LGPL.